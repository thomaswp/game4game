package edu.elon.honors.price.maker;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Environment;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.PlatformLayer;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.data.Tileset;
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

	public static final String MAP = "_map";
	public static final String DATA = "_data";

	private static final int DARK = Color.argb(255, 150, 150, 150);
	private static final float TRANS = 0.5f;

	private PlatformGame game;
	private PlatformMap map;
	private PlatformData data;
	private ArrayList<Tilemap> tilemaps;
	private Tilemap texturePreview;
	private Sprite actorPreview;
	private Sprite texturePreviewMask, undoMask, redoMask, borderRight;
	private Sprite menu, move, edit, draw, selection, cancel, layerUp, layerDown, undo, redo;
	private Sprite[] buttons;
	private RectHolder rectHolder;
	private float startScrollX, startScrollY;
	private int hold = 500;
	private boolean scrolling, menuTap;
	private String mapName;
	private Viewport actorViewport;
	private Sprite[][] actors;
	private ActorHolder actorHolder;

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	public PlatformMakerLogic(String mapName, RectHolder holder, ActorHolder actorHolder) {
		this.mapName = mapName;
		this.rectHolder = holder;
		this.actorHolder = actorHolder;
	}

	@Override
	public void initialize() {
		Graphics.setBackgroundColor(Color.WHITE);

		loadSprites();

		if (data.actorLayer) {
			if (actorHolder.getActorId() < -1)
				data.mode = MODE_MOVE;
			else if (data.mode == MODE_MOVE)
				data.mode = MODE_EDIT;
		} else {
			if (texturePreview == null)
				data.mode = MODE_MOVE;
			else if (data.mode == MODE_MOVE)
				data.mode = MODE_EDIT;
		}

	}

	@Override
	public void update(long timeElapsed) {

		updateButtons(timeElapsed);

		if (Input.isTapped() && Input.getLastTouchX() < menu.getX() &&
				(Input.getLastTouchY() < undo.getY() || Input.getLastTouchX() < undo.getX()))
			menuTap = false;

		if (menuTap) {
			if (Input.isTapped())
				hold = 1;
		} else {

			if (Input.isTapped()) {
				startScrollX = data.scrollX;
				startScrollY = data.scrollY;
				scrolling = true;
			}

			if (data.mode == MODE_MOVE) {
				updateMove();
			} else if (data.mode == MODE_EDIT || data.mode == MODE_DRAW) {
				if (data.actorLayer)
					updateEditActor();
				else
					updateEditTexture();
			}
		}


		if (!menuTap) {
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i] != null) {
					buttons[i].setOpacity(Input.isTouchDown() ? 0.2f : 1f);
				}
			}
		}

		actorViewport.setOpacity(data.actorLayer ? 1 : TRANS);
		for (int i = 0; i < tilemaps.size(); i++) {
			Tilemap tilemap = tilemaps.get(i);
			tilemap.setColor((data.actorLayer || i < data.layer) ? DARK : Color.WHITE);
			tilemap.setOpacity(i > data.layer ? TRANS : 1);
			if (i == map.layers.length - 1) {
				if (data.actorLayer) {
					tilemap.setShowingGrid(true);
					tilemap.getGrid().setOpacity(TRANS);
				} else {
					tilemap.setShowingGrid(i == data.layer);
					tilemap.getGrid().setOpacity(1);
				}
			} else {
				tilemap.setShowingGrid(i == data.layer);
			}

		}

		selection.setY(data.mode * 50 + 50);

		undoMask.setVisible(!(data.editIndex  >= 0));
		redoMask.setVisible(!(data.editIndex < data.actions.size() - 1));

		borderRight.setOriginX(-game.getMapWidth(map) + data.scrollX);

		actorViewport.setX(-data.scrollX);
		actorViewport.setY(-data.scrollY);
	}

	@Override
	public void save() {
		Game.saveObject(mapName + DATA, data);
		Game.saveObject(mapName + MAP, game);
	}

	public void saveFinal() {
		if (!Game.saveObject(GameMaker.PREFIX + mapName, game))
			throw new RuntimeException("Save Failed");
	}

	@Override
	public void load() {
		data = (PlatformData)Game.loadObject(mapName + DATA);
		game = (PlatformGame)Game.loadObject(mapName + MAP);

		if (game == null) {
			game = (PlatformGame)Game.loadObject(GameMaker.PREFIX + mapName);
			if (game == null) {
				game = new PlatformGame();
			}
		}		
		if (data == null) {
			data = new PlatformData();
		}

		map = game.maps.get(game.startMapId);
	}

	public void loadFinal() {
		PlatformGame game = (PlatformGame)Game.loadObject(GameMaker.PREFIX + mapName);
		if (game != null) {
			this.game = game;
			map = game.maps.get(game.startMapId);
			data = new PlatformData();
			save();
			Graphics.reset();
			loadSprites();
		} else {
			throw new RuntimeException("Load Failed!");
		}
	}

	private Tileset getTileset() {
		return game.getMapTileset(map);
	}

	private void updateButtons(long timeElapsed) {
		if (hold > 0) {
			hold -= timeElapsed;
		} else {
			if (touchingSprite(menu)) {
				if (Input.isTapped()) {
					if (data.actorLayer) {
						actorHolder.newActor(game);
					} else {
						rectHolder.newRect(getTileset().bitmapName, getTileset().tileWidth, getTileset().tileHeight);
					}
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
					if ((data.actorLayer && actorPreview != null) || 
							(!data.actorLayer && texturePreview != null)) {
						data.mode = MODE_EDIT;
					}
					menuTap = true;
				}
			}
			if (touchingSprite(draw)) {
				if (Input.isTapped()) {
					if ((data.actorLayer && actorPreview != null) || 
							(!data.actorLayer && texturePreview != null)) {
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
				if (changeLayer > 0) {
					if (data.layer < map.layers.length - 1) {
						data.layer++;
					} else {
						data.actorLayer = true;
						if (actorHolder.getActorId() < 0)
							data.mode = MODE_MOVE;
					}
				} else {
					if (data.layer > 0) {
						if (data.actorLayer) {
							data.actorLayer = false;
							if (rectHolder.getRect().isEmpty())
								data.mode = MODE_MOVE;
						} else {
							data.layer--;
						}
					}
				}
				drawChangeLayer();
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
	}

	private void updateMove() {
		if (Input.isTouchDown()) {
			if (scrolling) {
				data.scrollX = startScrollX - Input.getDistanceTouchX();
				data.scrollY = startScrollY - Input.getDistanceTouchY();

			}
		} else {
			scrolling = false;
		}
		float maxX = -Graphics.getWidth() + game.getMapWidth(map) + menu.getWidth();
		if (data.scrollX > maxX) {
			startScrollX -= data.scrollX - maxX;
			data.scrollX = maxX;
		} else if (data.scrollX < 0) {
			startScrollX -= data.scrollX;
			data.scrollX = 0;
		}
		float maxY = -Graphics.getHeight() + game.getMapHeight(map);
		if (data.scrollY > maxY) {	
			startScrollY -= data.scrollY - maxY;
			data.scrollY = maxY;
		} else if (data.scrollY < 0) {
			startScrollY -= data.scrollY;
			data.scrollY = 0;
		}
		for (int i = 0; i < tilemaps.size(); i++) {
			tilemaps.get(i).setScrollX((int)data.scrollX);
			tilemaps.get(i).setScrollY((int)data.scrollY);
		}
		if (texturePreview != null) {
			texturePreview.setVisible(false);
		}
		if (actorPreview != null) {
			actorPreview.setVisible(false);
		}
		cancel.setVisible(false);
	}

	private void updateEditTexture() {
		int tileWidth = getTileset().tileWidth;
		int tileHeight = getTileset().tileHeight;

		if (texturePreview.isVisible() && !Input.isTouchDown()) {
			if (!cancel.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY()))
				drawTexture();
		}
		if (Input.isTouchDown()) {
			float offX = data.scrollX % tileWidth;
			float offY = data.scrollY % tileHeight;
			float mX = Input.getLastTouchX() + offX;
			float mY = Input.getLastTouchY() + offY;
			mX -= texturePreview.getColumns() * tileWidth / 2;
			mY -= texturePreview.getRows() * tileHeight / 2;
			texturePreview.setScrollX(-(int)(mX / tileWidth + 0.5) * tileWidth + offX);
			texturePreview.setScrollY(-(int)(mY / tileHeight + 0.5) * tileHeight + offY);
			texturePreviewMask.setX(-texturePreview.getScrollX());
			texturePreviewMask.setY(-texturePreview.getScrollY());
			texturePreview.setVisible(true);

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
				texturePreview.setVisible(!cancel.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY()));
				if (texturePreview.isVisible()) {
					cancel.setColor(Color.argb(150, 150, 150, 150));
				} else {
					cancel.setColor(Color.argb(255, 255, 0, 0));
				}
			}

			if (data.mode == MODE_DRAW) {
				drawTexture();
			}
		} else {
			texturePreview.setVisible(false);
			cancel.setVisible(false);
		}
		texturePreviewMask.setVisible(texturePreview.isVisible());
	}

	private void updateEditActor() {
		int tileWidth = getTileset().tileWidth;
		int tileHeight = getTileset().tileHeight;

		if (actorPreview.isVisible() && !Input.isTouchDown()) {
			if (!cancel.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY()))
				drawActor();
		}
		if (Input.isTouchDown()) {
			int x = (int)((data.scrollX + Input.getLastTouchX() - actorPreview.getWidth() / 2) / tileWidth) * tileWidth;
			int y = (int)((data.scrollY + Input.getLastTouchY() - actorPreview.getHeight() / 2) / tileHeight) * tileHeight;
			actorPreview.setX(x);
			actorPreview.setY(y);
			actorPreview.setVisible(true);

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
				actorPreview.setVisible(!cancel.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY()));
				if (actorPreview.isVisible()) {
					cancel.setColor(Color.argb(150, 150, 150, 150));
				} else {
					cancel.setColor(Color.argb(255, 255, 0, 0));
				}
			}

			if (data.mode == MODE_DRAW) {
				drawActor();
			}
		} else {
			actorPreview.setVisible(false);
			cancel.setVisible(false);
		}
	}

	private void drawActor() {
		int tileWidth = getTileset().tileWidth;
		int tileHeight = getTileset().tileHeight;
		int column = (int)(actorPreview.getX() / tileWidth + 0.5);
		int row = (int) (actorPreview.getY() / tileHeight + 0.5);
		if (map.actorLayer.tiles[row][column] == -1) return;
		Action action = new Action(data.layer, null, row, column, actorHolder.getActorId());
		doAction(action);
	}

	private void drawTexture() {
		int tileWidth = getTileset().tileWidth;
		int tileHeight = getTileset().tileHeight;
		Rect sRect = rectHolder.getRect();
		int column = (int)((tilemaps.get(data.layer).getScrollX() - texturePreview.getScrollX()) / tileWidth + 0.5f);
		int row = (int)((tilemaps.get(data.layer).getScrollY() - texturePreview.getScrollY()) / tileHeight + 0.5f);
		Action action = new Action(data.layer, sRect, row, column, 0);
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
		if (action.rows > 0) {
			int[][] tiles = map.layers[action.layer].tiles;
			int[][] oldTiles = new int[action.rows][action.cols];
			for (int i = 0; i < action.rows; i++) {
				for (int j = 0; j < action.cols; j++) {
					int r = i + action.destRow, c = j + action.destCol;
					if (r < 0 || c < 0 || r >= tiles.length || c >= tiles[i].length)
						continue;
					oldTiles[i][j] = tiles[r][c];
					tiles[r][c] = (action.srcRow + i) * getTileset().columns + (action.srcCol + j);
				}
			}
			tilemaps.get(action.layer).setMap(tiles);
			action.previous = oldTiles;
		} else {
			action.previousId = map.getActorType(action.destRow, action.destCol);
			if (action.previousId != action.actorId) {
				if (action.actorId == -1) {
					for (int i = 0; i < map.rows; i++) {
						for (int j = 0; j < map.columns; j++) {
							if (map.getActorType(i, j) == -1) {
								action.previousHeroRow = i;
								action.previousHeroCol = j;
								drawActor(i, j, 0);
							}
						}
					}
				}
				drawActor(action.destRow, action.destCol, action.actorId);
			}
		}
	}

	private void undoAction() {
		Action action = data.actions.get(data.editIndex);
		if (action.rows > 0) {
			int[][] tiles = map.layers[action.layer].tiles;
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
		} else {
			if (action.previousId != action.actorId) {
				drawActor(action.destRow, action.destCol, action.previousId);
				if (action.actorId == -1) {
					drawActor(action.previousHeroRow, action.previousHeroCol, -1);
				}
			}
		}
		data.editIndex--;
	}


	private boolean touchingSprite(Sprite sprite) {
		return sprite.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY());
	}

	private void drawActor(int row, int column, int newId) {
		int instance = map.setActor(row, column, newId);
		if (actors[row][column] != null) {
			actors[row][column].dispose();
		}
		if (newId != 0) {
			int type = map.getActorType(row, column);
			PlatformActor actor = type > 0 ? game.actors[type] : game.hero;
			Bitmap bmp = Data.loadActor(actor.imageName);
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
			
			String text = "" + instance;
			Canvas canvas = new Canvas();
			canvas.setBitmap(bmp);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			paint.setTextSize(12);
			paint.setAntiAlias(true);
			canvas.drawRect(0, bmp.getHeight() - paint.getTextSize(), 
					paint.measureText(text), bmp.getHeight(), paint);
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.STROKE);
			canvas.drawText(text, 0, bmp.getHeight(), paint);
			
			actors[row][column] = new Sprite(actorViewport, bmp); 
			actors[row][column].setX(column * getTileset().tileWidth);
			actors[row][column].setY(row * getTileset().tileHeight);
		}
	}


	private void loadSprites() {
		Viewport.DefaultViewport.setZ(100);

		actorViewport = new Viewport(-data.scrollX, -data.scrollY, map.columns * getTileset().tileWidth, map.rows * getTileset().tileHeight);
		actorViewport.setOpacity(TRANS);
		actorViewport.setZ(10);
		actors = new Sprite[map.rows][map.columns];
		for (int i = 0; i < actors.length; i++) {
			for (int j = 0; j < actors[i].length; j++) {
				drawActor(i, j, map.getActorType(i, j));
			}
		}

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

		tilemaps = new ArrayList<Tilemap>(map.layers.length); 
		Tileset tileset = getTileset();
		Rect rect = new Rect();
		rect.set(0, 0, Graphics.getWidth(), Graphics.getHeight());
		for (int i = 0; i < map.layers.length; i++) {
			PlatformLayer layer = map.layers[i];
			Tilemap tm = new Tilemap(Data.loadTileset(tileset.bitmapName), 
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

		if (!rectHolder.getRect().isEmpty()) {
			Bitmap bmp = Data.loadTileset(tileset.bitmapName);
			Rect sRect = rectHolder.getRect();
			int[][] tiles = new int[sRect.height()][sRect.width()];
			for (int i = 0; i < tiles.length; i++) {
				for (int j = 0; j < tiles[i].length; j++) {
					tiles[i][j] = (sRect.top + i) * tileset.columns + (sRect.left + j); 
				}
			}
			texturePreview = new Tilemap(bmp, tileset.tileWidth, tileset.tileHeight, 
					tileset.tileSpacing, tiles, rect, 50);
			texturePreview.setVisible(false);
			texturePreviewMask = new Sprite(texturePreview.getViewport(), 0, 0, texturePreview.getWidth(), texturePreview.getHeight());
			texturePreviewMask.setZ(-20);
			texturePreviewMask.getBitmap().eraseColor(Color.BLACK);
		}

		if (actorHolder.getActorId() >= -1) {
			int id = actorHolder.getActorId();
			Bitmap mask;
			if (id != 0) {
				PlatformActor actor = id > 0 ? game.actors[id] : game.hero; 
				Bitmap actorBmp = Data.loadActor(actor.imageName);
				mask = Bitmap.createBitmap(actorBmp.getWidth() / 4, actorBmp.getHeight() / 4, Sprite.getDefaultConfig());
				mask.eraseColor(Color.BLACK);
				Canvas canvas = new Canvas();
				canvas.setBitmap(mask);
				paint.setColor(Color.WHITE);
				canvas.drawBitmap(actorBmp, 0, 0, paint);
			} else {
				mask = Bitmap.createBitmap(getTileset().tileWidth, getTileset().tileHeight, Sprite.getDefaultConfig());
				mask.eraseColor(Color.BLACK);
			}
			actorPreview = new Sprite(actorViewport, mask);
			actorPreview.setZ(10);
			actorPreview.setVisible(false);
		}
	}

	private void drawChangeLayer() {
		layerUp.getBitmap().eraseColor(Color.LTGRAY);
		String luText = (data.layer == map.layers.length - 1) ? "A" : "" + (data.layer + 1);
		if (data.actorLayer) luText = "-";
		Helper.drawCenteredText(layerUp, luText, Color.BLACK, 20);
		layerDown.getBitmap().eraseColor(Color.DKGRAY);
		String ldText = (data.layer == 0) ? "-" : "" + (data.layer - 1);
		Helper.drawCenteredText(layerDown, ldText, Color.BLACK, 20);
	}

	public static abstract class RectHolder {
		public abstract Rect getRect();
		public abstract void newRect(String bitmapName, int tileWidth, int tileHeight);
	}

	public static abstract class ActorHolder {
		public abstract int getActorId();
		public abstract void newActor(PlatformGame game);
	}

	private static class Action implements Serializable {
		private static final long serialVersionUID = 2L;

		public int layer;
		public int destRow, destCol, srcRow, srcCol, rows, cols, actorId, previousId,
			previousHeroRow, previousHeroCol;
		public int[][] previous;

		public Action(int layer, Rect srcRect, int destRow, int destCol, int actorId) {
			this.layer = layer;
			if (srcRect != null) {
				this.srcRow = srcRect.top;
				this.srcCol = srcRect.left;
				this.rows = srcRect.height();
				this.cols = srcRect.width();
			}
			this.destRow = destRow;
			this.destCol = destCol;
			this.actorId = actorId;
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
				a.cols == cols &&
				a.actorId == actorId &&
				a.previousHeroRow == previousHeroRow &&
				a.previousHeroCol == previousHeroCol;
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
		public boolean actorLayer;
	}
}
