package edu.elon.honors.price.data;

import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;

public class Tileset extends GameData {
	private static final long serialVersionUID = 2L;

	public int tileWidth, tileHeight, tileSpacing, rows, columns;
	public String name, bitmapName;
	
	@Override
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		tileWidth = fields.add(tileWidth);
		tileHeight = fields.add(tileHeight);
		tileSpacing = fields.add(tileSpacing);
		rows = fields.add(rows);
		columns = fields.add(columns);
		name = fields.add(name);
		bitmapName = fields.add(bitmapName);
	}
	
	public Tileset(String name, String bitmapName, 
			int tileWidth, int tileHeight, int rows, int columns) {
		this.name = name;
		this.bitmapName = bitmapName;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.rows = rows;
		this.columns = columns;
	}
	
	public int width() {
		return tileWidth * columns;
	}
	
	public int height() {
		return tileHeight * rows;
	}
}
