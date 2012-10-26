package edu.elon.honors.price.data;

import edu.elon.honors.price.game.Game;

@SuppressWarnings("unused")
public class Upgrader {
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		game.tilesets[0].bitmapName = "StickTiles.png";
//		game.maps.get(0).midGrounds.set(0, "whiteclouds-big.png");
//		game.maps.get(0).midGrounds.set(1, "mountain-big.png");
//		game.maps.get(0).midGrounds.set(2, "trees-big.png");
		game.tilesets[2].tileWidth = 64;
		game.tilesets[2].tileHeight = 64;
	}
	
	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Game.debug("Upgraded game from v%d to v%d...", from, to);
	}
}
