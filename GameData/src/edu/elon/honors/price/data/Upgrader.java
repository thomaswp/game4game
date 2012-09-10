package edu.elon.honors.price.data;

import java.util.LinkedList;

import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.game.Game;

public class Upgrader {
	@SuppressWarnings("deprecation")
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		game.tilesets[0].bitmapName = "StickTiles.png";
		game.hero.imageName = "hero.png";
	}
	
	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Game.debug("Upgraded game from v%d to v%d...", from, to);
	}
}
