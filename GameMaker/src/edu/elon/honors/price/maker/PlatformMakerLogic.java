package edu.elon.honors.price.maker;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Environment;
import edu.elon.honors.price.data.PlatformLayer;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.helper.Helper;
import edu.elon.honors.price.input.Input;

public class PlatformMakerLogic implements Logic {

	public static final int MODE_MOVE = 0;
	public static final int MODE_EDIT = 1;
	public static final int MODE_DRAW = 2;

	private static final int DARK = Color.argb(255, 150, 150, 150);
	private static final float TRANS = 0.5f;
	
	public static final String MAP = "_map";
	public static final String DATA = "_data";
	


	private PlatformMap map;
	private PlatformData data;
	private ArrayList<Tilemap> tilemaps;
	private Tilemap preview;
	private Sprite previewMask, undoMask, redoMask, borderRight;
	private Sprite menu, move, edit, draw, selection, cancel, layerUp, layerDown, undo, redo;
	private Sprite[] buttons;
	private RectHolder holder;
	private float startScrollX, startScrollY;
	private int hold = 500;
	private boolean scrolling, menuTap;
	private String mapName;

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	public PlatformMakerLogic(String mapName, RectHolder holder) {
		this.mapName = mapName;
		this.holder = holder;
	}

	@Override
	public void initialize() {
		Graphics.setBackgroundColor(Color.WHITE);

		loadSprites();

		if (preview == null)
			data.mode = MODE_MOVE;
		else if (data.mode == MODE_MOVE)
			data.mode = MODE_EDIT;
	}

