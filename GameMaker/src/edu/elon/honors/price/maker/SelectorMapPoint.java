package edu.elon.honors.price.maker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.SurfaceHolder;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;

public class SelectorMapPoint extends SelectorMapBase {

	private int originalX, originalY;

	protected MapView getMapView(PlatformGame game) {
		Bundle extras = getIntent().getExtras();
		originalX = extras.getInt("x");
		originalY = extras.getInt("y");
		return new PointView(this, game, originalX, originalY);
	}

	@Override
	protected boolean hasChanged() {
		PointView view = (PointView)this.view;
		int x = view.getX();
		int y = view.getY();
		return x != originalX || y != originalY;
	}

	@Override
	protected void finishOk() {
		PointView view = (PointView)this.view;
		Intent intent = new Intent();
		int x = view.getX();
		int y = view.getY();
		intent.putExtra("x", x);
		intent.putExtra("y", y);
		setResult(RESULT_OK, intent);
		super.finishOk();
	}

	protected static class PointView extends SelectorMapView {

		protected final static int SCROLL_BORDER = 25;
		protected final static int SCROLL_TICK = 3;

		protected int x, y;
		protected Paint paint;

		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}

		public PointView(Context context, PlatformGame game, int x, int y) {
			super(context, game);
			this.x = x;
			this.y = y;
			paint = new Paint();
		}
		
		@Override
		protected boolean showLeftButton() {
			return true;
		}

		public void surfaceCreated(SurfaceHolder holder) {
			super.surfaceCreated(holder);
			offX = -x + width / 2;
			offY = -y + height / 2;
		}

		protected boolean doSelection() {
			float x = Input.getLastTouchX();
			float y = Input.getLastTouchY();

			if (!leftButton.isInButton(x, y) && !rightButton.isInButton(x, y)) {
				this.x = (int)(x - offX);
				this.y = (int)(y - offY);
				validatePoint();
			}

			return false;
		}

		protected void updateSelection() {
			if (Input.isTouchDown() && !leftButton.down && !rightButton.down) {
				float x = Input.getLastTouchX();
				float y = Input.getLastTouchY();

				this.x = (int)(x - offX);
				this.y = (int)(y - offY);
				validatePoint();
				
				if (x <= SCROLL_BORDER) {
					offX += SCROLL_TICK;
					if (offX > 0) offX = 0;
				}
				if (y <= SCROLL_BORDER) {
					offY += SCROLL_TICK;
				}
				if (x >= width - SCROLL_BORDER) {
					offX -= SCROLL_TICK;
				}
				if (y >= height - SCROLL_BORDER) {
					offY -= SCROLL_TICK;
				}
			}
		}
		
		private void validatePoint() {
			int right = game.getMapWidth(game.getSelectedMap()) - 1;
			int bottom = game.getMapHeight(game.getSelectedMap()) - 1;
			
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			if (x > right) x = right;
			if (y > bottom) y = bottom;
		}

		public void drawGrid(Canvas c) {
			super.drawGrid(c);

			float offsetX = x + offX;
			float offsetY = y + offY;
			int radius = 30;
			
			paint.setColor(selectionFillColor);
			paint.setStyle(Style.FILL);
			c.drawCircle(offsetX, offsetY, radius, paint);

			paint.setColor(selectionBorderColor);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(selectionBorderWidth);
			c.drawCircle(offsetX, offsetY, radius, paint);
			
			c.drawLine(offsetX, offsetY - radius, 
					offsetX, offsetY + radius, paint);
			c.drawLine(offsetX - radius, offsetY, 
					offsetX + radius, offsetY, paint);

			paint.setColor(Color.BLACK);
			paint.setStyle(Style.FILL);
			paint.setTextSize(20);
			paint.setAntiAlias(true);

			String text = makeText();
			c.drawText(text, 0, paint.getTextSize(), paint);
		}

		protected String makeText() {
			StringBuilder sb = new StringBuilder();
			sb.append("(").append(x).append(", ").append(y).append(")");
			return sb.toString();
		}
	}
}
