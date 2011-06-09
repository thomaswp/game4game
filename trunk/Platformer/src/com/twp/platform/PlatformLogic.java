package com.twp.platform;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.BackgroundSprite;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.physics.Body;
import edu.elon.honors.price.physics.Physics;
import edu.elon.honors.price.physics.Vector;

public class PlatformLogic implements Logic {
	
	private static final int BORDER = 150;
	
	BackgroundSprite background, sky;
	Tilemap tilemap;
	JoyStick stick;
	Button button;
	AnimatedSprite hero;
	Body heroBody;
	Physics physics;
	Vector p = new Vector();

	private float mapX, mapY, bgX, bgY, skyScroll;
	private int skyStartY, startOceanY;

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		Bitmap bmp = Data.loadBitmap(R.drawable.ocean);
		startOceanY = Graphics.getHeight() - bmp.getHeight();
		background = new BackgroundSprite(bmp, new Rect(0, startOceanY, 
				Graphics.getWidth(), Graphics.getHeight() + 132), -5);
//		background = new BackgroundSprite(bmp, new Rect(0, Graphics.getHeight() - bmp.getHeight(), 
//				Graphics.getWidth(), Graphics.getHeight()), -5);
		
		bmp = Data.loadBitmap(R.drawable.sky);
		skyStartY = bmp.getHeight() - Graphics.getHeight();
		sky = new BackgroundSprite(bmp, new Rect(0, skyStartY, Graphics.getWidth(), skyStartY + Graphics.getHeight()), -5);

		stick = new JoyStick(60, Graphics.getHeight() - 60, 10, 50, Color.BLUE);
		button = new Button(Graphics.getWidth() - 60, Graphics.getHeight() - 60, 10, 50, Color.RED);

		//int[][] map = Tilemap.readMap(Data.loadString(R.string.map));
		int[][] map = new int[10][100];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (Math.random() > 0.6)
					map[i][j] = (int)(Math.random() * 64);
				else
					map[i][j] = -1;
			}
		}
		Rect rect = new Rect(0, 0, Graphics.getWidth(), Graphics.getHeight());
		tilemap = new Tilemap(Data.loadBitmap(R.drawable.tiles), 48, 48, 3, map, rect, -3);
		
		Bitmap[] frames = Tilemap.createTiles(Data.loadBitmap(R.drawable.hero), 32, 48, 0);
		hero = new AnimatedSprite(Viewport.DefaultViewport, frames, 0, 0);
		hero.centerOrigin();
		hero.Animate(150, 8, 4);
		hero.setZoom(1.3f);
		physics = new Physics();
		heroBody = new Body(physics, hero);
		for (int i = 0; i < Graphics.getViewports().size(); i++) {
			Game.debug(Graphics.getViewports().get(i).getZ());
		}
	}

	@Override
	public void update(long timeElapsed) {

		p.set(stick.getPull());
		p.multiply(0.2f);
		heroBody.setVelocity(p);
		//heroBody.getVelocity().add(new Vector(0, 0.01f));
		
		stick.update();
		button.update();
		
		heroBody.updatePhysics(timeElapsed);
		
		p.clear();
		
		if (heroBody.getX() < BORDER) {
			p.setX(BORDER - heroBody.getX());
		}
		if (heroBody.getX() > Graphics.getWidth() - BORDER) {
			p.setX((Graphics.getWidth() - BORDER) - heroBody.getX());
		}
		if (heroBody.getY() < BORDER) {
			p.setY(BORDER - heroBody.getY());
		}
		if (heroBody.getY() > Graphics.getHeight() - BORDER) {
			if (bgY < 0)
				p.setY((Graphics.getHeight() - BORDER) - heroBody.getY());
		}
		
		heroBody.getPosition().add(p);
		heroBody.updateSprite();
		
		p.multiply(-1);
		
		if (mapY + p.getY() <= 0)
			tilemap.scroll(p.getX(), p.getY());
		else
			tilemap.scroll(p.getX(), -mapY);
		mapX += p.getX(); mapY += p.getY();
		mapY = Math.min(0, mapY);
				
		p.multiply(0.7f);
		
		bgX += p.getX(); bgY += p.getY();
		bgY = Math.min(0, bgY);
		
		background.scroll(p.getX(), 0);
		background.getViewport().setY(startOceanY - bgY);
		
		sky.getViewport().setY(Math.min(0, skyStartY - bgY));
		float scroll = Math.min(0, sky.getViewport().getY() + bgY);
		sky.scroll(p.getX(), scroll - skyScroll);
		skyScroll = scroll;

		

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
