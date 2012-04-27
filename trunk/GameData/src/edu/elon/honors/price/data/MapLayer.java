package edu.elon.honors.price.data;

import java.io.Serializable;

public class MapLayer implements Serializable{
	private static final long serialVersionUID = 2L;
	
	public String name;
	public int rows, columns;
	public int[][] tiles;
	public boolean active;
	public int defaultValue;
	
	public MapLayer(String name, int rows, int columns, boolean active,
			int defaultValue) {
		this.rows = rows;
		this.columns = columns;
		this.active = active;
		this.tiles = new int[rows][columns];
		this.defaultValue = defaultValue;
		
		if (defaultValue != 0) {
			setAll(defaultValue);
		}
	}

	public void setAll(int id) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				tiles[i][j] = id;
			}
		}
	}

}
