package edu.elon.honors.price.graphics;

import java.util.HashMap;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import edu.elon.honors.price.game.Debug;
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
	private static final boolean DEBUG_STATUS = false;
	
	private Bitmap fpsBitmap;
	private Paint paint = new Paint();
	private Canvas canvas = new Canvas();
	private int fpsId = -1, rendered;
	private Grid fpsGrid;
	private int[] rTexture = new int[1];
	private int lastBGC;
	private int framesRendered = 0;
	//private Game game;
	private int textureCacheSize = 0;

	private Logic logic;

	private HashMap<Bitmap, Integer> textures = new HashMap<Bitmap, Integer>();

	private int[] mTextureNameWorkspace;
	private int[] mCropWorkspace;

	private LinkedList<Integer> resources;
	
	private boolean flush;
	
	private float lastR = 1, lastG = 1, lastB = 1, lastA = 1;
	private float globalScale;
	
	public int getFramesRendered() {
		return framesRendered;
	}
	
	public void setFlush(boolean flush) {
		this.flush = flush;
	}

	public void setLogic(Logic logic) {
		this.logic = logic;
	}
	
	public GraphicsRenderer(Game game) {
		mTextureNameWorkspace = new int[1];
		mCropWorkspace = new int[4];
		resources = new LinkedList<Integer>();
		//this.game = game;
	}
	
	public void setSize(GL10 gl) {
		int width = Graphics.getScreenWidth(), 
				height = Graphics.getScreenHeight();
		
		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		globalScale = Graphics.getGlobalScale();
		gl.glOrthof(0.0f, (int)(width / globalScale), 0.0f, (int)(height / globalScale), 0.0f, 1.0f);
		//gl.glScalef(Graphics.getGlobalScale(), Graphics.getGlobalScale(), 1f);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Graphics.resize(width, height);
		
		setSize(gl);
		
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glEnable(GL10.GL_BLEND);
		//gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//Helps correct messed up alpha for clouds, etc.
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	/**
	 * Called whenever the surface is created.  This happens at startup, and
	 * may be called again at runtime if the device context is lost (the screen
	 * goes to sleep, etc).  This function must fill the contents of vram with
	 * texture data and (when using VBOs) hardware vertex arrays.
	 */
	@Override
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
		gl.glEnable(GL10.GL_SCISSOR_TEST);
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
		Debug.write("Renderer Shut Down");
		flush(gl);
	}
	
	private void flush(GL10 gl) {
		long time = System.currentTimeMillis();
		textures.clear();
		textureCacheSize = 0;
		for (int i = 0; i < resources.size(); i++) {
			int x = resources.get(i);
			rTexture[0] = x;
			gl.glDeleteTextures(1, rTexture, 0);
		}
		for (int i = 0; i < Graphics.getViewports().size(); i++) {
			Viewport viewport = Graphics.getViewports().get(i);
			for (int j = 0; j < viewport.getSprites().size(); j++) {
				Sprite  s = viewport.getSprites().get(j);
				s.setTextureId(-1);
			}
		}
		//System.gc();
		time = System.currentTimeMillis() - time;
		Debug.write("Flush: " + textures.size() + "t, " + time + "ms");
		resources.clear();
		flush = false;
	}

	
	Sprite last;
	long times = 0;
	int frame = 0;
	/**
	 * Draws the the Sprites in each Viewport.
	 * @param canvas The canvas on which to draw.
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		long time = System.currentTimeMillis();

		if (flush) {
			flush(gl);
		}
		
		if (globalScale != Graphics.getGlobalScale()) {
			setSize(gl);
		}
		
		if (logic != null) {
			synchronized(logic) {
				//Debug.write("Draw");
				if (Graphics.getBackgroundColor() != lastBGC) {
					int color = Graphics.getBackgroundColor();
					gl.glClearColor(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color));
					lastBGC = color;
				}
				

				gl.glScissor(0, 0, Graphics.getScreenWidth(), Graphics.getScreenHeight());
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				Grid.beginDrawing(gl, true, false);

				for (int i = 0; i < Graphics.getViewports().size(); i++) {
					Viewport viewport = Graphics.getViewports().get(i);
					//Draw visible Viewports
					if (viewport != null && viewport.isVisible()) {

						int vColor = viewport.getColor();
						float vOpacity = viewport.getOpacity();

						for (int j = 0; j < viewport.getSprites().size(); j++) {
							Sprite  sprite = viewport.getSprites().get(j);
							if (sprite != null && sprite.isVisible()) {
								//Draw visible Sprites
								
								if (!viewport.isSpriteInBounds(sprite)) {
//									Debug.write(sprite.getRect().toString());
//									Debug.write(viewport.getRect().toString());
									continue;
								}

								Bitmap bmp = sprite.getBitmap();
								if (sprite.isBitmapModified()) {
									if (textures.containsKey(bmp)) {
										textures.remove(bmp);	
										sprite.setBitmapModified(false);
									}
								}
								if (sprite.getTextureId() == -1) {
									if (textures.containsKey(bmp)) {
										sprite.setTextureId(textures.get(bmp));
									} else {
										last = sprite;
										int rid = loadBitmap(gl, sprite.getBitmap());
										resources.add(rid);
										sprite.setTextureId(rid);
										textures.put(bmp, rid);
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

								gl.glPushMatrix();
								gl.glLoadIdentity();
								
								if (viewport.hasRect()) {
									gl.glTranslatef(viewport.getX(), -viewport.getY(), 0);
									int x = (int)(viewport.getX() * globalScale);
									int width = (int)(viewport.getWidth() * globalScale);
									int y = (int)((Graphics.getHeight() - viewport.getY() - viewport.getHeight()) * globalScale);
									int height = (int)(viewport.getHeight() * globalScale);
									gl.glScissor(x, y, width, height);
								} else {
									gl.glScissor(0, 0, Graphics.getScreenWidth(), Graphics.getScreenHeight());
								}
								
								
								float tx = (int)sprite.getX(); 
								float ty = (int)(Graphics.getHeight() + (bY - targetHeight) * 1 +	sprite.getOriginY() * 2	- sprite.getY());
								if (tx != 0 || ty != 0)
									gl.glTranslatef(tx, ty, 0);
								if (sprite.getRotation() != 0)
									gl.glRotatef(-sprite.getRotation(), 0, 0, 1);
								gl.glScalef(sprite.getZoomX(), sprite.getZoomY(), 1);
								tx = (int)(-sprite.getOriginX());
								ty = (int)(-bY - sprite.getOriginY());
								if (tx != 0 || ty != 0)
									gl.glTranslatef(tx, ty, 0);

								int sColor = sprite.getColor();
								float r = Color.red(sColor) / 255f * Color.red(vColor) / 255f;
								float g = Color.green(sColor) / 255f * Color.green(vColor) / 255f;
								float b = Color.blue(sColor) / 255f * Color.blue(vColor) / 255f;
								float a = Color.alpha(sColor) * sprite.getOpacity() / 255f * 
										Color.alpha(vColor) * vOpacity / 255f;
								if (r != lastR || g != lastG || b != lastB || a != lastA) {
									gl.glColor4f(r, g, b, a);
									lastR = r; lastG = g; lastB = b; lastA = a; 
								}
								
								sprite.getGrid().draw(gl, true, false);

								gl.glPopMatrix();
								
								rendered++;
							}
						}
					}
				}
				gl.glViewport(0, 0, Graphics.getScreenWidth(), Graphics.getScreenHeight());

				Graphics.updateFPSDraw();
				//if (fpsBitmap != null) fpsBitmapRefresh = false;
				if (Graphics.getFPSBitmapRefresh()) {
					updateFPSBitmap();
					if (fpsId > 0) {
						rTexture[0] = fpsId;
						gl.glDeleteTextures(1, rTexture, 0);
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
			if (DEBUG_STATUS) {
				Debug.write("" + (times / frame) + "ms, " + (rendered / frame) + "r, " + textures.size() + "t: " + Graphics.getFpsDraw() + "/" + Graphics.getFpsGame());
			}
			frame = 0;
			times = 0;
			rendered = 0;
		}
		
		framesRendered++;
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
		
//		if (fpsBitmap == null) {
//			
//			fpsBitmap = BitmapFactory.decodeResource(game.getResources(), 
//					R.drawable.whiteclouds);
//		}
	}
	
	private Grid createNewGrid(Bitmap bitmap) {
		//Debug.write("Create grid");

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
		long time = System.currentTimeMillis();
		
		int textureName = -1;
		if (gl != null) {
			gl.glGenTextures(1, mTextureNameWorkspace, 0);

			textureName = mTextureNameWorkspace[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE); //was GL10.GL_REPLACE

			int width = bitmap.getWidth(), height = bitmap.getHeight();
			int targetWidth = 1, targetHeight = 1;
			while (targetWidth < width) targetWidth *= 2;
			while (targetHeight < height) targetHeight *= 2;
			if ((width != targetWidth || height != targetHeight)) {
				
				//Debug.write("%s: %dx%d", last.getTag(), targetWidth, targetHeight);
//				int[] bmpPixels = new int[targetWidth * targetHeight];
//				bitmap.getPixels(bmpPixels, 0, targetWidth, 0, 0, width, height);
//				for (int i = 0; i < bmpPixels.length; i++) {
//					int c = bmpPixels[i];
//					bmpPixels[i] =
//						((c >> 16) & 0xff) +
//						((c & 0xff) << 16) +
//						(c & 0xff00ff00);
//				}
//				IntBuffer pixels = IntBuffer.wrap(bmpPixels);
//				gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, targetWidth, targetHeight, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);
				int pixels[] = new int[width * height];
				bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
				//Bitmap newBmp = Bitmap.createBitmap(pixels, 0, targetWidth, targetWidth, targetHeight, bitmap.getConfig());
				Bitmap newBmp = Bitmap.createBitmap(targetWidth, targetHeight, bitmap.getConfig());
				newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, newBmp, 0);
				
				if (bitmap != fpsBitmap) {
					textureCacheSize += newBmp.getWidth() * newBmp.getHeight() * 4;
				}

			} else {
				if(!bitmap.getConfig().equals(Config.ARGB_8888)) {
					Debug.write("Non-ARGB_8888 format!!");
				}
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bitmap, GL10.GL_UNSIGNED_BYTE, 0);
				
				if (bitmap != fpsBitmap) {
					textureCacheSize += bitmap.getWidth() * bitmap.getHeight() * 4;
				}
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
		time = System.currentTimeMillis() - time;
		
		if (bitmap != fpsBitmap) {
			if (DEBUG_STATUS) {
				Debug.write("Texture loaded (" + bitmap.getWidth() + "x" + bitmap.getHeight() + ": "
						+ time + "ms), " + textures.size() + " in cache (" +
						textureCacheSize + "b)");
			}
		}
		
		return textureName;
	}
}
