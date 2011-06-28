package com.twp.platform;

import java.io.File;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.PlatformLayer;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.data.R;
import edu.elon.honors.price.data.Tileset;
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
import edu.elon.honors.price.physics.Physics;
import edu.elon.honors.price.physics.Vector;

public class PlatformLogicGDX implements Logic {

	public static final float GRAVITY = 0.01f;

	private static final int BORDER = 130;
	private static final int BSIZE = 50;
	private static final int BBORDER = 15;

	public static final float SCALE = 50;

	PlatformMap map;
	PlatformGame game;
	ArrayList<PlatformBodyGDX> actors = new ArrayList<PlatformBodyGDX>();
	BackgroundSprite background, sky;
	Tilemap[] layers;
	JoyStick stick;
	Button button;
	AnimatedSprite hero;
	Vector p = new Vector();
	private World world;
	private PlatformBodyGDX heroBody;
	private Vector offset;

	private float mapX, mapY, bgX, bgY, skyScroll;
	private int skyStartY, startOceanY;

	private boolean paused;

	private String mapName;

	@Override
	public void setPaused(boolean paused) {
		paused = true;
	}

	public PlatformLogicGDX(String mapName) {
		this.mapName = mapName;
		paused = true;
	}

	@Override
	public void initialize() {
		if (game == null) game = new PlatformGame();
		map = game.maps.get(game.startMapId);

		Bitmap bmp = Game.loadBitmap(R.drawable.ocean);
		startOceanY = Graphics.getHeight() - bmp.getHeight();
		background = new BackgroundSprite(bmp, new Rect(0, Graphics.getHeight() - bmp.getHeight(), 
				Graphics.getWidth(), Graphics.getHeight()), -5);

		bmp = Game.loadBitmap(R.drawable.sky);
		skyStartY = bmp.getHeight() - Graphics.getHeight();
		sky = new BackgroundSprite(bmp, new Rect(0, skyStartY, Graphics.getWidth(), skyStartY + Graphics.getHeight()), -5);

		stick = new JoyStick(BSIZE + BBORDER, Graphics.getHeight() - BSIZE - BBORDER, 
				BBORDER, BSIZE, Color.argb(150, 0, 0, 255));
		button = new Button(Graphics.getWidth() - BSIZE - BBORDER, 
				Graphics.getHeight() - BSIZE - BBORDER, BBORDER, 
				BSIZE, Color.argb(150, 255, 0, 0));

		offset = new Vector();

		initPhysics();


		Viewport.DefaultViewport.setZ(3);

		update(1);
	}

