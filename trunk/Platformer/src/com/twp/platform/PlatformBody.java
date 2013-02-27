package com.twp.platform;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.RectF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.MapClass;
import edu.elon.honors.price.data.MapClass.CollidesWith;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Vector;

public abstract class PlatformBody implements IBehaving {
	protected static final float SCALE = PlatformLogic.SCALE;
	

	public final static float MAX_DENSITY = 10; 
	public final static float DENSITY_SCALE = 2 * (float)Math.log(MAX_DENSITY);

	public static float getDensity(float perc) {
		float mult = (perc - 0.5f) * DENSITY_SCALE;
		return (float)Math.exp(mult);
	}
	
	protected Body body;
	protected Sprite sprite;
	protected int id;
	protected PhysicsHandler physics;
	protected Vector2 lastVelocity = new Vector2();
	protected Vector scaledPosition = new Vector();
	protected ArrayList<PlatformBody> touchingBodies = new ArrayList<PlatformBody>();
	protected ArrayList<Fixture> touchingWalls = new ArrayList<Fixture>();
	protected ArrayList<Fixture> touchingFloors = new ArrayList<Fixture>();
	protected ArrayList<PlatformBody> collidedBodies = new ArrayList<PlatformBody>();
	protected boolean collidedWall;
	protected boolean disposed;
	protected BehaviorRuntime[] behaviorRuntimes;
	protected BodyAnimation animation;
	
	@Override
	public BehaviorRuntime[] getBehaviorRuntimes() {
		return behaviorRuntimes;
	}
	
	@Override
	public int getBehaviorCount() {
		if (behaviorRuntimes == null) return 0;
		return behaviorRuntimes.length;
	}
	
	public ArrayList<PlatformBody> getCollidedBodies() {
		return collidedBodies;
	}

	public boolean isCollidedWall() {
		return collidedWall;
	}
	
	public boolean isDisposed() {
		return disposed;
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
	
	public int getId() {
		return id;
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
	
	public void setVelocity(Vector2 velocity) {
		body.setLinearVelocity(velocity);
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
	
	public void setPosition(float x, float y) {
		body.setTransform(x, y, body.getAngle());
	}
	
	public void setScaledPosition(int x, int y) {
		setPosition(x / SCALE, y / SCALE);
	}

	public Sprite getSprite() {
		return sprite;
	}
	
	public PlatformBody(Viewport viewport, PhysicsHandler physics, int id, 
			float startX, float startY) {
		this.physics = physics;
		this.id = id;
	}
	
	public void dispose() {
		this.sprite.dispose();
		physics.postDisposeBody(this);
		disposed = true;
	}
	
	public void updateSprite(Vector offset) {
		setSpritePosition(sprite, body, offset);
		sprite.setRotation(body.getAngle() * 180 / (float)Math.PI);
	}
	
	public static Vector2 spriteToVect(Sprite sprite, Vector offset) {
		if (offset == null)
			return new Vector2(sprite.getX() / SCALE, sprite.getY() / SCALE);

		return new Vector2((sprite.getX() - offset.getX()) / SCALE, (sprite.getY() - offset.getY()) / SCALE);
	}
	
	public static void setSpritePosition(Sprite sprite, Body body, Vector offset) {
		sprite.setX(body.getPosition().x * SCALE + offset.getX());
		sprite.setY(body.getPosition().y * SCALE + offset.getY());
	}
	
	public static boolean contactBetween(Contact contact, PlatformBody body1, PlatformBody body2) {
		ArrayList<Fixture> fixtures1 = body1.getBody().getFixtureList(), 
		fixtures2 = body2.getBody().getFixtureList();
		Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();
		return ((fixtures1.contains(fixtureA) && fixtures2.contains(fixtureB)) ||
				(fixtures2.contains(fixtureA) && fixtures1.contains(fixtureB)));
	}

	public static boolean collides(PlatformBody body1, PlatformBody body2) {
		if (body1 == null || body1.collidesWith(body2)) {
			return body2 == null || body2.collidesWith(body1);
		}
		return false;
	}

	public static int getCollisionDirection(PlatformBody bodyA, PlatformBody bodyB) {
		RectF rectA = bodyA.getSprite().getRect();
		RectF rectB = bodyB.getSprite().getRect();
		float nx = rectB.centerX() - rectA.centerX();
		float ny = rectB.centerY() - rectA.centerY();

		int dir;
		if (Math.abs(nx) > Math.abs(ny)) {
			if (nx > 0) {
				//Debug.write(bodyA.getActor().name + ": Right");
				dir = ActorClass.RIGHT;
			} else {
				//Debug.write(bodyA.getActor().name + ": Left");
				dir = ActorClass.LEFT;
			}
		} else {
			if (ny > 0) {
				//Debug.write(bodyA.getActor().name + ": Below");
				dir = ActorClass.BELOW;
			} else {
				//Debug.write(bodyA.getActor().name + ": Above");
				dir = ActorClass.ABOVE;
			}
		}
		
		return dir;
	}
	
	protected boolean collidesWith(PlatformBody body) {
		CollidesWith with;
		if (body == null) {
			with = CollidesWith.Terrain;
		} else if (body instanceof ObjectBody) {
			if (((ObjectBody) body).isPlatform()) {
				with = CollidesWith.Terrain;
			} else {
				with = CollidesWith.Objects;
			}
		} else if (body instanceof ActorBody) {
			if (((ActorBody) body).isHero()) {
				with = CollidesWith.Hero;
			} else {
				with = CollidesWith.Actors;
			}
		} else {
			return true;
		}
		//Debug.write("%s with %s = %s %s", this, with, getMapClass().collidesWith(with), Arrays.toString(getMapClass().collidesWith));
		return getMapClass().collidesWith(with);
	}
	
	protected short getMaskBits() {
		short bits = 0;
		boolean[] collidesWith = getMapClass().collidesWith;
		for (int i = 0; i < collidesWith.length; i++) {
			if (collidesWith[i]) bits += (1 << i);
		}
		return bits;
	}
	
	public static short getCategoryBits(CollidesWith... collidesWith) {
		short bits = 0;
		for (int i = 0; i < collidesWith.length; i++) {
			bits += (1 << collidesWith[i].ordinal());
		}
		return bits;
	}
	
	public void doWallContact(Fixture fixture) {
		if (!touchingWalls.contains(fixture)) {
			touchingWalls.add(fixture);
		}
		setCollidedWall(true);
	}
	
	public void doFloorContact(Fixture fixture) {
		if (!touchingFloors.contains(fixture)) {
			touchingFloors.add(fixture);
		}
		onTouchGround();
	}
	
	public abstract MapClass getMapClass();
	
	public void update(long timeElapsed, Vector offset) {
		if (animation != null) {
			Vector2 bodyOffset = animation.getFrameOffset(timeElapsed);
			shiftBody(bodyOffset);

			if (animation.isFinished()) {
				animation = null;
			}
		}
	}
	
	public void animate(BodyAnimation animation, boolean force) {
		if (force && animation != null) {
			Vector2 resetVector = this.animation.getResetVector();
			shiftBody(resetVector);
			this.animation = null;
		}
		if (this.animation == null) {
			this.animation = animation;
			
		}
	}
	
	protected void shiftBody(Vector2 bodyOffset) {
		Vector2 bodyPos = body.getPosition();
		bodyOffset.add(bodyPos.x, bodyPos.y);
		body.setTransform(bodyOffset.x, bodyOffset.y, body.getAngle());
	}
	
	public void onTouchGround() { }
}
