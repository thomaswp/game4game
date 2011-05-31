package edu.elon.honors.price.graphics;

import java.util.Collections;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public final class Graphics {
	
	final static int FRAME_BLOCK = 60;
	
	private static LinkedList<Viewport> viewports = new LinkedList<Viewport>();
	private static int width, height;
	
	private static int frameCount = 0;
	private static long lastSystemTime = System.currentTimeMillis();
	private static int fps;
	private static boolean showFPS;
	
	//The Paint used in drawing
	private static Paint paint = new Paint();
	
	
	/**
	 * Returns the width of the current drawable area.
	 * @return The width
	 */
	public static int getWidth() {
		return width;
	}

	/**
	 * Sets the width of the current drawable area.
	 * @param width The width
	 */
	public static void setWidth(int width) {
		Graphics.width = width;
	}

	/**
	 * Returns the height of the current drawable area.
	 * @return
	 */
	public static int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the current drawable area.
	 * @param height The height
	 */
	public static void setHeight(int height) {
		Graphics.height = height;
	}
	
	public static boolean isShowFPS() {
		return showFPS;
	}

	public static void setShowFPS(boolean showFPS) {
		Graphics.showFPS = showFPS;
	}

	public static int getFps() {
		return fps;
	}

	/**
	 * Adds a viewport to the list of drawn viewports.
	 * @param viewport The viewport.
	 */
	public static void addViewport(Viewport viewport) {
		viewports.add(viewport);
	}
	
	/**
	 * Updates the Graphics class, which updates all of its Viewports.
	 */
	public static void update() {
		for (Viewport viewport : viewports) {
			if (viewport != null) {
				viewport.upadte();
			}
		}
		updateFPS();
	}
	
	/**
	 * Draws the the Sprites in each Viewport.
	 * @param canvas The canvas on which to draw.
	 */
	public static void draw(Canvas canvas) {
		//Clear the canvas
		paint.setARGB(255, 0, 0, 0);
		canvas.drawPaint(paint);
		paint.setARGB(255, 255, 255, 255);
		
		//Allow us to reset the clip to full if we need to
		boolean clipSet = false;
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		
		//Sort Viewports by Z
		Collections.sort(viewports);
		for (Viewport viewport : viewports) {
			//Draw visible Viewports
			if (viewport != null && viewport.isVisible()) {
				
				if (viewport.hasRect()) {
					//If the Viewport isn't the whole drawable area, set the clip
					canvas.clipRect(viewport.getRect());
					clipSet = true;
				} else if (clipSet) {
					//Otherwise restore it to the whole area
					canvas.restore();
					canvas.save(Canvas.CLIP_SAVE_FLAG);
					clipSet = false;
				}
				
				//Sort the Sprites as well
				Collections.sort(viewport.getSprites());
				for (Sprite sprite : viewport.getSprites()) {
					if (sprite != null && sprite.isVisible()) {
						//Draw visible Sprites
						
						//canvas.drawPath(sprite.getCollidableRegion().getBoundaryPath(), paint);
						//canvas.drawRect(sprite.getRect(), paint);
						canvas.drawBitmap(sprite.getBitmap(), sprite.getDrawMatrix(), paint);
					}
				}
			}
		}
		
		if (clipSet) {
			canvas.restore();
		}
		paint.setColor(Color.GRAY);
		paint.setTextSize(20);
		String fpsString = "" + fps;
		float width = paint.measureText(fpsString);
		canvas.drawText(fpsString, getWidth() - width, paint.getTextSize(), paint);
	}

	private static void updateFPS() {
		frameCount = (frameCount + 1) % FRAME_BLOCK;
		if (frameCount == 0) {
			long currentTime = System.currentTimeMillis();
			fps = (int)(FRAME_BLOCK * 1000 / (currentTime - lastSystemTime));
			lastSystemTime = currentTime;
		}
	}
}
