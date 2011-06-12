package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class PlatformMap implements Serializable{
	private static final long serialVersionUID = 1L;
	
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
		rows = 5;
		columns = 50;
		
		tileset = new Tileset(R.drawable.tiles, 48, 48);
		tileset.tileSpacing = 3;
		
		layers = new ArrayList<PlatformLayer>();
		PlatformLayer layer = new PlatformLayer("boxes", rows, columns, 5, true);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (Math.random() > 0.6)
					layer.tiles[i][j] = 6*8;//(int)(Math.random() * 64);
				else
					layer.tiles[i][j] = -1;
			}
		}
		layers.add(layer);
		layer = new PlatformLayer("boxes", rows, columns, -1, false);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (Math.random() > 0.4)
					layer.tiles[i][j] = 0;//(int)(Math.random() * 64);
				else
					layer.tiles[i][j] = -1;
			}
		}
		layers.add(layer);
	}
	
}
