package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.maker.MapEditorView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

public class MapEditorLayerTiles extends MapEditorLayer {

	private int layer;
	private Bitmap[] tiles, darkTiles;

	public MapEditorLayerTiles(MapEditorView parent, int layer) {
		super(parent);
		this.layer = layer;
		this.tiles = parent.tiles;
		this.darkTiles = parent.darkTiles;
	}

	@Override
	public void drawContent(Canvas c) {
		if (touchDown && showPreview) {
			Tileset tileset = game.getMapTileset(map);
			int tileWidth = tileset.tileWidth;
			int tileHeight = tileset.tileHeight;
			
			paint.setColor(Color.WHITE);
			Bitmap bmp = parent.tilesetImage;
			int x = (int)(touchX - bmp.getWidth() / 2);
			int edgeX = (int)parent.offX % tileWidth;
			x = (x - edgeX + tileWidth / 2) / tileWidth;
			x = x * tileWidth + edgeX;
			int y = (int)(touchY - bmp.getHeight() / 2);
			int edgeY = (int)parent.offY % tileHeight;
			y = (y - edgeY + tileHeight / 2) / tileHeight;
			y = y * tileHeight + edgeY;
			c.drawRect(x, y, x + bmp.getWidth(), y + bmp.getHeight(), paint);
			c.drawBitmap(parent.tilesetImage, x, y, paint);
		}
	}

	@Override
	public void drawLayer(Canvas c, DrawMode mode) {
		MapLayer layer = map.layers[this.layer];
		Tileset tileset = game.tilesets[map.tilesetId];

		paint.setAlpha(mode == DrawMode.Above ? MapEditorView.TRANS : 255);

		for (int j = 0; j < layer.rows; j++) {
			for (int k = 0; k < layer.columns; k++) {
				float x = k * tileset.tileWidth;
				float y = j * tileset.tileHeight;
				int tileId = layer.tiles[j][k];
				Bitmap tileBitmap = mode == DrawMode.Below ?
						darkTiles[tileId] : tiles[tileId];
						drawTile(c, x, y, tileId, tileBitmap);
			}
		}
	}

	protected void drawTile(Canvas c, float x, float y, int tileId, Bitmap tileBitmap) {
		c.drawBitmap(tileBitmap, x + getOffX(), y + getOffY(), paint);
	}

	@Override
	public void onSelect() {
		parent.selectTileset();
	}

	@Override
	public void refreshSelection() {
		Tileset tileset = game.tilesets[map.tilesetId];
		Rect selection = parent.tilesetSelection;
		if (selection.width() * selection.height() == 0) {
			parent.tilesetImage = null;
			return;
		}
		Bitmap tilesetBmp = Data.loadTileset(tileset.bitmapName);
		Bitmap bmp = Bitmap.createBitmap(tilesetBmp, 
				selection.left * tileset.tileWidth, 
				selection.top * tileset.tileHeight,
				selection.width() * tileset.tileWidth,
				selection.height() * tileset.tileHeight);
		parent.tilesetImage = bmp;
	}

	@Override
	public Bitmap getSelection() {
		return parent.tilesetImage;
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);
		placeTiles(x, y);
	}
	
	@Override
	public void onTouchDrag(float x, float y, boolean showPreview) {
		super.onTouchDrag(x, y, showPreview);
		
		if (parent.editMode == MapEditorView.EDIT_ALT) {
			placeTiles(x, y);
		}
	}
	
	private void placeTiles(float x, float y) {
		Tileset tileset = game.getMapTileset(map);
		int tileWidth = tileset.tileWidth;
		int tileHeight = tileset.tileHeight;
		Bitmap bmp = parent.tilesetImage;
		Rect rect = parent.tilesetSelection;
		
		int row = (int)(y - parent.offY - bmp.getHeight() / 2 + tileHeight / 2) / tileHeight;
		int col = (int)(x - parent.offX - bmp.getWidth() / 2 + tileWidth / 2) / tileWidth;
		
		MapLayer layer = map.layers[this.layer];
		
		TileAction action = new TileAction(this.layer);
		boolean changed = false;
		
		for (int i = 0; i < rect.height(); i++) {
			for (int j = 0; j < rect.width(); j++) {
				int destRow = row + i;
				int destCol = col + j;
				if (destRow < layer.rows && destCol < layer.columns) {
					int srcRow = rect.top + i;
					int srcCol = rect.left + j;
					int redoId = srcRow * tileset.columns + srcCol;
					int undoId = layer.tiles[destRow][destCol];
					if (redoId != undoId) changed = true;
					action.addPlacement(destRow, destCol, redoId, undoId);
				}
			}
		}
		
		if (changed) {
			parent.doAction(action);
		}
	}
	
	public static class TileAction extends Action {
		private ArrayList<Placement> placements;
		private int layer;
		
		public TileAction(int layer) {
			this.layer = layer;
			placements = new ArrayList<Placement>();
		}
		
		public void addPlacement(int row, int col, int redoId, int undoId) {
			placements.add(new Placement(row, col, redoId, undoId));
		}
		
		@Override
		public void undo(PlatformGame game) {
			for (int i = 0; i < placements.size(); i++) {
				Placement p = placements.get(i);
				game.getSelectedMap().layers[layer].tiles[p.row][p.col] = p.undoId; 
			}
		}

		@Override
		public void redo(PlatformGame game) {
			for (int i = 0; i < placements.size(); i++) {
				Placement p = placements.get(i);
				game.getSelectedMap().layers[layer].tiles[p.row][p.col] = p.redoId; 
			}
		}
		
		private static class Placement { 
			public int row, col, undoId, redoId;
			public Placement(int row, int col, int redoId, int undoId) {
				this.row = row; this.col = col; 
				this.redoId = redoId; this.undoId = undoId;
			}
		}
	}
}
