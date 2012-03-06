package com.twp.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.twp.platform.PlatformBody.DisposeCallback;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.ActorTrigger;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.RegionTrigger;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.Event.UITrigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.data.ActionIds;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.data.UILayout;
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

public class PlatformLogic implements Logic {

	public static final float GRAVITY = 10f;
	private static final int BORDER = 130;
	public static final float SCALE = 50;
	public static final int TRANS_GROUND = 215;

	private boolean test = false;

	Map map;
	PlatformGame game;
	ArrayList<ActorBody> actorBodies = new ArrayList<ActorBody>();
	ArrayList<ObjectBody> objectBodies = new ArrayList<ObjectBody>();
	ArrayList<PlatformBody> platformBodies = new ArrayList<PlatformBody>();

	BackgroundSprite background, sky;
	Tilemap[] layers;
	LinkedList<JoyStick> joysticks = new LinkedList<JoyStick>();
	LinkedList<Button> buttons = new LinkedList<Button>();
	AnimatedSprite hero;
	Vector p = new Vector(), joystickPull = new Vector();
	private World world;
	private ActorBody heroBody;
	private Vector offset;

	private Vector2 antiGravity = new Vector2(0, -GRAVITY), zeroVector = new Vector2();

	private ArrayList<ActorBody> toRemove = new ArrayList<ActorBody>();
	private ArrayList<ActorAddable> toAdd = new ArrayList<ActorAddable>();
	private ArrayList<QueuedContact> contacts = new ArrayList<PlatformLogic.QueuedContact>();

	private HashMap<Fixture, PlatformBody> bodyMap = new HashMap<Fixture, PlatformBody>();
	private HashMap<Fixture, Sprite> levelMap = new HashMap<Fixture, Sprite>();

	private Interpreter interpreter;

	private float mapX, mapY, bgX, bgY, skyScroll;
	private int skyStartY, startOceanY;

	private boolean paused;

	private String mapName;

	public ActorBody getHero() {
		return heroBody;
	}

	public PlatformGame getGame() {
		return game;
	}

	public Map getMap() {
		return map;
	}

	@Override
	public void setPaused(boolean paused) {
		paused = true;
	}

	public PlatformLogic(String mapName, Game host) {
		this.mapName = mapName;
		//this.test = mapName.equals("final-Map_1");
		this.interpreter = new Interpreter(this);
		paused = true;
	}

	@Override
	public void initialize() {
		Globals.setInstance(new Globals());

		if (game == null) game = new PlatformGame();
		map = game.maps.get(game.startMapId);

		Bitmap bmp = Data.loadBackground(map.groundImageName);
		startOceanY = map.groundY - TRANS_GROUND;
		background = new BackgroundSprite(bmp, new Rect(0, startOceanY, 
				Graphics.getWidth(), Graphics.getHeight()), -5);

		bmp = Data.loadBackground(map.skyImageName);
		Game.debug("%dx%d", bmp.getWidth(), bmp.getHeight());
		skyStartY = map.groundY - Graphics.getHeight();
		sky = new BackgroundSprite(bmp, new Rect(0, skyStartY, Graphics.getWidth(), map.groundY), -6);
		sky.scroll(0, Graphics.getHeight() - bmp.getHeight());

		for (int i = 0; i < game.uiLayout.buttons.size(); i++) {
			UILayout.Button button = game.uiLayout.buttons.get(i);
			int x = button.x >= 0 ? button.x : Graphics.getWidth() + button.x;
			int y = button.y >= 0 ? button.y : Graphics.getHeight() + button.y;
			buttons.add(new Button(x, y, 10, 
					button.radius, button.color));
		}
		for (int i = 0; i < game.uiLayout.joysticks.size(); i++) {
			UILayout.JoyStick joystick = game.uiLayout.joysticks.get(i);
			int x = joystick.x >= 0 ? joystick.x : Graphics.getWidth() + joystick.x;
			int y = joystick.y >= 0 ? joystick.y : Graphics.getHeight() + joystick.y;
			joysticks.add(new JoyStick(x, y, 10, 
					joystick.radius, joystick.color));
		}

		offset = new Vector();

		while (actorBodies.size() < map.actors.size()) actorBodies.add(null);
		while (objectBodies.size() < map.objects.size()) objectBodies.add(null);

		initPhysics();


		Viewport.DefaultViewport.setZ(3);

		update(1);
	}