	@Override
	public void update(long timeElapsed) {
		selection.setY(data.mode * 50 + 50);
		undoMask.setVisible(!(data.editIndex  >= 0));
		redoMask.setVisible(!(data.editIndex < data.actions.size() - 1));
		
		if (hold > 0) {
			hold -= timeElapsed;
		} else {
			if (touchingSprite(menu)) {
				if (Input.isTapped()) {
					holder.newRect(map.tileset.bitmapId, map.tileset.tileWidth, map.tileset.tileHeight);
					return;
				}
			}
			if (touchingSprite(move)) {
				if (Input.isTapped()) {
					data.mode = MODE_MOVE;
					menuTap = true;
				}
			}
			if (touchingSprite(edit)) {
				if (Input.isTapped()) {
					if (preview != null) {
						data.mode = MODE_EDIT;
					}
					menuTap = true;
				}
			}
			if (touchingSprite(draw)) {
				if (Input.isTapped()) {
					if (preview != null) {
						data.mode = MODE_DRAW;
					}
					menuTap = true;
				}
			}
			int changeLayer = 0;
			if (Input.isTapped()) {
				if (touchingSprite(layerUp)) {
					changeLayer = 1;
				} else if (touchingSprite(layerDown)) {
					changeLayer = -1;
				}
			}
			if (changeLayer != 0) {
				if (changeLayer > 0 && data.layer < map.layers.size() - 1) {
					tilemaps.get(data.layer).setColor(DARK);
					data.layer++;
					tilemaps.get(data.layer).setOpacity(1);
					drawChangeLayer();
				} else if (changeLayer < 0 && data.layer > 0) {
					tilemaps.get(data.layer).setOpacity(TRANS);
					data.layer--;
					tilemaps.get(data.layer).setColor(Color.WHITE);
					drawChangeLayer();
				}
				changeLayer = 0;
				menuTap = true;
			}
			if (touchingSprite(undo)) {
				if (Input.isTapped()) {
					if (!undoMask.isVisible()) {
						undoAction();
					}
					menuTap = true;
				}
			}
			if (touchingSprite(redo)) {
				if (Input.isTapped()) {
					if (!redoMask.isVisible()) {
						redoAction();
					}
					menuTap = true;
				}
			}
		}
		if (Input.isTapped() && Input.getLastTouchX() < menu.getX() &&
				(Input.getLastTouchY() < undo.getY() || Input.getLastTouchX() < undo.getX()))
			menuTap = false;
		
		if (menuTap) {
			if (Input.isTapped())
				hold = 1;
			return;
		}

		
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] != null) {
				buttons[i].setOpacity(Input.isTouchDown() ? 0.2f : 1f);
			}
		}
		
		if (Input.isTapped()) {
			startScrollX = data.scrollX;
			startScrollY = data.scrollY;
			scrolling = true;
		}

		if (data.mode == MODE_MOVE) {
			if (Input.isTouchDown()) {
				if (scrolling) {
					data.scrollX = startScrollX - Input.getDistanceTouchX();
					data.scrollY = startScrollY - Input.getDistanceTouchY();

				}
			} else {
				scrolling = false;
			}
			float maxX = -Graphics.getWidth() + map.getWidth() + menu.getWidth();
			if (data.scrollX > maxX) {
				startScrollX -= data.scrollX - maxX;
				data.scrollX = maxX;
			} else if (data.scrollX < 0) {
				startScrollX -= data.scrollX;
				data.scrollX = 0;
			}
			float maxY = -Graphics.getHeight() + map.getHeight();
			if (data.scrollY > maxY) {	
				startScrollY -= data.scrollY - maxY;
				data.scrollY = maxY;
			} else if (data.scrollY < 0) {
				startScrollY -= data.scrollY;
				data.scrollY = 0;
			}
			for (int i = 0; i < tilemaps.size(); i++) {
				tilemaps.get(i).setScrollX(data.scrollX);
				tilemaps.get(i).setScrollY(data.scrollY);
			}
			if (preview != null) {
				preview.setVisible(false);
			}
			cancel.setVisible(false);
		} else if (data.mode == MODE_EDIT || data.mode == MODE_DRAW) {
			int tileWidth = map.tileset.tileWidth;
			int tileHeight = map.tileset.tileHeight;
			
			if (preview.isVisible() && !Input.isTouchDown()) {
				if (!cancel.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY()))
					draw();
			}
			if (Input.isTouchDown()) {
				float offX = data.scrollX % tileWidth;
				float offY = data.scrollY % tileHeight;
				float mX = Input.getLastTouchX() + offX;
				float mY = Input.getLastTouchY() + offY;
				mX -= preview.getColumns() * tileWidth / 2;
				mY -= preview.getRows() * tileHeight / 2;
				preview.setScrollX(-(int)(mX / tileWidth + 0.5) * tileWidth + offX);
				preview.setScrollY(-(int)(mY / tileHeight + 0.5) * tileHeight + offY);
				previewMask.setX(-preview.getScrollX());
				previewMask.setY(-preview.getScrollY());
				preview.setVisible(true);

				if (data.mode == MODE_EDIT) {
					if (Input.isTapped()) {
						cancel.setVisible(true);
						if (Input.getLastTouchY() < Graphics.getHeight() / 2) {
							cancel.setY(Graphics.getHeight() - cancel.getHeight());
						} else {
							cancel.setY(0);
						}
						if (Input.getLastTouchX() < Graphics.getWidth() / 2) {
							cancel.setX(Graphics.getWidth() - cancel.getWidth());
						} else {
							cancel.setX(0);
						}
					}
					preview.setVisible(!cancel.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY()));
					if (preview.isVisible()) {
						cancel.setColor(Color.argb(150, 150, 150, 150));
					} else {
						cancel.setColor(Color.argb(255, 255, 0, 0));
					}
				}

				if (data.mode == MODE_DRAW) {
					draw();
				}
			} else {
				preview.setVisible(false);
				cancel.setVisible(false);
			}
			previewMask.setVisible(preview.isVisible());
		}

		borderRight.setOriginX(-map.getWidth() + data.scrollX);
	}

	@Override
	public void save(Activity parent) {
		Data.saveObject(mapName + DATA, parent, data);
		Data.saveObject(mapName + MAP, parent, map);
	}

	public void saveFinal(Activity parent) {
		if (!Data.saveObject(GameMaker.PREFIX + mapName, parent, map))
			throw new RuntimeException("Save Failed");
	}

	@Override
	public void load(Activity parent) {
		data = (PlatformData)Data.loadObject(mapName + DATA, parent);
		map = (PlatformMap)Data.loadObject(mapName + MAP, parent);
		
		if (map == null) {
			map = (PlatformMap)Data.loadObject(GameMaker.PREFIX + mapName, parent);
		}		
		if (data == null) {
			data = new PlatformData();
		}
	}

	public void loadFinal(Activity parent) {
		PlatformMap map = (PlatformMap)Data.loadObject(GameMaker.PREFIX + mapName, parent);
		if (map != null) {
			this.map = map;
			data = new PlatformData();
			save(parent);
			Graphics.reset();
			loadSprites();
		} else {
			throw new RuntimeException("Load Failed!");
		}
	}

	private void draw() {
		int tileWidth = map.tileset.tileWidth;
		int tileHeight = map.tileset.tileHeight;
		Rect sRect = holder.getRect();
		int column = (int)(tilemaps.get(data.layer).getScrollX() - preview.getScrollX() + 0.5) / tileWidth;
		int row = (int)(tilemaps.get(data.layer).getScrollY() - preview.getScrollY() + 0.5) / tileHeight;
		Action action = new Action(data.layer, sRect, row, column);
		doAction(action);
	}
	
	private void doAction(Action action) {
		while (data.actions.size() > data.editIndex + 1)
			data.actions.remove(data.actions.size() - 1);
		if (data.editIndex < data.actions.size() && data.editIndex >= 0 && 
				data.actions.get(data.editIndex).equals(action))
			return;
		data.actions.add(action);
		redoAction();
	}
	
	private void redoAction() {
		data.editIndex++;
		Action action = data.actions.get(data.editIndex);
		int[][] tiles = map.layers.get(action.layer).tiles;
		int[][] oldTiles = new int[action.rows][action.cols];
		for (int i = 0; i < action.rows; i++) {
			for (int j = 0; j < action.cols; j++) {
				int r = i + action.destRow, c = j + action.destCol;
				if (r < 0 || c < 0 || r >= tiles.length || c >= tiles[i].length)
					continue;
				oldTiles[i][j] = tiles[r][c];
				tiles[r][c] = (action.srcRow + i) * map.tileset.columns + (action.srcCol + j);
			}
		}
		tilemaps.get(action.layer).setMap(tiles);
		action.previous = oldTiles;
	}
	
	private void undoAction() {
		Action action = data.actions.get(data.editIndex);
		int[][] tiles = map.layers.get(action.layer).tiles;
		int[][] oldTiles = action.previous;
		for (int i = 0; i < action.rows; i++) {
			for (int j = 0; j < action.cols; j++) {
				int r = i + action.destRow, c = j + action.destCol;
				if (r < 0 || c < 0 || r >= tiles.length || c >= tiles[i].length)
					continue;
				tiles[r][c] = oldTiles[i][j];
			}
		}
		tilemaps.get(action.layer).setMap(tiles);
		data.editIndex--;
	}
	

	private boolean touchingSprite(Sprite sprite) {
		return sprite.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY());
	}
	
	private void loadSprites() {
		Viewport.DefaultViewport.setZ(100);

		menu = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, 0, 50, 50);
		menu.getBitmap().eraseColor(Color.BLUE);
		Helper.drawCenteredText(menu, "Select", Color.BLACK, 16);
		move = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, 50, 50, 50);
		move.getBitmap().eraseColor(Color.GREEN);
		Helper.drawCenteredText(move, "Move", Color.BLACK, 16);
		edit = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, 100, 50, 50);
		edit.getBitmap().eraseColor(Color.RED);
		Helper.drawCenteredText(edit, "Edit", Color.BLACK, 16);
		draw = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, 150, 50, 50);
		draw.getBitmap().eraseColor(Color.YELLOW);
		Helper.drawCenteredText(draw, "Draw", Color.BLACK, 16);
		cancel = new Sprite(Viewport.DefaultViewport, 0, 0, 75, 75);
		cancel.getBitmap().eraseColor(Color.WHITE);
		cancel.setZ(10);
		cancel.setVisible(false);
		Helper.drawCenteredText(cancel, "CANCEL", Color.BLACK, 20);
		undo = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 150, Graphics.getHeight() - 50, 50, 50);
		undo.getBitmap().eraseColor(Color.argb(255, 100, 100, 200));
		Helper.drawCenteredText(undo, "Undo", Color.BLACK, 16);
		undoMask = new Sprite(Viewport.DefaultViewport, undo.getX(), undo.getY(), (int)undo.getWidth(), (int)undo.getHeight());
		undoMask.getBitmap().eraseColor(Color.argb(175, 0, 0, 0));
		redo = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 100, Graphics.getHeight() - 50, 50, 50);
		redo.getBitmap().eraseColor(Color.argb(255, 100, 200, 100));
		Helper.drawCenteredText(redo, "Redo", Color.BLACK, 16);
		redoMask = new Sprite(Viewport.DefaultViewport, redo.getX(), redo.getY(), (int)redo.getWidth(), (int)redo.getHeight());
		redoMask.getBitmap().eraseColor(Color.argb(175, 0, 0, 0));

		selection = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, data.mode * 50 + 50, 50, 50);
		selection.setZ(10);

		layerUp = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, Graphics.getHeight() - 100, 50, 50);
		layerDown = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, Graphics.getHeight() - 50, 50, 50);
		drawChangeLayer();

		borderRight = new Sprite(Viewport.DefaultViewport, 0, 0, (int)menu.getWidth(), Graphics.getHeight());
		borderRight.getBitmap().eraseColor(Color.GRAY);
		borderRight.setZ(-10);
		borderRight.setVisible(false);

		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		selection.getBitmapCanvas().drawRect(0, 0, 50, 50, paint);
		paint.setColor(Color.BLACK);
		selection.getBitmapCanvas().drawRect(1, 1, 49, 49, paint);

		buttons = new Sprite[] {menu, move, edit, draw, selection, layerUp, layerDown, undo, redo, undoMask, redoMask};
		
		tilemaps = new ArrayList<Tilemap>(map.layers.size()); 
		Tileset tileset = map.tileset;
		Rect rect = new Rect();
		rect.set(0, 0, Graphics.getWidth(), Graphics.getHeight());
		for (int i = 0; i < map.layers.size(); i++) {
			PlatformLayer layer = map.layers.get(i);
			Tilemap tm = new Tilemap(Data.loadBitmap(tileset.bitmapId), 
					tileset.tileWidth, tileset.tileHeight, tileset.tileSpacing, 
					layer.tiles, rect, i);
			tm.setShowingGrid(true);
			tm.scroll(data.scrollX, data.scrollY);
			if (i < data.layer) {
				tm.setColor(DARK);
			} else if (i > data.layer) {
				tm.setOpacity(TRANS);
			}
			tilemaps.add(tm);
		}

		if (!holder.getRect().isEmpty()) {
			Bitmap bmp = Data.loadBitmap(tileset.bitmapId);
			Rect sRect = holder.getRect();
			int[][] tiles = new int[sRect.height()][sRect.width()];
			for (int i = 0; i < tiles.length; i++) {
				for (int j = 0; j < tiles[i].length; j++) {
					tiles[i][j] = (sRect.top + i) * tileset.columns + (sRect.left + j); 
				}
			}
			preview = new Tilemap(bmp, tileset.tileWidth, tileset.tileHeight, 
					tileset.tileSpacing, tiles, rect, 50);
			preview.setVisible(false);
			previewMask = new Sprite(preview.getViewport(), 0, 0, preview.getWidth(), preview.getHeight());
			previewMask.setZ(-20);
			previewMask.getBitmap().eraseColor(Color.BLACK);
		}
	}

	private void drawChangeLayer() {
		layerUp.getBitmap().eraseColor(Color.LTGRAY);
		String luText = (data.layer == map.layers.size() - 1) ? "-" : "" + (data.layer + 1);
		Helper.drawCenteredText(layerUp, luText, Color.BLACK, 20);
		layerDown.getBitmap().eraseColor(Color.DKGRAY);
		String ldText = (data.layer == 0) ? "-" : "" + (data.layer - 1);
		Helper.drawCenteredText(layerDown, ldText, Color.BLACK, 20);
	}

	public static abstract class RectHolder {
		public abstract Rect getRect();
		public abstract void newRect(int bitmapId, int tileWidth, int tileHeight);
	}
	
	private static class Action implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public int layer;
		public int destRow, destCol, srcRow, srcCol, rows, cols;
		public int[][] previous;
		
		public Action(int layer, Rect srcRect, int destRow, int destCol) {
			this.layer = layer;
			this.srcRow = srcRect.top;
			this.srcCol = srcRect.left;
			this.rows = srcRect.height();
			this.cols = srcRect.width();
			this.destRow = destRow;
			this.destCol = destCol;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof Action) {
				Action a = (Action)o;
				return a.layer == layer &&
					a.destRow == destRow &&
					a.destCol == destCol &&
					a.srcRow == srcRow &&
					a.srcCol == srcCol &&
					a.rows == rows &&
					a.cols == cols;
			}
			return false;
		}
	}

	private static class PlatformData implements Serializable {
		private static final long serialVersionUID = 5L;

		public ArrayList<Action> actions = new ArrayList<Action>();
		public int editIndex = -1;
		public int layer = 1, mode = MODE_MOVE;
		public float scrollX, scrollY;
	}
}
