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
	
	public PlatformGame() {
		maps = new ArrayList<PlatformMap>();
		maps.add(new PlatformMap());
		startMapId = 0;
		
		tilesets = new Tileset[1];
		tilesets[0] = new Tileset("tiles.png", 48, 48, 8, 8);
		
		actors = new PlatformActor[10];
		actors[1] = new PlatformActor();
		PlatformActor ghost = actors[1];
		ghost.imageName = "ghost.png";
		ghost.speed = 0.1f;
		ghost.jumpVelocity = 0.2f;
		ghost.edgeBehavior = PlatformActor.BEHAVIOR_TURN;
		ghost.wallBehavior = PlatformActor.BEHAVIOR_JUMP_TURN;
		ghost.actorContactBehaviors = new int[] {PlatformActor.BEHAVIOR_NONE, 
				PlatformActor.BEHAVIOR_NONE, PlatformActor.BEHAVIOR_TURN, PlatformActor.BEHAVIOR_TURN};
		ghost.heroContactBehaviors[PlatformActor.ABOVE] = PlatformActor.BEHAVIOR_DIE;
		ghost.name = "Ghost";
		
		actors[2] = new PlatformActor();
		actors[2].imageName = "vamp.png";
		actors[2].speed = 0.1f;
		actors[2].edgeBehavior = PlatformActor.BEHAVIOR_TURN;
		actors[2].wallBehavior = PlatformActor.BEHAVIOR_TURN;
		actors[2].actorContactBehaviors = new int[] {PlatformActor.BEHAVIOR_NONE, 
				PlatformActor.BEHAVIOR_NONE, PlatformActor.BEHAVIOR_TURN, PlatformActor.BEHAVIOR_TURN};
		actors[2].heroContactBehaviors[PlatformActor.ABOVE] = PlatformActor.BEHAVIOR_DIE;
		actors[2].name = "Vampire!";
		for (int i = 3; i < actors.length; i++) actors[i] = actors[1];
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
