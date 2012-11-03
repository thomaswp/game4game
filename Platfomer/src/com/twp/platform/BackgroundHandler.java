package com.twp.platform;

import android.graphics.Bitmap;
import android.graphics.Rect;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.graphics.BackgroundSprite;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.physics.Vector;

public class BackgroundHandler {

	private int startForegroundY;
	private BackgroundSprite foreground, background, midground;
	
	private PlatformGame game;
	private Map map;
	
	public BackgroundHandler(PlatformGame game) {
		this.game = game;
		this.map = game.getSelectedMap();
		
		Bitmap bmp = Data.loadForeground(map.groundImageName);
		int bottom = map.height(game);
		startForegroundY = bottom - map.groundY;
		foreground = new BackgroundSprite(bmp, new Rect(0, startForegroundY, 
				Graphics.getWidth(), startForegroundY + bmp.getHeight()), -6);

		bmp = Data.loadBackground(map.skyImageName);
		//Debug.write("%dx%d", Graphics.getWidth(), Graphics.getHeight());
		int startBackgroundY = startForegroundY - Graphics.getHeight();
		background = new BackgroundSprite(bmp, new Rect(0, startBackgroundY, 
				Graphics.getWidth(), startForegroundY), -7);
		background.scroll(0, Graphics.getHeight() - bmp.getHeight());		

		if (map.midGrounds.size() > 0) {
			Bitmap mid = Data.loadMidgrounds(map.midGrounds);
			int startMidgroundY = startForegroundY - 256;
			midground = new BackgroundSprite(mid, new Rect(0, 
					startMidgroundY, Graphics.getWidth(), startMidgroundY + 
					mid.getHeight()), -5);
		}
	}
	
	public void updateScroll(Vector offset) {
		float parallax = 0.7f;
		float scrollX = -offset.getX() * parallax;
		float offY = (offset.getY()) * parallax - (1 - parallax) * (map.height(game) - Graphics.getHeight());
		
		foreground.setY(startForegroundY + offY);
		foreground.setScroll(scrollX, 0);
		
		if (midground != null) {
			midground.setY(foreground.getY() - 256);
			midground.setScroll(scrollX, 0);
		}
		
		float bgMinY = foreground.getY() - background.getRect().height();
		float bgY = Math.min(0, bgMinY);
		float bgOffY = bgMinY - bgY;
		background.setY(bgY);
		background.setScroll(scrollX, -bgOffY);
	}
}
