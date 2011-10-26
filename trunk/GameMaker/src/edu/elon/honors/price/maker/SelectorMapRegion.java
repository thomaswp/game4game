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

public class SelectorMapRegion extends SelectorMapBase {

	private Rect originalSelection;

	protected MapView getMapView(PlatformGame game) {
		Bundle extras = getIntent().getExtras();
		originalSelection = new Rect(
				extras.getInt("left"),
				extras.getInt("top"),
				extras.getInt("right"),
				extras.getInt("bottom")
		);
		return new RegionView(this, game, originalSelection);
	}

	@Override
	protected boolean hasChanged() {
		RegionView view = (RegionView)this.view;
		Rect selection = view.getNormSelection();
		return selection.left != originalSelection.left ||
		selection.top != originalSelection.top ||
		selection.right != originalSelection.right ||
		selection.bottom != originalSelection.bottom;
	}

	@Override
	protected void finishOk() {
		RegionView view = (RegionView)this.view;
		Intent intent = new Intent();
		Rect rect = view.getNormSelection();
		intent.putExtra("left", rect.left);
		intent.putExtra("top", rect.top);
		intent.putExtra("right", rect.right);
		intent.putExtra("bottom", rect.bottom);
		setResult(RESULT_OK, intent);
		super.finishOk();
	}

	protected static class RegionView extends MapView {

		protected final static int SCROLL_BORDER = 25;
		protected final static int SCROLL_TICK = 3;

		protected Rect selection, normSelection;
		protected RectF selectionF;
		protected Paint paint;

		public Rect getNormSelection() {
			normSelection.set(selection);
			if (normSelection.left > normSelection.right) {
				int temp = normSelection.left;
				normSelection.left = normSelection.right;
				normSelection.right = temp;
			}

			if (normSelection.top > normSelection.bottom) {
				int temp = normSelection.top;
				normSelection.top = normSelection.bottom;
				normSelection.bottom = temp;
			}

			Map map = game.getSelectedMap();
			normSelection.left = Math.max(0, normSelection.left);
			normSelection.top = Math.max(0, normSelection.top);
			normSelection.right = Math.min(game.getMapWidth(map) - 1, normSelection.right);
			normSelection.bottom = Math.min(game.getMapHeight(map) - 1, normSelection.bottom);


			return normSelection;
		}

		public RegionView(Context context, PlatformGame game, Rect selection) {
			super(context, game);
			this.showLeftButton = true;
			this.selection = new Rect();
			this.selection.set(selection);
			normSelection = new Rect();
			selectionF = new RectF();
			paint = new Paint();
		}

		public void surfaceCreated(SurfaceHolder holder) {
			super.surfaceCreated(holder);
			offX = -selection.centerX() + width / 2;
			offY = -selection.centerY() + height / 2;
		}

		protected boolean doSelection() {
			float x = Input.getLastTouchX();
			float y = Input.getLastTouchY();

			if (!isInLeftButton(x, y) && !isInRightButton(x, y)) {
				float mapX = x - offX;
				float mapY = y - offY;
				selection.set((int)mapX, (int)mapY, (int)mapX, (int)mapY);
			}

			return false;
		}

		protected void updateSelection() {
			if (Input.isTouchDown() && !leftButtonDown && !rightButtonDown) {
				float x = Input.getLastTouchX();
				float y = Input.getLastTouchY();

				selection.right = (int)(x - offX);
				selection.bottom = (int)(y - offY);

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

		public void drawGrid(Canvas c) {
			super.drawGrid(c);

			Rect normSelection = getNormSelection();
			selectionF.set(normSelection);
			selectionF.offset(offX, offY);

			paint.setColor(selectionFillColor);
			paint.setStyle(Style.FILL);
			c.drawRect(selectionF, paint);

			paint.setColor(selectionBorderColor);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(selectionBorderWidth);
			c.drawRect(selectionF, paint);

			paint.setColor(Color.BLACK);
			paint.setStyle(Style.FILL);
			paint.setTextSize(20);
			paint.setAntiAlias(true);

			//			String text = makeText(selection);
			//			c.drawText(text, 0, paint.getTextSize(), paint);

			if (!normSelection.isEmpty()) {
				String text = topLeftString(normSelection);
				c.drawText(text, selectionF.left - paint.measureText(text) / 2, 
						selectionF.top - 5, paint);

				text = bottomRightString(normSelection);
				c.drawText(text, selectionF.right - paint.measureText(text) / 2, 
						selectionF.bottom + paint.getTextSize() + 5, paint);
			}
		}

		protected String makeText(Rect rect) {
			StringBuilder sb = new StringBuilder();
			sb.append("\u21F1: (").append(rect.left).append(", ").append(rect.top)
			.append(") - \u21F2: (").append(rect.right).append(", ").append(rect.bottom)
			.append(")");
			return sb.toString();
		}

		protected String topLeftString(Rect rect) {
			StringBuilder sb = new StringBuilder();
			sb.append("(").append(rect.left).append(", ").append(rect.top).append(")");
			return sb.toString();
		}

		protected String bottomRightString(Rect rect) {
			StringBuilder sb = new StringBuilder();
			sb.append("(").append(rect.right).append(", ").append(rect.bottom).append(")");
			return sb.toString();
		}
	}
}
