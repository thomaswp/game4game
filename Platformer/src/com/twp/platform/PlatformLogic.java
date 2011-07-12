package com.twp.platform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformActorInstance;
import edu.elon.honors.price.data.PlatformEvent;
import edu.elon.honors.price.data.PlatformEvent.Action;
import edu.elon.honors.price.data.PlatformEvent.ActorTrigger;
import edu.elon.honors.price.data.PlatformEvent.Parameters;
import edu.elon.honors.price.data.PlatformEvent.RegionTrigger;
import edu.elon.honors.price.data.PlatformEvent.SwitchTrigger;
import edu.elon.honors.price.data.PlatformEvent.VariableTrigger;
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

public class PlatformLogic implements Logic {

	public static final float GRAVITY = 10f;

	private static final int BORDER = 130;
	private static final int BSIZE = 50;
	private static final int BBORDER = 15;

	public static final float SCALE = 50;
	
	private boolean test = true;

	PlatformMap map;
	PlatformGame game;
	ArrayList<PlatformBody> actors = new ArrayList<PlatformBody>();
	BackgroundSprite background, sky;
	Tilemap[] layers;
	JoyStick stick;
	Button button;
	AnimatedSprite hero;
	Vector p = new Vector();
	private World world;
	private PlatformBody heroBody;
	private Vector offset;
	
	private Vector2 antiGravity = new Vector2(0, -GRAVITY), zeroVector = new Vector2();

	private ArrayList<PlatformBody> toRemove = new ArrayList<PlatformBody>();
	private ArrayList<ActorAddable> toAdd = new ArrayList<ActorAddable>();
	private ArrayList<QueuedContact> contacts = new ArrayList<PlatformLogic.QueuedContact>();

	private HashMap<Fixture, PlatformBody> actorMap = new HashMap<Fixture, PlatformBody>();
	private HashMap<Fixture, Sprite> levelMap = new HashMap<Fixture, Sprite>();
	
	private Interpreter interpreter;

	private float mapX, mapY, bgX, bgY, skyScroll;
	private int skyStartY, startOceanY;

	private boolean paused;

	private String mapName;
	
	public PlatformBody getHero() {
		return heroBody;
	}

	public PlatformGame getGame() {
		return game;
	}

	public PlatformMap getMap() {
		return map;
	}

	@Override
	public void setPaused(boolean paused) {
		paused = true;
	}

	public PlatformLogic(String mapName, Game host) {
		this.mapName = mapName;
		this.interpreter = new Interpreter(this);
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

		while (actors.size() < map.actors.size()) actors.add(null);

		initPhysics();


		Viewport.DefaultViewport.setZ(3);

		update(1);
	}

	private void initPhysics() {
		world = new World(new Vector2(0, GRAVITY), true);
		
		ContactFilter filter = new ContactFilter() {

			@Override
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
				if (!(actorMap.containsKey(fixtureA) && actorMap.containsKey(fixtureB)))
					return true;

				PlatformBody bodyA = actorMap.get(fixtureA);
				PlatformBody bodyB = actorMap.get(fixtureB);

				return PlatformBody.collides(bodyA, bodyB);
			}
		};
		world.setContactFilter(filter);
		
