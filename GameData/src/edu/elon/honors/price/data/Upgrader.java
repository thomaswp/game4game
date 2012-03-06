package edu.elon.honors.price.data;

import edu.elon.honors.price.game.Game;

public class Upgrader {
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		if (version < 3) {
			for (Map map : game.maps) {
				map.groundY = 48 * 4;
				map.groundImageName = "bgmntgrnd.png";
				map.skyImageName = "bgmntsky.jpg";
//				map.groundImageName = "bgnitegrnd.png";
//				map.skyImageName = "bgnitesky.jpg";
			}
			//upgraded(game);
		}
	}
	
	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Game.debug("Upgraded game from v%d to v%d...", from, to);
	}
}
