package edu.elon.honors.price.game;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.elon.honors.price.audio.Audio;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.GraphicsRenderer;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.GraphicsView;
import edu.elon.honors.price.graphics.MessageSprite;
import edu.elon.honors.price.input.Input;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * An abstract class which defines some helpful methods for Activities using
 * GraphicsView.
 * 
 * @author Thomas Price
 *
 */
public abstract class Game extends Activity {

	protected static Game currentGame;

	protected GraphicsView view;
	protected Handler alertHandler = new Handler(), toastHandler = new Handler();
	protected boolean alertShowing;
	protected String nextMessage;
	protected int toastOffset;
	protected Toast toast;

	public static Game getCurrentGame() {
		return currentGame;
	}
	
	public Logic getLogic() {
		return view.getLogic();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.write("Activity Created");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Cache.clearCache();
		Audio.setContext(this);
		Input.setVibrator((Vibrator)getSystemService(VIBRATOR_SERVICE));

		view = new GraphicsView(this);
		view.setRenderer(new GraphicsRenderer(this));
		this.setContentView(view);
	}

	//Game Activities need to gave a logic and GraphicsView
	protected abstract Logic getNewLogic();

	@Override
	public void onPause() {
		Debug.write("Activity Paused");

		Logic logic = view.getLogic();

		synchronized(logic) {
			//clear the Graphics View's Logic
			view.setLogic(null);
			//save the logic
			logic.save();
			Input.reset();
			//reset the Graphics
			Graphics.reset();
			view.getRenderer().setFlush(true);
		}

		Audio.stop();

		super.onPause();
	}

	@Override
	public void onResume() {
		Debug.write("Activity Resumed");

		
		currentGame = this;
		Input.reset();
		//Load the logic back on resume
		Logic logic = getNewLogic();
		view.setLogic(logic);

		synchronized (logic) {
			logic.load();
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Debug.write("Activity Destroyed");
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		Debug.write("Activity Started");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Debug.write("Activity Stopped");
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

	public static Bitmap loadBitmap(int id) {
		try {
			if (Cache.isBitmapRegistered(id)) {
				//Debug.write("Cache: " + id);
				return Cache.getRegisteredBitmap(id);
			}
			else {
				//Debug.write("Load New: " + id);
				Bitmap bmp = BitmapFactory.decodeResource(currentGame.getResources(), id);
				Cache.RegisterBitmap(id, bmp);
				return bmp;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String loadString(int id) {
		return getCurrentGame().getResources().getString(id);
	}

	/**
	 * Saves a serializable class, using the given activity with the given name.
	 * 
	 * @param name The name of the saved file. Used to load the data later.
	 * @param parent The Activity to use for the saving.
	 * @param data The serializable class to save.
	 */
	public static boolean saveObject(String name, Serializable data) {
		try {
			FileOutputStream fos = currentGame.openFileOutput(name, Context.MODE_WORLD_WRITEABLE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(data);
			out.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Loads a serializable class, using the given activity with the given name.
	 * 
	 * @param name The name of the file to be loaded.
	 * @param parent The Activity to use for the loading.
	 */
	public static Object loadObject(String name) {
		String[] files = currentGame.fileList();
		boolean found = false;
		for (int i = 0; i < files.length; i++)
			found |= files[i].equals(name);
		try {
			if (!found) {
				throw new RuntimeException("Cannnot find file '" + name + "'");
			}
			FileInputStream fis = currentGame.openFileInput(name);
			ObjectInputStream in = new ObjectInputStream(fis);
			Object data = in.readObject();
			in.close();
			return data;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	
	
	public static void showMessage(String message) {
		new MessageSprite(message);
	}

	public void showAlert(String message) {
		final Logic logic = view.getLogic();
		final String m = message;
		
		alertHandler.post(new Runnable() {
			@Override
			public void run() {
				if (alertShowing) {
					nextMessage = m;
					return;
				}
				alertShowing = true;
				synchronized (logic) {
					view.setLogic(null);
					AlertDialog alert = new AlertDialog.Builder(Game.getCurrentGame()).create();
					alert.setTitle("Debug");
					alert.setMessage(m);
					alert.show();
					alert.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							view.setLogic(logic);
							logic.setPaused(true);
							alertShowing = false;
							if (nextMessage != null) {
								showAlert(nextMessage);
								nextMessage = null;
							}
						}
					});
				}
			}
		});
	}
	
	public void showToast(String message) {
		final String m = message;
		toastHandler.post(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(currentGame, m, Toast.LENGTH_SHORT);
				}
				else {
					toast.setText(m);
				}
				toast.show();
			}
		});
	}
}