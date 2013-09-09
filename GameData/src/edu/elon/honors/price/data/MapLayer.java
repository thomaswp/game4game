package edu.elon.honors.price.data;

import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;

public class MapLayer extends GameData {
	private static final long serialVersionUID = 2L;
	
	public String name;
	public int rows, columns;
	public int[][] tiles;
	public boolean active;
	public int defaultValue;

	@Override
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		name = fields.add(name);
		rows = fields.add(rows);
		columns = fields.add(columns);
		tiles = fields.add2DArray(tiles);
		active = fields.add(active);
		defaultValue = fields.add(defaultValue);
	}
	
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
