package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class PlatformActorSelector extends Activity {

	ASView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		int[] ids = getIntent().getExtras().getIntArray("ids");
		int id = getIntent().getExtras().getInt("id");
		Bitmap[] bitmaps = new Bitmap[ids.length];
		for (int i = 0; i < ids.length; i++) {
			bitmaps[i] = Data.loadBitmap(ids[i]);
		}

		final PlatformActorSelector me = this;
		view = new ASView(this, bitmaps, id, new ASView.Poster() {
			@Override
			void post(int id) {
				Intent intent = new Intent();
				intent.putExtra("id", id);
				view.getThread().interrupt();
				me.setResult(RESULT_OK, intent);
				me.finish();
			}
		});
		setContentView(view);

		super.onCreate(savedInstanceState);
	}

	private static class ASView extends SurfaceView implements SurfaceHolder.Callback{

		private Bitmap[] bitmaps;
		private int height, width;
		private SurfaceHolder holder;
		private Paint paint = new Paint();
		private Thread thread;
		private long lastTime;
		private float startBitmapY;
		private boolean move;
		private int id = 0;
		private RectF okRect;
		private Poster poster;
		
		public Thread getThread() {
			return thread;
		}

		public ASView(Context context, Bitmap[] bitmaps, int id, Poster poster) {
			super(context);
			this.bitmaps = bitmaps;
			this.id = id;
			this.poster = poster;
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
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			this.width = width;
			this.height = height;
			okRect = new RectF(width - 50, 0, width, 50);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					lastTime = System.currentTimeMillis();
					while (true) {
						update();
						draw();
						if (thread.isInterrupted()) {
							break;
						}
					}
				}
			});
			thread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			boolean retry = true;
			thread.interrupt();
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
			thread = null;
		}

		public void draw() {
			Canvas canvas = holder.lockCanvas(null);
			try {
				canvas.drawColor(Color.WHITE);
//				canvas.drawBitmap(bitmap, bitmapX, bitmapY, paint);
//				paint.setStyle(Style.STROKE);
//				paint.setStrokeWidth(2);
//				paint.setColor(Color.GRAY);
//				
//				drawRect.set(selection.left * tileWidth, selection.top * tileHeight + (int)bitmapY, 
//						selection.right * tileWidth, selection.bottom * tileHeight + (int)bitmapY);
//				canvas.drawRect(drawRect, paint);
//				
//				paint.setColor(Color.GREEN);
//				paint.setStyle(Style.FILL);
				canvas.drawRect(okRect, paint);
			} finally {
				holder.unlockCanvasAndPost(canvas);
			}
		}

		private void update() {
//			long timeElapsed = System.currentTimeMillis() - lastTime;
//			lastTime += timeElapsed;
//			Input.update(timeElapsed);
//
//			if (Input.isTapped()) {
//				if (okRect.contains(Input.getLastTouchX(), Input.getLastTouchY())) {
//					poster.post(selection);
//					return;
//				}
//				startBitmapY = bitmapY;
//				move = Input.getLastTouchX() > bitmap.getWidth();
//				if (!move) {
//					startSelectX = Input.getLastTouchX();
//					startSelectY = Input.getLastTouchY() - bitmapY;
//				}
//			}
//
//			if (Input.isTouchDown()) {
//				if (move) {
//					bitmapY = startBitmapY + Input.getDistanceTouchY();
//					if (bitmapY > 0) {
//						startBitmapY -= bitmapY;
//						bitmapY = 0;
//					}
//					if (bitmapY < height - bitmap.getHeight()) {
//						startBitmapY -= (bitmapY - (height - bitmap.getHeight()));
//						bitmapY = height - bitmap.getHeight();
//					}
//				} else {
//					float x = Input.getLastTouchX(), y = Input.getLastTouchY() - bitmapY;
//					float left = Math.min(startSelectX, x), right = Math.max(startSelectX, x);
//					float top = Math.min(startSelectY, y), bottom = Math.max(startSelectY, y);
//					selection.set((int)(left / tileWidth), (int)(top / tileHeight), 
//							(int)(right / tileWidth) + 1, (int)(bottom / tileHeight) + 1);
//					selection.right = Math.min(selection.right, bitmap.getWidth() / tileWidth);
//				}
//			}
		}
		
		public static abstract class Poster {
			abstract void post(int id);
		}
	}
}
