package edu.elon.honors.price.maker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Cache;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Input;

public abstract class MapActivityBase extends SaveableActivity {

	public static final int SELECTION_FILL_COLOR = 
		Color.argb(200, 150, 150, 255);
	public static final int SELECTION_BORDER_COLOR = 
		Color.argb(255, 50, 50, 255);
	public static final int SELECTION_BORDER_WIDTH = 2;
	
	public static final int BUTTON_TEXT_SIZE = 18;
	
	private static final boolean DEBUG_FPS = true;

	protected PlatformGame game;
	protected MapView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (savedInstanceState != null && savedInstanceState.containsKey("game")) {
			game = (PlatformGame)savedInstanceState.getSerializable("game");
		}
		
		if (game == null) {
			game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		}
		view = getMapView(game, savedInstanceState);
		setContentView(view);

		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("game", game);
		view.onSaveInstanceState(outState);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		view.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		view.resume();
	}

	protected abstract MapView getMapView(PlatformGame game, Bundle savedInstanceState);

	public abstract static class MapView extends BasicCanvasView {

		protected int selectionFillColor = SELECTION_FILL_COLOR;
		protected int selectionBorderColor = SELECTION_BORDER_COLOR;
		protected int selectionBorderWidth = SELECTION_BORDER_WIDTH;

		protected PlatformGame game;
		protected Paint paint;
		protected float offX, offY;
		protected float startDragOffX;
		protected float startDragOffY;
		protected boolean moving;
		protected Bitmap[] tiles, actors, objects;
		protected Bitmap grid;
		private Bitmap bgBmp;
		private Bitmap midgrounds;
		protected ArrayList<Button> buttons;

		public int getButtonRad() {
			//return toPx(65);
			return height / 5;
		}

		public int getButtonBorder() {
			return getButtonRad() / 4;
		}

		public MapView(Context context, PlatformGame game, Bundle savedInstanceState) {
			super(context);
			this.game = game;
			paint = new Paint();

			refresh();
			
			if (savedInstanceState != null) {
				onLoadInstanceState(savedInstanceState);
			}
		}
		
		public void refresh() {
			Tileset tileset = game.tilesets[game.getSelectedMap().tilesetId];
			Bitmap tilesetBmp = Data.loadTileset(tileset.bitmapName, getContext());
			tiles = createTiles(tilesetBmp, tileset.tileWidth, tileset.tileHeight, 0);
			createActors();
			createObjects();
			bgBmp = null;
			midgrounds = null;
		}
		
		protected void onLoadInstanceState(Bundle savedInstanceState) {
			offX = savedInstanceState.getFloat("offX");
			offY = savedInstanceState.getFloat("offY");
		}
		
		public void onSaveInstanceState(Bundle outState) {
			outState.putFloat("offX", offX);
			outState.putFloat("offY", offY);
		}

		protected void doReleaseTouch(float x, float y) {
			if (!Input.isTouchDown()) {
				for (int i = 0; i < buttons.size(); i++) {
					Button button = buttons.get(i);
					if (button.down && button.isInButton(x, y)) {
						if (button.onReleasedHandler != null) {
							button.onReleasedHandler.run();
						}
					}
					button.down = false;
				}
				moving = false;
			}
		}
		protected boolean doPressButtons(float x, float y) {
			for (int i = 0; i < buttons.size(); i++) {
				Button button = buttons.get(i);
				if (button.isInButton(x, y)) {
					if (button.enabled) {
						if (button.onPressedHandler != null) {
							button.onPressedHandler.run();
						}
						button.down = true;
					}
					return true;
				}
			}
			return false;
		}

		protected void doMovementStart() {
			startDragOffX = offX;
			startDragOffY = offY;
			moving = true;
		}

		protected void doMovement() {
			if (moving) {
				if (Input.isTouchDown()) {
					offX = startDragOffX + Input.getDistanceTouchX();
					offY = startDragOffY + Input.getDistanceTouchY();
				}
			}
		}

		protected void doOriginBounding(int mapWidth, int mapHeight) {
			if (mapWidth < width) {
				offX = (width - mapWidth) / 2;
			} else {
				float edgeX = 0;
				if (offX > edgeX) {
					startDragOffX += edgeX - offX;
					offX = edgeX;
				}
				edgeX = Math.min(edgeX, this.width - mapWidth);
				if (offX < edgeX) {
					startDragOffX += edgeX - offX; 
					offX = edgeX;
				}
			}
			
			if (mapHeight < height) {
				offY = (height - mapHeight) / 2;
			} else {
				float edgeY = 0;
				if (offY > edgeY) {
					startDragOffY += edgeY - offY;
					offY = edgeY;
				}
				edgeY = Math.min(edgeY, this.height - mapHeight);
				if (offY < edgeY) {
					startDragOffY += edgeY - offY; 
					offY = edgeY;
				}
			}
		}

		protected abstract void doUpdate(int mapWidth, int mapHeight, float x, float y);

		protected void createButtons() { }

		@Override
		protected void update(long timeElapsed) {
			if (buttons == null) {
				buttons = new ArrayList<Button>();
				createButtons();
			}

			int mapWidth = game.getMapWidth(game.getSelectedMap());
			int mapHeight = game.getMapHeight(game.getSelectedMap());

			float x = Input.getLastTouchX();
			float y = Input.getLastTouchY();

			doUpdate(mapWidth, mapHeight, x, y);
		}

		private BitmapShader transShader;
		@Override
		public void onDraw(Canvas c) {
			if (width == 0 || height == 0) return;
			if (grid == null) createGrid();

			updateFPS();
			
			if (transShader == null) {
				Bitmap transBg = Data.loadEditorBmp("trans.png");
				transShader = new BitmapShader(transBg, 
						TileMode.REPEAT, TileMode.REPEAT);
			}

			c.drawColor(Color.WHITE);
			
			int mapHeight = game.getMapHeight(game.getSelectedMap());
			if (mapHeight < height) {
				paint.setShader(transShader);
				paint.setAlpha(getBackgroundTransparency());
				c.drawRect(0,  mapHeight - 1, width, height, paint);
				paint.setShader(null);
			}
			
			synchronized (game) {	
				drawBackground(c);
				drawContent(c);
				drawGrid(c);
				drawButtons(c);
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int frames = 0;
		long lastUpdate = System.currentTimeMillis();
		
		private void updateFPS() {
			frames++;
			long passed = System.currentTimeMillis() - lastUpdate;
			if (passed > 1000) {
				int fps = (int)(frames * 1000 / passed);
				if (DEBUG_FPS)
					Debug.write("%d fps", fps);
				
				while (lastUpdate + 1000 < System.currentTimeMillis()) {
					lastUpdate += 1000;
				}
				frames = 0;
			}
		}
		
		protected abstract void drawContent(Canvas c);

		protected int getBackgroundTransparency() {
			return 80;
		}

		protected void drawBackground(Canvas canvas) {
			if (bgBmp == null || bgBmp.getWidth() != width ||
					bgBmp.getHeight() != height) {
				bgBmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			}
			//Canvas canvas = new Canvas(bgBmp);
//			canvas.drawColor(Color.WHITE);
			
			Map map = game.getSelectedMap();
			Tileset tileset = game.tilesets[map.tilesetId];
			int mapWidth = tileset.tileWidth * map.columns;
			int mapHeight = tileset.tileHeight * map.rows;
			
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			//should be main canvas whether rendering offscreen or not
			canvas.drawRect(offX,  offY, offX + mapWidth, offY + mapHeight, paint);
			canvas.drawRect(0, 0, width, (int)offY + mapHeight, paint);
			
			
			float paralax = 0.7f;
			int pOffX = (int)(paralax * offX);
			float mOffY = offY + mapHeight - height;
			int pOffY = (int)(paralax * mOffY);
			if (offY > 0) pOffY = (mapHeight - height) / 2;
			
			int endWidth = Math.max(mapWidth, width);
			
			paint.reset();
			paint.setAlpha(getBackgroundTransparency());
			Bitmap foreground = Data.loadForeground(map.groundImageName);
			int fgHeight = height - map.groundY;
			for (int i = -foreground.getWidth(); i < endWidth; i += foreground.getWidth()) {
				int x = i + pOffX, y = fgHeight + pOffY;
				canvas.drawBitmap(foreground, x, y, paint);
			}
			Bitmap background = Data.loadBackground(map.skyImageName);
			int bgHeight = fgHeight - background.getHeight();
			for (int i = -background.getWidth(); i < endWidth; i += background.getWidth()) {
				for (int j = bgHeight; j > -mapHeight; j -= background.getHeight()) {
					canvas.drawBitmap(background, i + pOffX, j + pOffY, paint);
				}
			}
			
			if (map.midGrounds.size() > 0 && midgrounds == null) {
				createMidgrounds();
			}
			
			if (midgrounds != null) {
				int mgHeight = fgHeight - foreground.getHeight();
				for (int i = -midgrounds.getWidth(); i < endWidth; i += midgrounds.getWidth()) {
					canvas.drawBitmap(midgrounds, i + pOffX, mgHeight + pOffY, paint);
				}
			}
			
			//paint.setAlpha(getBackgroundTransparency());
			//c.drawBitmap(bgBmp, 0, 0, paint);
		}
		
		protected void createMidgrounds() {
			midgrounds = Data.loadMidgrounds(
					game.getSelectedMap().midGrounds);
		}

		protected void drawButtons(Canvas c) {
			for (int i = 0; i < buttons.size(); i++) {
				Button button = buttons.get(i);
				if (button.showing) {
					button.drawButton(c, paint);
				}
			}
		}

		protected void drawGrid(Canvas c) {
		
			Map map = game.getSelectedMap();
			Tileset tileset = game.tilesets[map.tilesetId];
			
			
			float minX = Math.max(0, offX), minY = Math.max(0, offY);
			float maxX = Math.min(width, map.width(game) + offX);
			float maxY = Math.min(height, map.height(game) + offY);
			paint.setColor(Color.argb(123, 123, 123, 123));
			paint.setStrokeWidth(2);
			paint.setStyle(Style.STROKE);
			
			for (int i = 0; i <= map.columns; i++) {
				float x = offX + i * tileset.tileWidth;
				if (x < minX || x > maxX) continue;
				c.drawLine(x, minY, x, maxY, paint);
			}

			for (int i = 0; i < map.rows; i++) {
				float y = offY + i * tileset.tileHeight;
				if (y < minY || y > maxY) continue;
				c.drawLine(minX, y, maxX, y, paint);
			}
			
			paint.setStyle(Style.FILL);
		}

		private void createGrid() {
			Map map = game.getSelectedMap();
			Tileset tileset = game.getMapTileset(map);

			int tileWidth = tileset.tileWidth;
			int tileHeight = tileset.tileHeight;

			int cols = this.width / tileWidth + 2;
			int rows = this.height / tileHeight + 2;

			cols = 1; rows = 1;
			
			int width = cols * tileWidth;
			int height = rows * tileHeight;

			Bitmap grid = Bitmap.createBitmap(width, height, Config.ARGB_8888);

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
			
			this.grid = grid;
		}

		protected void createActors() {
			actors = new Bitmap[game.actors.length];
			for (int i = 0; i < actors.length; i++) {
				actors[i] = createActor(i, game);
			}
		}
		
		protected Bitmap createActor(int index, PlatformGame game) {
			Bitmap bmp = Data.loadActorIcon(game.actors[index].imageName);
			bmp = Bitmap.createScaledBitmap(bmp,
					(int)(bmp.getWidth() * game.actors[index].zoom),
					(int)(bmp.getHeight() * game.actors[index].zoom),
					true);
			return bmp;
		}

		protected void createObjects() {
			objects = new Bitmap[game.objects.length];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = createObject(i, game);
			}
		}
		
		protected Bitmap createObject(int index, PlatformGame game) {
			Bitmap bmp = Data.loadObject(game.objects[index].imageName);
			bmp = Bitmap.createScaledBitmap(bmp,
					(int)(bmp.getWidth() * game.objects[index].zoom),
					(int)(bmp.getHeight() * game.objects[index].zoom),
					true);
			return bmp;
		}

		protected void drawActor(Canvas c, float dx, float dy, int instanceId, 
				Bitmap bmp, Paint paint) {

			c.drawBitmap(bmp, dx, dy, paint);

			String text = "" + instanceId;
			paint.setColor(Color.WHITE);
			paint.setAlpha(150);
			paint.setStyle(Style.FILL);
			c.drawRect(dx, dy + bmp.getHeight() - paint.getTextSize(), 
					dx + paint.measureText(text), dy + bmp.getHeight(), paint);
			paint.setColor(Color.BLACK);
			paint.setTextSize(12);
			c.drawText(text, dx, dy + bmp.getHeight(), paint);
		}

		protected static Bitmap[] createTiles(Tileset tileset, Context context) {
			Bitmap bmp = Data.loadTileset(tileset.bitmapName, context);
			return createTiles(bmp, tileset.tileWidth, 
					tileset.tileHeight, tileset.tileSpacing);
		}
		
		protected static Bitmap[] createTiles(Bitmap tilesBitmap, int tileWidth, int tileHeight, int tileSpacing) {
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

		protected Button createBottomLeftButton(String text) {
			float ctx =  getButtonRad() / 2f;
			float cty = height - getButtonBorder() * 2;

			return new Button(
					getButtonBorder(),
					height - getButtonBorder(),
					ctx,
					cty,
					getButtonRad(),
					text
			);
		}

		protected Button createBottomRightButton(String text) {
			float ctx =  width - getButtonRad() / 2f;
			float cty = height - getButtonBorder() * 2;

			return new Button(
					width - getButtonBorder(),
					height - getButtonBorder(),
					ctx,
					cty,
					getButtonRad(),
					text
			);
		}

		protected Button createTopRightButton(String text) {
			float ctx =  width - getButtonRad() / 2f;
			float cty = getButtonBorder() * 2;

			return new Button(
					width - getButtonBorder(),
					getButtonBorder(),
					ctx,
					cty,
					getButtonRad(),
					text
			);
		}

		protected Button createTopLeftButton(String text) {
			float ctx =  getButtonRad() / 2f;
			float cty = getButtonBorder() * 2;

			return new Button(
					getButtonBorder(),
					getButtonBorder(),
					ctx,
					cty,
					getButtonRad(),
					text
			);
		}

		protected class Button {
			public int cx, cy, radius;
			float ctx, cty;
			public String text;
			public boolean down;
			public boolean showing;
			public Runnable onPressedHandler;
			public Runnable onReleasedHandler;
			public Bitmap image;
			public boolean imageBorder;
			public int textColor;
			public boolean opaque;
			public float alphaFactor;
			public boolean enabled;
			private Rect bounds, src;
			private RectF dest;

			public Button(int cx, int cy, float ctx, float cty, int rad, String text) {
				this.cx = cx;
				this.cy = cy;
				this.ctx = ctx;
				this.cty = cty;
				this.radius = rad;
				this.text = text;
				showing = true;
				textColor = Color.BLACK;
				alphaFactor = 1;
				enabled = true;
				bounds = new Rect();
				src = new Rect();
				dest = new RectF();
			}


			protected boolean isInButton(float x, float y) {
				if (!showing) return false;

				float dx = x - cx;
				float dy = y - cy;

				return (dx * dx + dy * dy <= radius * radius);
			}

			protected void drawButton(Canvas c, Paint paint) {
				int innerRad = (int)(radius * 0.9f);
				int trans = (int)(150 * alphaFactor);

				paint.setColor(Color.DKGRAY);
				if (!down && !opaque) paint.setAlpha(trans);
				paint.setStyle(Style.FILL);
				c.drawCircle(cx, cy, radius, paint);

				if (enabled) {
					paint.setColor(Color.LTGRAY);
					if (!down && !opaque) paint.setAlpha(trans);
					c.drawCircle(cx, cy, innerRad, paint);
				}

				if (image != null) {
					float maxSemiWidth = radius - Math.abs(ctx - cx);
					float maxSemiHeight = radius - Math.abs(cty - cy);
					maxSemiWidth /= 1.9;
					maxSemiHeight /= 1.9;
					float semiWidth = image.getWidth() / 2;
					float semiHeight = image.getHeight() / 2f;
					float scaleWidth = Math.min(maxSemiWidth / semiWidth, 1);
					float scaleHeight = Math.min(maxSemiHeight / semiHeight, 1);
					float scale = Math.min(scaleWidth, scaleHeight);

					semiWidth *= scale; semiHeight *= scale;
					src.set(0, 0, image.getWidth(), image.getHeight());
					dest.set(ctx - semiWidth, cty - semiHeight, 
							ctx + semiWidth, cty + semiHeight);
					c.drawBitmap(image, src, dest, paint);
					if (imageBorder) {
						paint.setStyle(Style.STROKE);
						paint.setColor(Color.DKGRAY);
						if (!down && !opaque) paint.setAlpha(trans);
						c.drawRect(dest, paint);
						paint.setStyle(Style.FILL);
					}
				}


				paint.getTextBounds(text, 0, text.length(), bounds);
				paint.setColor(textColor);
				if (!down && !opaque) {
					paint.setAlpha((int)(255 * alphaFactor));
				}
				paint.setTextSize(Screen.dipToPx(BUTTON_TEXT_SIZE, getContext()));
				paint.setAntiAlias(true);
				float textSize = paint.measureText(text);
				float textX = ctx - textSize / 2;
				float textY = cty + 5;
				c.drawText(text, textX, textY, paint);
			}
		}
	}
}

