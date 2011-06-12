package edu.elon.honors.price.data;

import java.io.Serializable;

public class Tileset implements Serializable {
	private static final long serialVersionUID = 1L;

	public int bitmapId, tileWidth, tileHeight, tileSpacing;
	
	public Tileset(int bitmapId, int tileWidth, int tileHeight) {
		this.bitmapId = bitmapId;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
}
