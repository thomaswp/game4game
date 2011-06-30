package com.twp.platform;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private boolean isHero;
	private ArrayList<PlatformBodyGDX> actors;
	private int directionX = 1;
	private boolean stopped;
	private int lastBehaviorTime;
	private int stun;
	private World world;
	private Vector2 lastVelocity = new Vector2();

	public boolean isStunned() {
		return stun > 0;
	}
	
	public boolean isHero() {
		return isHero;
	}

	public Body getBody() {
		return body;
	}

	public PlatformActor getActor() {
		return actor;
	}

	public Vector2 getVelocity() {
		return body.getLinearVelocity();
	}

	public void setVelocity(float vx, float vy) {
		body.setLinearVelocity(vx, vy);
	}

	public void setVelocityX(float vx) {
		setVelocity(vx, getVelocity().y);
	}

	public void setVelocityY(float vy) {
		setVelocity(getVelocity().x, vy);
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

	public PlatformBodyGDX(Viewport viewport, World world, PlatformActor actor, float startX, float startY,
			boolean isHero, ArrayList<PlatformBodyGDX> actors) {
		this.actor = actor;
		Bitmap bitmap = Data.loadActor(actor.imageName);
		Bitmap[] frames;
		if (actor.animated) {
			frames = Tilemap.createTiles(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, 0); 
		} else {
			frames = new Bitmap[] { bitmap.copy(bitmap.getConfig(), true) };
		}
		for (Bitmap bmp : frames) {
			Canvas c = new Canvas();
			c.setBitmap(bmp);
			Paint p = new Paint();
			p.setStyle(Style.STROKE);
			p.setColor(Color.BLACK);
			c.drawOval(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), p);
			//c.drawRect(new Rect(0, 0, bmp.getWidth() - 1, bmp.getHeight() - 1), p);
		}
		this.sprite = new AnimatedSprite(viewport, frames, startX, startY);
		this.sprite.centerOrigin();
		this.sprite.setZoom(actor.zoom);
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
			actorFix.friction = actor.fixedRotation ? 0 : 1;
			actorFix.restitution = 0;
			actorFix.density = 1;
			body.createFixture(actorFix);
		}
		
		actors.add(this);
	}

	public static Vector2 spriteToVect(Sprite sprite, Vector offset) {
		if (offset == null)
			return new Vector2(sprite.getX() / SCALE, sprite.getY() / SCALE);

		return new Vector2((sprite.getX() - offset.getX()) / SCALE, (sprite.getY() - offset.getY()) / SCALE);
	}

	public void update(long timeElapsed, Vector offset) {
		lastBehaviorTime += timeElapsed;
		stun -= timeElapsed;
		
		if (isHero) {
			directionX = (int)Math.signum(getVelocity().x);
			stopped = Math.abs(directionX) < 0.001f; 
		}
		
		if (actor.animated && stun <= 0) {
			int frame = sprite.getFrame();
			if (directionX > 0 && (frame / 4 != 2 || !sprite.isAnimated())) {
				sprite.Animate(FRAME, 8, 4);
			} else if (directionX < 0 && (frame / 4 != 1 || !sprite.isAnimated())) {
				sprite.Animate(FRAME, 4, 4);
			}
			if (sprite.isAnimated()) {
				if (stopped) {
					sprite.setFrame(frame - frame % 4);
				}
			}
		}
		
		if (!isHero && actor.speed > 0)
			setVelocityX(stopped ? 0 : directionX * actor.speed);

		updateSprite(offset);
		lastVelocity.set(getVelocity());
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

	public void doBehavior(int behavior, PlatformBodyGDX cause) {
		if (lastBehaviorTime < BEHAVIOR_REST)
			return;
		lastBehaviorTime = 0;

		switch (behavior) {
		case PlatformActor.BEHAVIOR_STOP:
			stopped = true;
			break;
		case PlatformActor.BEHAVIOR_JUMP_TURN:
			setVelocityY(-actor.jumpVelocity);
		case PlatformActor.BEHAVIOR_TURN:
			directionX *= -1;
			break;
		case PlatformActor.BEHAVIOR_JUMP:
			setVelocityY(-actor.jumpVelocity);
			break;
		case PlatformActor.BEHAVIOR_START_STOP:
			stopped = !stopped;
			break;
		case PlatformActor.BEHAVIOR_STUN:
			stun = actor.stunDuration;
			setVelocityY(-actor.jumpVelocity / 1.5f);
			sprite.setColor(Color.WHITE);
			sprite.flash(Color.RED, actor.stunDuration);
			if (cause == null)
				directionX *= -1;
			else {
				directionX = (int)Math.signum(this.getSprite().getRect().left - cause.getSprite().getRect().left);
			}
			stopped = false;
			if (isHero) {
				Input.getVibrator().vibrate(actor.stunDuration / 2);
			}
			break;
		case PlatformActor.BEHAVIOR_DIE:
			dispose();
			break;
		}	
	}

	public static boolean contactBetween(Contact contact, PlatformBodyGDX body1, PlatformBodyGDX body2) {
		ArrayList<Fixture> fixtures1 = body1.getBody().getFixtureList(), 
		fixtures2 = body2.getBody().getFixtureList();
		Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();
		return ((fixtures1.contains(fixtureA) && fixtures2.contains(fixtureB)) ||
				(fixtures2.contains(fixtureA) && fixtures1.contains(fixtureB)));
	}

	public static boolean collides(PlatformBodyGDX body1, PlatformBodyGDX body2) {
		return collidesOneWay(body1, body2) && collidesOneWay(body2, body1);
	}

	private static boolean collidesOneWay(PlatformBodyGDX body1, PlatformBodyGDX body2) {
		PlatformActor actor1 = body1.getActor();
		if (body2.isHero) {
			return actor1.collidesWithHero;
		} else {
			return actor1.collidesWithActors;
		}
	}
}
