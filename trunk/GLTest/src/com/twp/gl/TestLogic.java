package com.twp.gl;

import android.app.Activity;
import android.graphics.Color;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;

public class TestLogic implements Logic {

	@Override
	public void update() {
		s.setX(s.getX() + 1f);
		s.setRotation(s.getRotation() + 1);
		
	}

	Sprite s, t;
	
	@Override
	public void initialize() {
		s = new Sprite(Viewport.DefaultViewport, 100, 100, 100, 100);
		s.getBitmapCanvas().drawPaint(Sprite.paintFromColor(Color.RED));
		s.centerOrigin();
		//s.setRotation(3);
		s.setZoom(1.5f);
		
		t = new Sprite(Viewport.DefaultViewport, 100, 100, 300, 3);
		t.getBitmap().eraseColor(Color.GREEN);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Activity parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(Activity parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}

}
