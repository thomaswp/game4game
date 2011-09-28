package edu.elon.honors.price.graphics;

import edu.elon.honors.price.game.Cache;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class BackgroundSprite {
	private Rect fullRect;
	private Bitmap tile;
	private Sprite[][] sprites;
	private Viewport viewport;
	
	
	public int getZ() {
		return viewport.getZ();
	}
	
	public void setZ(int z) {
		viewport.setZ(z);
	}
	
	public float getOpacity() {
		return viewport.getOpacity();
	}
	
	public void setOpacity(float opacity) {
		viewport.setOpacity(opacity);
	}
	
	public boolean isVisible() {
		return viewport.isVisible();
	}
	
	public void setVisible(boolean visible) {
		viewport.setVisible(visible);
	}

	public float getX() {
		return viewport.getY();
	}
	
	public void setX(float x) {
		viewport.setY(x);
	}
	
	public float getY() {
		return viewport.getY();
	}
	
	public void setY(float y) {
		viewport.setY(y);
	}
	
	public BackgroundSprite(Bitmap bitmap, Rect rect, int z) {
		viewport = createViewport(rect, z);
		fullRect = viewport.getRect();
		tile = bitmap;
		createSprites();
	}
	
	public void scroll(float x, float y) {
		shiftAll(-x, -y);
		
		Sprite corner = sprites[0][0];
		while (corner.getX() > 0) {
			shiftAll(-tile.getWidth(), 0);
		}
		while (corner.getX() < -tile.getWidth()) {
			shiftAll(tile.getWidth(), 0);
		}
		while (corner.getY() > 0) {
			shiftAll(0, -tile.getHeight());
		}
		while (corner.getY() < -tile.getHeight()) {
			shiftAll(0, tile.getHeight());
		}
	}

	public void dispose() {
		for (int i = 0; i < sprites.length; i++) {
			for (int j = 0; j < sprites[i].length; j++) {
				if (sprites[i][j] != null)
					sprites[i][j].dispose();
			}
		}
		Graphics.getViewports().remove(viewport);
	}

	private static Viewport createViewport(Rect rect, int z) {
		Viewport vp = new Viewport(rect.left, rect.top, rect.width(), rect.height());
		vp.setSorted(false);
		vp.setZ(z);
		return vp;
	}
	
	private void shiftAll(float x, float y) {
		for (int i = 0; i < sprites.length; i++) {
			for (int j = 0; j < sprites[i].length; j++) {
				if (sprites[i][j] != null) {
					Sprite s = sprites[i][j];
					s.setX(s.getX() + x);
					s.setY(s.getY() + y);
				}
			}
		}
	}

	private void createSprites() {
		int rows = fullRect.height() / tile.getHeight() + 2;
		int cols = fullRect.width() / tile.getWidth() + 2;
		
		sprites = new Sprite[rows][cols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				sprites[i][j] = new Sprite(viewport, tile);
				sprites[i][j].setX(j * tile.getWidth());
				sprites[i][j].setY(i * tile.getHeight());
			}
		}
	}

//	private static Bitmap createBitmap(Bitmap original, Rect rect) {
//		int code = original.hashCode() + BackgroundSprite.class.hashCode();
//		if (Cache.isBitmapRegistered(code)) {
//			//Game.debug("Cache: " + code);
//			return Cache.getRegisteredBitmap(code);
//		} else {
//			//Game.debug("Load New: " + code);
//			int width, height;
//			if (original.getWidth() < rect.width()) {
//				width = original.getWidth() * ((rect.width() / original.getWidth()) + 2);
//			} else {
//				width = original.getWidth() * 2;
//			}
//			if (original.getHeight() < rect.height()) {
//				height = original.getHeight() * ((rect.height() / original.getHeight()) + 2);
//			} else {
//				height = original.getHeight() * 2;
//			}
//			Bitmap bmp = Bitmap.createBitmap(width, height, Sprite.defaultConfig);
//			Canvas canvas = new Canvas();
//			canvas.setBitmap(bmp);
//			Paint paint = new Paint();
//			for (int i = 0; i < width; i += original.getWidth()) {
//				for (int j = 0; j < height; j += original.getHeight()) {
//					canvas.drawBitmap(original, i, j, paint);
//				}
//			}
//			Cache.RegisterBitmap(code, bmp);
//			return bmp;
//		}
//	}
}
