package edu.elon.honors.price.physics;

import java.io.Serializable;

public class Vector implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private float x, y;
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
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
	
	public Vector plus(Vector v) {
		return new Vector(x + v.getX(), y + v.getY());
	}
	
	public void subtract(Vector v) {
		this.x -= v.getX();
		this.y -= v.getY();
	}
	
	public Vector minus(Vector v) {
		return new Vector(x - v.getX(), y - v.getY());
	}
	
	public void multiply(float c) {
		this.x *= c;
		this.y *= c;
	}
	
	public Vector times(float c) {
		return new Vector(x * c, y * c);
	}
	
	public float magnitude() {
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public Vector unitVector() {
		return this.times(1 / magnitude());
	}
	
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
	
	public static float dot(Vector v1, Vector v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY();
	}
	
	public static Vector angleVectorDegrees(float angle) {
		return angleVectorRadians(angle * (float)Math.PI / 180);
	}
	
	public static Vector angleVectorRadians(float angle) {
		return new Vector((float)Math.cos(angle), (float)Math.sin(angle));
	}
	
	public String toString() {
		return "{" + x + "," + y + "}";
	}
}
