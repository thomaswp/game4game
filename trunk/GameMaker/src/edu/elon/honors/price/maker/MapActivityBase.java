package edu.elon.honors.price.maker;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Cache;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;

public abstract class MapActivityBase extends Activity {

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

	protected abstract MapView getMapView(PlatformGame game);

	protected abstract static class MapView extends BasicCanvasView {

		protected int selectionFillColor = Color.argb(200, 150, 150, 255);
		protected int selectionBorderColor = Color.argb(255, 50, 50, 255);
		protected int selectionBorderWidth = 2;

		protected PlatformGame game;
		protected Paint paint;
		protected float offX, offY;
		protected float startDragOffX;
		protected float startDragOffY;
		protected boolean moving;
		protected Bitmap[] tiles, actors, objects;
		protected Bitmap grid;
		protected ArrayList<Button> buttons;

		public int getButtonRad() {
			return height / 5;
		}

		public int getButtonBorder() {
			return getButtonRad() / 4;
		}

		public MapView(Context context, PlatformGame game) {
			super(context);
			this.game = game;
			paint = new Paint();

			Tileset tileset = game.tilesets[game.getSelectedMap().tilesetId];
			Bitmap tilesetBmp = Data.loadTileset(tileset.bitmapName, getContext());
			tiles = createTiles(tilesetBmp, tileset.tileWidth, tileset.tileHeight, 0);
			createActors();
			createObjects();
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

		protected void doOriginBounding(int width, int height) {
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

		protected abstract void doUpdate(int width, int height, float x, float y);

		protected void createButtons() { }

		@Override
		protected void update(long timeElapsed) {
			if (buttons == null) {
				buttons = new ArrayList<Button>();
				createButtons();
			}

			int width = game.getMapWidth(game.getSelectedMap());
			int height = game.getMapHeight(game.getSelectedMap());

			float x = Input.getLastTouchX();
			float y = Input.getLastTouchY();

			doUpdate(width, height, x, y);
		}

		public void onDraw(Canvas c) {
			if (grid == null) createGrid();

			c.drawColor(Color.WHITE);

			drawContent(c);
			drawGrid(c);
			drawButtons(c);
		}

		protected abstract void drawContent(Canvas c);

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
		
		private void createObjects() {
			objects = new Bitmap[game.objects.length];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = Data.loadObject(game.objects[i].imageName);
				objects[i] = Bitmap.createScaledBitmap(objects[i], 
						(int)(objects[i].getWidth() * game.objects[i].zoom), 
						(int)(objects[i].getHeight() * game.objects[i].zoom), true);
			}
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

		protected static class Button {
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
				paint.setTextSize(20);
				paint.setAntiAlias(true);
				float textSize = paint.measureText(text);
				float textX = ctx - textSize / 2;
				float textY = cty + 5;
				c.drawText(text, textX, textY, paint);
			}
		}
	}
}
