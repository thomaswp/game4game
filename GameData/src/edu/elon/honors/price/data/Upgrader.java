package edu.elon.honors.price.data;

import java.util.LinkedList;

import edu.elon.honors.price.game.Game;

public class Upgrader {
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		if (version < 4) {
			for (Map map : game.maps) {
				map.groundY = 48*4;
				map.groundImageName = "ground.png";
				map.skyImageName = "sky.png";
				map.midGrounds = new LinkedList<String>();
				map.midGrounds.add("whiteclouds.png");
				//map.midGrounds.add("mountain.png");
				//map.midGrounds.add("trees.png");
//				map.groundImageName = "bgnitegrnd.png";
//				map.skyImageName = "bgnitesky.jpg";
				Game.debug(map.groundImageName);
				
				game.objects[0].imageName = "rock.png";
				game.objects[0].zoom = 0.5f;
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
