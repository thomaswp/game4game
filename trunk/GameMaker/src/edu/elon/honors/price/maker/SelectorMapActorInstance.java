package edu.elon.honors.price.maker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.SurfaceHolder;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;

public class SelectorMapActorInstance extends SelectorMapBase {
	
	private int originalId;
	
	@Override
	protected MapView getMapView(PlatformGame game) {
		originalId = getIntent().getExtras().getInt("id");
		return new ActorInstanceView(this, game, originalId);
	}
	
	@Override
	protected boolean hasChanged() {
		ActorInstanceView view = (ActorInstanceView)this.view;
		return view.selectedId !=  originalId;
	}

	@Override
	protected void finishOk() {
		ActorInstanceView view = (ActorInstanceView)this.view;
		Intent intent = new Intent();
		intent.putExtra("id", view.selectedId);
		setResult(RESULT_OK, intent);
		super.finishOk();
	}

	private static class ActorInstanceView extends SelectorMapView {
		private int selectedId;
		private Paint paint;
		
		public ActorInstanceView(Context context, PlatformGame game, int selectedId) {
			super(context, game);
			this.selectedId = selectedId;
			paint = new Paint();
		}
		
		public void surfaceCreated(SurfaceHolder holder) {
			super.surfaceCreated(holder);
			
			Map map = game.getSelectedMap();
			Tileset tileset = game.getMapTileset(map);
			for (int i = 0; i < map.rows; i++) {
				for (int j = 0; j < map.columns; j++) {
					if (map.actorLayer.tiles[i][j] == selectedId) {
						float x = j * (tileset.tileWidth + 1);
						float y = i * (tileset.tileHeight + 1);
						offX = width / 2 - x;
						offY = height / 2 - y;
					}
				}
			}
		}

		@Override
		protected void drawActor(Canvas c, float dx, float dy, int instanceId,
				Bitmap bmp, Paint paint) {
			if (instanceId == selectedId) {
				paint.setColor(selectionFillColor);
				paint.setStyle(Style.FILL);
				c.drawRect(dx, dy, dx + bmp.getWidth(), dy + bmp.getHeight(), paint);
				paint.setColor(selectionBorderColor);
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(selectionBorderWidth);
				c.drawRect(dx + 2, dy + 2, dx + bmp.getWidth() - 2, dy + bmp.getHeight() - 2, paint);
			}
			super.drawActor(c, dx, dy, instanceId, bmp, paint);
		}

		protected boolean doSelection() {
			if (Input.isTapped()) {
				Map map = game.getSelectedMap();
				Tileset tileset = game.getMapTileset(map);
				
				float x = Input.getLastTouchX() - offX;
				float y = Input.getLastTouchY() - offY;
				
				int ix = (int)(x / tileset.tileWidth);
				int iy = (int)(y / tileset.tileHeight);
				
				if (ix < map.actorLayer.columns && iy < map.actorLayer.rows) {
					int id = map.actorLayer.tiles[iy][ix];
					if (id > 0) {
						selectedId = id;
						return true;
					}
				}
			}
			
			return false;
		}
	}
}
