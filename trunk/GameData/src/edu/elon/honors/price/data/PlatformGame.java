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
