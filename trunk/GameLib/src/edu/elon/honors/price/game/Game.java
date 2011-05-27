package edu.elon.honors.price.game;


import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.GraphicsView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

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
		view = new GraphicsView(this);
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
			//save the logic and dispose it
			logic.save(this);
			logic.dispose();
		}

		super.onPause();
		
		try {
			//We might as well just finalize because we've saved everything.
			//This also allows for some uniformity between Pause and Stop calls.
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		debug("Activity Resumed");
		
		//Load the logic back on resume
		Logic logic = getNewLogic();
		view.setLogic(logic);
		
		synchronized (logic) {
			logic.load(this);
		}

		super.onResume();
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		//Pause the Logic when the menu is opened
		Logic logic = view.getLogic();
		if (logic != null)
			logic.setPaused(true);
		
		return super.onMenuOpened(featureId, menu);
	}

	/**
	 * A method to write specially formatted debug text.
	 * 
	 * @param text The text to be written.
	 */
	public static void debug(String text) {
		System.out.println("---" + text + "---");
	}
}