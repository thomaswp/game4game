package edu.elon.honors.price.maker;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Rect;
import edu.elon.honors.price.data.PlatformLayer;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Tilemap;

public class PlatformMaker implements Logic {

	private PlatformMap map;
	private ArrayList<Tilemap> tilemaps;
	private boolean newMap;
	private int layer = 0;
	
	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}

	public PlatformMaker() {
		newMap = true;
	}
	
	@Override
	public void initialize() {
		if (newMap)
			map = new PlatformMap();
		
		loadSprites();
	}

	@Override
	public void update(long timeElapsed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Activity parent) {
		Data.saveObject("map", parent, map);
		
	}

	@Override
	public void load(Activity parent) {
		map = (PlatformMap)Data.loadObject("map", parent);
		if (map != null)
			newMap = false;
	}
	
	private void loadSprites() {
		tilemaps = new ArrayList<Tilemap>(map.layers.size());
		Tileset tileset = map.tileset;
		Rect rect = new Rect();
		rect.set(0, 0, Graphics.getWidth(), Graphics.getHeight());
		if (rect.right > map.getWidth())
			rect.right = map.getWidth();
		if (rect.bottom > map.getHeight()) {
			rect.bottom = map.getHeight();
		}
		Game.debug(rect);
		for (int i = 0; i < map.layers.size(); i++) {
			PlatformLayer layer = map.layers.get(i);
			Tilemap tm = new Tilemap(Data.loadBitmap(tileset.bitmapId), 
					tileset.tileWidth, tileset.tileHeight, tileset.tileSpacing, 
					layer.tiles, rect, layer.z);
			tm.setShowingGrid(true);
			tm.setVisible(i == this.layer);
			tilemaps.add(tm);
		}
	}

}
