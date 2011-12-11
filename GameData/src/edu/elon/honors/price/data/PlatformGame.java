package edu.elon.honors.price.data;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Rect;

public class PlatformGame implements Serializable {
	private static final long serialVersionUID = 3L;


	private transient Rect mapRect = new Rect();

	public ArrayList<Map> maps;
	public int startMapId;

	public Tileset[] tilesets;
	public ActorClass[] actors;
	public Hero hero;
	
	public String[] switchNames;
	public boolean[] switchValues;
	public String[] variableNames;
	public int[] variableValues;

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

		tilesets = new Tileset[1];
		tilesets[0] = new Tileset("tiles.png", 48, 48, 8, 8);

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

		if (o1 == null || o2 == null)
			return (o1 == o2);

		if (o1.getClass() != o2.getClass())
			return false;

		for (int i = 0; i < shallow.length; i++) {
			if (shallow[i].equals(o1.getClass()))
				return o1.equals(o2);
		}

		//Game.debug("Class:" + o1.getClass().getName());

		if (o1.getClass().isArray()) {
			if (Array.getLength(o1) != Array.getLength(o2))
				return false;
			
			String className = o1.getClass().toString();
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
		
		Class<?> c = o1.getClass();

		while (c != null) {
			for (Field field : c.getDeclaredFields()) {
				//Game.debug("Field:" + field.getName());
				try {
					if (o1.getClass() == ArrayList.class) {
						ArrayList<?> a1 = (ArrayList<?>)o1;
						ArrayList<?> a2 = (ArrayList<?>)o2;
						if (a1.size() != a2.size()) return false;
						for (int i = 0; i < a1.size(); i++) {
							if (!areEqual(a1.get(i), a2.get(i)))
								return false;
						}
					} else if (field.getType().isPrimitive()) {
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