package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class PlatformGame implements Serializable {
	private static final long serialVersionUID = 2L;
	
	
	private transient Rect mapRect = new Rect();
	
	public ArrayList<PlatformMap> maps;
	public int startMapId;
	
	public Tileset[] tilesets;
	public PlatformActor[] actors;
	public PlatformHero hero;
	
	public PlatformGame() {
		maps = new ArrayList<PlatformMap>();
		maps.add(new PlatformMap());
		startMapId = 0;
		
		tilesets = new Tileset[1];
		tilesets[0] = new Tileset("tiles.png", 48, 48, 8, 8);
		
		actors = new PlatformActor[3];
		actors[1] = new PlatformActor();
		PlatformActor ghost = actors[1];
		ghost.imageName = "ghost.png";
		ghost.speed = 1.5f;
		ghost.jumpVelocity = 4f;
		ghost.edgeBehavior = PlatformActor.BEHAVIOR_TURN;
		ghost.wallBehavior = PlatformActor.BEHAVIOR_JUMP_TURN;
		ghost.actorContactBehaviors = new int[] {PlatformActor.BEHAVIOR_NONE, 
				PlatformActor.BEHAVIOR_NONE, PlatformActor.BEHAVIOR_TURN, PlatformActor.BEHAVIOR_TURN};
		ghost.heroContactBehaviors[PlatformActor.ABOVE] = PlatformActor.BEHAVIOR_DIE;
		ghost.name = "Ghost";
		
		PlatformActor vlad = new PlatformActor();
		vlad.imageName = "vamp.png";
		vlad.speed = 1f;
		vlad.edgeBehavior = PlatformActor.BEHAVIOR_TURN;
		vlad.wallBehavior = PlatformActor.BEHAVIOR_TURN;
		vlad.actorContactBehaviors = new int[] {PlatformActor.BEHAVIOR_NONE, 
				PlatformActor.BEHAVIOR_NONE, PlatformActor.BEHAVIOR_TURN, PlatformActor.BEHAVIOR_TURN};
		vlad.heroContactBehaviors[PlatformActor.ABOVE] = PlatformActor.BEHAVIOR_DIE;
		vlad.name = "Vampire!";
		actors[2] = vlad;
		
		hero = new PlatformHero();
		hero.speed = 3.5f;
		hero.jumpVelocity = 6f;
		hero.stunDuration = 600;
		hero.imageName = "hero.png";
		hero.name = "Hero";
		hero.actorContactBehaviors[PlatformActor.BELOW] = PlatformActor.BEHAVIOR_JUMP;
		hero.actorContactBehaviors[PlatformActor.LEFT] = PlatformActor.BEHAVIOR_STUN;
		hero.actorContactBehaviors[PlatformActor.RIGHT] = PlatformActor.BEHAVIOR_STUN;
	}
	
	public Tileset getMapTileset(PlatformMap map) {
		return tilesets[map.tilesetId];
	}
	
	public int getMapWidth(PlatformMap map) {
		return map.columns * getMapTileset(map).tileWidth;
	}
	
	public int getMapHeight(PlatformMap map) {
		return map.rows * getMapTileset(map).tileHeight;
	}
	
	public Rect getMapRect(PlatformMap map) {
		mapRect.set(0, 0, getMapWidth(map), getMapHeight(map));
		return mapRect;
	}
}
