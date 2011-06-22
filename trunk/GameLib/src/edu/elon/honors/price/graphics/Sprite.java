package edu.elon.honors.price.graphics;

import java.util.HashMap;

import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;

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

	protected static Config defaultConfig = Config.ARGB_8888;

	private Bitmap bitmap;
	private Viewport viewport;

	private float x, y, originX, originY, rotation, zoomX, zoomY;
	private int z;
	private boolean visible, disposed;
	
	private int timeout = -1;

	private int textureId = -1;
	private Grid grid;
	private boolean bitmapModified;
	
	private int color = Color.WHITE;
	private float opacity = 1;
	
	private Canvas bitmapCanvas;
	
	private float lastViewportX, lastViewportY;
	
	private RectF rect = new RectF(), mapRect = new RectF();

	//Matrix to transform this sprite to its location, zoom and rotation
	private Matrix drawMatrix = new Matrix();
	private boolean resetMatrix = true;
	//Region of this sprite, not including transparent area
	private Region collidableRegion = new Region();
	//A path created for the bitmap of the opaque regions
	private Path bitmapPath = new Path();
	
	private int flashStartColor, flashColor, flashDuration, flashFrame;

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
		bitmapPath.reset();
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
		bitmapPath.reset();
		textureId = -1;
		bitmapModified = true;
		return bitmapCanvas;
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
		if (this.x != x)
			resetMatrix = true;
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
		if (this.y != y)
			resetMatrix = true;
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
		if (this.originX != originX)
			resetMatrix = true;
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
		if (this.originY != originY)
			resetMatrix = true;
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
		viewport.removeSprite(this);
		viewport.addSprite(this);
	}

	/**
	 * Gets the width of this Sprite's Bitmap.
	 * @return The width
	 */
	public float getWidth() {
		return bitmap.getWidth() * zoomX;
	}

	/**
	 * Gets the height of this Sprite's Bitmap.
	 * @return The height
	 */
	public float getHeight() {
		return bitmap.getHeight() * zoomY;
	}

	/**
	 * Gets the Rect of this sprite, or the Bitmap's Rect
	 * transformed by this Sprite's transform Matrix.
	 * @return
	 */
	public RectF getRect() {
		rect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
		getDrawMatrix().mapRect(mapRect, rect);
		return mapRect;
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
	 * Gets the x zoom of this sprite.
	 * Zoom of less than 1.0 shrinks the image and
	 * more than 1.0 enlarges it.
	 * @return The zoom
	 */
	public float getZoomX() {
		return zoomX;
	}

	/**
	 * Sets the x zoom of this sprite.
	 * Zoom of less than 1.0 shrinks the image and
	 * more than 1.0 enlarges it.
	 * @param zoom The zoom
	 */
	public void setZoomX(float zoom) {
		if (this.zoomX != zoom)
			resetMatrix = true;
		this.zoomX = zoom;
	}
	
	/**
	 * Gets the y zoom of this sprite.
	 * Zoom of less than 1.0 shrinks the image and
	 * more than 1.0 enlarges it.
	 * @return The zoom
	 */
	public float getZoomY() {
		return zoomY;
	}

	/**
	 * Sets the y zoom of this sprite.
	 * Zoom of less than 1.0 shrinks the image and
	 * more than 1.0 enlarges it.
	 * @param zoom The zoom
	 */
	public void setZoomY(float zoom) {
		if (this.zoomY != zoom)
			resetMatrix = true;
		this.zoomY = zoom;
	}
	
	/**
	 * Sets the x and y zoom of this sprite.
	 * Zoom of less than 1.0 shrinks the image and
	 * more than 1.0 enlarges it.
	 * @param zoom The zoom
	 */
	public void setZoom(float zoom) {
		setZoomX(zoom);
		setZoomY(zoom);
	}
	
	/**
	 * Gets the rotation of this Sprite.
	 * Rotation ranges from 0 to 360 degrees with 0 being North.
	 * @return The rotation
	 */
	public float getRotation() {
		return rotation;
	}
	
	/**
	 * Sets the rotation of this Sprite.
	 * Rotation ranges from 0 to 360 degrees with 0 being North.
	 * @param rotation The rotation
	 */
	public void setRotation(float rotation) {
		if (this.rotation != rotation % 360)
			resetMatrix = true;
		this.rotation = rotation % 360;
		while (this.rotation < 0)
			this.rotation += 360;
	}

	public Matrix getDrawMatrix() {
		if (lastViewportX != viewport.getX() ||
				lastViewportY != viewport.getY()) {
			lastViewportX = viewport.getX();
			lastViewportY = viewport.getY();
			resetMatrix = true;
		}
		if (resetMatrix) {
			createDrawMatrix();
		}
		return drawMatrix;
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
	
	public boolean isBitmapModified() {
		return bitmapModified;
	}

	public void setBitmapModified(boolean bitmapModified) {
		this.bitmapModified = bitmapModified;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isDisposed() {
		return disposed;
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
		this.zoomX = 1;
		this.zoomY = 1;
		viewport.addSprite(this);
	}

	/**
	 * Marks the sprite as disposed
	 */
	public void dispose() {
		this.disposed = true;
	}

	/**
	 * Gets the region of intersection between this sprite
	 * and another Sprite. If empty the two Sprites are disjoint.
	 * @param sprite The sprite to test
	 * @return The region of intersection.
	 */
	public Region intersection(Sprite sprite) {
		if (sprite.getRect().intersect(getRect())) {
			collideRegion.set(getCollidableRegion());
			collideRegion.op(sprite.getCollidableRegion(), Op.INTERSECT);
			return collideRegion;
		} else {
			return new Region();
		}
	}

	private Region rectRegion = new Region();
	private Region collideRegion = new Region();
	private Rect convertRect = new Rect();
	public Region intersection(RectF rect) {
		if (rect.intersect(getRect())) {
			collideRegion.set(getCollidableRegion());
			convertRect.set((int)rect.left, (int)rect.top, (int)rect.right, (int)rect.bottom);
			rectRegion.set(convertRect);
			collideRegion.op(rectRegion, Op.INTERSECT);
			return collideRegion;
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
	public void update(long timeElapsed) {
		if (timeout > 0) {
			if (timeElapsed >= timeout) {
				timeout = -1;
				dispose();
			} else {
				timeout -= timeElapsed;
			}
		}
		
		if (flashFrame < flashDuration) {
			float perc = flashFrame * 1.0f / flashDuration;
			
			int c1 = flashStartColor;
			int c2 = flashColor;
			if (perc < 0.5f) {
				perc *= 2;
			} else {
				perc = (1 - perc) * 2;
			}
		
			
			this.color = Color.argb(
					(int)(Color.alpha(c1) * (1 - perc) + Color.alpha(c2) * perc), 
					(int)(Color.red(c1) * (1 - perc) + Color.red(c2) * perc), 
					(int)(Color.green(c1) * (1 - perc) + Color.green(c2) * perc), 
					(int)(Color.blue(c1) * (1 - perc) + Color.blue(c2) * perc));
			
			flashFrame += timeElapsed;
			
			if (flashFrame >= flashDuration) {
				this.color = flashStartColor;
			}
		}
	}
	
	public void flash(int color, int duration) {
		this.flashStartColor = this.color;
		this.flashColor = color;
		this.flashDuration = duration;
		this.flashFrame = 0;
	}
	
	public String toString() {
		return "Sprite: {" + x + "," + y + "," + z + "}";
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
		drawMatrix.reset();
		//center the sprite at the origin
		if (originX != 0 || originY != 0)
			drawMatrix.postTranslate(-originX, -originY);
		//rotate and zoom
		if (rotation != 0)
			drawMatrix.postRotate(rotation);
		if (zoomX != 1 || zoomY != 1)
			drawMatrix.postScale(zoomX, zoomY);
		//then move it to it's position (relative to the Viewport)
		if (viewport.getX() != 0 || x != 0 || viewport.getY() != 0 || y != 0) {
			drawMatrix.postTranslate(viewport.getX() + x, 
					viewport.getY() + y);
		}
		resetMatrix = false;
	}

	private void createBitmapPath() {

		bitmapPath.reset();

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
	
	public int convexHull(float[] xs, float ys[]) {

		bitmapPath.reset();

		//points = points in the path, checks = accuracy of the path
		int points = 8, checks = 60;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		//middle x and y
		double mX = width / 2.0, mY = height / 2.0;

		//dTheta
		double dT = Math.PI * 2 / points;
		
		for (int i = 0; i < points; i ++) {
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
						xs[i] = x;
						ys[i] = y;
						
						//and go to the next theta
						break;
					}
				}
			}
		}	
		
		int pts = 8;
		for (int i = 0; i < 8; i += 2) {
			int before = (i + 7) % 8;
			int after = i + 1;
			float mpx = (xs[before] + xs[after]) / 2;
			float mpy = (ys[before] + ys[after]) / 2;
			double dx = xs[i] - mX, dy = ys[i] - mY; 
			double radThis = dx * dx + dy * dy;
			dx = mpx - mX; dy = mpy - mY;
			double radThat = dx * dx + dy * dy;
			if (radThis <= radThat + 1) {
				xs[i] = -1;
				ys[i] = -1;
				pts--;
			}
		}
		return pts;
	}

	private Path tempPath = new Path();
	private Path getPath() {
		//get the path and transform it by the drawMatrix
		if (bitmapPath.isEmpty()) {
			createBitmapPath();
		}
		tempPath.set(bitmapPath);
		tempPath.transform(getDrawMatrix());
		return tempPath;
	}

	private void createCollidableRegion() {
		//create a region from the path
		collidableRegion.setEmpty();
		collideRegion.set(viewport.getRect());
		collidableRegion.setPath(getPath(), collideRegion);
	}
}
