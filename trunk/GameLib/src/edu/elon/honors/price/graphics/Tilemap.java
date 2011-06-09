package edu.elon.honors.price.graphics;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Tilemap {
	private Bitmap[] tiles;
	private int[][] map;
	private Rect displayRect;
	private Viewport viewport;
	private Sprite[][] sprites;
	private int rows, columns, tileWidth, tileHeight;
	float scrollX, scrollY;
	
	public float getScrollX() {
		return scrollX;
	}

	public void setScrollX(float scrollX) {
		this.scrollX = scrollX;
		updateScroll();
	}

	public float getScrollY() {
		return scrollY;
	}

	public void setScrollY(float scrollY) {
		this.scrollY = scrollY;
		updateScroll();
	}

	public Rect getDisplayRect() {
		return displayRect;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public Tilemap(Bitmap tilesBitmap, int tileWidth, int tileHeight, int tileSpacing, 
			int[][] map, Rect displayRect, int z) {
		createTiles(tilesBitmap, tileWidth, tileHeight, tileSpacing);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.map = map;
		this.displayRect = displayRect;
		this.viewport = new Viewport(displayRect.left, displayRect.top, displayRect.width(), displayRect.height());
		this.viewport.setZ(z);
		this.rows = map.length;
		this.columns = map[0].length;
		generateSprites();
	}
	
	public void scroll(float x, float y) {
		scrollX += x;
		scrollY += y;
		updateScroll();
	}
	
	public static int[][] readMap(String csv) {
		String[] lines = csv.split("\n");
		int[][] map = new int[lines.length][];
		for (int i = 0; i < lines.length; i++) {
			String[] values = lines[i].split(",");
			map[i] = new int[values.length];
			for (int j = 0; j < values.length; j++) {
				if (values[j].length() > 0)
					map[i][j] = Integer.parseInt(values[j]);
				else
					map[i][j] = -1;
			}
		}
		return map;
	}
	
	private void updateScroll() {
		for (int i = 0; i < sprites.length; i++) {
			for (int j = 0; j < sprites[i].length; j++) {
				sprites[i][j].setOriginX((int)scrollX);
				sprites[i][j].setOriginY((int)scrollY);
			}
		}
	}
	
	private void generateSprites() {
		this.sprites = new Sprite[rows][columns];
		for (int i = 0; i < sprites.length; i++) {
			for (int j = 0; j < sprites[i].length; j++) {
				if (map[i][j] >= 0) {
					Sprite s = new Sprite(viewport, tiles[map[i][j]]);
					s.setX(j * tileWidth);
					s.setY(i * tileHeight);
					sprites[i][j] = s;
				} else {
					sprites[i][j] = null;
				}
			}
		}
	}
	
	private void createTiles(Bitmap tilesBitmap, int tileWidth, int tileHeight, int tileSpacing) {
		if ((tilesBitmap.getWidth() + tileSpacing) % (tileWidth + tileSpacing) != 0) {
			throw new RuntimeException("Impropper tile width");
		}
		if ((tilesBitmap.getHeight() + tileSpacing) % (tileHeight + tileSpacing) != 0) {
			throw new RuntimeException("Impropper tile height");
		}
		
		int rowTiles = (tilesBitmap.getWidth() + tileSpacing) / (tileWidth + tileSpacing);
		int columnTiles = (tilesBitmap.getHeight() + tileSpacing) / (tileHeight + tileSpacing);
		
		tiles = new Bitmap[rowTiles * columnTiles];
		
		int index = 0;
		for (int j = 0; j < tilesBitmap.getHeight(); j += tileHeight + tileSpacing) {
			for (int i = 0; i < tilesBitmap.getWidth(); i += tileWidth + tileSpacing) {
				tiles[index++] = Bitmap.createBitmap(tilesBitmap, i, j, tileWidth, tileHeight);
			}
		}
		
		
	}
}
