package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class PlatformGame implements Serializable {
	private static final long serialVersionUID = 2L;
	
	
	private transient Rect mapRect = new Rect();
	
	public ArrayList<Map> maps;
	public int startMapId;
	
	public Tileset[] tilesets;
	public ActorClass[] actors;
	public Hero hero;
	
	public PlatformGame() {
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
}
