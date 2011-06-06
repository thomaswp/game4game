package edu.elon.honors.price.graphics;

import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GraphicsRenderer implements Renderer {
	private Bitmap fpsBitmap;
	private Paint paint = new Paint();
	private Canvas canvas = new Canvas();
	private int fpsId = -1;
	private Grid fpsGrid;

	private Logic logic;

	private HashMap<Integer, Integer> textures = new HashMap<Integer, Integer>();

	private int[] mTextureNameWorkspace;
	private int[] mCropWorkspace;

	private LinkedList<Integer> resources;
	
	private boolean flush;
	
	public void setFlush(boolean flush) {
		this.flush = flush;
	}

	public void setLogic(Logic logic) {
		this.logic = logic;
	}
	
	public GraphicsRenderer() {
		mTextureNameWorkspace = new int[1];
		mCropWorkspace = new int[4];
		resources = new LinkedList<Integer>();
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Graphics.resize(width, height);

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
		flush(gl);
	}
	
	private void flush(GL10 gl) {
		Game.debug("Flush");
		textures = new HashMap<Integer, Integer>();
		int[] texture = new int[1];
		for (Integer x : resources) {
			texture[0] = x;
			gl.glDeleteTextures(1, texture, 0);
		}
		for (Viewport v : Graphics.getViewports()) {
			for (Sprite s : v.getSprites()) {
				s.setTextureId(-1);
			}
		}
		System.gc();
		resources = new LinkedList<Integer>();
		flush = false;
	}

	
	long times = 0;
	int frame = 0;
	/**
	 * Draws the the Sprites in each Viewport.
	 * @param canvas The canvas on which to draw.
	 */
	public void onDrawFrame(GL10 gl) {
		long time = System.currentTimeMillis();

		if (flush) {
			flush(gl);
		}
		
		if (logic != null) {
			synchronized(logic) {
				//Game.debug("Draw");

				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				Grid.beginDrawing(gl, true, false);

				//Allow us to reset the clip to full if we need to
				boolean clipSet = false;
				
				LinkedList<Viewport> viewports = Graphics.getViewports();

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

								if (sprite.isBitmapModified()) {
									int hash = sprite.getBitmap().hashCode();
									if (textures.containsKey(hash)) {
										//int rid = textures.get(hash);
										//int[] texture = {rid};
										//gl.glDeleteTextures(1, texture, 0);
										textures.remove(hash);	
										sprite.setBitmapModified(false);
									}
								}
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

//								gl.glTranslatef(viewport.getX(), Graphics.getHeight() - viewport.getY(), 0);
//								gl.glRotatef(-viewport.getRotation(), 0, 0, 1);
//								gl.glScalef(viewport.getZoomX(), viewport.getZoomY(), 0);
//								gl.glTranslatef(-viewport.getOriginX(), -viewport.getOriginY(), 0);
								
								gl.glTranslatef(sprite.getX(), Graphics.getHeight() + bY - targetHeight + sprite.getOriginY() * 2
										- sprite.getY(), 0);
								gl.glRotatef(-sprite.getRotation(), 0, 0, 1);
								gl.glScalef(sprite.getZoomX(), sprite.getZoomY(), 0);
								gl.glTranslatef(-sprite.getOriginX(), -bY - sprite.getOriginY(), 0);

								sprite.getGrid().draw(gl, true, false);

								gl.glPopMatrix();
							}
						}
					}
				}

				Graphics.updateFPSDraw();
				//if (fpsBitmap != null) fpsBitmapRefresh = false;
				if (Graphics.getFPSBitmapRefresh()) {
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
					gl.glTranslatef(Graphics.getWidth() - fpsBitmap.getWidth(), 
							Graphics.getHeight() - fpsBitmap.getHeight(), 0);
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
	
	private void updateFPSBitmap() {
		if (fpsBitmap == null) {
			fpsBitmap = Bitmap.createBitmap(64, 32, Config.ARGB_8888);
			canvas.setBitmap(fpsBitmap);
			paint.setColor(Color.GRAY);
			paint.setTextSize(16);
		}
		fpsBitmap.eraseColor(Color.TRANSPARENT);
		canvas.drawText(Graphics.getFpsDraw() + "/" + Graphics.getFpsGame(), 0, 12, paint);
		Graphics.setFPSBitmapRefresh(false);
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
	private int loadBitmap(GL10 gl, Bitmap bitmap) {
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