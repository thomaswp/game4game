package com.twp.asteroids;

import java.io.Serializable;
import java.util.Random;

import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.physics.Body;
import edu.elon.honors.price.physics.Physics;
import edu.elon.honors.price.physics.Vector;

public class Asteroid extends Body {
	private static final long serialVersionUID = 1L;

	public static final transient int SMALLEST_SIZE = 3;
	public static final transient Random rand = new Random();

	private static final transient float SCALE = 0.6f;

	private int size;
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	public static float getZoom(int size) {
		return (float)(1 / Math.pow(1.4, size)) * SCALE;
	}

	public float getZoom() {
		return getZoom(size);
	}

	public Asteroid(Physics physics, int size) {
		this(physics, 0, 0, size);
		float width = Graphics.getWidth(), height = Graphics.getHeight();
		float x = rand.nextFloat() * width / 5 + 
			(rand.nextBoolean() ? width * 3 / 5 + 
				rand.nextFloat() * width / 5 : 0);
		
		float y = rand.nextFloat() * height / 5 + 
			(rand.nextBoolean() ? height * 3 / 5 + 
				rand.nextFloat() * height / 5 : 0);
		this.position = new Vector(x, y);
	}

	public Asteroid(Physics physics, float x, float y, int size) {
		super(physics, x, y);
		this.size = Math.min(size, SMALLEST_SIZE);
		this.rotation = rand.nextFloat() * 360;
		float dx = rand.nextFloat() * 0.06f * (float)Math.pow(1.2, size);
		float dy = rand.nextFloat() * 0.06f * (float)Math.pow(1.2, size);
		this.velocity = new Vector(dx, dy);
		this.dRotation = rand.nextFloat() * (rand.nextBoolean() ? -1 : 1) * 0.1f;
		this.edgeBehavior = EdgeBehavior.FLIP;
		setZoom(getZoom());
	}
}
