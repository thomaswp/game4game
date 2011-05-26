package edu.elon.honors.price.graphics;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A class for a drawable sprites. Holds a Bitmap and provides
 * ways of manipulating it. 
 * 
 * @author Thomas Price
 *
 */
public class Sprite implements Comparable<Sprite> {

	private static Config defaultConfig = Config.ARGB_8888;
	
	private Bitmap bitmap;
	private Viewport viewport;
	
	private double x, y, originX, originY;
	private int z;
	private boolean visible;
	
	private Canvas bitmapCanvas;
	
	/**
	 * Gets the default Bitmap configuration for Bitmaps created by a Sprite.
	 * @return The Configuration
	 */
	public static Config getDefaultConfig() {
		return defaultConfig;
	}

	/**
	 * Sets the default Bitmap configuration for Bitmaps created by a Sprite.
	 * @param defaultConfig The Configuration
	 */
	public static void setDefaultConfig(Config defaultConfig) {
		Sprite.defaultConfig = defaultConfig;
	}

	/**
	 * Gets the Bitmap held by this Sprite
	 * @return The Bitmap
	 */
	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * Sets the Bitmap held by this Sprite.
	 * @param bitmap The Bitmap.
	 */
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		bitmapCanvas.setBitmap(bitmap);
	}

	/**
	 * Gets the Canvas used to draw on this Sprite's Bitmap.
	 * @return The Canvas
	 */
	public Canvas getBitmapCanvas() {
		return bitmapCanvas;
	}
	
	/**
	 * Gets the X coordinate of this Sprite.
	 * @return The X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the X coordinate of this Sprite
	 * @param x The X Coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the Y coordinate of this Sprite.
	 * @return The Y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the Y coordinate of this Sprite
	 * @param y The Y Coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Gets the X coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return The X coordinate
	 */
	public double getOriginX() {
		return originX;
	}

	/**
	 * Sets the X coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return
	 */
	public void setOriginX(double originX) {
		this.originX = originX;
	}

	/**
	 * Gets the Y coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return The Y coordinate
	 */
	public double getOriginY() {
		return originY;
	}

	/**
	 * Sets the Y coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return The Y coordinate
	 */
	public void setOriginY(double originY) {
		this.originY = originY;
	}

	/**
	 * Gets the Z coordinate of the Sprite. This determines
	 * the drawing order of this Sprite. High Z coordinates
	 * put the Sprite further on top.
	 * @return The Z coordinate
	 */
	public int getZ() {
		return z;
	}
	/**
	 * Sets the Z coordinate of the Sprite. This determines
	 * the drawing order of this Sprite. High Z coordinates
	 * put the Sprite further on top.
	 * @param z The Z coordinate
	 */
	public void setZ(int z) {
		this.z = z;
	}
	
	/**
	 * Gets the width of this Sprite's Bitmap.
	 * @return The width
	 */
	public int getWidth() {
		return bitmap.getWidth();
	}
	
	/**
	 * Gets the height of this Sprite's Bitmap.
	 * @return The height
	 */
	public int getHeight() {
		return bitmap.getHeight();
	}

	/**
	 * Gets the Viewport on which the Sprite is drawn.
	 * @return The Viweport
	 */
	public Viewport getViewport() {
		return viewport;
	}
	
	/**
	 * Gets whether or not this Sprite will be drawn
	 * @return The visibility
	 */
	public boolean isVisible() {
		return visible;
	}
	

	/**
	 * Sets whether or not this Sprite will be drawn
	 * @return The visibility
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Creates a Sprite with the given Viewport and coordinates and creates
	 * a new Bitmap with the given width and height.
	 * 
	 * @param viewport The Viewport
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @param width The width this Sprite's Bitmap
	 * @param height The height this Sprite's Bitmap
	 */
	public Sprite(Viewport viewport, double x, double y, int width, int height) {
		this(viewport, Bitmap.createBitmap(width, height, defaultConfig));
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a Sprite with the given width and height.
	 * @param viewport The Viewport
	 * @param bitmap The Bitmap
	 */
	public Sprite(Viewport viewport, Bitmap bitmap) {
		this.viewport = viewport;
		this.bitmap = bitmap;
		this.visible = true;
		this.bitmapCanvas = new Canvas(bitmap);
		viewport.addSprite(this);
	}
	
	/**
	 * Removes the Sprite's reference to its Bitmap and
	 * removes it from it's Viewport.
	 */
	public void dispose() {
		this.bitmap = null;
		viewport.removeSprite(this);
	}
	
	@Override
	public int compareTo(Sprite another) {
		return ((Integer)z).compareTo(another.z);
	}
	
	/**
	 * Updates the Sprite
	 */
	public void update() {

	}

	/**
	 * Creates a new Paint from the given color
	 * @param color The color
	 * @return
	 */
	public static Paint paintFromColor(int color) {
		Paint p = new Paint();
		p.setColor(color);
		return p;
	}
}
