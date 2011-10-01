package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.view.View;

public abstract class Page {
	
	protected Database parent;

	/**
	 * Gets the id for the XML layout associated with this page.
	 * @return The id
	 */
	public abstract int getViewId();
	/**
	 * Gets the name of this page
	 * @return The name
	 */
	public abstract String getName();
	/**
	 * Called when the page is created for the first time.
	 * This includes when this pages is switched to from
	 * another page. Any one-time initialization should go here.
	 */
	public abstract void onCreate();
	/**
	 * Called when the page is resumed, meaning that another
	 * activity was called on top of this page and is now finished.
	 */
	public abstract void onResume();
	
	public Page(Database parent) {
		this.parent = parent;
	}
	
	/**
	 * Called when the user is leaving this page, either
	 * to go to another page, leave the Database or because
	 * another Activity is being called. Any edited data
	 * should be saved at this point.
	 */
	protected void onPause() { }
	
	/**
	 * Returns the game currently being edited.
	 * @return
	 */
	protected PlatformGame getGame() {
		return parent.game;
	}
	
	/**
	 * Calls the parent's findViewById method.
	 * @param id The id of the view to find.
	 * @return The View
	 */
	protected View findViewById(int id) {
		return parent.findViewById(id);
	}
}