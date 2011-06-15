package edu.elon.honors.price.game;


import edu.elon.honors.price.audio.Audio;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.GraphicsRenderer;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.GraphicsView;
import edu.elon.honors.price.input.Input;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

/**
 * TODO:
 * Functions-
 * Sprite operations:
 * -squash
 * -spin
 * -score
 */

/**
 * An abstract class which defines some helpful methods for Activities using
 * GraphicsView.
 * 
 * @author Thomas Price
 *
 */
public abstract class Game extends Activity {

	protected GraphicsView view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		debug("Activity Created");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Data.setResources(getResources());
		Data.clearCache();
		Audio.setContext(this);
		Input.setVibrator((Vibrator)getSystemService(VIBRATOR_SERVICE));
		
		view = new GraphicsView(this);
		view.setRenderer(new GraphicsRenderer());
		this.setContentView(view);
	}
	
	//Game Activities need to gave a logic and GraphicsView
	protected abstract Logic getNewLogic();

	@Override
	public void onPause() {
		debug("Activity Paused");

		Logic logic = view.getLogic();
		
		synchronized(logic) {
			//clear the Graphics View's Logic
			view.setLogic(null);
			//save the logic
			logic.save(this);
			Input.reset();
			//reset the Graphics
			Graphics.reset();
		}
		
		Audio.stop();

		super.onPause();
	}

	@Override
	public void onResume() {
		debug("Activity Resumed");

		//view.getRenderer().setFlush(true);
		Input.reset();
		//Load the logic back on resume
		Logic logic = getNewLogic();
		view.setLogic(logic);
		
		synchronized (logic) {
			logic.load(this);
		}

		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		Game.debug("Activity Destroyed");
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		Game.debug("Activity Started");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Game.debug("Activity Stopped");
		super.onStop();
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		//Pause the Logic when the menu is opened
		Logic logic = view.getLogic();
		if (logic != null)
			logic.setPaused(true);
		view.getRenderer().setFlush(true);
		
		return super.onMenuOpened(featureId, menu);
	}

	public static void debug(Object o) {
		debug(o.toString());
	}
	
	public static void debug(float x) {
		debug("" + x);
	}
	
	/**
	 * A method to write specially formatted debug text.
	 * 
	 * @param text The text to be written.
	 */
	public static void debug(String text) {
		Log.d("Game", "---{" + text + "}---");
	}
}