package edu.elon.honors.price.graphics;

import java.util.Arrays;

import edu.elon.honors.price.game.Game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class Tilemap {
	private Bitmap[] tiles;
	private int[][] map;
	private Rect displayRect;
	private Viewport viewport;
	private Sprite[][] sprites;
	private int rows, columns, tileWidth, tileHeight;
	private float scrollX, scrollY;
	private boolean visible;
	
	private Bitmap gridBitmap;
	private BackgroundSprite grid;
	
	public float getScrollX() {
		return scrollX;
	}

	public void setScrollX(float scrollX) {
		scroll(scrollX - this.scrollX, 0);
	}

	public float getScrollY() {
		return scrollY;
	}

	public void setScrollY(float scrollY) {
		scroll(0, scrollY - this.scrollY);
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
	
	public Sprite[][] getSprites() {
		return sprites;
	}
	
	public boolean isShowingGrid() {
		return grid != null && grid.isVisible();
	}
	
	public void setShowingGrid(boolean showing) {
		if (showing) {
			if (grid == null)
				createGrid();
			grid.setVisible(true);
		} else {
			if (grid != null)
				grid.setVisible(false);
		}
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		viewport.setVisible(visible);
	}

	public Tilemap(Bitmap tilesBitmap, int tileWidth, int tileHeight, int tileSpacing, 
			int[][] map, Rect displayRect, int z) {
		this.tiles = createTiles(tilesBitmap, tileWidth, tileHeight, tileSpacing);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.map = map;
		this.displayRect = displayRect;
		this.viewport = new Viewport(displayRect.left, displayRect.top, displayRect.width(), displayRect.height());
		this.viewport.setSorted(false);
		this.viewport.setZ(z);
		this.rows = map.length;
		this.columns = map[0].length;
		this.visible = true;
		generateSprites();
	}
	
	public void scroll(float x, float y) {
		boolean updateVisible = (int)(scrollX / tileWidth) != (int)((scrollX + x) / tileWidth) ||
			(int)(scrollY / tileHeight) != (int)((scrollY + y) / tileHeight) ||
			(int)((scrollX + viewport.getWidth()) / tileWidth) != (int)((scrollX + viewport.getWidth() + x) / tileWidth) ||
			(int)((scrollY + viewport.getHeight()) / tileHeight) != (int)((scrollY + viewport.getHeight() + y) / tileHeight);
		scrollX += x;
		scrollY += y;
		if (grid != null) grid.scroll(x, y);
		updateScroll(updateVisible);
	}
	
	public static int[][] readMap(String csv) {
		String[] lines = csv.split("\n");
		int[][] map = new int[lines.length][];
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.endsWith(",")) line += "-1";
			String[] values = line.split(",");
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
	
	private void updateScroll(boolean updateVisible) {
		for (int i = 0; i < sprites.length; i++) {
			for (int j = 0; j < sprites[i].length; j++) {
				if (sprites[i][j] != null) {
					sprites[i][j].setOriginX(scrollX);
					sprites[i][j].setOriginY(scrollY);
					if (updateVisible)
						sprites[i][j].setVisible(visible && viewport.isSpriteInBounds(sprites[i][j]));
				}
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
	
	private void createGrid() {
		gridBitmap = Bitmap.createBitmap(tileWidth, tileHeight, Sprite.defaultConfig);
		Canvas c = new Canvas();
		Paint p = new Paint();
		p.setColor(Color.argb(200, 200, 200, 200));
		p.setStyle(Style.STROKE);
		c.setBitmap(gridBitmap);
		c.drawRect(0, 0, tileWidth, tileHeight, p);
		c.drawRect(1, 1, tileWidth - 1, tileHeight - 1, p);
		
		
		grid = new BackgroundSprite(gridBitmap, viewport);
	}
	
	public static Bitmap[] createTiles(Bitmap tilesBitmap, int tileWidth, int tileHeight, int tileSpacing) {
		if ((tilesBitmap.getWidth() + tileSpacing) % (tileWidth + tileSpacing) != 0) {
			throw new RuntimeException("Impropper tile width: " + tileWidth + "x + " + tileSpacing + " != " + tilesBitmap.getWidth());
		}
		if ((tilesBitmap.getHeight() + tileSpacing) % (tileHeight + tileSpacing) != 0) {
			throw new RuntimeException("Impropper tile height" + tileHeight + "x + " + tileSpacing + " != " + tilesBitmap.getHeight());
		}
		
		int rowTiles = (tilesBitmap.getWidth() + tileSpacing) / (tileWidth + tileSpacing);
		int columnTiles = (tilesBitmap.getHeight() + tileSpacing) / (tileHeight + tileSpacing);
		
		Bitmap[] tiles = new Bitmap[rowTiles * columnTiles];
		
		int index = 0;
		for (int j = 0; j < tilesBitmap.getHeight(); j += tileHeight + tileSpacing) {
			for (int i = 0; i < tilesBitmap.getWidth(); i += tileWidth + tileSpacing) {
				tiles[index++] = Bitmap.createBitmap(tilesBitmap, i, j, tileWidth, tileHeight);
			}
		}
		return tiles;
	}
}
