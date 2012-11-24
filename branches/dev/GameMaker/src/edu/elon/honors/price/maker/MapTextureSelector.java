package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Input;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

public class MapTextureSelector extends Activity {

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
		final MapTextureSelector me = this;
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
		setContentView(view);

		super.onCreate(savedInstanceState);
	}

	private static class TSView extends BasicCanvasView {

		private Bitmap bitmap;
		private int height;
		private SurfaceHolder holder;
		private Paint paint = new Paint();
		private float bitmapX, bitmapY, startBitmapY;
		private boolean move;
		private Rect selection = new Rect(0, 0, 1, 1), drawRect = new Rect();
		private int tileWidth, tileHeight;
		private float startSelectX, startSelectY;
		private RectF okRect;
		private Poster poster;

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
			float size = toPx(30);
			okRect = new RectF(width - size, 0, width, size);
		}
		
		@Override
		public void onDraw(Canvas c) {
			c.drawColor(Color.WHITE);
			c.drawBitmap(bitmap, bitmapX, bitmapY, paint);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			paint.setColor(Color.GRAY);

			drawRect.set(selection.left * tileWidth, selection.top * tileHeight + (int)bitmapY, 
					selection.right * tileWidth, selection.bottom * tileHeight + (int)bitmapY);
			c.drawRect(drawRect, paint);

			paint.setColor(Color.GREEN);
			paint.setStyle(Style.FILL);
			c.drawRect(okRect, paint);
		}

		@Override
		protected void update(long timeElapsed) {
			if (Input.isTapped()) {
				if (okRect.contains(Input.getLastTouchX(), Input.getLastTouchY())) {
					poster.post(selection);
					return;
				}
				startBitmapY = bitmapY;
				move = Input.getLastTouchX() > bitmap.getWidth();
				if (!move) {
					startSelectX = Input.getLastTouchX();
					startSelectY = Input.getLastTouchY() - bitmapY;
				}
			}

			if (Input.isTouchDown()) {
				if (move) {
					bitmapY = startBitmapY + Input.getDistanceTouchY();
					if (bitmapY > 0) {
						startBitmapY -= bitmapY;
						bitmapY = 0;
					}
					if (bitmapY < height - bitmap.getHeight()) {
						startBitmapY -= (bitmapY - (height - bitmap.getHeight()));
						bitmapY = height - bitmap.getHeight();
					}
				} else {
					float x = Input.getLastTouchX(), y = Input.getLastTouchY() - bitmapY;
					float left = Math.min(startSelectX, x), right = Math.max(startSelectX, x);
					float top = Math.min(startSelectY, y), bottom = Math.max(startSelectY, y);
					selection.set((int)(left / tileWidth), (int)(top / tileHeight), 
							(int)(right / tileWidth) + 1, (int)(bottom / tileHeight) + 1);
					selection.right = Math.min(selection.right, bitmap.getWidth() / tileWidth);
				}
			}
		}

		public static abstract class Poster {
			abstract void post(Rect rect);
		}
	}
}
