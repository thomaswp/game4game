package edu.elon.honors.price.graphics;

import java.util.HashMap;

import edu.elon.honors.price.game.Data;

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

	private int textureId = -1;
	private Grid grid;
	private static HashMap<Integer, Integer> bitmapHash = new HashMap<Integer, Integer>();
	
	private Canvas bitmapCanvas;

	//Matrix to transform this sprite to its location, zoom and rotation
	private Matrix drawMatrix;
	//Region of this sprite, not including transparent area
	private Region collidableRegion;
	//A path created for the bitmap of the opaque regions
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
		//the bitmap has changed, so reset the path
		bitmapPath = null;
		textureId = -1;
		if (isMutable()) {
			bitmapCanvas.setBitmap(bitmap);
		} else {
			bitmapCanvas = null;
		}
	}

	/**
	 * Gets the Canvas used to draw on this Sprite's Bitmap.
	 * Returns null if the Bitmap is not mutable. Do not store
	 * the bitmap canvas. This may cause unexpected results.
	 * Reacquire it each time you access it.
	 * @return The Canvas
	 */
	public Canvas getBitmapCanvas() {
		//If the player gets the canvas, they may alter it
		//so reset the path
		bitmapPath = null;
		textureId = -1;
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
	public float getWidth() {
		return bitmap.getWidth() * zoom;
	}

	/**
	 * Gets the height of this Sprite's Bitmap.
	 * @return The height
	 */
	public float getHeight() {
		return bitmap.getHeight() * zoom;
	}

	/**
	 * Gets the Rect of this sprite, or the Bitmap's Rect
	 * transformed by this Sprite's transform Matrix.
	 * @return
	 */
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

	/**
	 * Gets the zoom of this sprite.
	 * Zoom of less than 1.0 shrinks the image and
	 * more than 1.0 enlarges it.
	 * @return The zoom
	 */
	public float getZoom() {
		return zoom;
	}

	/**
	 * Sets the zoom of this sprite.
	 * Zoom of less than 1.0 shrinks the image and
	 * more than 1.0 enlarges it.
	 * @param zoom The zoom
	 */
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	/**
	 * Gets the rotation of this Sprite.
	 * Rotation ranges from 0 to 360 degrees with 0 being North.
	 * @return The rotation
	 */
	public float getRotation() {
		return rotation;
	}

	public Matrix getDrawMatrix() {
		createDrawMatrix();
		return drawMatrix;
	}

	/**
	 * Sets the rotation of this Sprite.
	 * Rotation ranges from 0 to 360 degrees with 0 being North.
	 * @param rotation The rotation
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation % 360;
		while (this.rotation < 0)
			this.rotation += 360;
	}

	/**
	 * Gets the collidable region for this sprite.
	 * This region includes all opaque regions of the bitmap
	 * transformed to the current location of the sprite.
	 * @return
	 */
	public Region getCollidableRegion() {
		createCollidableRegion();
		return collidableRegion;
	}
	
	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	
	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
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

	/**
	 * Gets the region of intersection between this sprite
	 * and another Sprite. If empty the two Sprites are disjoint.
	 * @param sprite The sprite to test
	 * @return The region of intersection.
	 */
	public Region intersection(Sprite sprite) {
		if (sprite.getRect().intersect(getRect())) {
			Region r = new Region(getCollidableRegion());
			r.op(sprite.getCollidableRegion(), Op.INTERSECT);
			return r;
		} else {
			return new Region();
		}
	}

	/**
	 * Centers the origin of this Sprite.
	 */
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

		//points = points in the path, checks = accuracy of the path
		int points = 120, checks = 60;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		//middle x and y
		double mX = width / 2.0, mY = height / 2.0;

		//dTheta
		double dT = Math.PI * 2 / points;

		//have we started the path
		boolean start = false;

		for (int i = 0; i < points - 1; i ++) {
			//increase theta - calculate sin and cos
			double theta = dT * i;
			double sin = Math.sin(theta);
			double cos = Math.cos(theta);
			double length;

			//find out the max possible length of a line at this angle
			//going out from the origin
			if (cos == 0)
				length = mY;
			else if (sin == 0)
				length = mX;
			else
				length = Math.min(mX / Math.abs(cos), mY / Math.abs(sin));

			//deltaLength
			double dl = length / checks;

			for (int j = checks; j >= 0; j--) {
				//at each length
				double l = dl * j;
				//get x and y
				int x = (int)(mX + l * cos);
				int y = (int)(mY - l * sin);

				//if it's in the Bitmap
				if (x > 0 && y > 0 && x < width && y < height) {
					//check the color
					int color = bitmap.getPixel(x, y);
					if (Color.alpha(color) > 0) {
						//if it's not transparent, add it to the path
						if (!start) {
							bitmapPath.moveTo(x, y);
							start = true;
						}
						else
							bitmapPath.lineTo(x, y);
						//and go to the next theta
						break;
					}
				}
			}
		}

		bitmapPath.close();		
	}

	private Path getPath() {
		//get the path and transform it by the drawMatrix
		if (bitmapPath == null) {
			createBitmapPath();
		}
		Path path = new Path(bitmapPath);
		path.transform(getDrawMatrix());
		return path;
	}

	private void createCollidableRegion() {
		//create a region from the path
		collidableRegion = new Region();
		collidableRegion.setPath(getPath(), new Region(viewport.getRect()));
	}
}
