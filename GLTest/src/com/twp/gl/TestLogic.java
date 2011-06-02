package com.twp.gl;

import android.app.Activity;
import android.graphics.Color;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;

public class TestLogic implements Logic {

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		Sprite s = new Sprite(Viewport.DefaultViewport, 0, 0, 64, 64);
		s.getBitmapCanvas().drawPaint(Sprite.paintFromColor(Color.BLACK));
		
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
