package edu.elon.honors.price.graphics;

import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class BackgroundSprite extends Sprite {
	private Rect fullRect;
	private int segmentWidth, segmentHeight;

	public BackgroundSprite(Bitmap bitmap, Rect rect, int z) {
		this(bitmap, createViewport(rect, z));
	}

	public BackgroundSprite(Bitmap bitmap, Viewport viewport) {
		super(viewport, createBitmap(bitmap, viewport.getRect()));
		fullRect = viewport.getRect();
		this.segmentWidth = bitmap.getWidth();
		this.segmentHeight = bitmap.getHeight();
	}

	public void scroll(float x, float y) {
		setOriginX(getOriginX() + x);
		setOriginY(getOriginY() + y);
		RectF spriteRect = getRect();
		fullRect = getViewport().getRect();
		while (spriteRect.left > fullRect.left) {
			this.setOriginX(this.getOriginX() + segmentWidth);
			spriteRect = getRect();
		}
		while (spriteRect.right < fullRect.right) {
			this.setOriginX(this.getOriginX() - segmentWidth);
			spriteRect = getRect();
		}
		while (spriteRect.top > fullRect.top) {
			this.setOriginY(this.getOriginY() + segmentHeight);
			spriteRect = getRect();
		}
		while (spriteRect.bottom < fullRect.bottom) {
			this.setOriginY(this.getOriginY() - segmentHeight);
			spriteRect = getRect();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		Graphics.getViewports().remove(getViewport());
	}

	private static Viewport createViewport(Rect rect, int z) {
		Viewport vp = new Viewport(rect.left, rect.top, rect.width(), rect.height());
		vp.setSorted(false);
		vp.setZ(z);
		return vp;
	}

	private static Bitmap createBitmap(Bitmap original, Rect rect) {
		int code = original.hashCode() + BackgroundSprite.class.hashCode();
		if (Data.isBitmapRegistered(code)) {
			return Data.getRegisteredBitmap(code);
		} else {
			int width, height;
			if (original.getWidth() < rect.width()) {
				width = original.getWidth() * ((rect.width() / original.getWidth()) + 2);
			} else {
				width = original.getWidth() * 2;
			}
			if (original.getHeight() < rect.height()) {
				height = original.getHeight() * ((rect.height() / original.getHeight()) + 2);
			} else {
				height = original.getHeight() * 2;
			}
			//Game.debug("CB:"+width+","+height);
			Bitmap bmp = Bitmap.createBitmap(width, height, Sprite.defaultConfig);
			Canvas canvas = new Canvas();
			canvas.setBitmap(bmp);
			Paint paint = new Paint();
			for (int i = 0; i < width; i += original.getWidth()) {
				for (int j = 0; j < height; j += original.getHeight()) {
					canvas.drawBitmap(original, i, j, paint);
				}
			}
			Data.RegisterBitmap(bmp, code);
			return bmp;
		}
	}
}
