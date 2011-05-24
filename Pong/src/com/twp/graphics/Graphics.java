package com.twp.graphics;

import java.util.Collections;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Paint;

public final class Graphics {
	
	private static LinkedList<Viewport> viewports;
	static {
		viewports = new LinkedList<Viewport>();
	}
	
	private static Paint paint;
	static {
		paint = new Paint();
	}
	
	private static int width, height;
	
	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		Graphics.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		Graphics.height = height;
	}

	private Graphics() {}
	
	public static void addViewport(Viewport viewport) {
		viewports.add(viewport);
	}
	
	public static void update() {
		for (Viewport viewport : viewports) {
			if (viewport != null) {
				viewport.upadte();
			}
		}
	}
	
	public static void draw(Canvas canvas) {
		paint.setARGB(255, 0, 0, 0);
		canvas.drawPaint(paint);
		paint.setARGB(255, 255, 255, 255);
		
		Collections.sort(viewports);
		for (Viewport viewport : viewports) {
			if (viewport != null) {
				Collections.sort(viewport.getSprites());
				for (Sprite sprite : viewport.getSprites()) {
					if (sprite != null) {
						canvas.drawBitmap(
								sprite.getBitmap(), 
								(float)(viewport.getX() + sprite.getX() - sprite.getOriginX()), 
								(float)(viewport.getY() + sprite.getY() - sprite.getOriginY()), 
								paint);
					}
				}
			}
		}
		
	}
}
