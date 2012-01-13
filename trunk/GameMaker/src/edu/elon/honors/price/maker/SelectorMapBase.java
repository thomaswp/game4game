package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.MapLayer;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Cache;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.maker.MapActivityBase.MapView;
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

public class SelectorMapBase extends MapActivityBase {
	
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
	
	@Override
	protected MapView getMapView(PlatformGame game) {
		return new SelectorMapView(this, game);
	}
	
	protected boolean hasChanged() {
		return false;
	}

	protected void finishOk() {
		finish();
	}
	
	protected static class SelectorMapView extends MapView {
		protected final static int MODE_MOVE = 0;
		protected final static int MODE_SELECT = 1;
		protected int mode;
		protected Button leftButton, rightButton;
		
		
		public SelectorMapView(Context context, PlatformGame game) {
			super(context, game);
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
			return !rightButton.showing || mode == MODE_SELECT;
		}
		
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
		
		protected void drawContent(Canvas c) {
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