		ContactListener listener = new ContactListener() {

			@Override
			public void endContact(Contact contact) {
				doEndContact(contact.getFixtureA(), contact.getFixtureB());
			}

			@Override
			public void beginContact(Contact contact) {		
				if (contact.isTouching()) {
					contacts.add(new QueuedContact(contact.getFixtureA(), contact.getFixtureB()));
				}
			}
		};
		world.setContactListener(listener);

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
						for (int l = 0; l < tileBody.getFixtureList().size(); l++) {
							levelMap.put(tileBody.getFixtureList().get(l), s);
						}
					}
				}
			}
		}

		PlatformLayer actorLayer = map.actorLayer;
		for (int i = 0; i < actorLayer.rows; i++) {
			for (int j = 0; j < actorLayer.columns; j++) {
				float x = (j + 0.5f) * game.getMapTileset(map).tileWidth;
				float y = (i + 0.5f) * game.getMapTileset(map).tileHeight;
				if (test) {
					if (i == 0 && j == 0) {
						PlatformActor rock = new PlatformActor();
						rock.animated = false;
						rock.fixedRotation = false;
						rock.imageName = "rock.png";
						rock.zoom = 0.6f;
						rock.name = "Rock";
						addActor(rock, -1, 60, 0);
					}
					if (i == 4 && j == 18) {
						PlatformActor dude = new PlatformActor();
						dude.imageName = "blank.png";
						dude.zoom = 3f;
						dude.animated = false;
						dude.name = "Dude";
						dude.heroContactBehaviors[PlatformActor.LEFT] = PlatformActor.BEHAVIOR_DIE;
						PlatformBody dudeBody = addActor(dude, -1, x, y);
						
						PlatformActor critter = new PlatformActor();
						critter.imageName = "critter.png";
						critter.zoom = 1.5f;
						critter.speed = 1;
						critter.name = "Critter";
						critter.edgeBehavior = PlatformActor.BEHAVIOR_TURN;
						critter.wallBehavior = PlatformActor.BEHAVIOR_TURN;
						critter.actorContactBehaviors[PlatformActor.LEFT] = PlatformActor.BEHAVIOR_TURN;
						critter.actorContactBehaviors[PlatformActor.RIGHT] = PlatformActor.BEHAVIOR_TURN;
						game.actors[2] = critter;
						
						ArrayList<Action> actions;
						Parameters params;
						ActorTrigger trigger;
						PlatformEvent event;
						
						for (int k = -1; k < 2; k++) {
							actions = new ArrayList<Action>();
							params = new Parameters(new Object[] {0, 0, 0, 3, 1, 0});
							actions.add(new Action(Action.ID_SET_VARIABLE, params));
							params = new Parameters(new Object[] {0, 0, 1, 0, k * 60, 0});
							actions.add(new Action(Action.ID_SET_VARIABLE, params));
							params = new Parameters(new Object[] {2, 1, 0, 0, 0, (int)(Math.random() * 2)}); 
							actions.add(new Action(Interpreter.ID_CREATE_ACTOR, params));
							trigger = new ActorTrigger(true, dudeBody.getId(), ActorTrigger.ACTION_COLLIDES_HERO);
							event = new PlatformEvent(actions);
							event.actorTriggers.add(trigger);
							map.events.add(event);
						}
						
						actions = new ArrayList<Action>();
						params = new Parameters(new Object[] {1, PlatformActor.BEHAVIOR_STUN});
						actions.add(new Action(Action.ID_ACTOR_BEHAVIOR, params));
						trigger = new ActorTrigger(false, 2, ActorTrigger.ACTION_COLLIDES_HERO);
						event = new PlatformEvent(actions);
						event.actorTriggers.add(trigger);
						map.events.add(event);
						
						Rect ladder = new Rect(22 * 48 + 6, 0, 23 * 48 - 6, 48 * 6);
						
						actions = new ArrayList<Action>();
						params = new Parameters(new Object[] {0});
						actions.add(new Action(Action.ID_HERO_SET_LADDER, params));
						RegionTrigger trigger2 = new RegionTrigger(ladder, RegionTrigger.MODE_CONTAIN, true);
						event = new PlatformEvent(actions);
						event.regionTriggers.add(trigger2);
						map.events.add(event);
						
						actions = new ArrayList<Action>();
						params = new Parameters(new Object[] {1});
						actions.add(new Action(Action.ID_HERO_SET_LADDER, params));
						trigger2 = new RegionTrigger(ladder, RegionTrigger.MODE_LOSE_TOUCH, true);
						event = new PlatformEvent(actions);
						event.regionTriggers.add(trigger2);
						map.events.add(event);
						
					}
				}
				int instanceId = actorLayer.tiles[i][j];
				if (instanceId > 0) {
					PlatformActorInstance instance = this.map.actors.get(instanceId);
					int actorId = instance.actorType;
					if (actorId > 0) {
						addActor(game.actors[actorId], instanceId, x, y);
					} else if (actorId == -1) {
						if (test)
							game.hero.actorContactBehaviors = new int[4];
						game.hero.zoom = 0.9f;
						game.hero.name = "Hero";
						heroBody = addActor(game.hero, 
								instanceId,
								x,
								y, 1, true);
						hero = heroBody.getSprite();
					}
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
				heroBody.jump();
			}
			
			if (heroBody.isOnLadder()) {
				heroBody.setVelocityY(stick.getPull().getY() * heroBody.getActor().speed);
				antiGravity.set(0, -GRAVITY * heroBody.getBody().getMass());
				heroBody.getBody().applyForce(antiGravity, zeroVector);
			}
			else {
				heroBody.setVelocityX(stick.getPull().getX() * heroBody.getActor().speed);
			}
		}

		for (int i = 0; i < actors.size(); i++) {
			if (actors.get(i) != null) {
				actors.get(i).update(timeElapsed, offset);
			}
		}

		world.step(timeElapsed / 1000.0f, 6, 6);

		
		for (int i = 0; i < contacts.size(); i++) {
			doContact(contacts.get(i));
		}
		contacts.clear();
		
		checkTriggers();
		checkBehaviors();
		
		for (int i = 0; i < toRemove.size(); i++) {
			removeActor(toRemove.get(i));
		}
		toRemove.clear();
		for (int i = 0; i < toAdd.size(); i++) {
			addActor(toAdd.get(i));
		}
		toAdd.clear();

		interpreter.update();

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

	public PlatformBody getBodyFromId(int id) {
		return actors.get(id);
	}

	public void queueActor(ActorAddable actor) {
		toAdd.add(actor);
	}

	public  PlatformBody addActor(ActorAddable actor) {
		return addActor(actor.actor, -1, actor.startX, actor.startY, actor.startDir);
	}

	private void checkTriggers() {
		for (int i = 0; i < map.events.size(); i++) {
			PlatformEvent event = map.events.get(i);
			boolean triggered = false;

			for (int j = 0; j < event.switchTriggers.size(); j++) {
				SwitchTrigger trigger = event.switchTriggers.get(j);
				triggered |= Globals.getSwitches()[trigger.switchId] == trigger.value; 
			}

			for (int j = 0; j < event.variableTriggers.size(); j++) {
				VariableTrigger trigger = event.variableTriggers.get(j);

				int value1 = Globals.getVariables()[trigger.variableId];
				int value2 = trigger.with == VariableTrigger.WITH_VALUE ? trigger.valueOrId :
					Globals.getVariables()[trigger.valueOrId];

				boolean result = false;
				switch (trigger.test) {
				case VariableTrigger.TEST_EQUALS: result = value1 == value2; break;
				case VariableTrigger.TEST_NOT_EQUALS: result = value1 != value2; break;
				case VariableTrigger.TEST_GT: result = value1 > value2; break;
				case VariableTrigger.TEST_LT: result = value1 < value2; break;
				case VariableTrigger.TEST_GEQ: result = value1 >= value2; break;
				case VariableTrigger.TEST_LEQ: result = value1 <= value2; break;
				}
				
				triggered |= result;
			}
			
			for (int j = 0; j < event.actorTriggers.size(); j++) {
				ActorTrigger trigger = event.actorTriggers.get(j);
				
				for (int k = 0; k < actors.size(); k++) {
					PlatformBody actor = actors.get(k);
					
					if (actor == null) continue;
					if (trigger.forInstance && k != trigger.id) continue;
					if (!trigger.forInstance && actor.getActor() != game.actors[trigger.id]) continue;
					
					if (trigger.action == ActorTrigger.ACTION_COLLIDES_HERO) {
						for (int l = 0; l < actor.getCollidedActors().size(); l++) {
							 triggered |= actor.getCollidedActors().get(l).isHero();
						}
					}
					if (trigger.action == ActorTrigger.ACTION_COLLIDES_ACTOR) {
						triggered |= actor.getCollidedActors().size() > 0;
					}
					if (trigger.action == ActorTrigger.ACTION_COLLIDES_WALL) {
						triggered |= actor.isCollidedWall();
					}
				}
			}
			
			for (int j = 0; j < event.regionTriggers.size(); j++) {
				final RegionTrigger trigger = event.regionTriggers.get(j);
				
				float left = trigger.left / SCALE;
				float top = trigger.top / SCALE;
				float right = trigger.right / SCALE;
				float bottom = trigger.bottom / SCALE;
				
				if (s == null) {
					s = new Sprite(Viewport.DefaultViewport, 
							(trigger.left + trigger.right) / 2 + offset.getX(), 
							(trigger.top + trigger.bottom) / 2 + offset.getY(), 
							trigger.right - trigger.left,
							trigger.bottom - trigger.top 
							);
					s.centerOrigin();
					s.getBitmap().eraseColor(Color.BLACK);
					s.setZ(-10);
					s.setVisible(false);
				}
				s.setX((trigger.left + trigger.right) / 2 + offset.getX());
				s.setY((trigger.top + trigger.bottom) / 2 + offset.getY());
				
				world.QueryAABB(new QueryCallback() {
					@Override
					public boolean reportFixture(Fixture fixture) {
						if (actorMap.containsKey(fixture)) {
							PlatformBody actor = actorMap.get(fixture);
							int index = -1;
							boolean inRegion = inRegion(actor, trigger);
							for (int k = 0; k < trigger.contacts.size(); k++) {
								if (trigger.contacts.get(k).object == actor) index = k;
							}
							if (index < 0) {
								int state = inRegion ? 5 : 2;
								trigger.contacts.add(new RegionTrigger.Contact(actor, state));
							} else {
								RegionTrigger.Contact contact = trigger.contacts.get(index);
								if (inRegion) {
									if (contact.state == 3) {
										contact.state = 4; //staying inside
									} else if (contact.state < 4) {
										contact.state = 5; //just fully entered
									}
								} else {
									if (contact.state == 0 || contact.state > 2)
										contact.state = 1; //staying touching or poked out
								}
							}
						}
						return true;
					}
				}, left, top, right, bottom);
				
				for (int k = 0; k < trigger.contacts.size(); k++) {
					RegionTrigger.Contact contact = trigger.contacts.get(k);

					//Game.debug(contact.state);
					if (trigger.mode == contact.state) {
						if ((trigger.onlyHero && ((PlatformBody)contact.object).isHero())
								|| !trigger.onlyHero) {
							triggered = true;
						}
					}
					
					contact.state--;
					if (contact.state < 0) {
						trigger.contacts.remove(k);
						k--;
					}
				}
			}
			
			if (triggered) {
				//Game.debug("TRIGGER");
				interpreter.doEvent(event);
			}
		}
	}
	Sprite s;
	
	private RectF regionRect = new RectF();
	private boolean inRegion(PlatformBody body, RegionTrigger trigger) {
		regionRect.set(trigger.left, trigger.top, trigger.right, trigger.bottom);
		regionRect.offset(offset.getX(), offset.getY());
		return regionRect.contains(body.getSprite().getRect());
	}
	
	private void checkBehaviors() {
		for (int i = 0; i < actors.size(); i++) {
			PlatformBody bodyA = actors.get(i);
			if (bodyA != null) {
				for (int j = 0; j < bodyA.getCollidedActors().size(); j++) {
					PlatformBody bodyB = bodyA.getCollidedActors().get(j);
					
					int dir = PlatformBody.getCollisionDirection(bodyA, bodyB);
					if (bodyB.isHero())
						bodyA.doBehaviorCollideHero(dir, bodyB);
					else
						bodyA.doBehavoirCollideActor(dir, bodyB);
				}
				if (bodyA.isCollidedWall()) {
					bodyA.doBehaviorWall();
				}
				if (bodyA.getDirectionX() != 0) {
					float y = bodyA.getPosition().y + (bodyA.getSprite().getHeight() / 2 + 5) / SCALE;
					float x = bodyA.getPosition().x + (bodyA.getSprite().getWidth() / 2 + 5) * bodyA.getDirectionX() / SCALE;
					FixtureCallback callback = new FixtureCallback(levelMap.keySet());
					world.QueryAABB(callback, x, y, x + 3 / SCALE, y + 3 / SCALE);
					if (!callback.contact && bodyA.isGrounded()) {
						bodyA.doBehaviorEdge();
					}
				}
			}
		}
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
			if (actors.get(i) != null) {
				actors.get(i).updateSprite(offset);
			}
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

	private PlatformBody addActor(PlatformActor actor, int id, float startX, float startY) {
		return addActor(actor, id, startX, startY, 1, false);
	}

	private PlatformBody addActor(PlatformActor actor, int id, float startX, float startY, int startDir) {
		return addActor(actor, id, startX, startY, startDir, false);
	}

	private PlatformBody addActor(PlatformActor actor, int id, float startX, float startY, 
			int startDir, boolean isHero) {
		if (id < 0) {
			id = actors.size();
			actors.add(null);
		}
		PlatformBody body = new PlatformBody(Viewport.DefaultViewport, world, actor, id,
				startX, startY, startDir, isHero, actors);
		actors.set(id, body);

		for (int i = 0; i < body.getBody().getFixtureList().size(); i++) {
			actorMap.put(body.getBody().getFixtureList().get(i), body);
		}
		return body;
	}

	private void removeActor(PlatformBody body) {
		for (int i = 0; i < body.getBody().getFixtureList().size(); i++) {
			actorMap.remove(body.getBody().getFixtureList().get(i));
		}
		body.dispose();
	}

	private void doContact(QueuedContact contact) {
		doContact(contact, false);
	}

	private void doContact(QueuedContact contact, boolean anti) {
		Fixture fixtureA = anti ? contact.fixtureB : contact.fixtureA;
		Fixture fixtureB = anti ? contact.fixtureA : contact.fixtureB;

		if (actorMap.containsKey(fixtureA)) {

			PlatformBody bodyA = actorMap.get(fixtureA);

			if (actorMap.containsKey(fixtureB)) {
				PlatformBody bodyB = actorMap.get(fixtureB);
				if (!bodyA.getTouchingActors().contains(bodyB)) {
					bodyA.getCollidedActors().add(bodyB);
					bodyA.getTouchingActors().add(bodyB);
				}
			} else if (levelMap.containsKey(fixtureB)) {
				Sprite spriteB = levelMap.get(fixtureB);

				RectF rectA = bodyA.getSprite().getRect();
				RectF rectB = spriteB.getRect();
				float nx = rectB.centerX() - rectA.centerX();
				float ny = rectB.centerY() - rectA.centerY();

				if (Math.abs(nx) > Math.abs(ny)) {
					if (!bodyA.getTouchingWalls().contains(fixtureB)) {
						bodyA.setCollidedWall(true);
						bodyA.getTouchingWalls().add(fixtureB);
					}
				} else if (ny > 0) {
					if (!bodyA.getTouchingFloors().contains(fixtureB)) {
						bodyA.getTouchingFloors().add(fixtureB);
					}
				}
				
				bodyA.setOnLadder(false);
			}

		}

		if (!anti) {
			doContact(contact, true);
		}
	}
	
	private void doEndContact(Fixture fixtureA, Fixture fixtureB) {
		doEndContact(fixtureA, fixtureB, false);
	}
	
	private void doEndContact(Fixture fixtureA, Fixture fixtureB, boolean anti) {
		if (actorMap.containsKey(fixtureA)) {
			PlatformBody bodyA = actorMap.get(fixtureA);
			if (actorMap.containsKey(fixtureB)) {
				PlatformBody bodyB = actorMap.get(fixtureB);
				bodyA.getTouchingActors().remove(bodyB);
			} else if (levelMap.containsKey(fixtureB)) {
				if (bodyA.getTouchingWalls().contains(fixtureB)) {
					bodyA.getTouchingWalls().remove(fixtureB);
				}
				if (bodyA.getTouchingFloors().contains(fixtureB)) {
					bodyA.getTouchingFloors().remove(fixtureB);
				}
			}
		}
		if (!anti) {
			doEndContact(fixtureB, fixtureA, true);
		}
	}

	public static class ActorAddable {
		public PlatformActor actor;
		public float startX, startY;
		public int startDir;

		public ActorAddable(PlatformActor actor, float startX, float startY) {
			this(actor, startX, startY, 1);
		}

		public ActorAddable(PlatformActor actor, float startX, float startY, int startDir) {
			this.actor = actor;
			this.startX = startX;
			this.startY = startY;
			this.startDir = startDir;
		}
	}

	private static class QueuedContact {
		public Fixture fixtureA, fixtureB;

		public QueuedContact(Fixture fixtureA, Fixture fixtureB) {
			this.fixtureA = fixtureA;
			this.fixtureB = fixtureB;
		}
	}
	
	private static class FixtureCallback implements QueryCallback {
		public boolean contact = false;
		public Set<Fixture> fixtures;
		
		public FixtureCallback(Set<Fixture> fixtures) {
			this.fixtures = fixtures;
		}
		
		@Override
		public boolean reportFixture(Fixture fixture) {
			if (fixtures.contains(fixture)) {
				contact = true;
			}
			return true;
		}
	}
}
