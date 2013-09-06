package edu.elon.honors.price.game;

/**
 * A game's logic. To work with other classes it must
 * have this interface's methods.
 * 
 * @author Thomas Price
 *
 */
public interface Logic {
	public void setPaused(boolean paused);
	public void initialize();
	public void update(long timeElapsed);
	public void save();
	public void load();
}
