package com.twp.asteroids;

import java.io.Serializable;

import android.graphics.Bitmap;
import edu.elon.honors.price.graphics.Sprite;

public class Explosion implements Serializable {
	public float x, y, zoom;
	public int state;
	public transient Sprite sprite;
	public transient Bitmap[] expAnimation;
	
	public Explosion(float x, float y, float zoom, Bitmap[] expAnimation) {
		this.x = x;
		this.y = y;
		this.zoom = zoom;
		this.expAnimation = expAnimation;
	}
	
	public void updateSprite() {
		sprite.setX(x);
		sprite.setY(y);
		sprite.setZoom(zoom);
		if (state < expAnimation.length) {
			sprite.setBitmap(expAnimation[state]);
		}
	}
	
	public void dispose() {
		sprite.dispose();
	}
}
