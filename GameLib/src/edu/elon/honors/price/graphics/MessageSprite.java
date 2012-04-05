package edu.elon.honors.price.graphics;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class MessageSprite extends Sprite {
	
	private final static int SHOW_TIME = 4000;
	private final static int FADE_TIME = 1000;
	
	private static Paint paint = new Paint();
	private static LinkedList<MessageSprite> queue = 
		new LinkedList<MessageSprite>();
	static {
		paint.setTextSize(20);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
	}
	
	private int shown = 0;
	
	public MessageSprite(String message) {
		super(Viewport.DefaultViewport, createBmp(message));
		setX(0);
		setY(Graphics.getHeight() - getHeight());
		setZ(100000);
		bump();
		queue.add(this);
	}
	
	public void bump() {
		for (int i = 0; i < queue.size(); i++) {
			MessageSprite sprite = queue.get(i);
			sprite.setY(sprite.getY() - sprite.getHeight());
		}
	}
	
	@Override
	public void update(long timeElapsed) {
		shown += timeElapsed;
		if (shown > SHOW_TIME) {
			float fade = shown - SHOW_TIME;
			if (fade > FADE_TIME) {
				dispose();
				return;
			} else {
				setOpacity(1 - fade / FADE_TIME);
			}
		}
		if (getY() < -getHeight()) {
			dispose();
			return;
		}
		
		super.update(timeElapsed);
	}
	
	private static Bitmap createBmp(String message) {
		float m = paint.measureText(message);
		int width = 1;
		while (width < m) width *= 2;
		
		Bitmap bmp = Bitmap.createBitmap(width, 32, 
				 Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		c.drawColor(Color.argb(150, 123, 123, 123));
		c.drawText(message, 0, paint.getTextSize() + 4, paint);
		return bmp;
	}
}
