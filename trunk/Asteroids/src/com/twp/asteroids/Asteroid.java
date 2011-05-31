package com.twp.asteroids;

import java.io.Serializable;
import java.util.Random;

import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;

public class Asteroid implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final transient int SMALLEST_SIZE = 3;
	public static final transient Random rand = new Random();

	private static final transient float SCALE = 0.6f;

	public float x, y, rotation, dx, dy, dRotation;
	public int size;

	public transient Sprite sprite;

	public static float getZoom(int size) {
		return (float)(1 / Math.pow(1.4, size)) * SCALE;
	}

	public float getZoom() {
		return getZoom(size);
	}

	public Asteroid(int size) {
		this(0, 0, size);
		float width = Graphics.getWidth(), height = Graphics.getHeight();
		x = rand.nextFloat() * width / 5 + 
			(rand.nextBoolean() ? width * 3 / 5 + 
				rand.nextFloat() * width / 5 : 0);
		
		y = rand.nextFloat() * height / 5 + 
			(rand.nextBoolean() ? height * 3 / 5 + 
				rand.nextFloat() * height / 5 : 0);
	}

	public Asteroid(float x, float y, int size) {
		this.size = size;
		this.x = x;
		this.y = y;
		rotation = rand.nextFloat() * 360;
		dx = rand.nextFloat() * 0.06f * (float)Math.pow(1.2, size);
		dy = rand.nextFloat() * 0.06f * (float)Math.pow(1.2, size);
		dRotation = rand.nextFloat() * (rand.nextBoolean() ? -1 : 1) * 0.1f;
	}

	public void updateSprite() {
		sprite.setX(x);
		sprite.setY(y);
		sprite.setZoom(getZoom());
		sprite.setRotation(rotation);
	}

	public void dispose() {
		sprite.dispose();
	}
}
