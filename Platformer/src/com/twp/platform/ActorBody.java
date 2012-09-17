package com.twp.platform;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.graphics.Bitmap;
import android.graphics.Color;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.physics.Vector;

public class ActorBody extends PlatformBody {

	private static final int FRAME = 150;
	private static final int BEHAVIOR_REST = 3;

	private AnimatedSprite sprite;
	private ActorClass actor;
	private boolean isHero;
	private int directionX = 1;
	private boolean stopped;
	private int lastBehaviorTime;
	private int stun;
	private boolean onLadder;
	private World world;
	
	private int animationFrames = 8, animations = 7;
	
	@Override
	public List<BehaviorInstance> getBehaviorInstances() {
		return actor.behaviors;
	}

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
	
	public boolean isStunned() {
		return stun > 0;
	}
	
	public boolean isHero() {
		return isHero;
	}
	
	public ActorClass getActor() {
		return actor;
	}

	public int getFacingDirectionX() {
		return (sprite.getFrame() / animationFrames) == 1 ? -1 : 1;
	}

	@Override
	public AnimatedSprite getSprite() {
		return sprite;
	}

	public ActorBody(Viewport viewport, PhysicsHandler physics, ActorClass actor, int id, 
			float startX, float startY, int startDir, boolean isHero) {
		super(viewport, physics, id, startX, startY);
		
		this.actor = actor;
		this.directionX = startDir;
		Bitmap bitmap = Data.loadActor(actor.imageName);
		Bitmap[] frames;
		if (actor.animated) {
			frames = Tilemap.createTiles(bitmap, bitmap.getWidth() / animationFrames, bitmap.getHeight() / animations, 0); 
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
		super.sprite = sprite;
		this.isHero = isHero;
		world = physics.getWorld();
		
		behaviorRuntimes = new BehaviorRuntime[actor.behaviors.size()];
		for (int i = 0; i < behaviorRuntimes.length; i++) {
			behaviorRuntimes[i] = new BehaviorRuntime(
					actor.behaviors.get(i), physics.getGame());
		}

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

	@Override
	public void update(long timeElapsed, Vector offset) {
		lastBehaviorTime += timeElapsed;
		stun -= timeElapsed;
		collidedBodies.clear();
		collidedWall = false;
		
		if (actor.fixedRotation) {
			float pi = (float)Math.PI;
			float gAngle = world.getGravity().angle() *	pi / 180 + 3 * pi / 2;
			float bAngle = body.getAngle();
			if (Math.abs(bAngle - gAngle) > 0) {
				if (bAngle - gAngle > pi) {
					gAngle += 2 * pi;
				}
				if (gAngle - bAngle > pi) {
					bAngle += 2 * pi;
				}
				bAngle = bAngle * 0.9f + gAngle * 0.1f;
			}
			body.setTransform(body.getPosition().x, 
					body.getPosition().y, bAngle);
		}
		
		if (isHero) {
			directionX = (int)Math.signum(getVelocity().x);
			stopped = Math.abs(directionX) < 0.001f; 
			if (getVelocity().x != 0)
				setOnLadder(false);
		}
		
		
		if (actor.animated && stun <= 0) {
			if (onLadder) {
				sprite.setFrame(3 * animationFrames);
			} else {
				int frame = sprite.getFrame();
				if (directionX > 0 && (frame / animationFrames != 2 || !sprite.isAnimated())) {
					sprite.Animate(FRAME, 2 * animationFrames, animationFrames);
				} else if (directionX < 0 && (frame / animationFrames != 1 || !sprite.isAnimated())) {
					sprite.Animate(FRAME, animationFrames, animationFrames);
				}
				if (sprite.isAnimated()) {
					if (stopped) {
						sprite.setFrame(frame - frame % animationFrames);
					}
				}
			}
		}
		
		if (!isHero && actor.speed > 0)
			setVelocityX(stopped ? 0 : directionX * actor.speed);

		updateSprite(offset);
		lastVelocity.set(getVelocity());
	}

	public void doBehaviorWall() {
		doBehavior(actor.wallBehavior, null);
	}
	
	public void doBehaviorEdge() {
		doBehavior(actor.edgeBehavior, null);
	}
	
	public void doBehavoirCollideActor(int dir, ActorBody cause) {
		doBehavior(actor.actorContactBehaviors[dir], cause);
	}
	
	public void doBehaviorCollideHero(int dir, ActorBody cause) {
		doBehavior(actor.heroContactBehaviors[dir], cause);
	}
	
	public void doBehavior(int behavior, ActorBody cause) {
		if (lastBehaviorTime < BEHAVIOR_REST)
			return;
		lastBehaviorTime = 0;

		switch (behavior) {
		case ActorClass.BEHAVIOR_STOP:
			stopped = true;
			break;
		case ActorClass.BEHAVIOR_JUMP_TURN:
			jump(false);
		case ActorClass.BEHAVIOR_TURN:
			directionX *= -1;
			break;
		case ActorClass.BEHAVIOR_JUMP:
			jump(false);
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
	
	public void stun(ActorBody cause) {
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
	
	public void setHorizontalVelocity(float hv) {
		Vector2 velocity = getVelocity();
		Vector2 gHat = world.getGravity().tempCopy().nor();
		//Project b onto gravity to erase horizontal velocity
		velocity.set(gHat.mul(gHat.dot(velocity)));
		//Game.debug("%f, %f", newVelocity.x, newVelocity.y);
		Vector2 horizontal = world.getGravity().tempCopy().rotate(-90);
		horizontal.mul(hv / horizontal.len());
		velocity.add(horizontal);
		
		setVelocity(velocity);
		Vector2.releaseTemps();
	}
	
	public void setVerticalVelocity(float hv) {
		Vector2 velocity = getVelocity();
		Vector2 gHat = world.getGravity().tempCopy().nor().rotate(-90);
		//Project b onto gravity to erase horizontal velocity
		velocity.set(gHat.mul(gHat.dot(velocity)));
		//Game.debug("%f, %f", newVelocity.x, newVelocity.y);
		Vector2 vertical = world.getGravity().tempCopy().rotate(180);
		vertical.mul(hv / vertical.len());
		velocity.add(vertical);
		
		setVelocity(velocity);
		Vector2.releaseTemps();
	}
	
	public void jump(boolean checkGrounded) {
		///if (!checkGrounded || isGrounded() || isOnLadder()) {
		setVerticalVelocity(actor.jumpVelocity);	
		onLadder = false;
		//}
	}

	@Override
	protected boolean collidesWith(PlatformBody body) {
		if (body instanceof ActorBody) {
			if (((ActorBody)body).isHero) {
				return actor.collidesWithHero;
			}
			return actor.collidesWithActors;
		} else if (body instanceof ObjectBody) {
			return actor.collidesWithObjects;
		}
		
		return false;
	}

	@Override
	public void onTouchGround() {
		this.onLadder = false;
	}
	
	public void checkBehavior() {
		for (int j = 0; j < collidedBodies.size(); j++) {
			if (collidedBodies.get(j) instanceof ActorBody)
			{
				ActorBody bodyB = (ActorBody)collidedBodies.get(j);
				
				int dir = getCollisionDirection(this, bodyB);
				if (bodyB.isHero())
					doBehaviorCollideHero(dir, bodyB);
				else
					doBehavoirCollideActor(dir, bodyB);
			}
		}
		if (collidedWall) {
			doBehaviorWall();
		}
		if (getDirectionX() != 0) {
			float y = getPosition().y + (sprite.getHeight() / 2 + 5) / SCALE;
			float x = getPosition().x + (sprite.getWidth() / 2 + 5) * getDirectionX() / SCALE;
			FixtureCallback callback = new FixtureCallback(physics);
			world.QueryAABB(callback, x, y, x + 3 / SCALE, y + 3 / SCALE);
			if (!callback.contact && isGrounded()) {
				doBehaviorEdge();
			}
		}
	}
	
	private static class FixtureCallback implements QueryCallback {
		public boolean contact = false;
		public PhysicsHandler physics;

		private FixtureCallback(PhysicsHandler physics) {
			this.physics = physics;
		}

		@Override
		public boolean reportFixture(Fixture fixture) {
			if (physics.getFixtureTile(fixture) != null) {
				contact = true;
				return false;
			}
			return true;
		}
	}
}