	private void initPhysics() {
		world = new World(new Vector2(0, GRAVITY), true);

		ContactFilter filter = new ContactFilter() {

			@Override
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
				if (!(bodyMap.containsKey(fixtureA) && bodyMap.containsKey(fixtureB)))
					return true;

				PlatformBody bodyA = bodyMap.get(fixtureA);
				PlatformBody bodyB = bodyMap.get(fixtureB);

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
			MapLayer layer = map.layers[i];
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

		MapLayer actorLayer = map.actorLayer;
		for (int i = 0; i < actorLayer.rows; i++) {
			for (int j = 0; j < actorLayer.columns; j++) {
				float x = (j + 0.5f) * game.getMapTileset(map).tileWidth;
				float y = (i + 0.5f) * game.getMapTileset(map).tileHeight;
				int instanceId = actorLayer.tiles[i][j];
				if (instanceId >= 0) {
					ActorInstance instance = this.map.actors.get(instanceId);
					int actorId = instance.classIndex;
					if (actorId > 0) {
						addActorBody(game.actors[actorId], instanceId, x, y);
					} else {
						if (test)
							game.hero.actorContactBehaviors = new int[4];
						game.hero.zoom = 0.9f;
						game.hero.name = "Hero";
						heroBody = addActorBody(game.hero, 
								instanceId,
								x,
								y, 1, true);
						hero = heroBody.getSprite();
					}
				}

			}
		}

		for (int i = 0; i < map.objects.size(); i++) {
			ObjectInstance instance = map.objects.get(i);
			if (instance != null) {
				float x = instance.startX;
				float y = instance.startY;
				int instanceId = instance.id;
				int objectId = instance.classIndex;
				addObjectBody(game.objects[objectId], instanceId, x, y);
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

		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).update();
		}
		for (int i = 0; i < joysticks.size(); i++) {
			joysticks.get(i).update();
		}

		if (!heroBody.isStunned()) {
			for (int i = 0; i < buttons.size(); i++) {
				if (game.uiLayout.buttons.get(i).defaultAction &&
						buttons.get(i).isTapped()) {
					heroBody.jump(true);
				}
			}

			boolean joyFound = false;
			joystickPull.set(0, 0);
			for (int i = 0; i < joysticks.size(); i++) {
				if (game.uiLayout.joysticks.get(i).defaultAction) {
					joystickPull.set(joysticks.get(i).getPull());
					joyFound = true;
				}
			}

			if (heroBody.isOnLadder()) {
				heroBody.setVelocityY(joystickPull.getY() * heroBody.getActor().speed);
				antiGravity.set(0, -GRAVITY * heroBody.getBody().getMass());
				heroBody.getBody().applyForce(antiGravity, zeroVector);
			}
			else {
				if (joyFound) {
					heroBody.setVelocityX(joystickPull.getX() * heroBody.getActor().speed);
				}
			}
		}

		for (int i = 0; i < platformBodies.size(); i++) {
			if (platformBodies.get(i) != null) {
				platformBodies.get(i).update(timeElapsed, offset);
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
			removePlatformBody(toRemove.get(i));
		}
		toRemove.clear();
		for (int i = 0; i < toAdd.size(); i++) {
			addActorBody(toAdd.get(i));
		}
		toAdd.clear();

		interpreter.update();

		updateScroll();
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
		game = (PlatformGame) Data.loadGame(mapName, Game.getCurrentGame());
	}

	public ActorBody getActorBodyFromId(int id) {
		if (id < 0 || id >= actorBodies.size()) {
			return null;
		}
		return actorBodies.get(id);
	}

	public ObjectBody getObjectBodyFromId(int id) {
		if (id < 0 || id >= objectBodies.size()) {
			return null;
		}
		return objectBodies.get(id);
	}

	public void queueActor(ActorAddable actor) {
		toAdd.add(actor);
	}

	private void checkTriggers() {
		for (int i = 0; i < map.events.length; i++) {
			Event event = map.events[i];
			event.clearTriggerInfo();
			boolean triggered = false;

			for (int j = 0; j < event.triggers.size(); j++) {
				Trigger generic = event.triggers.get(j);

				if (generic instanceof SwitchTrigger) {
					SwitchTrigger trigger = (SwitchTrigger)generic;
					triggered |= Globals.getSwitches()[trigger.switchId] == trigger.value;
				}


				if (generic instanceof VariableTrigger) {
					VariableTrigger trigger = (VariableTrigger)generic;

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

				if (generic instanceof ActorTrigger) {
					ActorTrigger trigger = (ActorTrigger)generic;

					for (int k = 0; k < actorBodies.size(); k++) {
						ActorBody body = actorBodies.get(k);

						if (body == null) continue;
						if (!(body instanceof ActorBody)) continue;
						if (trigger.forInstance && k != trigger.id) continue;
						if (!trigger.forInstance && body.getActor() != game.actors[trigger.id]) continue;

						if (trigger.action == ActorTrigger.ACTION_COLLIDES_HERO) {
							for (int l = 0; l < body.getCollidedBodies().size(); l++) {
								PlatformBody collided = body.getCollidedBodies().get(l);
								if (collided instanceof ActorBody) {
									triggered |= ((ActorBody)collided).isHero();
								}
							}
						}
						if (trigger.action == ActorTrigger.ACTION_COLLIDES_ACTOR) {
							triggered |= body.getCollidedBodies().size() > 0;
						}
						if (trigger.action == ActorTrigger.ACTION_COLLIDES_WALL) {
							triggered |= body.isCollidedWall();
						}
					}
				}

				if (generic instanceof RegionTrigger) {
					final RegionTrigger trigger = (RegionTrigger)generic;

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

					if (trigger.contacts == null) {
						trigger.contacts = new ArrayList<Event.RegionTrigger.Contact>();
					}
					for (int k = 0; k < trigger.contacts.size(); k++) {
						trigger.contacts.get(k).event = -1;
						trigger.contacts.get(k).checked = false;
					}
					world.QueryAABB(new QueryCallback() {
						@Override
						public boolean reportFixture(Fixture fixture) {
							if (bodyMap.containsKey(fixture)) {
								PlatformBody body = bodyMap.get(fixture);
								int index = -1;
								boolean inRegion = inRegion(body, trigger);
								for (int k = 0; k < trigger.contacts.size(); k++) {
									if (trigger.contacts.get(k).object == body) index = k;
								}
								if (index < 0) {
									int state = inRegion ? RegionTrigger.Contact.STATE_CONTAINED :
										RegionTrigger.Contact.STATE_TOUCHING;
									int event = inRegion ? RegionTrigger.MODE_CONTAIN :
										RegionTrigger.MODE_TOUCH;
									trigger.contacts.add(new RegionTrigger.Contact(body, state, event));
								} else {
									RegionTrigger.Contact contact = trigger.contacts.get(index);
									if (inRegion) {
										int newState = RegionTrigger.Contact.STATE_CONTAINED;
										if (contact.state != newState) {
											contact.event = RegionTrigger.MODE_CONTAIN;
											contact.state = newState;
										}
									} else {
										int newState = RegionTrigger.Contact.STATE_TOUCHING;
										if (contact.state != newState) {
											contact.event = 
												contact.state == RegionTrigger.Contact.STATE_CONTAINED ?
														RegionTrigger.MODE_LOSE_CONTAIN :
															RegionTrigger.MODE_TOUCH;
											contact.state = newState;
										}
									}
									contact.checked = true;
								}
							}
							return true;
						}
					}, left, top, right, bottom);

					for (int k = 0; k < trigger.contacts.size(); k++) {
						RegionTrigger.Contact contact = trigger.contacts.get(k);

						int contactEvent = contact.event;

						if (!contact.checked) {
							contactEvent = RegionTrigger.MODE_LOSE_TOUCH;
							trigger.contacts.remove(k);
							k--;
						}

						if (trigger.mode == contactEvent) {
							if (contact.object instanceof ActorBody) {
								ActorBody body = (ActorBody)contact.object;
								if (trigger.who == RegionTrigger.WHO_ACTOR) {
									triggered = true;
									event.tActor = body;
								} else if (trigger.who == RegionTrigger.WHO_HERO &&
										body.isHero()) {
									triggered = true;
									event.tActor = body;
								}
							} else {
								if (trigger.who == RegionTrigger.WHO_OBJECT) {
									ObjectBody body = (ObjectBody)contact.object;
									triggered = true;
									event.tObject = body;
								}
							}
						}


					}
				}
				else if (generic instanceof UITrigger) {
					UITrigger trigger = (UITrigger)generic;
					if (trigger.controlType == UITrigger.CONTROL_BUTTON) {
						Button button = buttons.get(trigger.index);
						if (button.isTapped() && trigger.condition == 
							UITrigger.CONDITION_PRESS) {
							triggered = true;
						}
						if (button.isReleased() && trigger.condition == 
							UITrigger.CONDITION_RELEASE) {
							triggered = true;
						}
					} else if (trigger.controlType == UITrigger.CONTROL_JOY) {
						JoyStick joy = joysticks.get(trigger.index);
						if (joy.isTapped() && trigger.condition == 
							UITrigger.CONDITION_PRESS) {
							triggered = true;
						}
						if (joy.isReleased() && trigger.condition == 
							UITrigger.CONDITION_RELEASE) {
							triggered = true;
						}
						if (joy.isPressed() && trigger.condition == 
							UITrigger.CONDITION_MOVE) {
							triggered = true;
						}
						if (triggered) {
							event.tVector = joy.getPull();
						}
					} else {
						if (Input.isTapped()) {
							int pid = Input.getTappedPointer();
							boolean inUse = false;
							for (int k = 0; k < buttons.size(); k++) {
								if (buttons.get(k).getPID() == pid) {
									inUse = true;
								}
							}
							for (int k = 0; k < joysticks.size(); k++) {
								if (joysticks.get(k).getPID() == pid) {
									inUse = true;
								}
							}
							if (!inUse) {
								touchPID = pid;
								if (trigger.condition == UITrigger.CONDITION_PRESS) {
									triggered = true;	
								}
							}
						}
						if (touchPID >= 0) {
							if (Input.isTouchDown(touchPID)) {
								if (trigger.condition == UITrigger.CONDITION_MOVE) {
									triggered = true;
								}
							} else {
								if (trigger.condition == UITrigger.CONDITION_RELEASE) {
									triggered = true;	
								}
								touchPID = -1;
							}
						}
					}
				}

				if (triggered) {
					break;
				}
			}
			if (triggered) {
				//Game.debug("TRIGGER");
				interpreter.doEvent(event);
			}
		}
	}
	Sprite s;
	private int touchPID = -1;

	private RectF regionRect = new RectF();
	private boolean inRegion(PlatformBody body, RegionTrigger trigger) {
		regionRect.set(trigger.left, trigger.top, trigger.right, trigger.bottom);
		regionRect.offset(offset.getX(), offset.getY());
		return regionRect.contains(body.getSprite().getRect());
	}

	private void checkBehaviors() {
		for (int i = 0; i < actorBodies.size(); i++) {
			ActorBody bodyA = actorBodies.get(i);
			if (bodyA != null) {
				for (int j = 0; j < bodyA.getCollidedBodies().size(); j++) {
					if (bodyA.getCollidedBodies().get(j) instanceof ActorBody)
					{
						ActorBody bodyB = (ActorBody)bodyA.getCollidedBodies().get(j);

						//Game.debug("%s v %s", bodyA.getActor().name, bodyB.getActor().name);

						int dir = ActorBody.getCollisionDirection(bodyA, bodyB);
						if (bodyB.isHero())
							bodyA.doBehaviorCollideHero(dir, bodyB);
						else
							bodyA.doBehavoirCollideActor(dir, bodyB);
					}
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

		for (int i = 0; i < platformBodies.size(); i++) {
			if (platformBodies.get(i) != null) {
				platformBodies.get(i).updateSprite(offset);
			}
		}

		p.multiply(-1);

		for (int i = 0; i < layers.length; i++)
			layers[i].scroll(p.getX(), p.getY());

		mapX += p.getX(); mapY += p.getY();
		mapY = Math.min(0, mapY);

		//p.multiply(0.7f);
		p.setX(p.getX() * 0.7f);

		bgX += p.getX(); bgY += p.getY();
		bgY = Math.min(0, bgY);

		background.scroll(p.getX(), 0);
		background.setY(startOceanY - bgY);

		sky.setY(Math.min(0, skyStartY - bgY));
		float scroll = Math.min(0, sky.getY() + bgY);
		sky.scroll(p.getX(), scroll - skyScroll);
		skyScroll = scroll;
	}

	public ObjectBody addObjectBody(ObjectAddable object) {
		return addObjectBody(object.object, -1, object.startX, object.startY);
	}

	private ObjectBody addObjectBody(ObjectClass object, int id, float startX, float startY) {
		if (id < 0) {
			id = objectBodies.size();
			objectBodies.add(null);
		}
		ObjectBody body = new ObjectBody(Viewport.DefaultViewport, world, 
				object, id, startX, startY, new DisposeCallback() {
			@Override
			public void onDispose(PlatformBody body) {
				objectBodies.set(body.id, null);
				platformBodies.remove(body);
			}
		});
		while (objectBodies.size() < id + 1) objectBodies.add(null);
		objectBodies.set(id, body);
		platformBodies.add(body);

		for (int i = 0; i < body.getBody().getFixtureList().size(); i++) {
			bodyMap.put(body.getBody().getFixtureList().get(i), body);
		}

		return body;
	}

	public  ActorBody addActorBody(ActorAddable actor) {
		return addActorBody(actor.actor, -1, actor.startX, actor.startY, actor.startDir);
	}

	private ActorBody addActorBody(ActorClass actor, int id, float startX, float startY) {
		return addActorBody(actor, id, startX, startY, 1, false);
	}

	private ActorBody addActorBody(ActorClass actor, int id, float startX, float startY, int startDir) {
		return addActorBody(actor, id, startX, startY, startDir, false);
	}

	private ActorBody addActorBody(ActorClass actor, int id, float startX, float startY, 
			int startDir, boolean isHero) {
		if (id < 0) {
			id = actorBodies.size();
			actorBodies.add(null);
		}
		ActorBody body = new ActorBody(Viewport.DefaultViewport, world, actor, id,
				startX, startY, startDir, isHero, new PlatformBody.DisposeCallback() {
			@Override
			public void onDispose(PlatformBody body) {
				actorBodies.set(body.id, null);
				platformBodies.remove(body);
			}
		});
		actorBodies.set(id, body);
		platformBodies.add(body);

		for (int i = 0; i < body.getBody().getFixtureList().size(); i++) {
			bodyMap.put(body.getBody().getFixtureList().get(i), body);
		}
		return body;
	}

	private void removePlatformBody(PlatformBody body) {
		for (int i = 0; i < body.getBody().getFixtureList().size(); i++) {
			bodyMap.remove(body.getBody().getFixtureList().get(i));
		}
		body.dispose();
	}

	private void doContact(QueuedContact contact) {
		doContact(contact, false);
	}

	private void doContact(QueuedContact contact, boolean anti) {
		Fixture fixtureA = anti ? contact.fixtureB : contact.fixtureA;
		Fixture fixtureB = anti ? contact.fixtureA : contact.fixtureB;

		if (bodyMap.containsKey(fixtureA)) {

			PlatformBody bodyA = bodyMap.get(fixtureA);

			if (bodyMap.containsKey(fixtureB)) {
				PlatformBody bodyB = bodyMap.get(fixtureB);
				if (!bodyA.getTouchingBodies().contains(bodyB)) {
					bodyA.getCollidedBodies().add(bodyB);
					bodyA.getTouchingBodies().add(bodyB);
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

				bodyA.onTouchGround();
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
		if (bodyMap.containsKey(fixtureA)) {
			PlatformBody bodyA = bodyMap.get(fixtureA);
			if (bodyMap.containsKey(fixtureB)) {
				PlatformBody bodyB = bodyMap.get(fixtureB);
				bodyA.getTouchingBodies().remove(bodyB);
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
		public ActorClass actor;
		public float startX, startY;
		public int startDir;

		public ActorAddable(ActorClass actor, float startX, float startY) {
			this(actor, startX, startY, 1);
		}

		public ActorAddable(ActorClass actor, float startX, float startY, int startDir) {
			this.actor = actor;
			this.startX = startX;
			this.startY = startY;
			this.startDir = startDir;
		}
	}

	public static class ObjectAddable {
		public ObjectClass object;
		public float startX, startY;

		public ObjectAddable(ObjectClass object, float startX, float startY) {
			this.object = object;
			this.startX = startX;
			this.startY = startY;
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
