package edu.elon.honors.price.graphics;

import java.util.LinkedList;

import android.graphics.Rect;

public class Viewport implements Comparable<Viewport> {

	//A width or height given when the Viewport should be stretched
	//to fill the drawable area
	public static final int STRETCH = -1;

	private int x, y, z, width, height;

	private boolean visible;

	private LinkedList<Sprite> sprites;

	public static Viewport DefaultViewport = new Viewport(0, 0, STRETCH, STRETCH);

	/**
	 * Gets the X coordinate of the Viewport
	 * @return The X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the X coordinate of the Viewport
	 * @param x The X coordinate
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the Y coordinate of the Viewport
	 * @return The Y Coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the Y coordinate of the Viewport
	 * @param y The Y Coordinate
	 */
	public void setY(int y) {
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
			return new Rect(0, 0, Graphics.getWidth(), Graphics.getHeight());
		}
		return new Rect(x, y, width + x, height + y);
	}

	/**
	 * Gets the list of Sprites contained in this Viewport 
	 * @return The Sprites
	 */
	public LinkedList<Sprite> getSprites() {
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

	/**
	 * Initializes the viewport with the given dimensions.
	 * 
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @param width The width
	 * @param height The height
	 */
	public Viewport(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.z = 0;
		this.sprites = new LinkedList<Sprite>();
		this.visible = true;

		Graphics.addViewport(this);
	}

	/**
	 * Adds a Sprite to this Viewport
	 * @param sprite the Sprite
	 */
	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}

	/**
	 * Removes a Sprite from this Viewport
	 * @param sprite The Sprite
	 */
	public void removeSprite(Sprite sprite) {
		sprites.remove(sprite);
	}

	@Override
	public int compareTo(Viewport another) {
		return ((Integer)z).compareTo(another.z);
	}

	/**
	 * Updates this Viewport and each Sprite in it
	 */
	public void upadte(long timeElapsed) {
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
