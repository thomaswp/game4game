package edu.elon.honors.price.maker;

import java.io.Serializable;
import java.lang.reflect.Field;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Debug;

/**
 * A {@link SaveableActivity} specifically for {@link PlatformGame}
 * objects. This class handles retrieving the game from
 * the parent Activity, checking for changes and returning it
 * when this Activity finishes.
 */
public class DatabaseActivity extends SaveableActivity {

	public static final int REQUEST_RETURN_GAME = 10;
	
	protected PlatformGame game;
	//protected Bundle extras;
	
	/**
	 * Creates the activity, setting the appropriate window
	 * feature and flags. Also retrieves the game, either from
	 * the savedInstanceSate (if not null) or the calling
	 * Intent.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Bundle  extras = getIntent().getExtras();
		
		if (savedInstanceState != null && savedInstanceState.containsKey("game")) {
			game = (PlatformGame)savedInstanceState.getSerializable("game");
		}
		if (game == null) {
			game = (PlatformGame)extras.getSerializable("game");
		}

		super.onCreate(savedInstanceState);
	}
	
	public void showAlert(String title, String message) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("Ok", null)
		.show();
	}
	
	protected void populateViews(View root) {
		populateViews(root, game);
	}
	
	public static void populateViews(View root, PlatformGame game) {
		if (root instanceof IPopulatable) {
			((IPopulatable) root).populate(game);
		}
		if (root instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)root;
			for (int i = 0; i < vg.getChildCount(); i++) {
				populateViews(vg.getChildAt(i), game);
			}
		}
	}
	
	public static int setPopulatableViewIds(View root, int startId) {
		if (root instanceof IPopulatable) {
			root.setId(startId++);
		}
		if (root instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)root;
			for (int i = 0; i < vg.getChildCount(); i++) {
				startId = setPopulatableViewIds(
						vg.getChildAt(i), startId);
			}
		}
		return startId;
	}
	
	/**
	 * Creates a new Intent with this as its context
	 * and the given class as its class, then adds
	 * the game as an extra and returns it.
	 * @param cls The activity class to call
	 * @return The new intent
	 */
	protected Intent getNewGameIntent(Class<? extends Activity> cls) {
		Intent intent = new Intent(this, cls);
		intent.putExtra("game", game);
		return intent;
	}
	
	/**
	 * Wrapper for <code>extras.getSerializable(key)</code>
	 */
	public Serializable getExtra(String key) {
		return getIntent().getExtras().getSerializable(key);
	}
	
	/**
	 * Get the given Serializable extra from this
	 * Activity's Intent and casts it to the given class. 
	 * @param <T> The class to cast to
	 * @param key The key
	 * @param c The class to cast to
	 * @return The extra, or null if it does not exist
	 * or if it is not castable to the given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getExtra(String key, Class<T> c) {
		Serializable s = getIntent().getExtras().getSerializable(key);
		if (s == null || !c.isInstance(s)) return null;
		return (T)s;
	}
	
	/**
	 * Get the given Behavior extra from this
	 * Activity's Intent.
	 * @param key The key
	 * @return The extra, or null if it does not exist
	 * or if it is not castable to the given type.
	 */
	public Behavior getBehaviorExtra(String key) {
		return getExtra(key, Behavior.class);
	}
	
	/**
	 * Get the given Event extra from this
	 * Activity's Intent.
	 * @param key The key
	 * @return The extra, or null if it does not exist
	 * or if it is not castable to the given type.
	 */
	public Event getEventExtra(String key) {
		return getExtra(key, Event.class);
	}
	
