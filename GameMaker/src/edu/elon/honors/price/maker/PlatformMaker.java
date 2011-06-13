package edu.elon.honors.price.maker;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import edu.elon.honors.price.data.PlatformLayer;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;

public class PlatformMaker implements Logic {

	public static final int MODE_MOVE = 0;
	
	private PlatformMap map;
	private PlatformData data;
	private ArrayList<Tilemap> tilemaps;
	private Sprite menu;
	private RectHolder holder;
	private float startScrollX, startScrollY;
	
	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}

	public PlatformMaker(RectHolder holder) {
		this.holder = holder;
	}
	
	@Override
	public void initialize() {
		if (map == null)
			map = new PlatformMap();
		if (data == null)
			data = new PlatformData();
		
		loadSprites();
	}

	@Override
	public void update(long timeElapsed) {
		if (Input.isTapped()) {
			if (menu.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY())) {
				holder.newRect(map.tileset.bitmapId);
			}
			startScrollX = data.scrollX;
			startScrollY = data.scrollY;
		} else 
		
		if (data.mode == MODE_MOVE) {
			if (Input.isTouchDown()) {
				data.scrollX = startScrollX - Input.getDistanceTouchX();
				data.scrollY = startScrollY - Input.getDistanceTouchY();
			}
			Game.debug(data.scrollX + "," + data.scrollY);
			data.scrollX = (float)Math.max(Math.min(data.scrollX, -Graphics.getWidth() + map.getWidth()), 0);
			data.scrollY = (float)Math.max(Math.min(data.scrollY, -Graphics.getHeight() + map.getHeight()), 0);
			for (int i = 0; i < tilemaps.size(); i++) {
				tilemaps.get(i).setScrollX(data.scrollX);
				tilemaps.get(i).setScrollY(data.scrollY);
			}
		}
	}

	@Override
	public void save(Activity parent) {
		Data.saveObject("data", parent, data);
	}
	
	public void saveFinal(Activity parent) {
		Data.saveObjectPublic("map-final", parent, map);
	}

	@Override
	public void load(Activity parent) {
		data = (PlatformData)Data.loadObject("data", parent);
		map = (PlatformMap)Data.loadObject("map", parent);
	}
	
	private void loadSprites() {
		Viewport.DefaultViewport.setZ(100);
		menu = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, 0, 50, 50);
		menu.getBitmap().eraseColor(Color.RED);
		
		tilemaps = new ArrayList<Tilemap>(map.layers.size()); 
		Tileset tileset = map.tileset;
		Rect rect = new Rect();
		rect.set(0, 0, Graphics.getWidth(), Graphics.getHeight());
		for (int i = 0; i < map.layers.size(); i++) {
			PlatformLayer layer = map.layers.get(i);
			Tilemap tm = new Tilemap(Data.loadBitmap(tileset.bitmapId), 
					tileset.tileWidth, tileset.tileHeight, tileset.tileSpacing, 
					layer.tiles, rect, layer.z);
			tm.setShowingGrid(true);
			tm.setVisible(i == data.layer);
			tm.scroll(data.scrollX, data.scrollY);
			tilemaps.add(tm);
		}
	}

	public static abstract class RectHolder {
		public abstract Rect getRect();
		public abstract void newRect(int bitmapId);
	}
	
	private static class PlatformData implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public int layer = 0;
		public int mode = 0;
		public float scrollX, scrollY;
	}
}
