package com.twp.platform;

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
import android.view.VelocityTracker;
import edu.elon.honors.price.data.ActorAnimator;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.MapClass;
import edu.elon.honors.price.data.ActorAnimator.Action;
import edu.elon.honors.price.data.MapClass.CollidesWith;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.physics.Vector;

public class ActorBody extends PlatformBody {

	private Sprite sprite;
	private ActorClass actor;
	private boolean isHero;
	private int directionX;
	private boolean stopped;
	private int stun;
	private boolean onLadder;
	private World world;
	private Vector2 respawnLocation;
	private Vector2 tempVector = new Vector2();
	private ActorAnimator animator;
	private Bitmap[][] frames;
	private boolean airJumped;
	private boolean collidedEdge;
	
	public boolean isCollidedEdge() {
		return collidedEdge;
	}
	
	@Override
	public MapClass getMapClass() {
		return actor;
	}
	
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

//	public int getFacingDirectionX() {
//		int set = getCurrentAnimSet();
//		if (set == SET_WALK_LEFT ||
//				set == SET_JUMP_LEFT ||
//				set == SET_ATTACK_LEFT) {
//			return -1;
//		} else if (set == SET_WALK_RIGHT ||
//				set == SET_JUMP_RIGHT ||
//				set == SET_ATTACK_RIGHT) {
//			return 1;
//		}
//		return 0;
//	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	public ActorBody(Viewport viewport, PhysicsHandler physics, ActorClass actor, int id, 
			float startX, float startY, int startDir, boolean isHero) {
		super(viewport, physics, id, startX, startY);
		
		this.actor = actor;
		this.directionX = startDir;
		this.animator = ActorAnimator.create(actor.imageName);
		
		stopped = true;
		
		Bitmap bitmap = Data.loadActor(actor.imageName);
		Action[] actions = Action.values();
		frames = new Bitmap[actions.length][];
		int frameWidth = bitmap.getWidth() / animator.getTotalCols();
		int frameHeight = bitmap.getHeight() / animator.getTotalRows();
		for (int i = 0; i < actions.length; i++) {
			Action action = actions[i];
			ActorAnimator.ActionParams actionParams =
					animator.getActionParams(action);
			frames[i] = new Bitmap[actionParams.frames];
			for (int j = 0; j < actionParams.frames; j++) {
				frames[i][j] = Bitmap.createBitmap(bitmap,
						(actionParams.column + j) * frameWidth, 
						actionParams.row * frameHeight, 
						frameWidth, frameHeight);
			}
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
		this.sprite = new Sprite(viewport, frames[ActorAnimator.Action.WalkingRight.ordinal()][0]);
		sprite.setX(startX); sprite.setY(startY);
		this.sprite.centerOrigin();
		Debug.write(actor.imageName + ", " + actor.zoom);
		this.sprite.setZoom(actor.zoom);
		this.sprite.setBaseColor(actor.color);
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
		actorDef.fixedRotation = true;
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
			if (isHero) {
				actorFix.filter.categoryBits = getCategoryBits(CollidesWith.Hero);
			} else {
				actorFix.filter.categoryBits = getCategoryBits(CollidesWith.Actors);
			}
			actorFix.filter.maskBits = getMaskBits();
			body.createFixture(actorFix);
			
			actorShape.dispose();
		}

		if (isHero) {
			respawnLocation = new Vector2(body.getPosition());
		}
	}
	
	@Override
	public void destroy() {
		if (isHero) {
			respawnHero();
		} else {
			super.destroy();
		}
	}

	@Override
	public void update(long timeElapsed, Vector offset) {
		super.update(timeElapsed, offset);
		
//		lastBehaviorTime += timeElapsed;
		stun -= timeElapsed;
		collidedBodies.clear();
		collidedWall = false;
		
		checkDeath();
		
		if (world.getGravity().len() > 0) {
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
			stopped = true;
			Vector2 horizontal = world.getGravity().tempCopy().rotate(-90);
			float hVelocity = getVelocity().dot(horizontal);
			if (Math.abs(hVelocity) > 0.0001) {
				directionX = (int)Math.signum(hVelocity);
				stopped = false;
				setOnLadder(false);
			} else {
				directionX = 0;
			}
			horizontal.releaseTemp();
		}

		animator.update(timeElapsed, directionX, isGrounded(), isOnLadder());
		sprite.setBitmap(frames[animator.getAction()][animator.getFrame()]);
		if (sprite.getZoomX() < 0 != animator.isFlipped()) {
			sprite.setZoomX(sprite.getZoomX() * -1);
		}
		
		if (!isHero && actor.speed > 0) {
			setHorizontalVelocity(stopped ? 0 : directionX * actor.speed);
		}

		updateSprite(offset);
		lastVelocity.set(getVelocity());
	}
	
	private void checkDeath() {
		if (body.getPosition().y - sprite.getHeight() / SCALE > 
			physics.getMapFloorMeters()) {
			destroy();
		}
	}
	
	private void respawnHero() {
		body.setTransform(respawnLocation, body.getAngle());
		body.setLinearVelocity(body.getLinearVelocity().mul(0));
		stun(null);
	}
	
	public void doBehaviorWall() {
		//doBehavior(actor.wallBehavior, null);
	}
	
	public void doBehaviorEdge() {
		//doBehavior(actor.edgeBehavior, null);
	}
	
	public void doBehavoirCollideActor(int dir, ActorBody cause) {
		//doBehavior(actor.actorContactBehaviors[dir], cause);
	}
	
	public void doBehaviorCollideHero(int dir, ActorBody cause) {
		//doBehavior(actor.heroContactBehaviors[dir], cause);
	}
	
	public void doBehavior(int behavior, ActorBody cause) {
//		if (lastBehaviorTime < BEHAVIOR_REST)
//			return;
//		lastBehaviorTime = 0;

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
			destroy();
			break;
		}	
	}
	
	public void stun(ActorBody cause) {
		stun = actor.stunDuration;
		sprite.flash(Color.RED, actor.stunDuration);
		if (cause != null) {
			setVelocityY(-actor.jumpVelocity / 1.5f);
			//if (cause == null)
			//	directionX *= -1;
			//else {
				directionX = (int)Math.signum(this.getSprite().getRect().left - cause.getSprite().getRect().left);
			//}
			stopped = false;
			if (isHero) {
				Input.getVibrator().vibrate(actor.stunDuration / 2);
			}
			onLadder = false;
		}
	}
	
	public void triggerAction() { 
		animator.action();
	}
	
	public void setHorizontalVelocity(float hv) {
		if (world.getGravity().len() == 0) {
			setVelocityX(hv);
			return;
		}
		
		Vector2 velocity = getVelocity();
		Vector2 gHat = world.getGravity().tempCopy().nor();
		//Project b onto gravity to erase horizontal velocity
		velocity.set(gHat.mul(gHat.dot(velocity)));
		Vector2 horizontal = world.getGravity().tempCopy().rotate(-90);
		horizontal.mul(hv / horizontal.len());
		velocity.add(horizontal);
		
		setVelocity(velocity);
		Vector2.releaseTemps();
	}
	
	public void setVerticalVelocity(float hv) {
		if (world.getGravity().len() == 0) {
			setVelocityY(-hv);
			return;
		}
		
		Vector2 velocity = getVelocity();
		Vector2 gHat = world.getGravity().tempCopy().nor().rotate(-90);
		//Project b onto gravity to erase horizontal velocity
		velocity.set(gHat.mul(gHat.dot(velocity)));
		//Debug.write("%f, %f", newVelocity.x, newVelocity.y);
		Vector2 vertical = world.getGravity().tempCopy().rotate(180);
		vertical.mul(hv / vertical.len());
		velocity.add(vertical);
		
		setVelocity(velocity);
		Vector2.releaseTemps();
	}
	
	public void jump(boolean checkGrounded) {
		boolean canJump;
		if (isGrounded()) {
			canJump = true;
		} else {
			canJump = actor.doubleJump && !airJumped;
		}
		
		if (!checkGrounded || canJump || isOnLadder()) {
			setVerticalVelocity(actor.jumpVelocity);	
			onLadder = false;
			animator.jump();
			if (!isGrounded()) airJumped = true;
		}
	}

	@Override
	public void onTouchGround() {
		this.onLadder = false;
		airJumped = false;
	}
	
	private FixtureCallback fixtureCallback = new FixtureCallback(physics);
	
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
		collidedEdge = false;
		if (getDirectionX() != 0) {
			float x = getPosition().x;
			float y = getPosition().y;
			tempVector.set((sprite.getWidth() / 2f + 5) * getDirectionX() / SCALE, 
					(sprite.getHeight() / 2f + 10) / SCALE);
			tempVector.rotate(physics.getGravityRotation());
			x += tempVector.x;
			y += tempVector.y;
			
			fixtureCallback.reset();
			float r = 1.5f / SCALE;
			world.QueryAABB(fixtureCallback, x - r, y - r, x + r, y + r);
			boolean contact = fixtureCallback.contact;
			if (!contact && isGrounded()) {
				collidedEdge = true;
			}
		}
	}
	
	private static class FixtureCallback implements QueryCallback {
		public boolean contact = false;
		public PhysicsHandler physics;

		private FixtureCallback(PhysicsHandler physics) {
			this.physics = physics;
		}
		
		public void reset() {
			contact = false;
		}

		@Override
		public boolean reportFixture(Fixture fixture) {
			if (physics.getFixtureTile(fixture) != null) {
				contact = true;
				return false;
			}
			PlatformBody body = physics.getFixtureBody(fixture); 
			if (body != null && body instanceof ObjectBody) {
				if (((ObjectBody)body).isPlatform()) {
					contact = true;
					return false;
				}
			}
			return true;
		}
	}
}