	/**
	 * An experimental (but stable) method that
	 * assigns any fields in this class marked with
	 * the {@link AutoAssign} annotation to the View
	 * whose id's name is equal to either that field's name
	 * or the name property of its AutoAssign annotation.
	 * Alternatively, you can mark the whole class with the
	 * AutoAssign annotation and any viable fields will be
	 * auto-assigned.
	 * <br /> <br />
	 * For example, if there is a field in this class defined as:
	 * 
	 * <p><code>
	 * &#64;AutoAssign private EditText editTextExample;
	 * </code></p>
	 * 
	 * and there is an EditText being displayed that you would
	 * normally retrieve with:
	 * 
	 * <p><code>
	 * editTextExample = findViewById(R.id.editTextExample);
	 * </code></p>
	 * 
	 * this method would make that assignment for you 
	 * automatically using reflection. Alternately, you can
	 * name the variable whatever you want, as long as you
	 * give the name of the View's id to the 
	 * Autoassign annotation. For example:
	 * 
	 * <p><code>
	 * &#64;AutoAssign(name = "editTextExample") <br />
	 * EditText myEditText;
	 * </code></p>
	 * 
	 * will work the same way as the previous example.
	 */
	protected void autoAssign() {
		Field[] fields = this.getClass().getDeclaredFields();
		boolean typeAnnotation = 
			this.getClass().getAnnotation(AutoAssign.class) != null;
		for (Field field : fields) {
			if (!View.class.isAssignableFrom(field.getType())) {
				continue;
			}
			AutoAssign aa = field.getAnnotation(AutoAssign.class);
			if (aa != null || typeAnnotation) {
				String name;
				if (aa != null && aa.name().length() > 0) {
					name = aa.name();
				} else {
					name = field.getName();
				}
				
				try {
					Field id = R.id.class.getField(name);
					int lid = (Integer)id.get(null);
					View view = findViewById(lid);
					if (view == null) {
						Debug.write("No view with id %s in conent view", name);
						continue;
					}
					if (!field.getType().isAssignableFrom(view.getClass())) {
						Debug.write("Cannot assign view of type %s to %s of type %s",
								view.getClass().getName(),
								field.getName(),
								field.getType().getName());
						continue;
					}
					field.setAccessible(true);
					field.set(this, view);
					//Debug.write("%s successfully assigned!", field.getName());
				} catch (Exception e) {
					Debug.write("Problem with AutoAssign for field %s in class %s",
							field.getName(), this.getClass().getName());
					Debug.write(e);
				}
			}
		}
	}

	/**
	 * Calls {@link #onFinishing()} before 
	 * passing on control to the super implementation,
	 * {@link SaveableActivity#onBackPressed()}.
	 */
	@Override
	public void onBackPressed() {
		onFinishing();
		super.onBackPressed();
	}

	/**
	 * Compares the game field to the original
	 * that was passed to this Activity through
	 * the intent. Returns true if the two are
	 * different. This method should be overwritten
	 * if you have additional things to check, or
	 * if there is only one thing that could have changed
	 * (no variable could be modifies, for instance), and
	 * you will check this yourself.
	 * @return true if changes have been made which would
	 * require saving 
	 */
	@Override
	protected boolean hasChanged() {
		//long time = System.currentTimeMillis();
		PlatformGame oldGame = (PlatformGame)getIntent().getExtras().getSerializable("game");
		boolean r = !GameData.areEqual(oldGame, game);
		//time = System.currentTimeMillis() - time;
		//Debug.write("Game compared in " + time + "ms");
		return r;
	}

	/**
	 * Finishes with RESULT_OK status and passes
	 * an intent with the game as an extra to the
	 * parent Activity. Also calls {@link #putExtras(Intent)}
	 * for you to add any other extras to be
	 * passed back to the parent. This method cannot
	 * itself be overwritten.
	 */
	@Override
	protected final void finishOk(Intent intent) {
		onFinishing();
		intent.putExtra("game", game);
		putExtras(intent);
		super.finishOk(intent);
	}	

	/**
	 * Called before the game is Serialized.
	 * This is an activity's opportunity to
	 * save any form data to the game.
	 */
	protected void onFinishing() { }

	/**
	 * Called when the game is finishing and passing
	 * data back to its parent. If you have any extras
	 * you want passed to the parent, add them here.
	 * @param intent
	 */
	protected void putExtras(Intent intent) { }
	
	/**
	 * Calls {@link #onFinishing()} and adds the game to
	 * the outState to be retrieved when the activity
	 * is restarted. Which the game itself it saved and
	 * reloaded with this method, most subclasses do
	 * not currently have good save state logic and will
	 * not function 100% correctly after being
	 * finished and reconstructed.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) { 
		super.onSaveInstanceState(outState);
		onFinishing();
		outState.putSerializable("game", game);
	}
	
	/**
	 * Retrieves the game from any a returning child activity
	 * if it exists and returned with RESULT_OK.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//removed so that Selectors can take advantage of the system 
		//if (requestCode == REQUEST_RETURN_GAME) { 
		if (resultCode == RESULT_OK) {
			if (data.hasExtra("game")) {
				Serializable obj = data.getExtras().getSerializable("game");;
				if (obj instanceof PlatformGame) {
					game = (PlatformGame) obj;
				}
			}
		}
		//}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected String getPreferenceId() {
		return game.ID;
	}
}
