package edu.elon.honors.price.data;

import java.util.LinkedList;

import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.game.Game;

public class Upgrader {
	@SuppressWarnings("deprecation")
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		
	}
	
	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Game.debug("Upgraded game from v%d to v%d...", from, to);
	}
}
