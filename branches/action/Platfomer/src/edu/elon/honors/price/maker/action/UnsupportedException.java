package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;

public class UnsupportedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnsupportedException() {
		super("Unsupported Operation!");
		Debug.write("Unsupported operation!! Not Good!");
	}

}
