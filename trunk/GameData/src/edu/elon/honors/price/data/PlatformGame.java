package edu.elon.honors.price.data;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import android.graphics.Rect;

public class PlatformGame extends GameData {
	private static final long serialVersionUID = 3L;

	private transient Rect mapRect = new Rect();

	protected int _VERSION_ = Upgrader.LATEST_VERSION;

	public ArrayList<Map> maps;
	public int selectedMapId;

	public UILayout uiLayout;

	public Tileset[] tilesets;
	public ActorClass[] actors;
	public ObjectClass[] objects;
	public Hero getHero() { return (Hero)actors[0]; }

	public String[] switchNames;
	public boolean[] switchValues;
	public String[] variableNames;
	public int[] variableValues;
	
	public LinkedList<Behavior> mapBehaviors, 
	actorBehaviors, objectBehaviors;

	public Object copyData;
	
	public final String ID;

	public int getVersion() {
		return _VERSION_;
	}
	
	public LinkedList<Behavior> getBehaviors(BehaviorType type) {
		switch (type) {
		case Map:  return mapBehaviors;
		case Actor: return actorBehaviors;
		case Object: return objectBehaviors;
		}
		return null;
	}
	
	public PlatformGame(String id) {
		ID = id;
		
		switchNames = new String[] { };
		switchValues = new boolean[] { };
		resizeSwitches(100);

		variableNames = new String[] { };
		variableValues = new int[] { };
		resizeVariables(100);

		tilesets = new Tileset[3];
		tilesets[0] = new Tileset("Default", "StickTiles.png", 64, 64, 8, 8);
		//tilesets[1] = new Tileset("Ice", "ice.png", 48, 48, 8, 8);
		tilesets[1] = new Tileset("Grass", "grass.png", 64, 64, 8, 8);
		
		maps = new ArrayList<Map>();
		maps.add(new Map(this));
		selectedMapId = 0;


		actors = new ActorClass[3];
		actors[1] = new ActorClass();
		ActorClass ghost = actors[1];
		ghost.imageName = "ghost.png";
		ghost.speed = 1.5f;
		ghost.jumpVelocity = 4f;
		ghost.edgeBehavior = ActorClass.BEHAVIOR_TURN;
		ghost.wallBehavior = ActorClass.BEHAVIOR_JUMP_TURN;
		ghost.actorContactBehaviors = new int[] {ActorClass.BEHAVIOR_NONE, 
				ActorClass.BEHAVIOR_NONE, ActorClass.BEHAVIOR_TURN, ActorClass.BEHAVIOR_TURN};
		ghost.heroContactBehaviors[ActorClass.ABOVE] = ActorClass.BEHAVIOR_DIE;
		ghost.name = "Ghost";

		ActorClass vlad = new ActorClass();
		vlad.imageName = "StickMan.png";
		vlad.speed = 1f;
		vlad.edgeBehavior = ActorClass.BEHAVIOR_TURN;
		vlad.wallBehavior = ActorClass.BEHAVIOR_TURN;
		vlad.actorContactBehaviors = new int[] {ActorClass.BEHAVIOR_NONE, 
				ActorClass.BEHAVIOR_NONE, ActorClass.BEHAVIOR_TURN, ActorClass.BEHAVIOR_TURN};
		vlad.heroContactBehaviors[ActorClass.ABOVE] = ActorClass.BEHAVIOR_DIE;
		vlad.name = "Stick Man";
		actors[2] = vlad;

		Hero hero = new Hero();
		hero.speed = 3.5f;
		hero.jumpVelocity = 6f;
		hero.stunDuration = 600;
		hero.imageName = "hero.png";
		hero.name = "Hero";
		hero.actorContactBehaviors[ActorClass.BELOW] = ActorClass.BEHAVIOR_JUMP;
		hero.actorContactBehaviors[ActorClass.LEFT] = ActorClass.BEHAVIOR_STUN;
		hero.actorContactBehaviors[ActorClass.RIGHT] = ActorClass.BEHAVIOR_STUN;
		actors[0] = hero;

		objects = new ObjectClass[2];


		for (int i = 0; i < 1; i++) {
			objects[i * 2] = new ObjectClass();
			objects[i * 2].name = "Rock";
			objects[i * 2].imageName = "rock.png";
			objects[i * 2].zoom = 0.5f;

			objects[i * 2 + 1] = new ObjectClass();
			objects[i * 2 + 1].name = "Triangle";
			objects[i * 2 + 1].imageName = "triangle.png";
			objects[i * 2 + 1].zoom = 1;
		}

		uiLayout = new UILayout();
		
		mapBehaviors = new LinkedList<Behavior>();
		actorBehaviors = new LinkedList<Behavior>();
		objectBehaviors = new LinkedList<Behavior>();
	}

	private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException {
		//long time = System.currentTimeMillis();
		in.defaultReadObject();
		Upgrader.upgrade(this);
		//time = System.currentTimeMillis() - time;
		//Debug.write("Read in " + time + "ms");
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		//long time = System.currentTimeMillis();
		out.defaultWriteObject();
		//time = System.currentTimeMillis() - time;
		//Debug.write("Written in " + time + "ms");
	}

	public Map getSelectedMap() {
		return maps.get(selectedMapId);
	}

	public Tileset getMapTileset(Map map) {
		return tilesets[map.tilesetId];
	}

	public int getMapWidth(Map map) {
		return map.columns * getMapTileset(map).tileWidth;
	}

	public int getMapHeight(Map map) {
		return map.rows * getMapTileset(map).tileHeight;
	}

	public Rect getMapRect(Map map) {
		mapRect.set(0, 0, getMapWidth(map), getMapHeight(map));
		return mapRect;
	}
	
	public void resizeSwitches(int newSize) {
		switchNames = Arrays.copyOf(switchNames, newSize);
		switchValues = Arrays.copyOf(switchValues, newSize);
		for (int i = 0; i < switchNames.length; i++) {
			if (switchNames[i] == null) {
				switchNames[i] = String.format("Switch%03d", i);
			}
		}

		//TODO: Important stuff for when variables no longer exist!
	}

	public void resizeVariables(int newSize) {
		variableNames = Arrays.copyOf(variableNames, newSize);
		variableValues = Arrays.copyOf(variableValues, newSize);
		for (int i = 0; i < variableNames.length; i++) {
			if (variableNames[i] == null) {
				variableNames[i] = String.format("Variable%03d", i);
			}
		}

		//TODO: Important stuff for when variables no longer exist!
	}
}
