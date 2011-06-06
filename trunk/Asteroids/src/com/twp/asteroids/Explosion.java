package com.twp.asteroids;

import java.io.Serializable;

import android.graphics.Bitmap;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.physics.Body;
import edu.elon.honors.price.physics.Physics;

public class Explosion extends Body {
	private static final long serialVersionUID = 1L;
	
	public int state;
	public transient Bitmap[] expAnimation;
	
	public Explosion(Physics physics, float x, float y, float zoom, Bitmap[] expAnimation) {
		super(physics, x, y);
		setZoom(zoom);
		this.expAnimation = expAnimation;
	}
	
	@Override
	public void updateSprite() {
		super.updateSprite();
		if (state < expAnimation.length) {
			sprite.setBitmap(expAnimation[state]);
		}
	}
}
