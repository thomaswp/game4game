package com.twp.graphics;

import com.twp.game.Game;
import com.twp.input.Input;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GraphicsView extends SurfaceView implements SurfaceHolder.Callback {

	private Thread thread;
	
	private Game game;
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public GraphicsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		final SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return Input.onTouch(v, event);
			}
		});
		
		thread = new Thread(new Runnable() {
			public void run() {
				while (Graphics.getWidth() == 0);
				game.initialize();
				doLoop(surfaceHolder);
			}
		});
		
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, 
			int format, int width, int height) {
		synchronized (surfaceHolder) {
			Graphics.setWidth(width);
			Graphics.setHeight(height);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		thread.interrupt();
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent msg) {
		super.onKeyDown(keycode, msg);
		Input.keyDown(keycode, msg);
		return true;
		
	}
	
	@Override
	public boolean onKeyUp(int keycode, KeyEvent msg) {
		super.onKeyUp(keycode, msg);
		Input.keyUp(keycode, msg);
		return true;
	}

	private void doLoop(SurfaceHolder surfaceHolder) {
		while(!thread.isInterrupted()) {
			Canvas c = null;
			try {
				c = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					Input.update();
					if (game != null) {
						game.update();
					}
					Graphics.update();
					Graphics.draw(c);
				}
			} finally {
				if (c != null) {
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}
