package com.twp.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.SparseArray;

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
import com.twp.platform.PlatformLogic.ActorAddable;
import com.twp.platform.PlatformLogic.ObjectAddable;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Vector;

public class PhysicsHandler {


	public static final float GRAVITY = 10f;
	public static final float SCALE = 50;

	private ArrayList<ActorBody> actorBodies = new ArrayList<ActorBody>();
	private ArrayList<ObjectBody> objectBodies = new ArrayList<ObjectBody>();
	private ArrayList<PlatformBody> platformBodies = new ArrayList<PlatformBody>();
	private World world;
	private ActorBody heroBody;

	private ArrayList<ActorBody> toRemove = new ArrayList<ActorBody>();
	private ArrayList<ActorAddable> toAdd = new ArrayList<ActorAddable>();
	private ArrayList<QueuedContact> contacts = new ArrayList<QueuedContact>();
	
	private Fixture rightWallFixture, leftWallFixture, topWallFixture;
	//private Body rightWallBody, leftWallBody;

	private PlatformGame game;
	private Map map;
	private Tilemap[] layers;
	
	private int nextActorId = 0, nextObjectId = 0;

	private Vector2 gravity = new Vector2();

	private HashMap<Fixture, PlatformBody> bodyMap = new HashMap<Fixture, PlatformBody>();
	private HashMap<Fixture, Sprite> levelMap = new HashMap<Fixture, Sprite>();

	private float mapFloor, mapRight, mapLeft;

	public World getWorld() {
		return world;
	}

	public PlatformGame getGame() {
		return game;
	}

	public float getMapFloorMeters() {
		return mapFloor / SCALE;
	}

	public float getMapFloorPx() {
		return mapFloor;
	}
	
	public float getMapRightPx() {
		return mapRight;
	}
	
	public float getMapLeftPx() {
		return mapLeft;
	}

	public ActorBody getHero() {
		return heroBody;
	}

	public List<PlatformBody> getPlatformBodies() {
		return platformBodies;
	}

	public PlatformBody getFixtureBody(Fixture fixture) {
		return bodyMap.get(fixture);
	}

	public Sprite getFixtureTile(Fixture fixture) {
		return levelMap.get(fixture);
	}

	public ObjectBody getLastCreatedObject() {
		if (objectBodies.size() == 0) return null;
		return objectBodies.get(objectBodies.size() - 1);
	}

	public ActorBody getLastCreatedActor() {
		if (actorBodies.size() == 0) return null;
		return actorBodies.get(actorBodies.size() - 1);
	}

	public void setGravity(Vector vector) {
		if (vector.magnitude() < 0.001) {
			gravity.set(0, 0.001f);
		} else {
			gravity.set(vector.getX(), vector.getY());
		}
		world.setGravity(gravity);
		for (PlatformBody body : platformBodies) {
			if (body != null) {
				body.getBody().setAwake(true);
			}
		}
	}

	public ActorBody getActorBodyFromId(int id) {
		for (int i = 0; i < actorBodies.size(); i++) {
			ActorBody body = actorBodies.get(i);
			if (body.id == id) return body;
		}
		return null;
	}

	public ObjectBody getObjectBodyFromId(int id) {
		for (int i = 0; i < objectBodies.size(); i++) {
			ObjectBody body = objectBodies.get(i);
			if (body.id == id) return body;
		}
		return null;
	}

	public PhysicsHandler(PlatformLogic logic) {
		this.game = logic.getGame();
		this.map = game.getSelectedMap();
		initPhysics();
	}

	public void queueActor(ActorAddable actor) {
		toAdd.add(actor);
	}

	public void destroyBody(PlatformBody body) {
		world.destroyBody(body.getBody());
		if (body instanceof ObjectBody) {
			objectBodies.remove(getObjectBodyFromId(body.id));
		} else if (body instanceof ActorBody) {
			actorBodies.remove(getActorBodyFromId(body.id));
		}
		platformBodies.remove(body);
	}

