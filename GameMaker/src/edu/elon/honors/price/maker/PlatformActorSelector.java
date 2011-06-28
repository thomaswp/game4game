package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
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

		String[] ids = getIntent().getExtras().getStringArray("ids");
		String[] names = getIntent().getExtras().getStringArray("names");
		
		int id = getIntent().getExtras().getInt("id");
		if (id == -1) id = 1; else if (id != 0) id++;
			
		Bitmap[] bitmaps = new Bitmap[ids.length+1];
		bitmaps[0] = Bitmap.createBitmap(32 * 2, 48 * 2, Sprite.getDefaultConfig());
		bitmaps[0].eraseColor(Color.RED);
	
		Bitmap actorStart = Bitmap.createBitmap(32 * 2, 48 * 2, Sprite.getDefaultConfig());
		actorStart.eraseColor(Color.GREEN);
		bitmaps[1] = actorStart;

		for (int i = 1; i < ids.length; i++) {
			Bitmap source = Data.loadActor(ids[i]);
			bitmaps[i+1] = Bitmap.createBitmap(source, 0, 0, source.getWidth() / 4, source.getHeight() / 4);
			bitmaps[i+1] = Bitmap.createScaledBitmap(bitmaps[i+1], bitmaps[i+1].getWidth() * 2, bitmaps[i+1].getHeight() * 2, false);
		}

		final PlatformActorSelector me = this;
		view = new ASView(this, bitmaps, names, id, new ASView.Poster() {
			@Override
			void post(int id) {
				Intent intent = new Intent();
				if (id == 1) id = -1; else if (id != 0) id--;
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

		private static int BORDER = 150;

		private Bitmap[] bitmaps;
		private String[] names;
		private Rect[] bitmapRects;
		private int height, width;
		private SurfaceHolder holder;
		private Paint paint = new Paint();
		private Thread thread;
		private long lastTime;
		private float startScrollY;
		private float scrollY;
		private boolean move;
		private int id = 0;
		private RectF okRect, drawRect = new RectF();
		private Poster poster;

		public Thread getThread() {
			return thread;
		}

		public ASView(Context context, Bitmap[] bitmaps, String[] names, int id, Poster poster) {
			super(context);
			this.bitmaps = bitmaps;
			this.id = id;
			this.poster = poster;
			this.names = names;
			holder = getHolder();
			holder.addCallback(this);

			setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
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
			createRects();

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


				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(1);
				for (int i = 0; i < bitmapRects.length; i++) {
					paint.setColor(Color.WHITE);
					canvas.drawBitmap(bitmaps[i], bitmapRects[i].left, bitmapRects[i].top - scrollY, paint);
					paint.setColor(Color.argb(100, 0, 0, 0));
					canvas.drawRect(bitmapRects[i], paint);
				}

				if (id >= 0) {
					paint.setStrokeWidth(2);
					paint.setColor(Color.argb(255, 0, 0, 255));
					drawRect.set(bitmapRects[id]);
					drawRect.offset(0, -scrollY);
					canvas.drawRect(drawRect, paint);

				}

				paint.setColor(Color.GREEN);
				paint.setStyle(Style.FILL);
				canvas.drawRect(okRect, paint);

				if (id >= 0) {
					String text = id == 0 ? "Clear" : id == 1 ? "Hero Start" :names[id-1];
					paint.setColor(Color.BLACK);
					paint.setTextSize(30);
					paint.setAntiAlias(true);
					paint.setStyle(Style.FILL);
					float size = paint.measureText(text);
					canvas.drawText(text, width - size - 10, height - 10, paint);
				}

			} finally {
				holder.unlockCanvasAndPost(canvas);
			}
		}

		private void createRects() {
			bitmapRects = new Rect[bitmaps.length];
			int x = 0, y = 0, maxHeight = 0;
			for (int i = 0; i < bitmaps.length; i++) {
				Bitmap bmp = bitmaps[i];
				if (x + bmp.getWidth() + BORDER >= Graphics.getWidth() && x != 0) {
					x = 0;
					y += maxHeight;
					maxHeight = 0;
				}
				bitmapRects[i] = new Rect(x, y, x + bmp.getWidth(), y + bmp.getHeight());
				maxHeight = Math.max(maxHeight, bmp.getHeight());
				x += bmp.getWidth();
			}
		}

		private void update() {
			long timeElapsed = System.currentTimeMillis() - lastTime;
			lastTime += timeElapsed;
			Input.update(timeElapsed);

			if (Input.isTapped()) {
				if (okRect.contains(Input.getLastTouchX(), Input.getLastTouchY())) {
					poster.post(id);
					return;
				}
				move = Input.getLastTouchX() > Graphics.getWidth() - BORDER;
				startScrollY = scrollY;
				if (!move) {
					for (int i = 0; i < bitmapRects.length; i++) {
						if (bitmapRects[i].contains((int)Input.getLastTouchX(), 
								(int)(Input.getLastTouchY() + scrollY))) {
							id = i;
						}
					}
				}
			}

			if (Input.isTouchDown()) {
				if (move) {
					scrollY = startScrollY - Input.getDistanceTouchY();
					if (scrollY < 0) {
						startScrollY -= scrollY;
						scrollY = 0;
					}
					int bot = Math.max(bitmapRects[bitmapRects.length - 1].bottom, height);
					if (scrollY > bot - height) {
						startScrollY -= (scrollY - (bot - height));
						scrollY = bot - height;
					}
				}
			}
		}

		public static abstract class Poster {
			abstract void post(int id);
		}
	}
}
