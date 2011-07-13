package edu.elon.honors.price.graphics;


import edu.elon.honors.price.audio.Audio;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.input.Input;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * A View class which holds the Thread running a Game's
 * Logic and which updates Input and Graphics. Also
 * implements this Views SurfaceHolder.Callback.
 * 
 * @author Thomas
 *
 */
public class GraphicsView extends GLSurfaceView {

	private Thread thread;
	private Logic logic;
	private GraphicsRenderer renderer;

	/**
	 * Gets the Thread on which the Logic is running.
	 * @return The Thread.
	 */
	public Thread getThread() {
		return thread;
	}
	
	public GraphicsRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Gets the Logic this View is running.
	 * @return The Logic
	 */
	public Logic getLogic() {
		return logic;
	}

	public void setRenderer (GraphicsRenderer renderer) {
		super.setRenderer(renderer);
		this.renderer = renderer;
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
		renderer.setLogic(logic);
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
	public void surfaceCreated(SurfaceHolder holder) {
		Game.debug("Surface Created");

		super.surfaceCreated(holder);

		//If for some reasons the Thread is still running, let's get rid of it.
		if (thread != null) {
			thread.interrupt();
		}

		//Create the Thread
		final SurfaceHolder surfaceHolder = getHolder();
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//Wait for the surface to be created
				while (Graphics.getWidth() == 0 || Graphics.getHeight() == 0);
				synchronized (logic) {
					//initialize the game Logic
					logic.initialize();
				}
				while (renderer.getFramesRendered() == 0);
				//Update Loop
				doLoop(surfaceHolder);
			}
		});

		//And start it
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Game.debug("Surface Destroyed");
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

		super.surfaceDestroyed(holder);
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
		long lastTime = System.currentTimeMillis();
		long lastUpdate = System.currentTimeMillis();
		final int TARGET_FPS = 60;
		int frame = 0;
		while(!thread.isInterrupted()) {
			try {
				//Make sure we're thread safe
				if (logic != null) {
					synchronized (logic) {
						if (logic != null) {
							//Game.debug("Game Loop");
							//Update everything
							long timeElapsed = System.currentTimeMillis() - lastUpdate;
							lastUpdate += timeElapsed;
							Input.update(timeElapsed);
							logic.update(timeElapsed);
							Graphics.update(timeElapsed);
							Audio.update();
						}
					}
					frame++;
					long timePassed = System.currentTimeMillis() - lastTime;
					int sleep = 1000 * frame / TARGET_FPS - (int)timePassed;
					if (sleep > 0) {
						Thread.sleep(sleep);
					}
					if (frame == TARGET_FPS) {
						frame = 0;
						lastTime = System.currentTimeMillis();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
