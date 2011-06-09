package com.twp.platform;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.BackgroundSprite;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.physics.Vector;

public class PlatformLogic implements Logic {

	Sprite player;
	BackgroundSprite background;
	Tilemap tilemap;
	JoyStick stick;
	Button button;
	
	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		Bitmap bmp = Data.loadBitmap(R.drawable.ocean);
		background = new BackgroundSprite(bmp, new Rect(0, 0, Graphics.getWidth(), Graphics.getHeight()), -5);
		//player.centerOrigin();
		stick = new JoyStick(60, Graphics.getHeight() - 60, 10, 50, Color.BLUE);
		button = new Button(Graphics.getWidth() - 60, Graphics.getHeight() - 60, 10, 50, Color.RED);
		
		int[][] map = Tilemap.readMap(Data.loadString(R.string.map));
		Rect rect = new Rect(0, 0, Graphics.getWidth(), Graphics.getHeight());
		tilemap = new Tilemap(Data.loadBitmap(R.drawable.tiles), 48, 48, 3, map, rect, -3);

	}

	@Override
	public void update(long timeElapsed) {

		stick.update();
		Vector p = stick.getPull();
		p.multiply(2f);
		background.scroll(p.getX(), 0);
		p.multiply(1.5f);
		tilemap.scroll(p.getX(), 0);
		button.update();

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
