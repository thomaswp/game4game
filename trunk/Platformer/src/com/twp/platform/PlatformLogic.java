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

public class PlatformLogic implements Logic {

	Sprite player;
	BackgroundSprite background;

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		background = new BackgroundSprite(Data.loadBitmap(R.drawable.tile), 
				new Rect(10, 10, Graphics.getWidth() - 10, Graphics.getHeight() - 10), -5);
		player = new Sprite(Viewport.DefaultViewport, 0, 50, 10, 10);
		player.getBitmap().eraseColor(Color.BLUE);
		//player.centerOrigin();
	}

	private float lastX = -1, lastY = -1;
	@Override
	public void update(long timeElapsed) {
		if (Input.isTouchDown()) {
			if (lastX < 0) {
				lastX = Input.getLastTouchX();
				lastY = Input.getLastTouchY();
			} else {
				float dx = Input.getLastTouchX() - lastX;
				float dy = Input.getLastTouchY() - lastY;
				lastX += dx;
				lastY += dy;
				background.scroll(-dx , -dy);
			}
		} else {
			lastX = -1;
			lastY = -1;
		}

		player.setY(background.getRect().bottom);

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
