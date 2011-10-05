package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Cache;
import edu.elon.honors.price.input.Input;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SelectorMapBase extends Activity {

	protected PlatformGame game;
	protected MapView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		view = getMapView(game);
		setContentView(view);

		super.onCreate(savedInstanceState);
	}

	protected MapView getMapView(PlatformGame game) {
		return new MapView(this, game);
	}

	@Override
	public void onBackPressed() {
		if (hasChanged()) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Keep Changes?")
			.setMessage("Do you want to keep the changes you made to this page?")
			.setPositiveButton("Keep Changes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finishOk();
				}

			})
			.setNeutralButton("Discard Changes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}

			})
			.setNegativeButton("Stay Here", null)
			.show();	
		} else {
			finish();
		}
	}

	protected boolean hasChanged() {
		return false;
	}

	protected void finishOk() {
		finish();
	}

	protected static class MapView extends BasicCanvasView {

		protected final static int MODE_MOVE = 0;
		protected final static int MODE_SELECT = 1;
		
		protected PlatformGame game;
		private Paint paint;
		protected float offX, offY;
		private float startDragOffX, startDragOffY;
		private boolean moving;
		protected Bitmap[] tiles;
		protected Bitmap[] actors;
		private Bitmap grid;
		protected boolean showRightButton;
		protected boolean rightButtonDown;
		protected boolean showLeftButton;
		protected boolean leftButtonDown;
		protected int mode;

		public int getButtonRad() {
			return height / 5;
		}

		public int getButtonBorder() {
			return getButtonRad() / 4;
		}

		protected String getRightButtonText() {
			return "Ok";
		}

		protected String getLeftButtonText() {
			switch (mode) {
			case MODE_MOVE: return "Move";
			case MODE_SELECT: return "Select";
			}
			return "";
		}
		
		protected boolean shouldMove() {
			return !showLeftButton || mode == MODE_MOVE; 
		}
		
		protected boolean shouldSelect() {
			return !showLeftButton || mode == MODE_SELECT;
		}

		public MapView(Context context, PlatformGame game) {
			super(context);
			this.game = game;
			paint = new Paint();
			showRightButton = true;

			Tileset tileset = game.tilesets[game.getSelectedMap().tilesetId];
			Bitmap tilesetBmp = Data.loadTileset(tileset.bitmapName, getContext());
			tiles = createTiles(tilesetBmp, tileset.tileWidth, tileset.tileHeight, 0);
			createActors();
		}

		protected boolean isInRightButton(float x, float y) {
			if (!showRightButton) return false;

			int rad = getButtonRad();
			int border = getButtonBorder();

			float dx = x - (width - border);
			float dy = y - (height - border);

			return (dx * dx + dy * dy <= rad * rad);
		}
		
		protected boolean isInLeftButton(float x, float y) {
			if (!showLeftButton) return false;

			int rad = getButtonRad();
			int border = getButtonBorder();

			float dx = x - (border);
			float dy = y - (height - border);

			return (dx * dx + dy * dy <= rad * rad);
		}

		protected boolean doSelection() {
			return false;
		}
		
		@Override
		protected void update(long timeElapsed) {
			Input.update(timeElapsed);

			int width = game.getMapWidth(game.getSelectedMap());
			int height = game.getMapHeight(game.getSelectedMap());

			float x = Input.getLastTouchX();
			float y = Input.getLastTouchY();

			if (!Input.isTouchDown()) {
				if (rightButtonDown && isInRightButton(x, y)) {
					onRightButtonReleased();
				}
				
				if (leftButtonDown && isInLeftButton(x, y)) {
					onLeftButtonReleased();
				}
				
				rightButtonDown = false;
				leftButtonDown = false;
				moving = false;
			}

			if (Input.isTapped()) {
				boolean buttons = true;
				if (shouldSelect()) {
					buttons = !doSelection();
				}
				
				boolean move = shouldMove();
				
				if (buttons) {
					if (isInRightButton(x, y)) {
						rightButtonDown = true;
						move = false;
					} else if (isInLeftButton(x,y)) {
						leftButtonDown = true;
						move = false;
					}
				}
				
				if (move) {
					startDragOffX = offX;
					startDragOffY = offY;
					moving = true;
				}
			}

			if (moving) {
				if (Input.isTouchDown()) {
					offX = startDragOffX + Input.getDistanceTouchX();
					offY = startDragOffY + Input.getDistanceTouchY();
				}
			}

			if (offX > 0) {
				startDragOffX -= offX;
				offX = 0;
			}
			float edgeX = this.width - width;
			if (offX < edgeX) {
				startDragOffX += edgeX - offX; 
				offX = edgeX;
			}
			if (offY > 0) {
				startDragOffY -= offY;
				offY = 0;
			}
			float edgeY = this.height - height;
			if (offY < edgeY) {
				startDragOffY += edgeY - offY; 
				offY = edgeY;
			}
		}
		
		protected void onRightButtonReleased() {
			((SelectorMapBase)getContext()).finishOk();
		}
		
		protected void onLeftButtonReleased() {
			mode = (mode + 1) % 2;
		}

		public void onDraw(Canvas c) {
			if (grid == null) createGrid();

			c.drawColor(Color.WHITE);

			Map map = game.getSelectedMap();
			Tileset tileset = game.tilesets[map.tilesetId];

			paint.setColor(Color.WHITE);

			for (int i = 0; i < map.layers.length; i++) {
				MapLayer layer = map.layers[i];

				for (int j = 0; j < layer.rows; j++) {
					for (int k = 0; k < layer.columns; k++) {
						float x = k * tileset.tileWidth;
						float y = j * tileset.tileHeight;
						int tileId = layer.tiles[j][k];
						Bitmap tileBitmap = tiles[tileId];
						drawTile(c, x, y, tileId, tileBitmap);
					}
				}

				paint.setAlpha(200);
			}

			drawActors(c);
			drawGrid(c);
			drawButtons(c);
		}

		protected void drawButtons(Canvas c) {
			int rad = getButtonRad();
			int border = getButtonBorder();
			
			if (showRightButton) {
				float cx = width - border;
				float cy = height - border;
				String text = getRightButtonText();
				float ctx =  width - rad / 2f;
				drawButton(c, cx, cy, ctx, text, rightButtonDown);
			}
			
			if (showLeftButton) {
				float cx = border;
				float cy = height - border;
				String text = getLeftButtonText();
				float ctx =  rad / 2f;
				drawButton(c, cx, cy, ctx, text, leftButtonDown);
			}
		}
		
		protected void drawButton(Canvas c, float cx, float cy, float ctx, String text, boolean down) {
			int rad = getButtonRad();
			int border = getButtonBorder();
			int innerRad = (int)(rad * 0.9f);
			
			paint.setColor(Color.DKGRAY);
			if (!down) paint.setAlpha(150);
			paint.setStyle(Style.FILL);

			c.drawCircle(cx, cy, rad, paint);
			paint.setColor(Color.LTGRAY);
			if (!down) paint.setAlpha(150);
			c.drawCircle(cx, cy, innerRad, paint);

			paint.setColor(Color.BLACK);
			paint.setTextSize(20);
			float textSize = paint.measureText(text);
			float textX = ctx - textSize / 2;
			float textY = height - border * 3 / 2;
			c.drawText(text, textX, textY, paint);
		}

		protected void drawTile(Canvas c, float x, float y, int tileId, Bitmap tileBitmap) {
			c.drawBitmap(tileBitmap, x + offX, y + offY, paint);
		}

		protected void drawActors(Canvas c) {
			paint.setAlpha(255);
			paint.setTextSize(12);
			paint.setAntiAlias(true);

			Map map = game.getSelectedMap();
			Tileset tileset = game.tilesets[map.tilesetId];

			for (int i = 0; i < map.actorLayer.rows; i++) {
				for (int j = 0; j < map.actorLayer.columns; j++) {
					float x = j * tileset.tileWidth;
					float y = i * tileset.tileHeight;
					int instanceId = map.actorLayer.tiles[i][j];

					int index = -1;
					if (instanceId == 1) {
						index = 0;
					} else if (instanceId > 1){
						ActorInstance instance = map.actors.get(instanceId);
						index = instance.classIndex;
					}
					if (index > -1) {
						Bitmap bmp = actors[index];
						float sx = (tileset.tileWidth - bmp.getWidth()) / 2f;
						float sy = (tileset.tileHeight - bmp.getHeight()) / 2f;
						float dx = x + offX + sx;
						float dy = y + offY + sy;

						drawActor(c, dx, dy, instanceId, actors[index]);
					}
				}
			}
		}

		protected void drawActor(Canvas c, float dx, float dy, int instanceId, 
				Bitmap bmp) {

			c.drawBitmap(bmp, dx, dy, paint);

			String text = "" + instanceId;
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			c.drawRect(dx, dy + bmp.getHeight() - paint.getTextSize(), 
					dx + paint.measureText(text), dy + bmp.getHeight(), paint);
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.STROKE);
			c.drawText(text, dx, dy + bmp.getHeight(), paint);
		}

		protected void drawGrid(Canvas c) {
			Map map = game.getSelectedMap();
			Tileset tileset = game.tilesets[map.tilesetId];
			paint.setAlpha(200);
			c.drawBitmap(grid, offX % tileset.tileWidth, offY % tileset.tileHeight, paint);
		}

		private void createGrid() {
			Map map = game.getSelectedMap();
			Tileset tileset = game.getMapTileset(map);

			int tileWidth = tileset.tileWidth;
			int tileHeight = tileset.tileHeight;

			int cols = this.width / tileWidth + 2;
			int rows = this.height / tileHeight + 2;

			int width = cols * tileWidth;
			int height = rows * tileHeight;

			grid = Bitmap.createBitmap(width, height, Config.ARGB_8888);

			Canvas c = new Canvas();
			c.setBitmap(grid);

			paint.setColor(Color.argb(200, 200, 200, 200));
			paint.setStyle(Style.STROKE);

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					float x = j * tileWidth;
					float y = i * tileHeight;
					c.drawRect(x, y, x + tileWidth, y + tileHeight, paint);
					c.drawRect(x + 1, y + 1, x + tileWidth - 1, y + tileHeight - 1, paint);
				}
			}
		}

		private void createActors() {
			actors = new Bitmap[game.actors.length];
			actors[0] = Data.loadActor(game.hero.imageName);
			for (int i = 1; i < actors.length; i++) {
				actors[i] = Data.loadActor(game.actors[i].imageName);
			}
			for (int i = 0; i < actors.length; i++) {
				actors[i] = Bitmap.createBitmap(actors[i], 0, 0, 
						actors[i].getWidth() / 4, actors[i].getHeight() / 4);
			}
		}

		private static Bitmap[] createTiles(Bitmap tilesBitmap, int tileWidth, int tileHeight, int tileSpacing) {
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
					String cacheName = tilesBitmap.hashCode() + ":" + i + "x" + j;
					if (Cache.isBitmapRegistered(cacheName)) {
						tiles[index++] = Cache.getRegisteredBitmap(cacheName);
					}
					else {
						Bitmap bmp = Bitmap.createBitmap(tilesBitmap, i, j, tileWidth, tileHeight);
						Cache.RegisterBitmap(cacheName, bmp);
						tiles[index++] = bmp;
					}
				}
			}
			return tiles;
		}
	}
}
