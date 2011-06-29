package com.twp.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
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
	private Vector2 temp = new Vector2();

	private ArrayList<PlatformBodyGDX> toRemove = new ArrayList<PlatformBodyGDX>();
	private ArrayList<ActorAddable> toAdd = new ArrayList<ActorAddable>();
	private ArrayList<QueuedContact> contacts = new ArrayList<PlatformLogicGDX.QueuedContact>();

	private HashMap<Fixture, PlatformBodyGDX> fixtureMap = new HashMap<Fixture, PlatformBodyGDX>();

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
				float x = j * game.getMapTileset(map).tileWidth;
				float y = i * game.getMapTileset(map).tileHeight;
				if (i == 0 && j == 0) {
					PlatformActor rock = new PlatformActor();
					rock.animated = false;
					rock.fixedRotation = false;
					rock.imageName = "rock.png";
					rock.zoom = 0.6f;
					rock.name = "Rock";
					//addActor(rock, 60, 0);
				}
				if (i == 4 && j == 18) {
					PlatformActor dude = new PlatformActor();
					dude.imageName = "blank.png";
					dude.zoom = 3f;
					dude.animated = false;
					dude.name = "Dude";
					final PlatformBodyGDX actor = addActor(dude, x, y);
					final float rx = x;
					ContactFilter filter = new ContactFilter() {

						@Override
						public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
							if (!(fixtureMap.containsKey(fixtureA) && fixtureMap.containsKey(fixtureB)))
								return true;

							PlatformBodyGDX bodyA = fixtureMap.get(fixtureA);
							PlatformBodyGDX bodyB = fixtureMap.get(fixtureB);

							return PlatformBodyGDX.collides(bodyA, bodyB);
						}
					};
					world.setContactFilter(filter);
					ContactListener listener = new ContactListener() {

						@Override
						public void endContact(Contact contact) {

						}

						@Override
						public void beginContact(Contact contact) {
							Vector2 normal = contact.getWorldManifold().getNormal();							
							contacts.add(new QueuedContact(contact.getFixtureA(), contact.getFixtureB(), normal));

							if (PlatformBodyGDX.contactBetween(contact, heroBody, actor)) {
								if (!toRemove.contains(actor) && actors.contains(actor)) {
									toRemove.add(actor);
									PlatformActor critter = new PlatformActor();
									critter.zoom = 1.3f;
									critter.imageName = "critter.png";
									critter.collidesWithActors = false;
									critter.name = "Critter";
									for (int i = 0; i < 5; i++) {
										toAdd.add(new ActorAddable(critter, rx + (int)(Math.random() * 40), (int)(Math.random() * 10)));
									}
								}
							}
						}
					};
					world.setContactListener(listener);
				}
				int actorId = actorLayer.tiles[i][j];
				if (actorId > 0) {
					addActor(game.actors[actorId], x, y);
				} else if (actorId == -1) {
					game.hero.animated = true;
					game.hero.fixedRotation = true;
					game.hero.collidesWithActors = true;
					game.hero.zoom = 0.9f;
					game.hero.name = "Hero";
					heroBody = addActor(game.hero, 
							j * tileset.tileWidth,
							i * tileset.tileHeight, true);
					hero = heroBody.getSprite();
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
				heroBody.setVelocity(heroBody.getVelocity().x, -6.5f);
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
		
		for (int i = 0; i < contacts.size(); i++) {
			doContact(contacts.get(i));
		}
		contacts.clear();
		for (int i = 0; i < toRemove.size(); i++) {
			removeActor(toRemove.get(i));
		}
		toRemove.clear();
		for (int i = 0; i < toAdd.size(); i++) {
			addActor(toAdd.get(i));
		}
		toAdd.clear();

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

	private PlatformBodyGDX addActor(ActorAddable actor) {
		return addActor(actor.actor, actor.startX, actor.startY);
	}

	private PlatformBodyGDX addActor(PlatformActor actor, float startX, float startY) {
		return addActor(actor, startX, startY, false);
	}

	private PlatformBodyGDX addActor(PlatformActor actor, float startX, float startY, boolean isHero) {
		PlatformBodyGDX body = new PlatformBodyGDX(Viewport.DefaultViewport, world, actor, 
				startX, startY, isHero, actors);
		for (int i = 0; i < body.getBody().getFixtureList().size(); i++) {
			fixtureMap.put(body.getBody().getFixtureList().get(i), body);
		}
		return body;
	}

	private void removeActor(PlatformBodyGDX body) {
		for (int i = 0; i < body.getBody().getFixtureList().size(); i++) {
			fixtureMap.remove(body.getBody().getFixtureList().get(i));
		}
		body.dispose();
	}

	private void doContact(QueuedContact contact) {
		doContact(contact, false);
	}

	private void doContact(QueuedContact contact, boolean anti) {
		Fixture fixtureA = anti ? contact.fixtureB : contact.fixtureA;
		Fixture fixtureB = anti ? contact.fixtureA : contact.fixtureB;

		float nx = contact.normal.x;
		float ny = contact.normal.y;
		if (anti) {
			nx *= -1;
			ny *= -1;
		}

		if (fixtureMap.containsKey(fixtureA)) {

			PlatformBodyGDX bodyA = fixtureMap.get(fixtureA);

			if (fixtureMap.containsKey(fixtureB)) {
				PlatformBodyGDX bodyB = fixtureMap.get(fixtureB);
				int dir;
				if (Math.abs(nx) > Math.abs(ny)) {
					if (nx > 0) {
						Game.debug(bodyA.getActor().name + ": Right");
						dir = PlatformActor.RIGHT;
					} else {
						Game.debug(bodyA.getActor().name + ": Left");
						dir = PlatformActor.LEFT;
					}
				} else {
					if (ny > 0) {
						Game.debug(bodyA.getActor().name + ": Below");
						dir = PlatformActor.BELOW;
					} else {
						Game.debug(bodyA.getActor().name + ": Above");
						dir = PlatformActor.ABOVE;
					}
				}
				
				int[] behaviors = bodyB.isHero() ? bodyA.getActor().heroContactBehaviors : 
					bodyA.getActor().actorContactBehaviors;				
				bodyA.doBehavior(behaviors[dir], bodyB);
			} else {
				if (Math.abs(nx) * 2 > Math.abs(ny)) {
					Game.debug(bodyA.getActor().name + ": Wall");
					Game.debug(nx + ", " + ny);
					bodyA.doBehavior(bodyA.getActor().wallBehavior, null);
				}
			}
		}

		if (!anti) {
			doContact(contact, true);
		}
	}

	private static class ActorAddable {
		public PlatformActor actor;
		public float startX, startY;

		public ActorAddable(PlatformActor actor, float startX, float startY) {
			this.actor = actor;
			this.startX = startX;
			this.startY = startY;
		}
	}

	private static class QueuedContact {
		public Fixture fixtureA, fixtureB;
		public Vector2 normal;

		public QueuedContact(Fixture fixtureA, Fixture fixtureB, Vector2 normal) {
			this.fixtureA = fixtureA;
			this.fixtureB = fixtureB;
			this.normal = normal;
		}
	}
}
