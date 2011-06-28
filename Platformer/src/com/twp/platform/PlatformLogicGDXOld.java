package com.twp.platform;

import java.io.File;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.elon.honors.price.data.PlatformLayer;
import edu.elon.honors.price.data.PlatformMap;
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
import edu.elon.honors.price.physics.Vector;

public class PlatformLogicGDX implements Logic {

	private static final int BORDER = 150;
	private static final int FRAME = 150;
	private static final int BSIZE = 50;
	private static final int BBORDER = 15;
	
	private static final float SCALE = 50;

	PlatformMap map;
	BackgroundSprite background, sky;
	Tilemap[] layers;
	JoyStick stick;
	Button button;
	AnimatedSprite hero;
	Vector p = new Vector();
	Vector heroOff = new Vector();

	private float mapX, mapY, bgX, bgY, skyScroll;
	private int skyStartY, startOceanY;

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		if (map == null) map = new PlatformMap();
		
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

//		layers = new Tilemap[map.layers.size()];
//		Tileset tileset = map.tileset;
//		for (int i = 0; i < layers.length; i++) {
//			PlatformLayer layer = map.layers.get(i);
//			layers[i] = new Tilemap(Data.loadBitmap(tileset.bitmapId), 
//					tileset.tileWidth, tileset.tileHeight, tileset.tileSpacing, 
//					layer.tiles, Graphics.getRect(), i * 2);
//		}

		Bitmap[] frames = Tilemap.createTiles(Data.loadBitmap(R.drawable.hero), 32, 48, 0);
		hero = new AnimatedSprite(Viewport.DefaultViewport, frames, 48, -48);
		hero.setFrame(8);
		hero.setZoom(0.8f);
		hero.centerOrigin();
		
		Viewport.DefaultViewport.setZ(3);
		
		initPhysics();
	}
	
	private Body heroBody;
	private World world;
	private void initPhysics() {
		world = new World(new Vector2(0, 10f), true);
		
		BodyDef heroDef = new BodyDef();
		heroDef.position.set(spriteToVect(hero));
		heroDef.type = BodyType.DynamicBody;
		heroDef.fixedRotation = true;
		heroBody = world.createBody(heroDef);
		PolygonShape heroShape = new PolygonShape();
		heroShape.setAsBox(hero.getWidth() / SCALE / 2, hero.getHeight() / SCALE / 2);
		FixtureDef heroFix = new FixtureDef();
		heroFix.shape = heroShape;
		heroFix.friction = 0;
		heroFix.restitution = 0;
		heroFix.density = 1;
		heroBody.createFixture(heroFix);
		
		for (int k = 0; k < layers.length; k++) {
			if (!map.layers[k].active)
				continue;
			
			Sprite[][] sprites = layers[k].getSprites();
			
			for (int i = 0; i < sprites.length; i++) {
				for (int j = 0; j < sprites[i].length; j++) {
					Sprite s = sprites[i][j];
					if (s != null) {
						BodyDef tileDef = new BodyDef();
						tileDef.position.set(spriteToVect(s));
						tileDef.type = BodyType.StaticBody;
						Body tileBody = world.createBody(tileDef);
						PolygonShape tileShape = new PolygonShape();
						tileShape.setAsBox(s.getWidth() / SCALE / 2, s.getHeight() / SCALE / 2);
						tileBody.createFixture(tileShape, 1);
					}
				}
			}
		}
	}
	
	private Vector2 spriteToVect(Sprite sprite) {
		return new Vector2((sprite.getX() + sprite.getWidth() / 2)/ SCALE, (sprite.getY() - sprite.getHeight() / 2) / SCALE);
	}
	
	private void setSpritePosition(Sprite sprite, Body body) {
		sprite.setX(body.getPosition().x * SCALE);
		sprite.setY(body.getPosition().y * SCALE + sprite.getHeight());
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
			heroBody.setLinearVelocity(heroBody.getLinearVelocity().x, -7);
		}
		heroBody.setLinearVelocity(stick.getPull().getX() * 3.5f, heroBody.getLinearVelocity().y);

		world.step(timeElapsed / 1000.0f, 6, 2);
		//Game.debug(heroBody.getPosition());

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

		heroOff.add(p);
		setSpritePosition(hero, heroBody);
		hero.setX(hero.getX() + heroOff.getX());
		hero.setY(hero.getY() + heroOff.getY());

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
		map = (PlatformMap) Data.loadObjectPublic("map-final", parent);

	}

}
