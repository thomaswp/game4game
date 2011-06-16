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
import edu.elon.honors.price.data.ResourceProvider;
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

public class PlatformMaker implements Logic {

	public static final int MODE_MOVE = 0;
	public static final int MODE_EDIT = 1;

	public static final int DARK = Color.argb(255, 150, 150, 150);
	public static final float TRANS = 0.5f;


	private PlatformMap map;
	private PlatformData data;
	private ArrayList<Tilemap> tilemaps;
	private Tilemap preview;
	private Sprite previewMask;
	private Sprite menu, move, edit, selection, layerUp, layerDown;
	private Sprite borderRight;
	private RectHolder holder;
	private float startScrollX, startScrollY;
	private int mode;
	private int hold = 500;
	private boolean scrolling;

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub

	}

	public PlatformMaker(RectHolder holder) {
		this.holder = holder;
		if (holder.getRect().width() > 0 && holder.getRect().height() > 0) {
			mode = MODE_EDIT;
		}
		Graphics.setBackgroundColor(Color.WHITE);
	}

	@Override
	public void initialize() {
		if (map == null)
			map = new PlatformMap();
		if (data == null)
			data = new PlatformData();

		loadSprites();
	}

	@Override
	public void update(long timeElapsed) {
		selection.setY(mode * 50 + 50);

		if (hold > 0) {
			hold -= timeElapsed;
		} else {
			if (menu.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY())) {
				if (Input.isTapped()) {
					holder.newRect(map.tileset.bitmapId, map.tileset.tileWidth, map.tileset.tileHeight);
				}
				return;
			}
			if (move.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY())) {
				if (Input.isTapped()) {
					mode = MODE_MOVE;
				}
				return;
			}
			if (edit.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY())) {
				if (Input.isTapped()) {
					if (preview != null) {
						mode = MODE_EDIT;
					}
				}
				return;
			}
			int changeLayer = 0;
			if (Input.isTapped()) {
				if (layerUp.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY())) {
					changeLayer = 1;
				} else if (layerDown.getRect().contains(Input.getLastTouchX(), Input.getLastTouchY())) {
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
				return;
			}
		}

		if (Input.isTapped()) {
			startScrollX = data.scrollX;
			startScrollY = data.scrollY;
			scrolling = true;
		} 

		if (mode == MODE_MOVE) {
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
		} else if (mode == MODE_EDIT) {
			int tileWidth = map.tileset.tileWidth;
			int tileHeight = map.tileset.tileHeight;
			if (preview.isVisible() && !Input.isTouchDown()) {
				Rect sRect = holder.getRect();
				int column = (int)(tilemaps.get(data.layer).getScrollX() - preview.getScrollX() + 0.5) / tileWidth;
				int row = (int)(tilemaps.get(data.layer).getScrollY() - preview.getScrollY() + 0.5) / tileHeight;
				int[][] tiles = map.layers.get(data.layer).tiles;
				for (int i = 0; i < sRect.height(); i++) {
					for (int j = 0; j < sRect.width(); j++) {
						int r = i + row, c = j + column;
						if (r < 0 || c < 0 || r >= tiles.length || c >= tiles[i].length)
							continue;

						tiles[r][c] = (sRect.top + i) * map.tileset.columns + (sRect.left + j);
					}
				}
				tilemaps.get(data.layer).setMap(tiles);
			}
			if (Input.isTouchDown() && Input.getLastTouchX() < menu.getX()) {
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
			} else {
				preview.setVisible(false);
			}
			previewMask.setVisible(preview.isVisible());
		}
		
		borderRight.setOriginX(-map.getWidth() + data.scrollX);
	}

	@Override
	public void save(Activity parent) {
		Data.saveObject("data", parent, data);
		Data.saveObject("map", parent, map);
	}

	public void saveFinal(Activity parent) {
		Data.saveObjectPublic("map-final", parent, map);
	}

	@Override
	public void load(Activity parent) {
		data = (PlatformData)Data.loadObject("data", parent);
		map = (PlatformMap)Data.loadObject("map", parent);
	}
 
	public void loadFinal(Activity parent) {
		map = (PlatformMap)Data.loadObjectPublic("map-final", parent);
		Graphics.reset();
		loadSprites();
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
		selection = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, mode * 50 + 50, 50, 50);
		Helper.drawCenteredText(edit, "Edit", Color.BLACK, 16);
		selection.setZ(10);

		layerUp = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, Graphics.getHeight() - 100, 50, 50);
		layerDown = new Sprite(Viewport.DefaultViewport, Graphics.getWidth() - 50, Graphics.getHeight() - 50, 50, 50);
		drawChangeLayer();

		borderRight = new Sprite(Viewport.DefaultViewport, 0, 0, (int)menu.getWidth(), Graphics.getHeight());
		borderRight.getBitmap().eraseColor(Color.GRAY);
		borderRight.setZ(-10);

		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		selection.getBitmapCanvas().drawRect(0, 0, 50, 50, paint);
		paint.setColor(Color.BLACK);
		selection.getBitmapCanvas().drawRect(1, 1, 49, 49, paint);

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

		if (mode == MODE_EDIT) {
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

//	private void darken(Tilemap tilemap) {
//		float[] mat = new float[] {
//				DARK, 0, 0, 0, 0,
//				0, DARK, 0, 0, 0,
//				0, 0, DARK, 0, 0,
//				0, 0, 0, 1, 0
//		};
//		applyMatrix(tilemap, mat);
//	}
//
//	private void lighten(Tilemap tilemap) {
//		float[] mat = new float[] {
//				1 / DARK, 0, 0, 0, 0,
//				0, 1 / DARK, 0, 0, 0,
//				0, 0, 1 / DARK, 0, 0,
//				0, 0, 0, 1, 0
//		};
//		applyMatrix(tilemap, mat);
//	}
//
//	private void transluce(Tilemap tilemap) {
//		float[] mat = new float[] {
//				1, 0, 0, 0, 0,
//				0, 1, 0, 0, 0,
//				0, 0, 1, 0, 0,
//				0, 0, 0, TRANS, 0
//		};
//		applyMatrix(tilemap, mat);
//	}
//
//	private void opace(Tilemap tilemap) {
//		float[] mat = new float[] {
//				1, 0, 0, 0, 0,
//				0, 1, 0, 0, 0,
//				0, 0, 1, 0, 0,
//				0, 0, 0, 1 / TRANS, 0
//		};
//		applyMatrix(tilemap, mat);
//	}
//
//	private void applyMatrix(Tilemap tilemap, float[] mat) {
//		ColorMatrix cm = new ColorMatrix(mat);
//		Sprite[][] sprites = tilemap.getSprites();
//		Paint paint = new Paint();
//		paint.setColorFilter(new ColorMatrixColorFilter(cm));
//		for (int i = 0; i < sprites.length; i++) {
//			for (int j = 0; j < sprites[i].length; j++) {
//				if (sprites[i][j] != null) {
//					Bitmap bmp = sprites[i][j].getBitmap();
//					Bitmap newBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Sprite.getDefaultConfig());
//					sprites[i][j].setBitmap(newBmp);
//					sprites[i][j].getBitmapCanvas().drawBitmap(bmp, 0, 0, paint);
//				}
//			}
//		}
//	}

	public static abstract class RectHolder {
		public abstract Rect getRect();
		public abstract void newRect(int bitmapId, int tileWidth, int tileHeight);
	}

	private static class PlatformData implements Serializable {
		private static final long serialVersionUID = 3L;

		public int layer = 1;
		public float scrollX, scrollY;
	}
}
