package edu.elon.honors.price.data;

import java.io.Serializable;

public class Tileset implements Serializable {
	private static final long serialVersionUID = 2L;

	public int bitmapId, tileWidth, tileHeight, tileSpacing, rows, columns;
	
	public Tileset(int bitmapId, int tileWidth, int tileHeight, int rows, int columns) {
		this.bitmapId = bitmapId;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.rows = rows;
		this.columns = columns;
	}
	
}
