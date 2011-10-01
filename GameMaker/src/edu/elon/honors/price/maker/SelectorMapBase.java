package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Cache;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SelectorMapBase extends Activity {
	
	public final static int CODE = 0;
	
	protected PlatformGame game;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		
		setContentView(new MapView(this, game));
		
		super.onCreate(savedInstanceState);
	}
	
	private static class MapView extends BasicCanvasView {
		
		private PlatformGame game;
		private Paint paint;
		private float offX, offY;
		private float startDragOffX, startDragOffY;
		private Bitmap[] tiles;
		private Bitmap[] actors;
		private Bitmap grid;
		
		public MapView(Context context, PlatformGame game) {
			super(context);
			this.game = game;
			paint = new Paint();
			
			Tileset tileset = game.tilesets[game.getSelectedMap().tilesetId];
			Bitmap tilesetBmp = Data.loadTileset(tileset.bitmapName, getContext());
			tiles = createTiles(tilesetBmp, tileset.tileWidth, tileset.tileHeight, 0);
			createActors();
		}

		@Override
		protected void update(long timeElapsed) {
			Input.update(timeElapsed);
			
			Tileset tileset = game.getMapTileset(game.getSelectedMap());
			int width = game.getMapWidth(game.getSelectedMap());
			int height = game.getMapHeight(game.getSelectedMap());
			
			if (Input.isTapped()) {
				startDragOffX = offX;
				startDragOffY = offY;
			}
			
			if (Input.isTouchDown()) {
				offX = startDragOffX + Input.getDistanceTouchX();
				offY = startDragOffY + Input.getDistanceTouchY();
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
						Bitmap tile = tiles[tileId];
						c.drawBitmap(tile, x + offX, y + offY, paint);
					}
				}
				
				paint.setAlpha(200);
			}
			
			paint.setAlpha(255);
			paint.setTextSize(12);
			paint.setAntiAlias(true);
			
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
						c.drawBitmap(actors[index], dx, dy, paint);
						
						String text = "" + instanceId;
						paint.setColor(Color.WHITE);
						paint.setStyle(Style.FILL);
						c.drawRect(dx, dy + bmp.getHeight() - paint.getTextSize(), 
								dx + paint.measureText(text), dy + bmp.getHeight(), paint);
						paint.setColor(Color.BLACK);
						paint.setStyle(Style.STROKE);
						c.drawText(text, dx, dy + bmp.getHeight(), paint);
					}
				}
			}
			
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
