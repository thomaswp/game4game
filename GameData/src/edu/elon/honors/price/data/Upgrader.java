package edu.elon.honors.price.data;

import java.util.LinkedList;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;

@SuppressWarnings("unused")
public class Upgrader {
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		for (ObjectClass o : game.objects) {
			if (o.collidesWith == null) {
				o.collidesWith = new boolean[4];
			}
		}
		for (ActorClass o : game.actors) {
			if (o.collidesWith == null) {
				o.collidesWith = new boolean[4];
			}
		}
		for (Map map : game.maps) {
			if (map.behaviors == null) {
				map.behaviors = new LinkedList<BehaviorInstance>();
			}
		}
	}
	
	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Debug.write("Upgraded game from v%d to v%d...", from, to);
	}
}

