package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.maker.MapActivityBase.MapView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MapEditorTextureSelector extends Activity {

	TSView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Bitmap bitmap = Data.loadTileset(getIntent().getExtras().getString("id"));
		int tileWidth = getIntent().getExtras().getInt("tileWidth");
		int tileHeight = getIntent().getExtras().getInt("tileHeight");
		int left = getIntent().getExtras().getInt("left");
		int top = getIntent().getExtras().getInt("top");
		int right = getIntent().getExtras().getInt("right");
		int bottom = getIntent().getExtras().getInt("bottom");
		Rect r = new Rect(left, top, right, bottom);
		final MapEditorTextureSelector me = this;
		
		
		view = new TSView(this, bitmap, r, tileWidth, tileHeight, new TSView.Poster() {
			@Override
			void post(Rect rect) {
				Input.reset();
				Intent intent = new Intent();
				intent.putExtra("left", rect.left);
				intent.putExtra("top", rect.top);
				intent.putExtra("right", rect.right);
				intent.putExtra("bottom", rect.bottom);
				me.setResult(RESULT_OK, intent);
				me.finish();
			}
		});
		LinearLayout.LayoutParams lps = new LayoutParams(
				bitmap.getWidth() + Screen.dipToPx(50, this), 
				LayoutParams.FILL_PARENT);
		setContentView(view, lps);

		super.onCreate(savedInstanceState);
	}

	private static class TSView extends BasicCanvasView {

		private Bitmap bitmap;
		private SurfaceHolder holder;
		private Paint paint = new Paint();
		private float bitmapX, bitmapY, startBitmapY;
		private boolean move;
		private Rect selection = new Rect(0, 0, 1, 1), drawRect = new Rect();
		private int tileWidth, tileHeight;
		private float startSelectX, startSelectY;
		private PointF okCenter;
		private float okRad;
		private Poster poster;
		private boolean buttonDown;

		public TSView(Context context, Bitmap bitmap, Rect selection, int tileWidth, int tileHeight, Poster poster) {
			super(context);
			this.bitmap = bitmap;
			this.tileWidth = tileWidth;
			this.tileHeight = tileHeight;
			this.poster = poster;
			this.selection = selection;
			holder = getHolder();
			holder.addCallback(this);

			setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//Link it to Input
					return Input.onTouch(v, event);
				}
			});
		}

		@Override
		protected void initializeGraphics() {
			okRad = toPx(40);
			float x = bitmap.getWidth() + okRad;
			float y = width - x;
			okCenter = new PointF(x, y);
			
			Debug.write("%d, %d", width, height);
		}
		
		@Override
		public void onDraw(Canvas c) {
			c.drawColor(Color.DKGRAY);
			paint.setColor(Color.WHITE);
			c.drawRect(bitmapX, bitmapY, bitmapX + bitmap.getWidth(), 
					bitmapY + bitmap.getHeight(), paint);
			c.drawBitmap(bitmap, bitmapX, bitmapY, paint);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(SelectorMapBase.SELECTION_BORDER_WIDTH);
			paint.setColor(SelectorMapBase.SELECTION_BORDER_COLOR);
			paint.setAlpha(255);

			drawRect.set(selection.left * tileWidth, selection.top * tileHeight + (int)bitmapY, 
					selection.right * tileWidth, selection.bottom * tileHeight + (int)bitmapY);
			c.drawRect(drawRect, paint);

			int alpha = buttonDown ? 255 : 150;
			paint.setColor(Color.DKGRAY);
			paint.setAlpha(alpha);
			paint.setStyle(Style.FILL);
			c.drawCircle(okCenter.x, okCenter.y, okRad, paint);
			paint.setColor(Color.LTGRAY);
			paint.setAlpha(alpha);
			c.drawCircle(okCenter.x, okCenter.y, okRad * 0.9f, paint);
			
			paint.setTextSize(Screen.spToPx(
					MapActivityBase.BUTTON_TEXT_SIZE - 2, getContext()));
			String text = "Ok";
			paint.setColor(Color.BLACK);
			float textWidth = paint.measureText(text);
			float x = (width + bitmap.getWidth()) / 2f - textWidth * 1 / 3;
			float y = paint.getTextSize() * 1.4f;
			c.drawText(text, x, y, paint);
		}

		@Override
		protected void update(long timeElapsed) {
			float dx = Input.getLastTouchX() - okCenter.x;
			float dy = Input.getLastTouchY() - okCenter.y;
			boolean inButton = (dx * dx + dy * dy < okRad * okRad);
			
			if (Input.isTapped()) {
				if (inButton) {
					buttonDown = true;
					move = false;
				} else {
					startBitmapY = bitmapY;
					move = Input.getLastTouchX() > bitmap.getWidth();
					if (!move) {
						startSelectX = Input.getLastTouchX();
						startSelectY = Input.getLastTouchY() - bitmapY;
					}
				}
			}

			if (Input.isTouchDown()) {
				if (move) {
					bitmapY = startBitmapY + Input.getDistanceTouchY();
					if (bitmapY + bitmap.getHeight() < height) {
						startBitmapY -= (bitmapY - (height - bitmap.getHeight()));
						bitmapY = height - bitmap.getHeight();
					}
					if (bitmapY > 0) {
						startBitmapY -= bitmapY;
						bitmapY = 0;
					}
				} else if (!inButton) {
					float x = Input.getLastTouchX(), y = Input.getLastTouchY() - bitmapY;
					float left = Math.min(startSelectX, x), right = Math.max(startSelectX, x);
					float top = Math.min(startSelectY, y), bottom = Math.max(startSelectY, y);
					selection.set((int)(left / tileWidth), (int)(top / tileHeight), 
							(int)(right / tileWidth) + 1, (int)(bottom / tileHeight) + 1);
					selection.left = Math.max(selection.left, 0);
					selection.top = Math.max(selection.top, 0);
					selection.right = Math.min(selection.right, bitmap.getWidth() / tileWidth);
					selection.bottom = Math.min(selection.bottom, bitmap.getHeight() / tileHeight);
				}
			} else {
				if (buttonDown && inButton) {
					poster.post(selection);
				}
				buttonDown = false;
			}
		}

		public static abstract class Poster {
			abstract void post(Rect rect);
		}
	}
}
