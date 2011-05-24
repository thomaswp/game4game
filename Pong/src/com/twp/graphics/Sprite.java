package com.twp.graphics;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class Sprite implements Comparable<Sprite> {
	

	private static Config defaultConfig;
	static {
		defaultConfig = Config.RGB_565;
	}
	
	private Bitmap bitmap;
	private Viewport viewport;
	
	private double x, y, originX, originY;
	
	private int z;
	
	public static Config getDefaultConfig() {
		return defaultConfig;
	}

	public static void setDefaultConfig(Config defaultConfig) {
		Sprite.defaultConfig = defaultConfig;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getOriginX() {
		return originX;
	}

	public void setOriginX(double originX) {
		this.originX = originX;
	}

	public double getOriginY() {
		return originY;
	}

	public void setOriginY(double originY) {
		this.originY = originY;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public Viewport getViewport() {
		return viewport;
	}

	public Sprite() {
		this(Viewport.DefaultViewport);
	}
	
	public Sprite(Viewport viewport) {
		this(viewport, null);
	}

	public Sprite(Viewport viewport, double x, double y, int width, int height) {
		this(viewport, Bitmap.createBitmap(width, height, defaultConfig));
		this.x = x;
		this.y = y;
	}
	
	public Sprite(Viewport viewport, Bitmap bitmap) {
		this.viewport = viewport;
		this.bitmap = bitmap;
		viewport.addSprite(this);
	}
	
	@Override
	public int compareTo(Sprite another) {
		return ((Integer)z).compareTo(another.z);
	}
	
	public void update() {
		
	}
}
