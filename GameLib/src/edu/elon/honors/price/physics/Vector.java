package edu.elon.honors.price.physics;

import java.io.Serializable;

public class Vector implements Serializable{
	private static final long serialVersionUID = 2L;
	
	private float x, y;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}
	
	public void addX(float dx) {
		this.x += dx;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void addY(float dy) {
		this.y += dy;
	}

	public Vector set(Vector v) {
		set(v.getX(), v.getY());
		return this;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void add(Vector v) {
		this.x += v.getX();
		this.y += v.getY();
	}
	
	public Vector() {
		this(0, 0);
	}
	
	public void clear() {
		set(0, 0);
	}
	
//	public Vector plus(Vector v) {
//		return new Vector(x + v.getX(), y + v.getY());
//	}
	
	public Vector subtract(Vector v) {
		this.x -= v.getX();
		this.y -= v.getY();
		return this;
	}
	
//	public Vector minus(Vector v) {
//		return new Vector(x - v.getX(), y - v.getY());
//	}
	
	public Vector multiply(float c) {
		this.x *= c;
		this.y *= c;
		return this;
	}
	
//	public Vector times(float c) {
//		return new Vector(x * c, y * c);
//	}
	
	public float magnitude() {
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public Vector makeUnitVector() {
		if (magnitude() != 0) {
			multiply(1 / magnitude());
		}
		return this;
	}
	
//	public Vector unitVector() {
//		return magnitude() == 0 ? new Vector() : this.times(1 / magnitude());
//	}
	
	public float angleDegrees() {
		return (float)(angleRadians() * 180 / Math.PI); 
	}
	
	public float angleRadians() {
		double angle;
		if (x == 0) {
			angle = y > 0 ? Math.PI / 2 : 3 * Math.PI / 2;
		} else {
			angle = Math.atan(y / x);;
			if (x < 0) angle += Math.PI;
		}
		return (float)angle;
	}
	
	public Vector copy() {
		return new Vector(x, y);
	}
	
	public static float dot(Vector v1, Vector v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY();
	}
	
	public static void makeAngleVectorDegrees(float angle, Vector v) {
		makeAngleVectorRadians(angle * (float)Math.PI / 180, v);
	}
	
	public static void makeAngleVectorRadians(float angle, Vector v) {
		v.set((float)Math.cos(angle), (float)Math.sin(angle));
	}
	
	public String toString() {
		return "{" + x + "," + y + "}";
	}
}
