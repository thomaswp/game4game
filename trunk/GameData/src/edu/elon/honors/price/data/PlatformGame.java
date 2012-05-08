package edu.elon.honors.price.data;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Rect;

public class PlatformGame implements Serializable {
	private static final long serialVersionUID = 3L;

	private transient Rect mapRect = new Rect();

	protected int _VERSION_ = 2;

	public ArrayList<Map> maps;
	public int startMapId;

	public UILayout uiLayout;

	public Tileset[] tilesets;
	public ActorClass[] actors;
	public ObjectClass[] objects;
	public Hero hero;

	public String[] switchNames;
	public boolean[] switchValues;
	public String[] variableNames;
	public int[] variableValues;
	

	public Object copyData;

	public int getVersion() {
		return _VERSION_;
	}
	
	public PlatformGame() {
		switchNames = new String[] { };
		switchValues = new boolean[] { };
		resizeSwitches(100);

		variableNames = new String[] { };
		variableValues = new int[] { };
		resizeVariables(100);

		maps = new ArrayList<Map>();
		maps.add(new Map());
		startMapId = 0;

		tilesets = new Tileset[3];
		tilesets[0] = new Tileset("Default", "tiles.png", 48, 48, 8, 8);
		tilesets[1] = new Tileset("Ice", "ice.png", 48, 48, 8, 8);
		tilesets[2] = new Tileset("Grass", "grass.png", 48, 48, 8, 8);

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
		vlad.imageName = "vamp.png";
		vlad.speed = 1f;
		vlad.edgeBehavior = ActorClass.BEHAVIOR_TURN;
		vlad.wallBehavior = ActorClass.BEHAVIOR_TURN;
		vlad.actorContactBehaviors = new int[] {ActorClass.BEHAVIOR_NONE, 
				ActorClass.BEHAVIOR_NONE, ActorClass.BEHAVIOR_TURN, ActorClass.BEHAVIOR_TURN};
		vlad.heroContactBehaviors[ActorClass.ABOVE] = ActorClass.BEHAVIOR_DIE;
		vlad.name = "Vampire!";
		actors[2] = vlad;

		hero = new Hero();
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
	}

	private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException {
		//long time = System.currentTimeMillis();
		in.defaultReadObject();
		Upgrader.upgrade(this);
		//time = System.currentTimeMillis() - time;
		//Game.debug("Read in " + time + "ms");
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		//long time = System.currentTimeMillis();
		out.defaultWriteObject();
		//time = System.currentTimeMillis() - time;
		//Game.debug("Written in " + time + "ms");
	}

	public Map getSelectedMap() {
		return maps.get(startMapId);
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

	private static Class<?>[] shallow = {Integer.class, Long.class, Short.class, Double.class, 
		Float.class, Boolean.class, Byte.class, Character.class, String.class};

	public static boolean areEqual(Object o1, Object o2) {

		Class<?> c = o1.getClass();
		//Game.debug("Class:" + o1.getClass().getName());
		
		if (o1 == null || o2 == null)
			return (o1 == o2);

		if (c != o2.getClass())
			return false;

		for (int i = 0; i < shallow.length; i++) {
			if (shallow[i].equals(c)) {
				if (o1.equals(o2)) {
					return true;
				} else {
					return false;
				}
			}
		}

		if (c.isArray()) {
			if (Array.getLength(o1) != Array.getLength(o2))
				return false;

			String className = c.toString();
			int index = className.lastIndexOf("[");
			boolean shallowArray = className.length() > 1 && index == 0 && 
			className.charAt(1) != 'L';
			shallowArray = false;

			for (int i = 0; i < Array.getLength(o1); i++) {
				if (shallowArray) {
					if (Array.get(o1, i).equals(Array.get(o2, i)))
						return false;
				} else {
					if (!areEqual(Array.get(o1, i), Array.get(o2, i)))
						return false;
				}
			}
		}


		if (o1 instanceof List) {
			List<?> a1 = (List<?>)o1;
			List<?> a2 = (List<?>)o2;
			if (a1.size() != a2.size()) return false;
			for (int i = 0; i < a1.size(); i++) {
				if (!areEqual(a1.get(i), a2.get(i)))
					return false;
			}
		}

		while (c != null) {
			for (Field field : c.getDeclaredFields()) {
				//Game.debug("Field:" + field.getName());
				try {
					if (field.getType().isPrimitive()) {
						if (!field.get(o1).equals(field.get(o2)))
							return false;
					} else {
						if (!areEqual(field.get(o1), field.get(o2)))
							return false;
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
			c = c.getSuperclass();
		}

		return true;
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
