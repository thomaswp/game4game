package edu.elon.honors.price.game;

import android.app.Activity;

/**
 * A game's logic. To work with other classes it must
 * have this interface's methods.
 * 
 * @author Thomas Price
 *
 */
public interface Logic {
	public void update();
	public void initialize();
	public void dispose();
	public void save(Activity parent);
	public void load(Activity parent);
	public void setPaused(boolean paused);
}
