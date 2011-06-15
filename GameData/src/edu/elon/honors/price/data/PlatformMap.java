package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class PlatformMap implements Serializable{
	private static final long serialVersionUID = 8L;
	
	private transient Rect rect = new Rect();
	
	public Tileset tileset;
	public ArrayList<PlatformLayer> layers;
	
	public int rows, columns;
	
	public int getWidth() {
		return columns * tileset.tileWidth;
	}
	
	public int getHeight() {
		return rows * tileset.tileHeight;
	}
	
	public Rect getRect() {
		rect.set(0, 0, getWidth(), getHeight());
		return rect;
	}
	
	public PlatformMap() {
		rows = 8;
		columns = 30;
		
		tileset = new Tileset(R.drawable.tiles, 48, 48, 8, 8);
		tileset.tileSpacing = 0;
		
		layers = new ArrayList<PlatformLayer>();
		PlatformLayer layer = new PlatformLayer("background", rows, columns, false);
		layers.add(layer);
		layer = new PlatformLayer("l1", rows, columns, true);
		layers.add(layer);
		layer = new PlatformLayer("l2", rows, columns, false);
//		for (int i = 0; i < rows; i++) {
//			for (int j = 0; j < columns; j++) {
//				if (Math.random() > 0.6)
//					layer.tiles[i][j] = (int)(Math.random() * 64);
//				else
//					layer.tiles[i][j] = -1;
//			}
//		}
		layers.add(layer);
	}
	
}