	private void initPhysics() {
		world = new World(new Vector2(0, 10f), true);

		layers = new Tilemap[map.layers.length];
		Tileset tileset = game.getMapTileset(map);
		for (int i = 0; i < layers.length; i++) {
			PlatformLayer layer = map.layers[i];
			layers[i] = new Tilemap(Data.loadTileset(tileset.bitmapName), 
					tileset.tileWidth, tileset.tileHeight, tileset.tileSpacing, 
					layer.tiles, Graphics.getRect(), i * 2);
		}

		for (int k = 0; k < layers.length; k++) {
			if (!map.layers[k].active)
				continue;

			Sprite[][] sprites = layers[k].getSprites();

			for (int i = 0; i < sprites.length; i++) {
				for (int j = 0; j < sprites[i].length; j++) {
					Sprite s = sprites[i][j];
					if (s != null) {
						BodyDef tileDef = new BodyDef();
						float x = (s.getX() + s.getWidth() / 2) / SCALE;
						float y = (s.getY() + s.getHeight() / 2) / SCALE;
						float width = s.getWidth() / SCALE;
						float height = s.getHeight() / SCALE;
						tileDef.position.set(x, y);
						tileDef.type = BodyType.StaticBody;
						Body tileBody = world.createBody(tileDef);
						PolygonShape tileShape = new PolygonShape();
						int tileId = map.layers[k].tiles[i][j]; 
						if (tileId == 28) {
							Vector2[] vertices = new Vector2[] {
									new Vector2(-width / 2, height / 2),
									new Vector2(-width / 2, -height / 2),
									new Vector2(width / 2, 0),
									new Vector2(width / 2, height / 2)
							};
							tileShape.set(vertices);
						} else if (tileId == 29) {
							Vector2[] vertices = new Vector2[] {
									new Vector2(-width / 2, height / 2),
									new Vector2(-width / 2, 0),
									new Vector2(width / 2, height / 2)
							};
							tileShape.set(vertices);
						} else if (tileId == 37) {
							Vector2[] vertices = new Vector2[] {
									new Vector2(-width / 2, height / 2),
									new Vector2(-width / 2, 0),
									new Vector2(width / 2, -height / 2),
									new Vector2(width / 2, height / 2)
							};
							tileShape.set(vertices);
						} else if (tileId == 36) {
							Vector2[] vertices = new Vector2[] {
									new Vector2(-width / 2, height / 2),
									new Vector2(width / 2, 0),
									new Vector2(width / 2, height / 2)
							};
							tileShape.set(vertices);
						} else {
							tileShape.setAsBox(width / 2, height / 2);
						}
						tileBody.createFixture(tileShape, 1);
					}
				}
			}
		}
		
		PlatformLayer actorLayer = map.actorLayer;
		for (int i = 0; i < actorLayer.rows; i++) {
			for (int j = 0; j < actorLayer.columns; j++) {
				if (i == 0 && j == 0) {
					PlatformActor rock = new PlatformActor();
					rock.animated = false;
					rock.fixedRotation = false;
					rock.imageName = "rock.png";
					PlatformBodyGDX actor = new PlatformBodyGDX(Viewport.DefaultViewport, world,
							rock, 60, 0, 0.9f, layers, map, false, actors);
					actors.add(actor);
				}
				int actorId = actorLayer.tiles[i][j];
				if (actorId > 0) {
					float x = j * game.getMapTileset(map).tileWidth;
					float y = i * game.getMapTileset(map).tileHeight;
					PlatformBodyGDX actor = new PlatformBodyGDX(Viewport.DefaultViewport, world,
							game.actors[actorId], x, y, 0.9f, layers, map, false, actors);
					actors.add(actor);
				} else if (actorId == -1) {
					game.hero.animated = true;
					game.hero.fixedRotation = true;
					heroBody = new PlatformBodyGDX(Viewport.DefaultViewport, world, game.hero, 
							j * tileset.tileWidth,
							i * tileset.tileHeight, 
							0.9f, layers, map, true, actors);
					hero = heroBody.getSprite();
					actors.add(heroBody);
				}
			}
		}
	}


	@Override
	public void update(long timeElapsed) {

		if (Input.isTapped()) paused = false;
		if (paused) {
			updateScroll();
			return;
		}

		stick.update();
		button.update();


		if (!heroBody.isStunned()) {
			if (button.isTapped()) {
				heroBody.setVelocity(heroBody.getVelocity().x, -7);
			}

			heroBody.setVelocity(stick.getPull().getX() * 3.5f, heroBody.getVelocity().y);
		}

		for (int i = 0; i < actors.size(); i++) {
			actors.get(i).update(timeElapsed, offset);
		}
		//		for (int i = 0; i < actors.size(); i++) {
		//			actors.get(i).updateEvents();
		//		}

		world.step(timeElapsed / 1000.0f, 6, 6);

		updateScroll();
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		game = (PlatformGame) Data.loadGame(mapName, Game.getCurrentGame());
	}

	private void updateScroll() {
		p.clear();

		RectF heroRect = hero.getRect();
		if (heroRect.left < BORDER) {
			p.setX(BORDER - heroRect.left);
		}
		if (heroRect.right > Graphics.getWidth() - BORDER) {
			p.setX((Graphics.getWidth() - BORDER) - heroRect.right);
		}
		if (heroRect.top < BORDER) {
			p.setY(BORDER - heroRect.top);
		}
		if (heroRect.bottom > Graphics.getHeight() - BORDER) {
			if (bgY < 0)
				p.setY((Graphics.getHeight() - BORDER) - heroRect.bottom);
		}

		offset.add(p);

		for (int i = 0; i < actors.size(); i++) {
			actors.get(i).updateSprite(offset);
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
}
