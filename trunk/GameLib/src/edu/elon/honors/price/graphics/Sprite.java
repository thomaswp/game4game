package edu.elon.honors.price.graphics;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;

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

	private float x, y, originX, originY, rotation, zoom;
	private int z;
	private boolean visible;

	private Canvas bitmapCanvas;

	private Matrix drawMatrix;

	private Region collidableRegion;
	private Path bitmapPath;

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
		bitmapPath = null;
		if (isMutable()) {
			bitmapCanvas.setBitmap(bitmap);
		} else {
			bitmapCanvas = null;
		}
	}

	/**
	 * Gets the Canvas used to draw on this Sprite's Bitmap.
	 * Returns null if the Bitmap is not mutable.
	 * @return The Canvas
	 */
	public Canvas getBitmapCanvas() {
		bitmapPath = null;
		return bitmapCanvas;
	}

	/**
	 * Gets the X coordinate of this Sprite.
	 * @return The X coordinate
	 */
	public float getX() {
		return x;
	}

	/**
	 * Sets the X coordinate of this Sprite
	 * @param x The X Coordinate
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Gets the Y coordinate of this Sprite.
	 * @return The Y coordinate
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets the Y coordinate of this Sprite
	 * @param y The Y Coordinate
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Gets the X coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return The X coordinate
	 */
	public float getOriginX() {
		return originX;
	}

	/**
	 * Sets the X coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return
	 */
	public void setOriginX(float originX) {
		this.originX = originX;
	}

	/**
	 * Gets the Y coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return The Y coordinate
	 */
	public float getOriginY() {
		return originY;
	}

	/**
	 * Sets the Y coordinate of this Sprite's origin.
	 * The origin is point where the Sprite will be draw.
	 * @return The Y coordinate
	 */
	public void setOriginY(float originY) {
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
		return (int)(bitmap.getWidth() * zoom);
	}

	/**
	 * Gets the height of this Sprite's Bitmap.
	 * @return The height
	 */
	public int getHeight() {
		return (int)(bitmap.getHeight() * zoom);
	}

	public RectF getRect() {
		RectF r = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF r2 = new RectF();
		getDrawMatrix().mapRect(r2, r);
		return r2;
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
	 * Gets whether or not this Sprite's Bitmap can be edited
	 * using it's canvas.
	 * @return
	 */
	public boolean isMutable() {
		return bitmap.isMutable();
	}

	/**
	 * Sets the mutability of this Sprite's bitmap.
	 * @param mutable
	 */
	public void setMutable(boolean mutable) {
		if (mutable != bitmap.isMutable()) {
			bitmap = bitmap.copy(defaultConfig, mutable);
			if (mutable) {
				bitmapCanvas = new Canvas();
				bitmapCanvas.setBitmap(bitmap);
			} else {
				bitmapCanvas = null;
			}
		}
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public float getRotation() {
		return rotation;
	}

	public Matrix getDrawMatrix() {
		createDrawMatrix();
		return drawMatrix;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Region getCollidableRegion() {
		createCollidableRegion();
		return collidableRegion;
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
	public Sprite(Viewport viewport, float x, float y, int width, int height) {
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
		this.bitmapCanvas = new Canvas();
		setBitmap(bitmap);
		this.visible = true;
		this.zoom = 1;
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

	public Region intersection(Sprite sprite) {
		if (sprite.getRect().intersect(getRect())) {
			Region r = new Region(getCollidableRegion());
			r.op(sprite.getCollidableRegion(), Op.INTERSECT);
			return r;
		} else {
			return new Region();
		}
	}

	public void centerOrigin() {
		originX = getWidth() / 2;
		originY = getHeight() / 2;
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

	private void createDrawMatrix() {
		drawMatrix = new Matrix();
		//center the sprite at the origin
		drawMatrix.postTranslate(-originX, -originY);
		//rotate and zoom
		drawMatrix.postRotate(rotation);
		drawMatrix.postScale(zoom, zoom);
		//then move it to it's position (relative to the Viewport)
		drawMatrix.postTranslate(viewport.getX() + x, 
				viewport.getY() + y);
	}

	private void createBitmapPath() {

		bitmapPath = new Path();

		int points = 120, checks = 60;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		double mX = width / 2.0, mY = height / 2.0;

		double dT = Math.PI * 2 / points;

		boolean start = false;

		for (int i = 0; i < points - 1; i ++) {
			double theta = dT * i;
			double sin = Math.sin(theta);
			double cos = Math.cos(theta);
			double length;

			if (cos == 0)
				length = mY;
			else if (sin == 0)
				length = mX;
			else
				length = Math.min(mX / Math.abs(cos), mY / Math.abs(sin));

			double dl = length / checks;

			for (int j = checks; j >= 0; j--) {
				double l = dl * j;
				int x = (int)(mX + l * cos);
				int y = (int)(mY - l * sin);

				if (x > 0 && y > 0 && x < width && y < height) {
					int color = bitmap.getPixel(x, y);
					if (Color.alpha(color) > 0) {
						if (!start) {
							bitmapPath.moveTo(x, y);
							start = true;
						}
						else
							bitmapPath.lineTo(x, y);
						break;
					}
				}
			}
		}

		bitmapPath.close();		
	}

	private Path getPath() {
		if (bitmapPath == null) {
			createBitmapPath();
		}
		Path path = new Path(bitmapPath);
		path.transform(getDrawMatrix());
		return path;
	}

	private void createCollidableRegion() {
		collidableRegion = new Region();
		collidableRegion.setPath(getPath(), new Region(viewport.getRect()));
	}
}
