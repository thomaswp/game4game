package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class Page extends LinearLayout {
	
	protected Database parent;
	protected boolean isCreated;
	
	public boolean isCreated() {
		return isCreated;
	}

	/**
	 * Gets the id for the XML layout associated with this page.
	 * @return The id
	 */
	public abstract int getLayoutId();
	
	/**
	 * Gets the name of this page
	 * @return The name
	 */
	public abstract String getName();

	public Page(Database parent) {
		super(parent);
		this.parent = parent;
	}
	
	/**
	 * Called when the page is created for the first time.
	 * This includes when this pages is switched to from
	 * another page. Any one-time initialization should go here.
	 */
	public void onCreate(ViewGroup parentView) {
		isCreated = true;
		parent.getLayoutInflater().inflate(getLayoutId(), this);
		ViewGroup.LayoutParams lps = new ViewGroup.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		parentView.addView(this, lps);
	}
	
	public abstract void addEditorButtons();
	
	/**
	 * Called when the page is resumed, meaning that another
	 * activity was called on top of this page and is now finished.
	 */
	public abstract void onResume();
	
	/**
	 * Called when the user is leaving this page, either
	 * to go to another page, leave the Database or because
	 * another Activity is being called. Any edited data
	 * should be saved at this point.
	 */
	protected abstract void onPause();
	
	/**
	 * Returns the game currently being edited.
	 * @return
	 */
	protected PlatformGame getGame() {
		return parent.game;
	}
	
	protected void setGame(PlatformGame game) {
		parent.game = game;
	}
	
	/**
	 * Called when a requested Activity returns with a result.
	 * 
	 * @param requestCode The Activity's request code 
	 * @param resultCode The Activity's result code (will always be OK if this is called)
	 * @param data The data returned by this Activity.
	 */
	public void onActivityResult(int requestCode, Intent data) {
		
	}
	
	private String convertKey(String key) {
		return getName() + "_" + key;
	}
	
	protected void putPreference(String key, Object value) {
		parent.putPreference(convertKey(key), value);
	}
	
	protected int getIntPreference(String key, int defaultValue) {
		return parent.getIntPreference(convertKey(key), defaultValue);
	}
	
	protected long getLongPreference(String key, long defaultValue) {
		return parent.getLongPreference(convertKey(key), defaultValue);
	}
	
	protected boolean getBooleanPreference(String key, boolean defaultValue) {
		return parent.getBooleanPreference(convertKey(key), defaultValue);
	}
	
	protected float getFloatPreference(String key, float defaultValue) {
		return parent.getFloatPreference(convertKey(key), defaultValue);
	}
	
	protected String getStringPreference(String key, String defaultValue) {
		return parent.getStringPreference(convertKey(key), defaultValue);
	}
}
