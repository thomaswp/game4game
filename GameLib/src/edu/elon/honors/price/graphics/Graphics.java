package edu.elon.honors.price.graphics;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class Graphics implements Renderer {

	final static int FRAME_BLOCK = 60;

	private static LinkedList<Viewport> viewports = new LinkedList<Viewport>();
	private static int width, height;

	private static int frameCountDraw = 0;
	private static long lastSystemTimeDraw = System.currentTimeMillis();
	private static int fpsDraw;
	private static int frameCountGame = 0;
	private static long lastSystemTimeGame = System.currentTimeMillis();
	private static int fpsGame;
	private static boolean showFPS;
	private static Bitmap fpsBitmap;
	private static boolean fpsBitmapRefresh;
	private static Paint paint = new Paint();
	private static Canvas canvas = new Canvas();
	private static int fpsId = -1;
	private static Grid fpsGrid;

	private static Logic logic;


	private static HashMap<Integer, Integer> textures = new HashMap<Integer, Integer>();

	// Pre-allocated arrays to use at runtime so that allocation during the
	// test can be avoided.
	private static int[] mTextureNameWorkspace;
	private static int[] mCropWorkspace;

	private LinkedList<Integer> resources;


	/**
	 * Returns the width of the current drawable area.
	 * @return The width
	 */
	public static int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the current drawable area.
	 * @return
	 */
	public static int getHeight() {
		return height;
	}

	public static boolean isShowingFPS() {
		return showFPS;
	}

	public static void setShowingFPS(boolean showFPS) {
		Graphics.showFPS = showFPS;
	}

	public static int getFpsDraw() {
		return fpsDraw;
	}

	public static int getFpsGame() {
		return fpsGame;
	}

	//	public int[] getConfigSpec() {
	//		// We don't need a depth buffer, and don't care about our
	//		// color depth.
	//		int[] configSpec = { EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_NONE };
	//		return configSpec;
	//	}

	public static void setLogic(Logic logic) {
		Graphics.logic = logic;
	}

	public Graphics() {
		// Pre-allocate and store these objects so we can use them at runtime
		// without allocating memory mid-frame.
		mTextureNameWorkspace = new int[1];
		mCropWorkspace = new int[4];
		resources = new LinkedList<Integer>();
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Graphics.width = width;
		Graphics.height = height;

		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, width, 0.0f, height, 0.0f, 1.0f);

		gl.glShadeModel(GL10.GL_FLAT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	/**
	 * Called whenever the surface is created.  This happens at startup, and
	 * may be called again at runtime if the device context is lost (the screen
	 * goes to sleep, etc).  This function must fill the contents of vram with
	 * texture data and (when using VBOs) hardware vertex arrays.
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0f, 0f, 0f, 1);
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_LIGHTING);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Called when the rendering thread shuts down.  This is a good place to
	 * release OpenGL ES resources.
	 * @param gl
	 */
	public void onShutdown(GL10 gl) {
		int[] texture = new int[1];
		for (Integer x : resources) {
			texture[0] = x;
			gl.glDeleteTextures(1, texture, 0);
		}
	}

	
	long times = 0;
	int frame = 0;
	/**
	 * Draws the the Sprites in each Viewport.
	 * @param canvas The canvas on which to draw.
	 */
	public void onDrawFrame(GL10 gl) {
		long time = System.currentTimeMillis();
		if (Graphics.logic != null) {
			synchronized(Graphics.logic) {
				//Game.debug("Draw");

				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				Grid.beginDrawing(gl, true, false);

				//Allow us to reset the clip to full if we need to
				boolean clipSet = false;

				//Sort Viewports by Z
				Collections.sort(viewports);
				for (Viewport viewport : viewports) {
					//Draw visible Viewports
					if (viewport != null && viewport.isVisible()) {

						if (viewport.hasRect()) {
							//If the Viewport isn't the whole drawable area, set the clip
							clipSet = true;
						} else if (clipSet) {
							//Otherwise restore it to the whole area
							clipSet = false;
						}

						//Sort the Sprites as well
						Collections.sort(viewport.getSprites());
						for (Sprite sprite : viewport.getSprites()) {
							if (sprite != null && sprite.isVisible()) {
								//Draw visible Sprites

								if (sprite.getTextureId() == -1) {
									int hash = sprite.getBitmap().hashCode();
									if (textures.containsKey(hash)) {
										sprite.setTextureId(textures.get(hash));
									} else {
										int rid = loadBitmap(gl, sprite.getBitmap());
										resources.add(rid);
										sprite.setTextureId(rid);
										textures.put(hash, rid);
									}
								}
								if (sprite.getGrid() == null) {
									sprite.setGrid(createNewGrid(sprite.getBitmap()));
								}

								gl.glBindTexture(GL10.GL_TEXTURE_2D, sprite.getTextureId());

								int h = sprite.getBitmap().getHeight(); 
								int targetHeight = 1;
								while (targetHeight < h) targetHeight *= 2;
								int bY = targetHeight - h;

								// Draw using verts or VBO verts.
								gl.glPushMatrix();
								gl.glLoadIdentity();
								gl.glTranslatef(sprite.getX() + viewport.getX(), height + bY - targetHeight + sprite.getOriginY() * 2
										- (sprite.getY() + viewport.getY()), 0);
								gl.glRotatef(-sprite.getRotation(), 0, 0, 1);
								gl.glScalef(sprite.getZoom(), sprite.getZoom(), 0);
								gl.glTranslatef(-sprite.getOriginX(), -bY - sprite.getOriginY(), 0);

								sprite.getGrid().draw(gl, true, false);

								gl.glPopMatrix();
							}
						}
					}
				}

				updateFPSDraw();
				//if (fpsBitmap != null) fpsBitmapRefresh = false;
				if (fpsBitmapRefresh) {
					updateFPSBitmap();
					if (fpsId > 0) {
						int[] texture = new int[] { fpsId };
						gl.glDeleteTextures(1, texture, 0);
					}
					fpsId = loadBitmap(gl, fpsBitmap);
				}
				if (fpsBitmap != null) {
					if (fpsGrid == null) {
						fpsGrid = createNewGrid(fpsBitmap);
					}
					gl.glBindTexture(GL10.GL_TEXTURE_2D, fpsId);
					gl.glPushMatrix();
					gl.glLoadIdentity();
					gl.glTranslatef(width - fpsBitmap.getWidth(), height - fpsBitmap.getHeight(), 0);
					fpsGrid.draw(gl, true, false);
					gl.glPopMatrix();

				}
				Grid.endDrawing(gl);
			}
		}
		times += System.currentTimeMillis() - time;
		frame++;
		if (frame == 60) {
			Game.debug("" + (times / frame));
			frame = 0;
			times = 0;
		}
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
		updateFPSGame();
	}


	private static void updateFPSDraw() {
		frameCountDraw = (frameCountDraw + 1) % FRAME_BLOCK;
		if (frameCountDraw == 0) {
			long currentTime = System.currentTimeMillis();
			fpsDraw = (int)(FRAME_BLOCK * 1000 / (currentTime - lastSystemTimeDraw));
			lastSystemTimeDraw = currentTime;
			fpsBitmapRefresh = true;
		}
	}

	private static void updateFPSGame() {
		frameCountGame = (frameCountGame + 1) % FRAME_BLOCK;
		if (frameCountGame == 0) {
			long currentTime = System.currentTimeMillis();
			fpsGame = (int)(FRAME_BLOCK * 1000 / (currentTime - lastSystemTimeGame));
			lastSystemTimeGame = currentTime;
			fpsBitmapRefresh = true;
		}
	}

	private void updateFPSBitmap() {
		if (fpsBitmap == null) {
			fpsBitmap = Bitmap.createBitmap(64, 32, Config.ARGB_8888);
			canvas.setBitmap(fpsBitmap);
			paint.setColor(Color.GRAY);
			paint.setTextSize(16);
		}
		fpsBitmap.eraseColor(Color.TRANSPARENT);
		canvas.drawText(fpsDraw + "/" + fpsGame, 0, 12, paint);
		fpsBitmapRefresh = false;
	}

	private Grid createNewGrid(Bitmap bitmap) {
		//Game.debug("Create grid");

		int width = bitmap.getWidth(), height = bitmap.getHeight();
		int targetWidth = 1, targetHeight = 1;
		while (targetWidth < width) targetWidth *= 2;
		while (targetHeight < height) targetHeight *= 2;

		Grid grid = new Grid(2, 2, false);
		grid.set(0, 0,  0.0f, 0.0f, 0.0f, 0.0f, 1.0f, null);
		grid.set(1, 0, targetWidth, 0.0f, 0.0f, 1.0f, 1.0f, null);
		grid.set(0, 1, 0.0f, targetHeight, 0.0f, 0.0f, 0.0f, null);
		grid.set(1, 1, targetWidth, targetHeight, 0.0f, 
				1.0f, 0.0f, null );
		return grid;
	}

	/** 
	 * Loads a bitmap into OpenGL and sets up the common parameters for 
	 * 2D texture maps. 
	 */
	public static int loadBitmap(GL10 gl, Bitmap bitmap) {
		//Game.debug("Load texture");
		int textureName = -1;
		if (gl != null) {
			gl.glGenTextures(1, mTextureNameWorkspace, 0);

			textureName = mTextureNameWorkspace[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

			int width = bitmap.getWidth(), height = bitmap.getHeight();
			int targetWidth = 1, targetHeight = 1;
			while (targetWidth < width) targetWidth *= 2;
			while (targetHeight < height) targetHeight *= 2;
			if ((width != targetWidth || height != targetHeight)) {
				//				int[] bmpPixels = new int[width * height];
				//				bitmap.getPixels(bmpPixels, 0, width, 0, 0, width, height);
				//				ByteBuffer pixels = ByteBuffer.allocateDirect(targetWidth * targetHeight * 4);
				//
				//				int index = 0;
				//
				//				for (int i = 0; i < targetHeight; i++) {
				//					for (int j = 0; j < targetWidth; j++) {
				//						if (i < height && j < width) {
				//							int pix = bmpPixels[i * width + j];
				//							pixels.put(index++, (byte)Color.red(pix));
				//							pixels.put(index++,(byte)Color.green(pix));
				//							pixels.put(index++, (byte)Color.blue(pix));
				//							pixels.put(index++, (byte)Color.alpha(pix));
				//						} else {
				//							for (int k = 0; k < 4; k++) {
				//								pixels.put(index++, (byte)0);
				//							}
				//						}
				//					}
				//				}
				//
				//				gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, targetWidth, targetHeight, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);

				int[] bmpPixels = new int[targetWidth * targetHeight];
				bitmap.getPixels(bmpPixels, 0, targetWidth, 0, 0, width, height);
				for (int i = 0; i < bmpPixels.length; i++) {
					int c = bmpPixels[i];
					bmpPixels[i] =
						((c >> 16) & 0xff) +
						((c & 0xff) << 16) +
						(c & 0xff00ff00);
				}
				IntBuffer pixels = IntBuffer.wrap(bmpPixels);
				gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, targetWidth, targetHeight, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);

			} else {
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			}

			mCropWorkspace[0] = 0;
			mCropWorkspace[1] = targetWidth;
			mCropWorkspace[2] = targetHeight;
			mCropWorkspace[3] = -targetHeight;

			((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, 
					GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);


			int error = gl.glGetError();
			if (error != GL10.GL_NO_ERROR) {
				Log.e("Graphics", "Texture Load GLError: " + error);
			}

		}

		return textureName;
	}
}
