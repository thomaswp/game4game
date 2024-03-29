package edu.elon.honors.price.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Body;
import edu.elon.honors.price.physics.Physics;
import edu.elon.honors.price.physics.Vector;

public class Helper {
	
	private static Viewport textViewport;
	private static Paint textPaint;
	static { 
		textViewport = new Viewport(0, 0, -1, -1);
		textViewport.setZ(Integer.MAX_VALUE); 
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
	}
	public static void popupText(Physics physics, String text, Vector pos, int color, float size, int timeout) {
		textPaint.setColor(color);
		textPaint.setTextSize(size);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		float textWidth = textPaint.measureText(text);
		int width = Graphics.powerOfTwoSize((int)textWidth + 1);
		int height = Graphics.powerOfTwoSize((int)size + 1);
		Sprite s = new Sprite(textViewport, pos.getX(), pos.getY(), width, height);
		s.getBitmapCanvas().drawText(text, (width - textWidth) / 2, (height + size) / 2, textPaint);
		s.centerOrigin();
		Body b = new Body(physics, s);
		b.setVelocity(new Vector(0, -0.02f));
		b.setTimeout(timeout);
	}
	
	public static void drawCenteredText(Sprite sprite, String text, int color, float size) {
		Canvas canvas = sprite.getBitmapCanvas();
		textPaint.setColor(color);
		textPaint.setTextSize(size);
		textPaint.setTypeface(Typeface.DEFAULT);
		float textWidth = textPaint.measureText(text);
		int width = sprite.getBitmap().getWidth();
		int height = sprite.getBitmap().getHeight();
		canvas.drawText(text, (width - textWidth) / 2, (height + size) / 2, textPaint);
	}
}
