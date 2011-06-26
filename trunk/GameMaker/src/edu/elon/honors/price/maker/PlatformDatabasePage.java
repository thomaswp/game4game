package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.view.View;

public abstract class PlatformDatabasePage {
	
	protected PlatformDatabase parent;
	protected int viewId;
	
	public abstract void onCreate();
	public abstract void onResume();
	public abstract void onPause();

	public int getViewId() {
		return viewId;
	}
	
	public PlatformDatabasePage(PlatformDatabase parent, int viewId) {
		this.parent = parent;
		this.viewId = viewId;
	}
	
	protected PlatformGame getGame() {
		return parent.game;
	}
	
	protected View findViewById(int id) {
		return parent.findViewById(id);
	}
}
