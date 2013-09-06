package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorAction;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.MapEditorView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class MapEditorLayerTiles extends MapEditorLayerSelectable<Point> {

	public static final int PAINTING_MODE = 1;
	
	private int layer;
	private Bitmap[] tiles, darkTiles;
	private ArrayList<Point> solidTiles = new ArrayList<Point>();
	private Tileset tileset;

	@Override
	public void setGame(PlatformGame game) {
		super.setGame(game);
		tiles = parent.tiles;
		darkTiles = parent.darkTiles;
		tileset = map.getTileset(game);
		
		solidTiles.clear();
		int[][] layer = map.layers[this.layer].tiles;
		for (int i = 0; i < layer.length; i++) {
			for (int j = 0; j < layer[i].length; j++) {
				if (layer[i][j] != 0) solidTiles.add(new Point(i, j));
			}
		}
	}
	
	public MapEditorLayerTiles(MapEditorView parent, int layer) {
		super(parent);
		this.layer = layer;
		this.tiles = parent.tiles;
		this.darkTiles = parent.darkTiles;
		setGame(game);
	}

	@Override
	public void drawContentNormal(Canvas c) {
		if (touchDown && showPreview) {
			int tileWidth = tileset.tileWidth;
			int tileHeight = tileset.tileHeight;

			paint.setColor(Color.WHITE);
//			Bitmap bmp = parent.tilesetImage;
//			int x = (int)(touchX - bmp.getWidth() / 2);
//			int edgeX = (int)parent.offX % tileWidth;
//			x = (x - edgeX + tileWidth / 2) / tileWidth;
//			x = x * tileWidth + edgeX;
//			int y = (int)(touchY - bmp.getHeight() / 2);
//			int edgeY = (int)parent.offY % tileHeight;
//			y = (y - edgeY + tileHeight / 2) / tileHeight;
//			y = y * tileHeight + edgeY;
//			c.drawRect(x, y, x + bmp.getWidth(), y + bmp.getHeight(), paint);
			float x = getBitmapCol(touchX) * tileWidth + parent.offX;
			float y = getBitmapRow(touchY) * tileHeight + parent.offY;
			c.drawBitmap(parent.tilesetImage, x, y, paint);
		}
	}

	@Override
	public void drawLayerNormal(Canvas c, DrawMode mode) {
		
		MapLayer layer = map.layers[this.layer];
		Tileset tileset = game.tilesets[map.tilesetId];

		paint.setAlpha(mode == DrawMode.Above ? MapEditorView.TRANS : 255);

		for (int j = 0; j < layer.rows; j++) {
			for (int k = 0; k < layer.columns; k++) {
				float x = k * tileset.tileWidth;
				float y = j * tileset.tileHeight;
				int tileId = layer.tiles[j][k];
				if (tileId > 0) {
					Bitmap tileBitmap = mode == DrawMode.Below ?
							darkTiles[tileId] : tiles[tileId];
							drawTile(c, x, y, tileId, tileBitmap);
				}
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
		if (selection.left == 0 && selection.top == 0 &&
				selection.right == 1 && selection.bottom == 1) {
			Canvas c = new Canvas(bmp);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(3);
			paint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
			c.drawRect(0,  0, tileset.tileWidth - 1, tileset.tileHeight - 1, paint);
		}
		parent.tilesetImage = bmp;
	}

	@Override
	public Bitmap getSelection() {
		return parent.tilesetImage;
	}

	@Override
	public void onTouchUpNormal(float x, float y) {
		placeTiles(x, y);
		TutorialUtils.fireCondition(EditorAction.MapEditorPlaceTile, 
				parent.getContext());
	}

	@Override
	public void onTouchDragNormal(float x, float y, boolean showPreview) {
		if (parent.editMode == PAINTING_MODE) {
			placeTiles(x, y);
		}
	}
	
	private int getBitmapRow(float touchY) {
		int tileHeight = tileset.tileHeight;
		Bitmap bmp = parent.tilesetImage;
		return Math.round((touchY - parent.offY - bmp.getHeight() / 2) / tileHeight);
	}

	private int getBitmapCol(float touchX) {
		int tileWidth = tileset.tileWidth;
		Bitmap bmp = parent.tilesetImage;
		return Math.round((touchX - parent.offX - bmp.getWidth() / 2) / tileWidth);
	}
	
	private void placeTiles(float x, float y) {
		Rect rect = parent.tilesetSelection;

		int row = getBitmapRow(y);
		int col = getBitmapCol(x);

		MapLayer layer = map.layers[this.layer];

		TileAction action = new TileAction(this.layer);
		boolean changed = false;

		for (int i = 0; i < rect.height(); i++) {
			for (int j = 0; j < rect.width(); j++) {
				int destRow = row + i;
				int destCol = col + j;
				if (destRow >= 0 && destRow < layer.rows && destCol >=0 && destCol < layer.columns) {
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

	public class TileAction extends Action {
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
				updateSolid(p, false);
			}
		}

		@Override
		public void redo(PlatformGame game) {
			for (int i = 0; i < placements.size(); i++) {
				Placement p = placements.get(i);
				game.getSelectedMap().layers[layer].tiles[p.row][p.col] = p.redoId;
				updateSolid(p, true);
			}
		}
		
		private void updateSolid(Placement p, boolean redo) {
			Point loc = new Point(p.row, p.col);
			int id = redo ? p.redoId : p.undoId;
			boolean add = id != 0;
			if (add) {
				if (!solidTiles.contains(loc)) {
					solidTiles.add(loc);
				}
			} else {
				solidTiles.remove(loc);
				selectedObjects.remove(loc);
			}
		}

		private class Placement { 
			public int row, col, undoId, redoId;
			public Placement(int row, int col, int redoId, int undoId) {
				this.row = row; this.col = col; 
				this.redoId = redoId; this.undoId = undoId;
			}
		}
	}

	@Override
	protected Bitmap loadIcon() {
		switch (layer) {
		case 0: return BitmapFactory.decodeResource(parent.getResources(), 
				R.drawable.layer1);
		case 1: return BitmapFactory.decodeResource(parent.getResources(), 
				R.drawable.layer2);
		case 2: return BitmapFactory.decodeResource(parent.getResources(), 
				R.drawable.layer3);
		}
		return null;

	}
	
	private final static EditorButton[] editButtons = new EditorButton[] {
		EditorButton.MapEditorDrawModePencil,
		EditorButton.MapEditorDrawModePaint,
		EditorButton.MapEditorDrawModeSelect
	};
	
	@Override
	protected EditorButton[] getEditButtons() {
		return editButtons;
	}
	
	@Override
	protected void loadEditIcons() {
		editIcons.add(
				BitmapFactory.decodeResource(parent.getResources(),
						R.drawable.edit));
		editIcons.add(
				BitmapFactory.decodeResource(parent.getResources(),
						R.drawable.paint));
		editIcons.add(
				BitmapFactory.decodeResource(parent.getResources(),
						R.drawable.select));
	}

	@Override
	protected ArrayList<Point> getAllItems() {
		return solidTiles;
	}

	@Override
	protected void getDrawBounds(Point item, RectF bounds) {
		bounds.set(item.y * tileset.tileWidth, item.x * tileset.tileHeight,
				(item.y + 1) * tileset.tileWidth, (item.x + 1) * tileset.tileHeight);
		bounds.offset(getOffX(), getOffY());
	}

	@Override
	protected void snapDrawBounds(Point item, RectF bounds, List<Point> ignore) {
		float left = bounds.left;
		float top = bounds.top;
		
		int tileWidth = tileset.tileWidth;
		int tileHeight = tileset.tileHeight;
		left = (int)(left / tileWidth + 0.5f) * tileWidth;
		top = (int)(top / tileHeight + 0.5f) * tileHeight;
		
		int maxLeft = tileWidth * (map.columns - 2);
		int maxTop = tileWidth * (map.rows - 2);
		
		left = Math.min(Math.max(tileset.tileWidth, left), maxLeft);
		top = Math.min(Math.max(tileset.tileHeight, top), maxTop);
		Debug.write(left);
				
		bounds.offsetTo(left, top);
	}

	@Override
	protected Bitmap getBitmap(Point item, DrawMode mode) {
		MapLayer layer = map.layers[this.layer];
		int tileId = layer.tiles[item.x][item.y];
		return tiles[tileId];
					
	}

//	@Override
//	protected void shiftItem(Point item, float offX, float offY) {
//		MapLayer layer = map.layers[this.layer];
//		Tileset tileset = map.getTileset(game);
//		int newRow = item.x + Math.round(offY / tileset.tileHeight);
//		int newCol = item.y + Math.round(offX / tileset.tileWidth);
//		layer.tiles[newRow][newCol] = layer.tiles[item.x][item.y];
//		layer.tiles[item.x][item.y] = 0;
//		while (solidTiles.remove(item));
//		item.set(newRow, newCol);
//		solidTiles.add(item);
//	}
//
//	@Override
//	protected void delete(Point item) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	protected void add(Point item) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	protected boolean inSelectingMode() {
		return parent.editMode == MapEditorView.EDIT_ALT2;
	}

	@Override
	protected Action doShift(final LinkedList<Point> items, 
			final float offX, final float offY) {
		
		final int offRows = Math.round(offY / tileset.tileHeight);
		final int offCols = Math.round(offX / tileset.tileWidth);
		
		if (offRows == 0 && offCols == 0) return null;
		
		TileAction action = new TileAction(layer) {;
			@Override
			public void redo(PlatformGame game) {
				LinkedList<Point> reselect = new LinkedList<Point>();
				for (Point item : items) {
					if (selectedObjects.contains(item)) reselect.add(item);
				}
				super.redo(game);
				for (Point item : reselect) {
					selectedObjects.add(new Point(
							item.x + offRows, item.y + offCols));
				}
			}
		};
		for (Point item : items) {
			int nx = item.x + offRows, ny = item.y + offCols;
			int nId = map.layers[layer].tiles[nx][ny];
			int oId = map.layers[layer].tiles[item.x][item.y];
			action.addPlacement(nx, ny, oId, nId);
			
			boolean skipDelete = false;
			for (Point mItem : items) {
				if (mItem.x + offRows == item.x &&
						mItem.y + offCols == item.y) {
					skipDelete = true;
					break;
				}
			}
			if (!skipDelete) {
				action.addPlacement(item.x, item.y, 0, oId);
			}
		}
		
		return action;
	}

	@Override
	protected Action doDelete(final LinkedList<Point> items) {
		TileAction action = new TileAction(layer);
		for (Point item : items) {
			int oId = map.layers[layer].tiles[item.x][item.y];
			action.addPlacement(item.x, item.y, 0, oId);
		}
		
		return action;
	}
	
	@Override
	protected void setBoundedRect(Point item, RectF rect) {
		rect.set(item.y * tileset.tileWidth, item.x * tileset.tileHeight, 
				(item.y + 1) * tileset.tileWidth - 1, 
				(item.x + 1) * tileset.tileHeight - 1);
	}
}
