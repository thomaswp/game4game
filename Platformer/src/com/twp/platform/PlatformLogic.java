package com.twp.platform;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.BackgroundSprite;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.physics.Vector;

public class PlatformLogic implements Logic {

	Sprite player;
	BackgroundSprite background;
	JoyStick stick;
	
	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		background = new BackgroundSprite(Data.loadBitmap(R.drawable.ocean), 
				new Rect(0, 0, Graphics.getWidth(), Graphics.getHeight()), -5);
		//player.centerOrigin();
		stick = new JoyStick(60, Graphics.getHeight() - 60, 10, 50);

	}

	private float lastX = -1, lastY = -1;
	@Override
	public void update(long timeElapsed) {
//		if (Input.isTouchDown()) {
//			if (lastX < 0) {
//				lastX = Input.getLastTouchX();
//				lastY = Input.getLastTouchY();
//			} else {
//				float dx = Input.getLastTouchX() - lastX;
//				float dy = Input.getLastTouchY() - lastY;
//				lastX += dx;
//				lastY += dy;
//				background.scroll(-dx , -dy);
//			}
//		} else {
//			lastX = -1;
//			lastY = -1;
//		}
		stick.update();
		Vector p = stick.getPull();
		p.multiply(0.1f);
		background.scroll(p.getX(), p.getY());

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
