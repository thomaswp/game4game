package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Input;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class BasicCanvasView extends SurfaceView implements SurfaceHolder.Callback {

	private long lastUpdate;
	private boolean running;
	
	protected Thread thread;
	protected int width, height;
	
	protected boolean paused;

	protected abstract void update(long timeElapsed);
	
	public BasicCanvasView(Context context) {
		super(context);
		getHolder().addCallback(this);
		Input.reset();
		Input.setVibrator((Vibrator)getContext().
				getSystemService(Context.VIBRATOR_SERVICE));
		Input.setMultiTouch(false);
	}
	
	public void pause() {
		paused = true;
	}
	
	public void resume() {
		paused = false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Input.onTouch(this, event);
		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.width = width;
		this.height = height;
		Debug.write("Change: %d, %d", width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.width = holder.getSurfaceFrame().width();
		this.height = holder.getSurfaceFrame().height();
		initializeGraphics();
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
	
	/**
	 * Override this method for initialization
	 * logic that requires width and height to
	 * be set
	 */
	protected void initializeGraphics() {
		
	}
	
	private void updateThread() {
		long timeElapsed = System.currentTimeMillis() - lastUpdate;
		lastUpdate += timeElapsed;
		if (paused) {
			return;
		}
		
		Input.update(timeElapsed);
		update(timeElapsed);
		
		Canvas c = getHolder().lockCanvas();
		if (c != null) {
			try {
				onDraw(c);
			} finally {
				getHolder().unlockCanvasAndPost(c);
			}
		}
	}
	
	protected float toPx(float dip) {
		return Screen.dipToPx(dip, getContext());
	}
}
