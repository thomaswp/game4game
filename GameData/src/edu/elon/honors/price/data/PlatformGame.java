package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class PlatformGame implements Serializable {
	private static final long serialVersionUID = 2L;
	
	private static final int START_SIZE = 10;
	
	private transient Rect mapRect = new Rect();
	
	public ArrayList<PlatformMap> maps;
	public int startMapId;
	
	public Tileset[] tilesets;
	public PlatformActor[] actors;
	
	public PlatformGame() {
		maps = new ArrayList<PlatformMap>();
		maps.add(new PlatformMap());
		startMapId = 0;
		
		tilesets = new Tileset[START_SIZE];
		tilesets[0] = new Tileset(R.drawable.tiles, 48, 48, 8, 8);
		
		actors = new PlatformActor[START_SIZE];
		actors[1] = new PlatformActor();
		PlatformActor ghost = actors[1];
		ghost.imageId = R.drawable.ghost;
		ghost.speed = 0.1f;
		ghost.jumpVelocity = 0.2f;
		ghost.edgeBehavior = PlatformActor.BEHAVIOR_TURN;
		ghost.wallBehavior = PlatformActor.BEHAVIOR_JUMP_TURN;
		ghost.actorContactBehaviors = new int[] {PlatformActor.BEHAVIOR_NONE, 
				PlatformActor.BEHAVIOR_NONE, PlatformActor.BEHAVIOR_TURN, PlatformActor.BEHAVIOR_TURN};
		ghost.heroContactBehaviors[PlatformActor.ABOVE] = PlatformActor.BEHAVIOR_DIE;
		
		actors[2] = new PlatformActor();
		actors[2].imageId = R.drawable.vamp;
		actors[2].speed = 0.1f;
		actors[2].edgeBehavior = PlatformActor.BEHAVIOR_TURN;
		actors[2].wallBehavior = PlatformActor.BEHAVIOR_TURN;
		actors[2].actorContactBehaviors = new int[] {PlatformActor.BEHAVIOR_NONE, 
				PlatformActor.BEHAVIOR_NONE, PlatformActor.BEHAVIOR_TURN, PlatformActor.BEHAVIOR_TURN};
		actors[2].heroContactBehaviors[PlatformActor.ABOVE] = PlatformActor.BEHAVIOR_DIE;
		
		maps.get(0).actorLayer.tiles[0][0] = 1;
		maps.get(0).actorLayer.tiles[1][2] = 1;
		maps.get(0).actorLayer.tiles[3][3] = 2;
		maps.get(0).actorLayer.tiles[2][5] = 2;
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
