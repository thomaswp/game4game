package edu.elon.honors.price.data;

import java.io.Serializable;

public class PlatformLayer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public String name;
	public int rows, columns, z;
	public int[][] tiles;
	public boolean active;
	
	public PlatformLayer(String name, int rows, int columns, int z, boolean active) {
		this.rows = rows;
		this.columns = columns;
		this.z = z;
		this.active = active;
		this.tiles = new int[rows][columns];
	}

}
