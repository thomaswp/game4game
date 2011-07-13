package com.twp.platform;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.physics.Vector;

public class PlatformBody {

	private static final int FRAME = 150;
	private static final int BEHAVIOR_REST = 3;
	private static final float SCALE = PlatformLogic.SCALE;

	private Body body;
	private AnimatedSprite sprite;
	private ActorClass actor;
	private int id;
	private boolean isHero;
	private ArrayList<PlatformBody> bodies;
	private int directionX = 1;
	private boolean stopped;
	private int lastBehaviorTime;
	private int stun;
	private World world;
	private Vector2 lastVelocity = new Vector2();
	private Vector scaledPosition = new Vector();
	private ArrayList<PlatformBody> touchingBodies = new ArrayList<PlatformBody>();
	private ArrayList<Fixture> touchingWalls = new ArrayList<Fixture>();
	private ArrayList<Fixture> touchingFloors = new ArrayList<Fixture>();
	private ArrayList<PlatformBody> collidedBodies = new ArrayList<PlatformBody>();
	private boolean collidedWall;
	private boolean onLadder;

	public boolean isOnLadder() {
		return onLadder;
	}

	public void setOnLadder(boolean onLadder) {
		if (onLadder) {
			setVelocityX(0);
		}
		this.onLadder = onLadder;
	}

	public int getDirectionX() {
		return directionX;
	}

	public void setDirectionX(int directionX) {
		this.directionX = directionX;
	}

	public ArrayList<PlatformBody> getCollidedBodies() {
		return collidedBodies;
	}

	public boolean isCollidedWall() {
		return collidedWall;
	}
	
	public void setCollidedWall(boolean collidedWall) {
		this.collidedWall = collidedWall;
	}

	public boolean isTouchingWall() {
		return touchingWalls.size() > 0;
	}
	
	public ArrayList<Fixture> getTouchingWalls() {
		return touchingWalls;
	}
	
	public ArrayList<Fixture> getTouchingFloors() {
		return touchingFloors;
	}

	public ArrayList<PlatformBody> getTouchingBodies() {
		return touchingBodies;
	}

	public boolean isGrounded() {
		return touchingFloors.size() > 0;
	}
	
	public boolean isStunned() {
		return stun > 0;
	}
	
	public boolean isHero() {
		return isHero;
	}
	
	public int getId() {
		return id;
	}

	public Body getBody() {
		return body;
	}

