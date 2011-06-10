package com.twp.platform;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
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
	private static final int FRAME = 150;
	private static final int BSIZE = 50;
	private static final int BBORDER = 15;

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

		stick = new JoyStick(BSIZE + BBORDER, Graphics.getHeight() - BSIZE - BBORDER, 
				BBORDER, BSIZE, Color.argb(150, 0, 0, 255));
		button = new Button(Graphics.getWidth() - BSIZE - BBORDER, 
				Graphics.getHeight() - BSIZE - BBORDER, BBORDER, 
				BSIZE, Color.argb(150, 255, 0, 0));

		//int[][] map = Tilemap.readMap(Data.loadString(R.string.map));
		int[][] map = new int[10][100];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (Math.random() > 0.6)
					map[i][j] = 6*8;//(int)(Math.random() * 64);
				else
					map[i][j] = -1;
			}
		}
		Rect rect = new Rect(0, 0, Graphics.getWidth(), Graphics.getHeight());
		tilemap = new Tilemap(Data.loadBitmap(R.drawable.tiles), 48, 48, 3, map, rect, -3);

		Bitmap[] frames = Tilemap.createTiles(Data.loadBitmap(R.drawable.hero), 32, 48, 0);
		hero = new AnimatedSprite(Viewport.DefaultViewport, frames, 48, -48);
		hero.centerOrigin();
		hero.setFrame(8);
		hero.setZoom(0.9f);
		physics = new Physics();
		heroBody = new Body(physics, hero);
	}

	@Override
	public void update(long timeElapsed) {

		stick.update();
		button.update();

		Vector pull = stick.getPull();
		int frame = hero.getFrame();
		if (pull.getX() > 0 && (frame / 4 != 2 || !hero.isAnimated())) {
			hero.Animate(FRAME, 8, 4);
		} else if (pull.getX() < 0 && (frame / 4 != 1 || !hero.isAnimated())) {
			hero.Animate(FRAME, 4, 4);
		}
		if (hero.isAnimated()) {
			if (pull.getX() == 0) {
				hero.setFrame(frame - frame % 4);
			}
		}

		if (button.isTapped()) {// && Math.abs(heroBody.getVelocity().getY()) < 0.02f) {
			heroBody.getVelocity().setY(-0.3f);
		}
		heroBody.getVelocity().setX(stick.getPull().getX() * 0.2f);
		heroBody.getVelocity().addY(0.01f);

		Sprite[][] sprites = tilemap.getSprites();
		RectF heroRect = heroBody.getSprite().getRect();
		Vector heroV = heroBody.getVelocity();
		
		if (heroV.getX() != 0) {
			heroRect.offset(heroV.getX() * timeElapsed, 0);
			for (int i = 0; i < sprites.length; i++) {
				for (int j = 0; j < sprites[i].length; j++) {
					Sprite s = sprites[i][j];
					if (s != null) {
						if (s.isVisible()) {
							if (s.getRect().intersect(heroRect)) {
								heroRect.offset(-heroV.getX() * timeElapsed, 0);
								heroV.setX(0);
								break;
							}
						}

					}
				}
			}
			heroRect.offset(-heroV.getX() * timeElapsed, 0);
		}
		
		if (heroV.getY() != 0) {
			heroRect.offset(0, heroV.getY() * timeElapsed);
			for (int i = 0; i < sprites.length; i++) {
				for (int j = 0; j < sprites[i].length; j++) {
					Sprite s = sprites[i][j];
					if (s != null) {
						if (s.isVisible()) {
							if (s.getRect().intersect(heroRect)) {
								heroRect.offset(0, -heroV.getY() * timeElapsed);
								heroV.setY(0);
								break;
							}
						}

					}
				}
			}
			heroRect.offset(0, -heroV.getY() * timeElapsed);
		}
		
		if (heroV.getX() != 0 && heroV.getY() != 0) {
			heroRect.offset(heroV.getX() * timeElapsed, heroV.getY() * timeElapsed);
			for (int i = 0; i < sprites.length; i++) {
				for (int j = 0; j < sprites[i].length; j++) {
					Sprite s = sprites[i][j];
					if (s != null) {
						if (s.isVisible()) {
							if (s.getRect().intersect(heroRect)) {
								heroRect.offset(-heroV.getX() * timeElapsed, -heroV.getY() * timeElapsed);
								heroV.clear();
								break;
							}
						}

					}
				}
			}
			heroRect.offset(-heroV.getX() * timeElapsed, -heroV.getY() * timeElapsed);
		}
		
		
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
		
		tilemap.scroll(p.getX(), p.getY());
		
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
