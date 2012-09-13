package edu.elon.honors.price.data;

import java.util.LinkedList;

import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.game.Game;

public class Upgrader {
	@SuppressWarnings("deprecation")
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		game.tilesets[0].bitmapName = "StickTiles.png";
		game.maps.get(0).midGrounds.set(0, "whiteclouds-big.png");
		game.maps.get(0).midGrounds.set(1, "mountain-big.png");
		game.maps.get(0).midGrounds.set(2, "trees-big.png");
	}
	
	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Game.debug("Upgraded game from v%d to v%d...", from, to);
	}
}