	public ActorClass getActor() {
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
	
	public Vector getScaledPosition() {
		scaledPosition.set(body.getPosition().x * SCALE, 
				body.getPosition().y * SCALE);
		return scaledPosition;
	}

	public int getFacingDirectionX() {
		return (sprite.getFrame() / 4) == 1 ? -1 : 1;
	}

	public AnimatedSprite getSprite() {
		return sprite;
	}

	public PlatformBody(Viewport viewport, World world, ActorClass actor, int id, 
			float startX, float startY, int startDir, boolean isHero, ArrayList<PlatformBody> bodies) {
		this.id = id;
		this.actor = actor;
		this.directionX = startDir;
		Bitmap bitmap = Data.loadActor(actor.imageName);
		Bitmap[] frames;
		if (actor.animated) {
			frames = Tilemap.createTiles(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, 0); 
		} else {
			frames = new Bitmap[] { bitmap.copy(bitmap.getConfig(), true) };
		}
//		for (Bitmap bmp : frames) {
//			Canvas c = new Canvas();
//			c.setBitmap(bmp);
//			Paint p = new Paint();
//			p.setStyle(Style.STROKE);
//			p.setColor(Color.BLACK);
//			c.drawOval(new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), p);
//			//c.drawRect(new Rect(0, 0, bmp.getWidth() - 1, bmp.getHeight() - 1), p);
//		}
		this.sprite = new AnimatedSprite(viewport, frames, startX, startY);
		this.sprite.centerOrigin();
		this.sprite.setZoom(actor.zoom);
		this.isHero = isHero;
		this.bodies = bodies;
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
	}

	public static Vector2 spriteToVect(Sprite sprite, Vector offset) {
		if (offset == null)
			return new Vector2(sprite.getX() / SCALE, sprite.getY() / SCALE);

		return new Vector2((sprite.getX() - offset.getX()) / SCALE, (sprite.getY() - offset.getY()) / SCALE);
	}

	public void update(long timeElapsed, Vector offset) {
		lastBehaviorTime += timeElapsed;
		stun -= timeElapsed;
		collidedBodies.clear();
		collidedWall = false;
		
		if (isHero) {
			directionX = (int)Math.signum(getVelocity().x);
			stopped = Math.abs(directionX) < 0.001f; 
			if (getVelocity().x != 0)
				setOnLadder(false);
		}
		
		if (actor.animated && stun <= 0) {
			if (onLadder) {
				sprite.setFrame(12);
			} else {
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
		this.bodies.set(id, null);
	}

	public void doBehaviorWall() {
		doBehavior(actor.wallBehavior, null);
	}
	
	public void doBehaviorEdge() {
		doBehavior(actor.edgeBehavior, null);
	}
	
	public void doBehavoirCollideActor(int dir, PlatformBody cause) {
		doBehavior(actor.actorContactBehaviors[dir], cause);
	}
	
	public void doBehaviorCollideHero(int dir, PlatformBody cause) {
		doBehavior(actor.heroContactBehaviors[dir], cause);
	}
	
	public void doBehavior(int behavior, PlatformBody cause) {
		if (lastBehaviorTime < BEHAVIOR_REST)
			return;
		lastBehaviorTime = 0;

		switch (behavior) {
		case ActorClass.BEHAVIOR_STOP:
			stopped = true;
			break;
		case ActorClass.BEHAVIOR_JUMP_TURN:
			jump();
		case ActorClass.BEHAVIOR_TURN:
			directionX *= -1;
			break;
		case ActorClass.BEHAVIOR_JUMP:
			jump();
			break;
		case ActorClass.BEHAVIOR_START_STOP:
			stopped = !stopped;
			break;
		case ActorClass.BEHAVIOR_STUN:
			stun(cause);
			break;
		case ActorClass.BEHAVIOR_DIE:
			dispose();
			break;
		}	
	}
	
	public void stun(PlatformBody cause) {
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
		onLadder = false;
	}
	
	public void jump() {
		if (isGrounded() || isOnLadder()) {
			setVelocity(getVelocity().x, -actor.jumpVelocity);
			onLadder = false;
		}
	}

	public static boolean contactBetween(Contact contact, PlatformBody body1, PlatformBody body2) {
		ArrayList<Fixture> fixtures1 = body1.getBody().getFixtureList(), 
		fixtures2 = body2.getBody().getFixtureList();
		Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();
		return ((fixtures1.contains(fixtureA) && fixtures2.contains(fixtureB)) ||
				(fixtures2.contains(fixtureA) && fixtures1.contains(fixtureB)));
	}

	public static boolean collides(PlatformBody body1, PlatformBody body2) {
		return collidesOneWay(body1, body2) && collidesOneWay(body2, body1);
	}

	public static int getCollisionDirection(PlatformBody bodyA, PlatformBody bodyB) {
		RectF rectA = bodyA.getSprite().getRect();
		RectF rectB = bodyB.getSprite().getRect();
		float nx = rectB.centerX() - rectA.centerX();
		float ny = rectB.centerY() - rectA.centerY();

		int dir;
		if (Math.abs(nx) > Math.abs(ny)) {
			if (nx > 0) {
				//Game.debug(bodyA.getActor().name + ": Right");
				dir = ActorClass.RIGHT;
			} else {
				//Game.debug(bodyA.getActor().name + ": Left");
				dir = ActorClass.LEFT;
			}
		} else {
			if (ny > 0) {
				//Game.debug(bodyA.getActor().name + ": Below");
				dir = ActorClass.BELOW;
			} else {
				//Game.debug(bodyA.getActor().name + ": Above");
				dir = ActorClass.ABOVE;
			}
		}
		
		return dir;
	}
	
	private static boolean collidesOneWay(PlatformBody body1, PlatformBody body2) {
		ActorClass actor1 = body1.getActor();
		if (body2.isHero) {
			return actor1.collidesWithHero;
		} else {
			return actor1.collidesWithActors;
		}
	}
}
