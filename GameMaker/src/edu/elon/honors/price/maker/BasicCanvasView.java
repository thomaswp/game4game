package edu.elon.honors.price.maker;

import edu.elon.honors.price.input.Input;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class BasicCanvasView extends SurfaceView implements SurfaceHolder.Callback {

	private long lastUpdate;
	private boolean running;
	
	protected Thread thread;
	protected int width, height;

	protected abstract void update(long timeElapsed);
	
	public BasicCanvasView(Context context) {
		super(context);
		getHolder().addCallback(this);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		Input.onTouch(this, event);
		return true;//super.onTouchEvent(event);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.width = holder.getSurfaceFrame().width();
		this.height = holder.getSurfaceFrame().height();
		running = true;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(running) {
					updateThread();
				}
			}
		});
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		running = false;
		while(true) {
			try {
				thread.join();
				Input.reset();
				break;
			} catch (Exception e) {}
		}
	}	
	
	private void updateThread() {
		long timeElapsed = System.currentTimeMillis() - lastUpdate;
		lastUpdate += timeElapsed;
		
		update(timeElapsed);
		Canvas c = getHolder().lockCanvas();
		try {
			onDraw(c);
		} finally {
			getHolder().unlockCanvasAndPost(c);
		}
	}
}
