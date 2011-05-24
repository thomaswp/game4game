package com.twp.graphics;

import java.util.LinkedList;

public class Viewport implements Comparable<Viewport> {

	private int x, y, z, width, height;
	
	private LinkedList<Sprite> sprites;
	
	public static Viewport DefaultViewport;
	static {
		DefaultViewport = new Viewport(0, 0, -1, -1);
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public LinkedList<Sprite> getSprites() {
		return sprites;
	}

	public Viewport(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		z = 0;
		sprites = new LinkedList<Sprite>();
		
		Graphics.addViewport(this);
	}
	
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}
	
	@Override
	public int compareTo(Viewport another) {
		return ((Integer)z).compareTo(another.z);
	}
	
	public void upadte() {
		for (Sprite sprite : sprites) {
			if (sprite != null) {
				sprite.update();
			}
		}
	}
	
	
}
