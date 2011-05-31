package com.twp.asteroids;

import java.io.Serializable;

import edu.elon.honors.price.graphics.Sprite;

public class Bullet implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final transient float SPEED = 0.3f;
	public float x, y, direction;
	public transient Sprite sprite;
	
	public float getDx() {
		return SPEED * (float)Math.cos(direction);
	}

	public float getDy() {
		return SPEED * (float)Math.sin(direction);
	}
	
	public Bullet(float x, float y, float direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	public void updateSprite() {
		sprite.setX(x);
		sprite.setY(y);
	}
	
	public void dispose() {
		sprite.dispose();
	}
}
