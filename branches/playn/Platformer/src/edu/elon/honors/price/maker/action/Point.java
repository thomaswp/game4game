package edu.elon.honors.price.maker.action;

public class Point {
	public int x, y;
	public Point(int x, int y) {
		this.x = x; this.y = y;
	}
	public Point() {
		
	}
	public Point(float x, float y) {
		setF(x, y);
	}
	public void set(int x, int y) {
		this.x = x; this.y = y;
	}
	public void set(Point point) {
		set(point.x, point.y);
	}
	public void setF(float x, float y) {
		set(Math.round(x), Math.round(y));
	}
}
