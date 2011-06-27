package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.view.View;

public abstract class PlatformDatabasePage {
	
	protected PlatformDatabase parent;
	protected int viewId;
	protected String name;
	
	public abstract void onCreate();
	public abstract void onResume();

	public int getViewId() {
		return viewId;
	}
	
	public String getName() {
		return name;
	}
	
	public PlatformDatabasePage(PlatformDatabase parent, int viewId, String name) {
		this.parent = parent;
		this.viewId = viewId;
		this.name = name;
	}
	
	protected PlatformGame getGame() {
		return parent.game;
	}
	
	protected View findViewById(int id) {
		return parent.findViewById(id);
	}
}
