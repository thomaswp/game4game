package edu.elon.honors.price.maker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.SurfaceHolder;
import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.input.Input;

public class SelectorMapActorInstance extends SelectorMapBase {
	
	private int originalId;
	
	@Override
	protected MapView getMapView(PlatformGame game, 
			Bundle savedInstanceState) {
		originalId = getIntent().getExtras().getInt("id");
		return new ActorInstanceView(this, game, 
				savedInstanceState, originalId);
	}
	
	@Override
	protected boolean hasChanged() {
		ActorInstanceView view = (ActorInstanceView)this.view;
		return view.selectedId !=  originalId;
	}

	@Override
	protected void finishOk(Intent intent) {
		ActorInstanceView view = (ActorInstanceView)this.view;
		intent.putExtra("id", view.selectedId);
		super.finishOk(intent);
	}

	private static class ActorInstanceView extends SelectorMapView {
		private int selectedId;
		private RectF selectedRect = new RectF();
		
		public ActorInstanceView(Context context, PlatformGame game, 
				Bundle savedInstanceState, int selectedId) {
			super(context, game, savedInstanceState);
			this.selectedId = selectedId;
			paint = new Paint();
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			super.surfaceCreated(holder);
			
			Map map = game.getSelectedMap();
			Tileset tileset = game.getMapTileset(map);
			for (int i = 0; i < map.actors.size(); i++) {
				ActorInstance instance = map.actors.get(i);
				if (instance.id == selectedId) {
					float x = instance.column * (tileset.tileWidth + 1);
					float y = instance.row * (tileset.tileHeight + 1);
					offX = width / 2 - x;
					offY = height / 2 - y;
				}
			}
		}

		@Override 
		protected void drawActors(Canvas c) {
			selectedRect.setEmpty();
			super.drawActors(c);
		}
		
		@Override
		protected void drawGrid(Canvas c) {
			super.drawGrid(c);
			if (!selectedRect.isEmpty()) {
				paint.setColor(selectionBorderColor);
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(selectionBorderWidth);
				paint.setAntiAlias(false);
				c.drawRect(selectedRect, paint);
			}
		}
		
		@Override
		protected void drawActor(Canvas c, float dx, float dy, int instanceId,
				Bitmap bmp, Paint paint) {
			if (instanceId == selectedId) {
				paint.setColor(selectionFillColor);
				paint.setStyle(Style.FILL);
				selectedRect.set(dx, dy, dx + bmp.getWidth(), dy + bmp.getHeight());
				c.drawRect(selectedRect, paint);
			}
			super.drawActor(c, dx, dy, instanceId, bmp, paint);
		}

		@Override
		protected boolean doSelection() {
			if (Input.isTapped()) {
				Map map = game.getSelectedMap();
				Tileset tileset = game.getMapTileset(map);
				
				float x = Input.getLastTouchX() - offX;
				float y = Input.getLastTouchY() - offY;
				
				int ix = (int)(x / tileset.tileWidth);
				int iy = (int)(y / tileset.tileHeight);
				
				ActorInstance selected = map.getActorInstance(iy, ix);
				if (selected != null) {
					selectedId = selected.id;
					return true;
				}
			}
			
			return false;
		}
	}
}
