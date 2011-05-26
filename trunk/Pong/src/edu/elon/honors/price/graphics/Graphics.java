package edu.elon.honors.price.graphics;

import java.util.Collections;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Paint;

public final class Graphics {
	
	private static LinkedList<Viewport> viewports = new LinkedList<Viewport>();
	private static int width, height;
	
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
						canvas.drawBitmap(
								sprite.getBitmap(),
								//Use the Viewport's X, plus the Sprites, minus it's origin
								(float)(viewport.getX() + sprite.getX() - sprite.getOriginX()), 
								(float)(viewport.getY() + sprite.getY() - sprite.getOriginY()), 
								paint);
					}
				}
			}
		}
	}
	
	public static class Handle<T> {
		private T[] items;
		
		public T[] getItems() {
			return items;
		}
		
		public Handle(T... items) {
			this.items = items;
		}
	}
}
