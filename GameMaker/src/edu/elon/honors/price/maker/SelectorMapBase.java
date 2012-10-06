package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.input.Input;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

public class SelectorMapBase extends MapActivityBase {
	
	@Override
	protected MapView getMapView(PlatformGame game, 
			Bundle savedInstanceState) {
		return new SelectorMapView(this, game, savedInstanceState);
	}
	
	public static class SelectorMapView extends MapView {
		protected final static int MODE_MOVE = 0;
		protected final static int MODE_SELECT = 1;
		protected int mode;
		protected Button leftButton, rightButton;
		
		
		public SelectorMapView(Context context, PlatformGame game, 
				Bundle savedInstanceState) {
			super(context, game, savedInstanceState);
		}
		
		@Override
		protected void createButtons() {
			leftButton = createBottomLeftButton(getLeftButtonText());
			leftButton.showing = showLeftButton();
			leftButton.onReleasedHandler = new Runnable() {
				@Override
				public void run() {
					onLeftButtonReleased();
				}
			};
			leftButton.onPressedHandler = new Runnable() {
				@Override
				public void run() {
					onLeftButtonPressed();
				}
			};
			buttons.add(leftButton);
			
			rightButton = createBottomRightButton(getRightButtonText());
			rightButton.showing = showRightButton();
			rightButton.onReleasedHandler = new Runnable() {
				@Override
				public void run() {
					onRightButtonReleased();
				}
			};
			rightButton.onPressedHandler = new Runnable() {
				@Override
				public void run() {
					onRightButtonPressed();
				}
			};
			buttons.add(rightButton);
		}
		
		protected boolean showRightButton() {
			return true;
		}
		
		protected boolean showLeftButton() {
			return false;
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
			return !leftButton.showing || mode == MODE_MOVE; 
		}

		protected boolean shouldSelect() {
			return !leftButton.showing || mode == MODE_SELECT;
		}
		
		/**
		 * Called when the player taps the screen.
		 * Start selection here.
		 * @return true if selection was successful (and
		 * therefore if the player taps a button it should
		 * not be ignored in favor of selection on the map).
		 * This method should return false in at least some
		 * cases or buttons will be inoperable.
		 */
		protected boolean doSelection() {
			return false;
		}

		protected void updateSelection() {

		}
		
		protected void onRightButtonReleased() {
			((SelectorMapBase)getContext()).finishOk();
		}

		protected void onLeftButtonReleased() {
			mode = (mode + 1) % 2;
		}
		
		protected void onRightButtonPressed() { }

		protected void onLeftButtonPressed() { }
		
		@Override
		protected void doUpdate(int width, int height, float x, float y) {
			doReleaseTouch(x, y);

			if (Input.isTapped()) {
				boolean buttons = true;
				if (shouldSelect()) {
					buttons = !doSelection();
				}

				boolean move = shouldMove();
				if (buttons) {
					if (doPressButtons(x, y)) move = false;
				}

				if (move) {
					doMovementStart();
				}
			}

			doMovement();
			
			if (!moving && shouldSelect()) {
				updateSelection();
			}

			doOriginBounding(width, height);
			leftButton.text = getLeftButtonText();
			rightButton.text = getRightButtonText();
		}
		
		@Override
		protected void drawContent(Canvas c) {
			drawTiles(c);
			drawActors(c);
			drawObjects(c);
		}
		
		protected void drawTiles(Canvas c) {
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
		}

		protected void drawTile(Canvas c, float x, float y, int tileId, Bitmap tileBitmap) {
			c.drawBitmap(tileBitmap, x + offX, y + offY, paint);
		}
		
		protected void drawObject(Canvas c, ObjectInstance instance, float x, float y, 
				Bitmap bitmap, Paint paint) {
			paint.setAlpha(255);
			c.drawBitmap(bitmap, x, y, paint);
		}
		
		protected void drawObjects(Canvas c) {
			for (int i = 0; i < game.getSelectedMap().objects.size(); i++) {
				ObjectInstance instance = game.getSelectedMap().objects.get(i);
				Bitmap bitmap = objects[instance.classIndex];
				float x = instance.startX - bitmap.getWidth() / 2 + offX;
				float y = instance.startY - bitmap.getHeight() / 2 + offY;
				drawObject(c, instance, x, y, bitmap, paint);
			}
		}
		
		protected void drawActors(Canvas c) {
			paint.setAlpha(255);
			paint.setAntiAlias(true);
			
			Map map = game.getSelectedMap();
			Tileset tileset = game.tilesets[map.tilesetId];

			for (int i = 0; i < map.actorLayer.rows; i++) {
				for (int j = 0; j < map.actorLayer.columns; j++) {
					float x = j * tileset.tileWidth;
					float y = i * tileset.tileHeight;
					int instanceId = map.actorLayer.tiles[i][j];
					int actorClass = map.getActorType(i, j);

					if (actorClass > -1) {
						Bitmap bmp = actors[actorClass];
						float sx = (tileset.tileWidth - bmp.getWidth()) / 2f;
						float sy = (tileset.tileHeight - bmp.getHeight()) / 2f;
						float dx = x + offX + sx;
						float dy = y + offY + sy;

						drawActor(c, dx, dy, instanceId, actors[actorClass], paint);
					}
				}
			}
		}
	}
}
