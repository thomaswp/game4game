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
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.input.Input;

public class SelectorMapObjectInstance extends SelectorMapBase {
	private int originalId;
	
	@Override
	protected MapView getMapView(PlatformGame game, 
			Bundle savedInstanceState) {
		originalId = getIntent().getExtras().getInt("id");
		return new ObjectInstanceView(this, game, 
				savedInstanceState, originalId);
	}
	
	@Override
	protected boolean hasChanged() {
		ObjectInstanceView view = (ObjectInstanceView)this.view;
		return view.selectedId !=  originalId;
	}

	@Override
	protected void finishOk(Intent intent) {
		ObjectInstanceView view = (ObjectInstanceView)this.view;
		intent.putExtra("id", view.selectedId);
		super.finishOk(intent);
	}

	private static class ObjectInstanceView extends SelectorMapView {
		private int selectedId;
		private RectF selectedRect = new RectF();
		
		public ObjectInstanceView(Context context, PlatformGame game, 
				Bundle savedInstanceState, int selectedId) {
			super(context, game, savedInstanceState);
			this.selectedId = selectedId;
			paint = new Paint();
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			super.surfaceCreated(holder);
			
			Map map = game.getSelectedMap();
			if (selectedId >= 0) {
				ObjectInstance instance = map.objects.get(selectedId);
				offX = -instance.startX;
				offY = -instance.startY;
				doOriginBounding(width, height);
			}
		}

		@Override 
		protected void drawObjects(Canvas c) {
			selectedRect.setEmpty();
			super.drawObjects(c);
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
		protected void drawObject(Canvas c, ObjectInstance instance, float cx, float cy, 
				Bitmap bitmap, Paint paint) {
			if (instance.id == selectedId) {
				paint.setColor(selectionFillColor);
				paint.setStyle(Style.FILL);
				selectedRect.set(cx - bitmap.getWidth() / 2, cy - bitmap.getHeight() / 2, 
						cx + bitmap.getWidth() / 2, cy + bitmap.getHeight() / 2);
				c.drawRect(selectedRect, paint);
			}
			super.drawObject(c, instance, cx, cy, bitmap, paint);
		}

		@Override
		protected boolean doSelection() {
			if (Input.isTapped()) {
				Map map = game.getSelectedMap();

				float x = Input.getLastTouchX() - offX;
				float y = Input.getLastTouchY() - offY;
				
				boolean select = false;
				for (int i = 0; i < map.objects.size(); i++) {
					ObjectInstance instance = map.objects.get(i);
					Bitmap bmp = objects[instance.classIndex];
					float left = instance.startX - bmp.getWidth() / 2;
					float top = instance.startY - bmp.getHeight() / 2;
					float right = instance.startX + bmp.getWidth() / 2;
					float bottom = instance.startY + bmp.getHeight() / 2;
					if (x >= left && x < right && y >= top && y < bottom) {
						selectedId = instance.id;
						select = true;
					}
				}
				return select;
			}
			
			return false;
		}
	}
}
