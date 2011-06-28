package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.view.View;

public abstract class PlatformDatabasePage {
	
	protected PlatformDatabase parent;

	public abstract int getViewId();
	public abstract String getName();
	public abstract void onCreate();
	public abstract void onResume();
	
	public PlatformDatabasePage(PlatformDatabase parent) {
		this.parent = parent;
	}
	
	protected void onPause() { }
	
	protected PlatformGame getGame() {
		return parent.game;
	}
	
	protected View findViewById(int id) {
		return parent.findViewById(id);
	}
}
