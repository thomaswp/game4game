package edu.elon.honors.price.data;

import java.io.Serializable;

public class Tileset implements Serializable {
	private static final long serialVersionUID = 2L;

	public int tileWidth, tileHeight, tileSpacing, rows, columns;
	public String bitmapName;
	
	public Tileset(String bitmapName, int tileWidth, int tileHeight, int rows, int columns) {
		this.bitmapName = bitmapName;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.rows = rows;
		this.columns = columns;
	}
	
}
