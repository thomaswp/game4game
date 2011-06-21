package com.twp.platform;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.PlatformLayer;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.data.R;
import edu.elon.honors.price.data.Tileset;
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

	public static final float GRAVITY = 0.01f;

	private static final int BORDER = 150;
	private static final int BSIZE = 50;
	private static final int BBORDER = 15;

	PlatformMap map;
	PlatformGame game;
	ArrayList<PlatformBody> actors = new ArrayList<PlatformBody>();
	BackgroundSprite background, sky;
	Tilemap[] layers;
	JoyStick stick;
	Button button;
	AnimatedSprite hero;
	PlatformBody heroBody;
	Physics physics;
	Vector p = new Vector();

	private float mapX, mapY, bgX, bgY, skyScroll;
	private int skyStartY, startOceanY;

	private String mapName;

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	public PlatformLogic(String mapName) {
		this.mapName = mapName;
	}

	@Override
	public void initialize() {
		if (game == null) game = new PlatformGame();
		map = game.maps.get(game.startMapId);

		Bitmap bmp = Data.loadBitmap(R.drawable.ocean);
		startOceanY = Graphics.getHeight() - bmp.getHeight();
		background = new BackgroundSprite(bmp, new Rect(0, Graphics.getHeight() - bmp.getHeight(), 
				Graphics.getWidth(), Graphics.getHeight()), -5);

		bmp = Data.loadBitmap(R.drawable.sky);
		skyStartY = bmp.getHeight() - Graphics.getHeight();
		sky = new BackgroundSprite(bmp, new Rect(0, skyStartY, Graphics.getWidth(), skyStartY + Graphics.getHeight()), -5);

		stick = new JoyStick(BSIZE + BBORDER, Graphics.getHeight() - BSIZE - BBORDER, 
				BBORDER, BSIZE, Color.argb(150, 0, 0, 255));
		button = new Button(Graphics.getWidth() - BSIZE - BBORDER, 
				Graphics.getHeight() - BSIZE - BBORDER, BBORDER, 
				BSIZE, Color.argb(150, 255, 0, 0));

		layers = new Tilemap[map.layers.length];
		Tileset tileset = game.getMapTileset(map);
		for (int i = 0; i < layers.length; i++) {
			PlatformLayer layer = map.layers[i];
			layers[i] = new Tilemap(Data.loadBitmap(tileset.bitmapId), 
					tileset.tileWidth, tileset.tileHeight, tileset.tileSpacing, 
					layer.tiles, Graphics.getRect(), i * 2);
		}

		PlatformActor heroActor = new PlatformActor();
		heroActor.imageId = R.drawable.hero;
		heroActor.jumpVelocity = 0.3f;
		heroActor.stunDuration = 600;
		heroActor.speed = 0.2f;
		heroActor.actorContactBehaviors[PlatformActor.BELOW] = PlatformActor.BEHAVIOR_JUMP;
		heroActor.actorContactBehaviors[PlatformActor.RIGHT] = PlatformActor.BEHAVIOR_STUN;
		heroActor.actorContactBehaviors[PlatformActor.LEFT] = PlatformActor.BEHAVIOR_STUN;

		physics = new Physics();
		heroBody = new PlatformBody(Viewport.DefaultViewport, physics, heroActor, 
				48, -48, layers, map, true, actors);
		hero = heroBody.getSprite();
		hero.centerOrigin();
		hero.setFrame(8);
		hero.setZoom(0.9f);
		actors.add(heroBody);

		PlatformLayer actorLayer = map.actorLayer;
		for (int i = 0; i < actorLayer.rows; i++) {
			for (int j = 0; j < actorLayer.columns; j++) {
				int actorId = actorLayer.tiles[i][j];
				if (actorId > 0) {
					float x = j * game.getMapTileset(map).tileWidth;
					float y = i * game.getMapTileset(map).tileHeight;
					PlatformBody actor = new PlatformBody(Viewport.DefaultViewport, physics,
							game.actors[actorId], x, y, layers, map, false, actors);
					actors.add(actor);
				}
			}
		}

		Viewport.DefaultViewport.setZ(3);
	}

	@Override
	public void update(long timeElapsed) {

		stick.update();
		button.update();


		if (!heroBody.isStunned()) {
			if (button.isTapped()) {
				heroBody.getVelocity().setY(-0.3f);
			}
			heroBody.getVelocity().setX(stick.getPull().getX() * 0.2f);
		}

		for (int i = 0; i < actors.size(); i++) {
			actors.get(i).update(timeElapsed);
		}
		for (int i = 0; i < actors.size(); i++) {
			actors.get(i).updateEvents();
		}

		p.clear();

		if (hero.getX() < BORDER) {
			p.setX(BORDER - hero.getX());
		}
		if (hero.getX() > Graphics.getWidth() - BORDER) {
			p.setX((Graphics.getWidth() - BORDER) - hero.getX());
		}
		if (hero.getY() < BORDER) {
			p.setY(BORDER - hero.getY());
		}
		if (hero.getY() > Graphics.getHeight() - BORDER) {
			if (bgY < 0)
				p.setY((Graphics.getHeight() - BORDER) - hero.getY());
		}

		physics.getSpriteOffset().add(p);

		for (int i = 0; i < actors.size(); i++) {
			actors.get(i).updateSprite();
		}

		p.multiply(-1);

		for (int i = 0; i < layers.length; i++)
			layers[i].scroll(p.getX(), p.getY());

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
		game = (PlatformGame) Data.loadObjectPublic(mapName, parent);

	}

}