	private void initPhysics() {
		mapFloor = map.height(game);
		mapLeft = 0;
		mapRight = map.width(game);

		world = new World(new Vector2(0, GRAVITY), true);
		
		createWalls();

		ContactFilter filter = new ContactFilter() {

			@Override
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
				if (!bodyMap.containsKey(fixtureA) && !bodyMap.containsKey(fixtureB))
					return false;

				PlatformBody bodyA = bodyMap.get(fixtureA);
				PlatformBody bodyB = bodyMap.get(fixtureB);
				
				if (isBoundaryFixture(fixtureA) || isBoundaryFixture(fixtureB)) {
					return true;
				}

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

		//Coordinates from the center of the box, using Cartesian (not texture) coordinates, going clockwise
		SparseArray<double[]> shapeMap = new SparseArray<double[]>(12);

		//Ground Up 30 Left
		shapeMap.put(12, new double[] {
				-0.5, -0.5,
				0.5, 0,
				0.5, -0.5
		});

		//Ground Up 30 Right
		shapeMap.put(13, new double[] {
				-0.5, -0.5,
				-0.5, 0,
				0.5, 0.5,
				0.5, -0.5
		});

		//Ground Down 30 Left
		shapeMap.put(14, new double[] {
				-0.5, -0.5,
				-0.5, 0.5,
				0.5, 0,
				0.5, -0.5
		});

		//Ground Down 30 Right
		shapeMap.put(15, new double[] {
				-0.5, -0.5,
				-0.5, 0,
				0.5, -0.5
		});

		//Ground Up 45
		shapeMap.put(36, new double[] {
				-0.5, -0.5,
				0.5, 0.5,
				0.5, -0.5

		});
		//Ground Down 45
		shapeMap.put(37, new double[] {
				-0.5, 0.5,
				0.5, -0.5,
				-0.5, -0.5
		});

		//Roof Up 30 Left
		shapeMap.put(28, new double[] {
				-0.5, -0.5,
				-0.5, 0.5,
				0.5, 0.5,
				0.5, 0
		});

		//Roof Up 30 Right
		shapeMap.put(29, new double[] {
				-0.5, 0,
				-0.5, 0.5,
				0.5, 0.5
		});

		//Roof Down 30 Left
		shapeMap.put(30, new double[] {
				-0.5, 0.5,
				0.5, 0.5,
				0.5, 0
		});

		//Roof Down 30 Right
		shapeMap.put(31, new double[] {
				-0.5, 0,
				-0.5, 0.5,
				0.5, 0.5,
				0.5, -0.5
		});

		//Roof Up 45
		shapeMap.put(46, new double[] {
				-0.5, -0.5,
				-0.5, 0.5,
				0.5, 0.5

		});
		//Roof Down 45
		shapeMap.put(47, new double[] {
				-0.5, 0.5,
				0.5, 0.5,
				0.5, -0.5
		});

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
						if (shapeMap.get(tileId) != null) {
							double[] set = shapeMap.get(tileId);
							Vector2[] vertices = new Vector2[set.length / 2];
							for (int v = 0; v < vertices.length; v++) {
								vertices[v] = new Vector2(
										(float)set[v * 2] * width,
										(float)set[v * 2 + 1] * -height
										);
							}
							tileShape.set(vertices);
						} else {
							if (tileId == 36) {
								//Ground Up 45
								Vector2[] vertices = new Vector2[] {
										new Vector2(-width / 2, height / 2),
										new Vector2(width / 2, -height / 2),
										new Vector2(width / 2, height / 2)
								};
								tileShape.set(vertices);
							} else if (tileId == 37) {
								//Ground Down 45
								Vector2[] vertices = new Vector2[] {
										new Vector2(-width / 2, height / 2),
										new Vector2(-width / 2, -height / 2),
										new Vector2(width / 2, height / 2)
								};
								tileShape.set(vertices);
							} else if (tileId == 14) {
								//Ground Down 30 high
								Vector2[] vertices = new Vector2[] {
										new Vector2(-width / 2, height / 2),
										new Vector2(-width / 2, -height / 2),
										new Vector2(width / 2, 0),
										new Vector2(width / 2, height / 2)
								};
								tileShape.set(vertices);
							} else if (tileId == 15) {
								//Ground Down 30 low
								Vector2[] vertices = new Vector2[] {
										new Vector2(-width / 2, height / 2),
										new Vector2(-width / 2, 0),
										new Vector2(width / 2, height / 2)
								};
								tileShape.set(vertices);
							} else if (tileId == 13) {
								//Ground Up 30 high
								Vector2[] vertices = new Vector2[] {
										new Vector2(-width / 2, height / 2),
										new Vector2(-width / 2, 0),
										new Vector2(width / 2, -height / 2),
										new Vector2(width / 2, height / 2)
								};
								tileShape.set(vertices);
							} else if (tileId == 12) {
								//Ground Up 30 low
								Vector2[] vertices = new Vector2[] {
										new Vector2(-width / 2, height / 2),
										new Vector2(width / 2, 0),
										new Vector2(width / 2, height / 2)
								};
								tileShape.set(vertices);
							} else {
								tileShape.setAsBox(width / 2, height / 2);
							}
						}
						tileBody.createFixture(tileShape, 1);
						tileShape.dispose();
						for (int l = 0; l < tileBody.getFixtureList().size(); l++) {
							levelMap.put(tileBody.getFixtureList().get(l), s);
						}
					}
				}
			}
		}

		for (int i = 0; i < map.actors.size(); i++) {
			ActorInstance instance = map.actors.get(i);
			float x = (instance.column + 0.5f) * game.getMapTileset(map).tileWidth;
			float y = (instance.row + 0.5f) * game.getMapTileset(map).tileHeight;
			int instanceId = instance.id;
			int actorId = instance.classIndex;
			if (actorId > 0) {
				addActorBody(game.actors[actorId], instanceId, x, y);
			} else {
				heroBody = addActorBody(game.getHero(), 
						instanceId,
						x,
						y, 1, true);
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
	
	private boolean isBoundaryFixture(Fixture fixture) {
		return fixture == rightWallFixture || 
				fixture == leftWallFixture || 
				fixture == topWallFixture;
	}
	
	private void createWalls() {
		float thickness = 60 / SCALE;
		float width = map.width(game) / SCALE;
		float height = map.height(game) / SCALE;
		
		PolygonShape horizontalWallShape = new PolygonShape();
		horizontalWallShape.setAsBox(width / 2, thickness / 2);
		
		PolygonShape verticalWallShape = new PolygonShape();
		verticalWallShape.setAsBox(thickness / 2, height / 2);
		
		BodyDef wallDef = new BodyDef();
		wallDef.position.set(-thickness / 2, height / 2);
		wallDef.type = BodyType.StaticBody;
		Body leftWallBody = world.createBody(wallDef);
		leftWallFixture = leftWallBody.createFixture(verticalWallShape, 1);
	
		
		wallDef = new BodyDef();
		wallDef.position.set(width + thickness / 2, height / 2);
		wallDef.type = BodyType.StaticBody;
		Body rightWallBody = world.createBody(wallDef);
		rightWallFixture = rightWallBody.createFixture(verticalWallShape, 1);
		
		wallDef = new BodyDef();
		wallDef.position.set(width / 2, -thickness / 2);
		wallDef.type = BodyType.StaticBody;
		Body topWallBody = world.createBody(wallDef);
		topWallFixture = topWallBody.createFixture(horizontalWallShape, 1);
		
		horizontalWallShape.dispose();
		verticalWallShape.dispose();
		
	}
	
//	private void updateWallSprites(Vector offset) {
//	Sprite rightSprite = new Sprite(Viewport.DebugViewport, 
//			0, 0, widthPx, heightPx);
//	rightSprite.setOriginX(widthPx / 2);
//	rightSprite.setOriginY(heightPx / 2);
//	leftSprite.getBitmap().eraseColor(Color.RED);
//	levelMap.put(rightWallFixture, rightSprite);
	
//		Sprite leftSprite = levelMap.get(leftWallFixture);
//		Sprite rightSprite = levelMap.get(rightWallFixture);
//		
//		
//		
//		float y = Graphics.getHeight() / 2;
//		leftSprite.setY(y);
//		rightSprite.setY(y);
//		leftSprite.setX(offset.getX() - leftSprite.getWidth() / 2);
//		rightSprite.setX(offset.getX() + map.width(game) + rightSprite.getWidth() / 2);
//		
//		sprite.setX(body.getPosition().x * SCALE + offset.getX());
//		sprite.setY(body.getPosition().y * SCALE + offset.getY());
//	}

	public void update(long timeElapsed, Vector offset) {
		
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
	}

	public void addAndRemove() {
		for (int i = 0; i < toRemove.size(); i++) {
			removePlatformBody(toRemove.get(i));
		}
		toRemove.clear();
		for (int i = 0; i < toAdd.size(); i++) {
			addActorBody(toAdd.get(i));
		}
		toAdd.clear();
	}

	public void checkBehaviors() {
		for (int i = 0; i < actorBodies.size(); i++) {
			ActorBody body = actorBodies.get(i);
			if (body != null) {
				body.checkBehavior();
			}
		}
	}

	public void updateScroll(Vector offset) {
		for (int i = 0; i < platformBodies.size(); i++) {
			if (platformBodies.get(i) != null) {
				platformBodies.get(i).updateSprite(offset);
			}
		}
		for (int i = 0; i < layers.length; i++) {
			layers[i].setScrollX(-offset.getX());
			layers[i].setScrollY(-offset.getY());
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

	public ObjectBody addObjectBody(ObjectAddable object) {
		return addObjectBody(object.object, -1, object.startX, object.startY);
	}

	private ObjectBody addObjectBody(ObjectClass object, int id, float startX, float startY) {
		if (id < 0) {
			id = nextObjectId;
		}
		ObjectBody body = new ObjectBody(Viewport.DefaultViewport, this, 
				object, id, startX, startY);
		objectBodies.add(body);
		platformBodies.add(body);
		nextObjectId = Math.max(id, nextObjectId) + 1;

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
			id = nextActorId;
		}
		
		ActorBody body = new ActorBody(Viewport.DefaultViewport, this, actor, id,
				startX, startY, startDir, isHero);
		actorBodies.add(body);
		platformBodies.add(body);
		
		nextActorId = Math.max(id, nextActorId) + 1;

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
			} else if (levelMap.containsKey(fixtureB) || isBoundaryFixture(fixtureB)) {
				RectF rectA = bodyA.getSprite().getRect();
				
				boolean wall, floor;
				if (isBoundaryFixture(fixtureB)) {
					wall = fixtureB == rightWallFixture || fixtureB == leftWallFixture;
					floor = false;
				} else {
					Sprite spriteB = levelMap.get(fixtureB);
					RectF rectB = spriteB.getRect();
					
					float nx = rectB.centerX() - rectA.centerX();
					float ny = rectB.centerY() - rectA.centerY();
					
					wall = Math.abs(nx) > Math.abs(ny);
					floor = !wall && ny > 0;
				}


				if (wall) {
					if (!bodyA.getTouchingWalls().contains(fixtureB)) {
						bodyA.setCollidedWall(true);
						bodyA.getTouchingWalls().add(fixtureB);
					}
				} else if (floor) {
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

	public void regionCallback(final BodyCallback callback, float left, 
			float top, float right, float bottom) {
		world.QueryAABB(new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fixture) {
				if (bodyMap.containsKey(fixture)) {
					return callback.doCallback(bodyMap.get(fixture));
				}
				return true;
			}
		}, left, top, right, bottom);
	}

	public static abstract class BodyCallback {
		public abstract boolean doCallback(PlatformBody body);
	}

	private static class QueuedContact {
		public Fixture fixtureA, fixtureB;

		public QueuedContact(Fixture fixtureA, Fixture fixtureB) {
			this.fixtureA = fixtureA;
			this.fixtureB = fixtureB;
		}
	}
}

