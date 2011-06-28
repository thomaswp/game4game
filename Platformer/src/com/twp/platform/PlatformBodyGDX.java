package com.twp.platform;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.physics.Physics;
import edu.elon.honors.price.physics.Vector;

public class PlatformBodyGDX {

	private static final int FRAME = 150;
	private static final int BEHAVIOR_REST = 3;
	private static final float SCALE = PlatformLogicGDX.SCALE;

	private Body body;
	private AnimatedSprite sprite;
	private PlatformActor actor;
	private Tilemap[] layers;
	private PlatformMap map;
	private RectF rect = new RectF();
	private Vector temp = new Vector();
	private boolean grounded;
	private boolean isHero;
	private ArrayList<PlatformBodyGDX> actors;
	private int lastDir;
	private int lastBehavior;
	private RectF lastRect = new RectF();
	private boolean collideX, collideY, collideBoth, edge;
	private int stun;
	private World world;

	public boolean isStunned() {
		return stun > 0;
	}

	public Body getBody() {
		return body;
	}

	public Vector2 getVelocity() {
		return body.getLinearVelocity();
	}

	public void setVelocity(float vx, float vy) {
		body.setLinearVelocity(vx, vy);
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public int getFacingDirectionX() {
		return (sprite.getFrame() / 4) == 1 ? -1 : 1;
	}

	public AnimatedSprite getSprite() {
		return sprite;
	}

	public PlatformBodyGDX(Viewport viewport, World world, PlatformActor actor, float startX, float startY, float zoom,
			Tilemap[] layers, PlatformMap map, boolean isHero, ArrayList<PlatformBodyGDX> actors) {
		this.actor = actor;
		Bitmap bitmap = Data.loadActor(actor.imageName);
		Bitmap[] frames;
		if (actor.animated) {
			frames = Tilemap.createTiles(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, 0); 
			for (Bitmap bmp : frames) {
				Canvas c = new Canvas();
				c.setBitmap(bmp);
				Paint p = new Paint();
				p.setStyle(Style.STROKE);
				p.setColor(Color.BLACK);
				//c.drawCircle(bmp.getHeight() / 2, bmp.getHeight() / 2, bmp.getHeight() / 2, p);
				c.drawRect(new Rect(0, 0, bmp.getWidth() - 1, bmp.getHeight() - 1), p);
			}
		} else {
			frames = new Bitmap[] { bitmap };
		}
		this.sprite = new AnimatedSprite(viewport, frames, startX, startY);
		this.sprite.centerOrigin();
		this.sprite.setZoom(zoom);
		this.layers = layers;
		this.map = map;
		this.isHero = isHero;
		this.actors = actors;
		this.world = world;

		BodyDef actorDef = new BodyDef();
		actorDef.position.set(spriteToVect(sprite, null));
		actorDef.type = BodyType.DynamicBody;
		actorDef.fixedRotation = actor.fixedRotation;
		body = world.createBody(actorDef);
		for (int i = -1; i < 2; i += 2) {
			CircleShape actorShape = new CircleShape();
			FixtureDef actorFix = new FixtureDef();
			if (sprite.getWidth() > sprite.getHeight()) {
				actorShape.setRadius(sprite.getHeight() / SCALE / 2);
				actorShape.setPosition(new Vector2(i * (sprite.getHeight() - sprite.getWidth()) / 2 / SCALE, 0));
			} else {
				actorShape.setRadius(sprite.getWidth() / SCALE / 2);
				actorShape.setPosition(new Vector2(0, i * (sprite.getHeight() - sprite.getWidth()) / 2 / SCALE));
			}
			actorFix.shape = actorShape;
			actorFix.friction = 0;
			actorFix.restitution = 0;
			actorFix.density = 1;
			body.createFixture(actorFix);
		}

	}

	public static Vector2 spriteToVect(Sprite sprite, Vector offset) {
		if (offset == null)
			return new Vector2(sprite.getX() / SCALE, sprite.getY() / SCALE);

		return new Vector2((sprite.getX() - offset.getX()) / SCALE, (sprite.getY() - offset.getY()) / SCALE);
	}

	public void update(long timeElapsed, Vector offset) {
		if (actor.animated && stun <= 0) {
			int frame = sprite.getFrame();
			if (getVelocity().x > 0 && (frame / 4 != 2 || !sprite.isAnimated())) {
				sprite.Animate(FRAME, 8, 4);
			} else if (getVelocity().x < 0 && (frame / 4 != 1 || !sprite.isAnimated())) {
				sprite.Animate(FRAME, 4, 4);
			}
			if (sprite.isAnimated()) {
				if (getVelocity().x == 0) {
					sprite.setFrame(frame - frame % 4);
				}
			}
		}

		updateSprite(offset);
	}

	public static void setSpritePosition(Sprite sprite, Body body, Vector offset) {
		sprite.setX(body.getPosition().x * SCALE + offset.getX());
		sprite.setY(body.getPosition().y * SCALE + offset.getY());
	}

	public void updateSprite(Vector offset) {
		setSpritePosition(sprite, body, offset);
		if (!actor.fixedRotation) {
			sprite.setRotation(body.getAngle() * 180 / (float)Math.PI);
		}
	}

	public void dispose() {
		this.sprite.dispose();
		this.world.destroyBody(body);
		this.actors.remove(this);
	}

	public void doBehavior(int behavior, Vector oldVelocity, PlatformBodyGDX cause) {
		//		if (lastBehavior < BEHAVIOR_REST)
		//			return;
		//		lastBehavior = 0;
		//
		//		Vector velocity = body.getVelocity();
		//		switch (behavior) {
		//		case PlatformActor.BEHAVIOR_STOP:
		//			velocity.setX(0);
		//			break;
		//		case PlatformActor.BEHAVIOR_JUMP_TURN:
		//			velocity.setY(-actor.jumpVelocity);
		//		case PlatformActor.BEHAVIOR_TURN:
		//			velocity.setX(-Math.signum(oldVelocity.getX()) * actor.speed);
		//			break;
		//		case PlatformActor.BEHAVIOR_JUMP:
		//			velocity.setY(-actor.jumpVelocity);
		//			break;
		//		case PlatformActor.BEHAVIOR_START_STOP:
		//			if (velocity.getX() == 0) {
		//				if (lastDir == 0) {
		//					velocity.setX(actor.speed);
		//				} else {
		//					velocity.setX(actor.speed * lastDir);
		//				}
		//			} else {
		//				velocity.setX(0);
		//			}
		//			break;
		//		case PlatformActor.BEHAVIOR_STUN:
		//			stun = actor.stunDuration;
		//			velocity.setY(-actor.jumpVelocity / 1.5f);
		//			sprite.flash(Color.RED, actor.stunDuration);
		//			if (cause == null)
		//				velocity.setX(actor.speed * -getFacingDirectionX() / 1.5f);
		//			else {
		//				float dir = Math.signum(this.getSprite().getRect().left - cause.getSprite().getRect().left);
		//				velocity.setX(actor.speed * dir / 1.5f);
		//			}
		//			if (isHero) {
		//				Input.getVibrator().vibrate(actor.stunDuration / 2);
		//			}
		//			break;
		//		case PlatformActor.BEHAVIOR_DIE:
		//			dispose();
		//			break;
		//		}	
	}
}
