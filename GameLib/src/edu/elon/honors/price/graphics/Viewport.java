package edu.elon.honors.price.graphics;

import java.util.ArrayList;
import java.util.Collections;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

public class Viewport implements Comparable<Viewport> {

	//A width or height given when the Viewport should be stretched
	//to fill the drawable area
	public static final int STRETCH = -1;

	private int z, width, height;
	private float x, y;

	private float zoomX = 1, zoomY = 1, rotation, originX, originY;

	private boolean visible, sorted = true;

	private Rect rect = new Rect();
	private RectF rectF = new RectF();

	private int color = Color.WHITE;
	private float opacity = 1;
	
	private ArrayList<Sprite> sprites;

	public static Viewport DefaultViewport = new Viewport(0, 0, STRETCH, STRETCH);

	/**
	 * Gets the X coordinate of the Viewport
	 * @return The X coordinate
	 */
	public float getX() {
		return x;
	}

	/**
	 * Sets the X coordinate of the Viewport
	 * @param x The X coordinate
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Gets the Y coordinate of the Viewport
	 * @return The Y Coordinate
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets the Y coordinate of the Viewport
	 * @param y The Y Coordinate
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Gets the Z coordinate of the Viewport. This determines
	 * the drawing order of this Viewport. High Z coordinates
	 * put the Viewport further on top.
	 * @return The Z Coordinate
	 */
	public int getZ() {
		return z;
	}
	
	/**
	 * Sets the Z coordinate of the Viewport. This determines
	 * the drawing order of this Viewport. High Z coordinates
	 * put the Viewport further on top.
	 * @param z The Z Coordinate
	 */
	public void setZ(int z) {
		this.z = z;
		Graphics.getViewports().remove(this);
		Graphics.addViewport(this);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = Math.min(Math.max(opacity, 0), 1);
	}

	public float getZoomX() {
		return zoomX;
	}

	public void setZoomX(float zoomX) {
		this.zoomX = zoomX;
	}

	public float getZoomY() {
		return zoomY;
	}

	public void setZoomY(float zoomY) {
		this.zoomY = zoomY;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getOriginX() {
		return originX;
	}

	public void setOriginX(float originX) {
		this.originX = originX;
	}

	public float getOriginY() {
		return originY;
	}

	public void setOriginY(float originY) {
		this.originY = originY;
	}


	/**
	 * Gets the width of the Viewport.
	 * Viewport.STRETCH means it will fill the drawable area.
	 * @return The width
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * Sets the width of the Viewport.
	 * Viewport.STRETCH means it will fill the drawable area.
	 * @param width The width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Gets the height of the Viewport.
	 * Viewport.STRETCH means it will fill the drawable area.
	 * @return The height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the Viewport.
	 * Viewport.STRETCH means it will fill the drawable area.
	 * @param height The width
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Returns true if the Viewport has a defined
	 * height and width.
	 * @return 
	 */
	public boolean hasRect() {
		return height != STRETCH && width != STRETCH;
	}

	/**
	 * Gets the rect that the Viewport fills.
	 * @return
	 */
	public Rect getRect() {
		if (!hasRect()) {
			rect.set(0, 0, Graphics.getWidth(), Graphics.getHeight());
		} else {
			rect.set((int)x, (int)y, width + (int)x, height + (int)y);
		}
		return rect;
	}

	public RectF getRectF() {
		if (!hasRect()) {
			rectF.set(0, 0, Graphics.getWidth(), Graphics.getHeight());
		} else {
			rectF.set((int)x, (int)y, width + (int)x, height + (int)y);
		}
		return rectF;
	}

	/**
	 * Gets the list of Sprites contained in this Viewport 
	 * @return The Sprites
	 */
	public ArrayList<Sprite> getSprites() {
		return sprites;
	}

	/**
	 * Gets whether or not this Viewport's Sprites will be drawn
	 * @return The visibility
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets whether or not this Viewport's Sprites will be drawn
	 * @param visible The visibility
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
		if (sorted) {
			Collections.sort(sprites);
		}
	}

	public Viewport() {
		this(0, 0, STRETCH, STRETCH);
	}

	/**
	 * Initializes the viewport with the given dimensions.
	 * 
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @param width The width
	 * @param height The height
	 */
	public Viewport(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.z = 0;
		this.sprites = new ArrayList<Sprite>(100);
		this.visible = true;

		Graphics.addViewport(this);
	}

	/**
	 * Adds a Sprite to this Viewport
	 * @param sprite the Sprite
	 */
	public void addSprite(Sprite sprite) {
		if (sorted) {
			for (int i = 0; i < sprites.size(); i++) {
				if (sprite.getZ() < sprites.get(i).getZ()) {
					sprites.add(i, sprite);
					return;
				}
			}
		}
		sprites.add(sprite);
	}

	/**
	 * Removes a Sprite from this Viewport
	 * @param sprite The Sprite
	 */
	public void removeSprite(Sprite sprite) {
		sprites.remove(sprite);
	}

	public boolean isSpriteInBounds(Sprite sprite) {
		RectF spriteRect = sprite.getRect();
		RectF viewportRect = getRectF();
		return !(spriteRect.left > viewportRect.right ||
				spriteRect.right < viewportRect.left ||
				spriteRect.top > viewportRect.bottom ||
				spriteRect.bottom < viewportRect.top);
	}

	@Override
	public int compareTo(Viewport another) {
		return ((Integer)z).compareTo(another.z);
	}

	/**
	 * Updates this Viewport and each Sprite in it
	 */
	public void update(long timeElapsed) {
		for (int i = 0; i < sprites.size(); i++) {
			Sprite sprite = sprites.get(i);
			if (sprite.isDisposed()) {
				sprites.remove(sprite);
				i--;
			} else if (sprite != null) {
				sprite.update(timeElapsed);
			}
		}
	}


}
