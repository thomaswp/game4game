package edu.elon.honors.price.data;

import java.io.Serializable;

public class PlatformLayer implements Serializable{
	private static final long serialVersionUID = 2L;
	
	public String name;
	public int rows, columns;
	public int[][] tiles;
	public boolean active;
	
	public PlatformLayer(String name, int rows, int columns, boolean active) {
		this.rows = rows;
		this.columns = columns;
		this.active = active;
		this.tiles = new int[rows][columns];
	}

}
