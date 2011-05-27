package edu.elon.honors.price.graphics;


import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.input.Input;

import android.content.Context;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * A View class which holds the Thread running a Game's
 * Logic and which updates Input and Graphics. Also
 * implements this Views SurfaceHolder.Callback.
 * 
 * @author Thomas
 *
 */
public class GraphicsView extends SurfaceView implements SurfaceHolder.Callback {

	private Thread thread;
	private Logic logic;

	/**
	 * Gets the Thread on which the Logic is running.
	 * @return The Thread.
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Gets the Logic this View is running.
	 * @return The Logic
	 */
	public Logic getLogic() {
		return logic;
	}

	/**
	 * Sets the Logic this view is running.
	 * @param logic The Logic
	 */
	public void setLogic(Logic logic) {
		if (this.logic == null)
			this.logic = logic;
		else {
			synchronized (this.logic) {
				this.logic = logic;			
			}
		}
	}

	public GraphicsView(Context context) {
		super(context);

		Game.debug("Graphics View Created");

		//Create a Touch Listener
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Link it to Input
				return Input.onTouch(v, event);
			}
		});

		//Add the surface holder
		final SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, 
			int format, int width, int height) {
		synchronized (surfaceHolder) {
			//Set the drawable area to this surface's dimensions.
			Graphics.setWidth(width);
			Graphics.setHeight(height);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		Game.debug("Surface Holder Created");

		//If for some reasons the Thread is still running, let's get rid of it.
		if (thread != null) {
			thread.interrupt();
		}

		//Create the Thread
		final SurfaceHolder surfaceHolder = getHolder();
		thread = new Thread(new Runnable() {
			public void run() {
				//Wait for the surface to be created
				while (Graphics.getWidth() == 0);
				synchronized (logic) {
					//initialize the game Logic
					logic.initialize();
				}
				//Update Loop
				doLoop(surfaceHolder);
			}
		});

		//And start it
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		//End the thread safely
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

	@Override
	public boolean onKeyDown(int keycode, KeyEvent msg) {
		super.onKeyDown(keycode, msg);
		//Pass key input to Input
		Input.keyDown(keycode, msg);
		return true;

	}

	@Override
	public boolean onKeyUp(int keycode, KeyEvent msg) {
		super.onKeyUp(keycode, msg);
		//Pass key input to Input
		Input.keyUp(keycode, msg);
		return true;
	}

	private void doLoop(SurfaceHolder surfaceHolder) {
		while(!thread.isInterrupted()) {
			Canvas c = null;
			try {
				//Make sure we're thread safe
				if (logic != null) {
					synchronized (logic) {
						if (logic != null) {
							//Lock the canvas
							c = surfaceHolder.lockCanvas(null);
							synchronized (surfaceHolder) {
								//Update everything
								Input.update();
								logic.update();
								Graphics.update();
								
								//And draw
								Graphics.draw(c);
							}
						}
					}
				}
			} finally {
				if (c != null) {
					//Ensure the surgaceHolder is unlocked.
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}
