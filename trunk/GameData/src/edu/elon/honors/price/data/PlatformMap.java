package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class PlatformMap implements Serializable{
	private static final long serialVersionUID = 9L;
	
	public int tilesetId;
	public PlatformLayer[] layers;
	public PlatformLayer actorLayer;
	public int rows, columns;
	
	public PlatformMap() {
		
		rows = 8;
		columns = 30;
		
		tilesetId = 0;
		
		actorLayer = new PlatformLayer("actors", rows, columns, true);
		actorLayer.tiles[0][0] = -1;
		
		layers = new PlatformLayer[3];
		PlatformLayer layer = new PlatformLayer("background", rows, columns, false);
		layers[0] = layer;
		layer = new PlatformLayer("l1", rows, columns, true);
		layers[1] = layer;
		layer = new PlatformLayer("l2", rows, columns, false);
		layers[2] = layer;
	}
	
}
