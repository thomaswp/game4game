package com.twp.ptest;

import android.app.Activity;
import android.graphics.Color;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;

public class TestLogic implements Logic {

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}

	Sprite s;
	
	@Override
	public void initialize() {
		s = new Sprite(Viewport.DefaultViewport, 0, 0, 100, 100);
		s.getBitmap().eraseColor(Color.BLUE);
	}

	@Override
	public void update(long timeElapsed) {
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

}
