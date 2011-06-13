package edu.elon.honors.price.maker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class TextureSelector extends Activity {
	
	TSView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getIntent().getExtras().getInt("id"));
		view = new TSView(this, bitmap);
		setContentView(view);
		
		super.onCreate(savedInstanceState);
	}
	
	
	private static class TSView extends SurfaceView implements SurfaceHolder.Callback{

		private Bitmap bitmap;
		private int height, width;
		private SurfaceHolder holder;
		private Paint paint = new Paint();
		private Thread thread;
		
		public TSView(Context context, Bitmap bitmap) {
			super(context);
			this.bitmap = bitmap;
			holder = getHolder();
			holder.addCallback(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			this.width = width;
			this.height = height;
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					update();
					draw();
				}
			});
			thread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
		
		public void draw() {
			Canvas canvas = holder.lockCanvas(null);
			try {
				paint.setColor(Color.BLUE);
				canvas.drawColor(Color.WHITE);
				canvas.drawBitmap(bitmap, 0, 0, paint);
			} finally {
				holder.unlockCanvasAndPost(canvas);
			}
		}

		private void update() {
			
		}
	}
}
