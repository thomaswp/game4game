package edu.elon.honors.price.physics;

import java.io.Serializable;

import android.graphics.RectF;
import android.graphics.Region;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;

public class Body implements Serializable {
	public enum EdgeBehavior {
		NONE,
		FLIP,
		DESTROY
	}

	private static final long serialVersionUID = 1L;

	public static final float DEFAULT_EDGE_THRESH = 3;

	private transient Physics physics;
	
	protected Vector position;
	protected Vector velocity;

	protected float rotation;
	protected float dRotation;

	protected float zoomX = 1, zoomY = 1;

	protected int timeout = -1;

	protected transient Sprite sprite;
	
	protected boolean disposed;

	protected EdgeBehavior edgeBehavior = EdgeBehavior.NONE;
	protected float flipThreshold = DEFAULT_EDGE_THRESH;

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public float getX() {
		return position.getX();
	}
	
	public void setX(float x) {
		position.setX(x);
	}
	
	public float getY() {
		return position.getY();
	}
	
	public void setY(float y) {
		position.setY(y);
	}
	
	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getdRotation() {
		return dRotation;
	}

	public void setdRotation(float dRotation) {
		this.dRotation = dRotation;
	}

	public float getDirectionDegrees() {
		return velocity.angleDegrees();
	}

	public void setDirectionDegrees(float angle) {
		setDirectionRadians((float)(angle * Math.PI / 180));
	}

	public float getDirectionRadians() {
		return velocity.angleRadians();
	}

	public void setDirectionRadians(float angle) {
		velocity = Vector.angleVectorRadians(angle).times(velocity.magnitude());
	}

	public float getSpeed() {
		return velocity.magnitude();
	}

	public void setSpeed(float speed) {
		if (velocity.magnitude() == 0)
			velocity = new Vector(speed, 0);
		else
			velocity.multiply(speed / velocity.magnitude());
	}

	public float getZoomX() {
		return zoomX;
	}

	public void setZoomX(float zoomX) {
		this.zoomX = zoomX;
	}

	public float getZoomY() {
		return zoomY;
	}

	public void setZoomY(float zoomY) {
		this.zoomY = zoomY;
	}
	
	public void setZoom(float zoom) {
		setZoomX(zoom);
		setZoomY(zoom);
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public EdgeBehavior getEdgeBehavior() {
		return edgeBehavior;
	}

	public void setEdgeBehavior(EdgeBehavior edgeBehavior) {
		this.edgeBehavior = edgeBehavior;
	}

	public float getFlipThreshold() {
		return flipThreshold;
	}

	public void setFlipThreshold(float flipThreshold) {
		this.flipThreshold = flipThreshold;
	}

	public Physics getPhysics() {
		return physics;
	}
	
	public void setPhysics(Physics physics) {
		this.physics = physics;
		physics.addBody(this);
	}

	public boolean isDisposed() {
		return disposed;
	}
	
	public Body(Physics physics, Sprite sprite) {
		this(physics, sprite.getX(), sprite.getY());
		this.sprite = sprite;
	}

	public Body(Physics physics, float x, float y) {
		this(physics, new Vector(x, y));
	}

	public Body(Physics physics, Vector position) {
		this.position = position;
		this.velocity = new Vector(0, 0);
		this.physics = physics;
		physics.addBody(this);
	}

	public void dispose() {
		disposed = true;
		if (sprite != null) {
			sprite.dispose();
			sprite = null;
		}
	}

	public void accelerate(Vector accel) {
		velocity.add(accel);
	}

	public Region intersection(Body body) {
		return intersection(body.sprite);
	}

	public Region intersection(Sprite s) {
		if (s == null || sprite == null) {
			return new Region();
		}
		return sprite.intersection(s);
	}

	public void updateSprite() {
		if (sprite != null) {
			sprite.setX(position.getX());
			sprite.setY(position.getY());
			sprite.setRotation(rotation);
			sprite.setZoomX(zoomX);
			sprite.setZoomY(zoomY);
		}
	}

	public void updatePhysics(long timeElapsed) {
		if (timeout > 0) {
			if (timeElapsed >= timeout) {
				timeout = -1;
				dispose();
			} else {
				timeout -= timeElapsed;
			}
		}

		position.add(velocity.times(timeElapsed));
		rotation += dRotation * timeElapsed;

		doEdgeBehavior();
	}

	private void doEdgeBehavior() {
		RectF rect;
		if (sprite == null) {
			 rect = new RectF(position.getX(), position.getY(), 
					position.getX() + 1, position.getY() + 1);
		} else {
			rect = sprite.getRect();
		}
		int width = Graphics.getWidth();
		int height = Graphics.getHeight();
		
		if (edgeBehavior == EdgeBehavior.FLIP) {
			if (rect.left > width + flipThreshold) {
				position.setX(position.getX() - rect.right);
			}
			if (rect.right < -flipThreshold) {
				position.setX(width + position.getX() - rect.left);
			}
			if (rect.top > height + flipThreshold) {
				position.setY(position.getY() - rect.bottom);
			}
			if (rect.bottom < -flipThreshold) {
				position.setY(height + position.getY() - rect.top);
			}
		} else if (edgeBehavior == EdgeBehavior.DESTROY) {
			if (!rect.intersect(Graphics.getRectF())) {
				dispose();
			}
		}
	}
}
