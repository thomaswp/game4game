package edu.elon.honors.price.data;

import java.util.LinkedList;

import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.game.Game;

public class Upgrader {
	@SuppressWarnings("deprecation")
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
		if (version < 4) {
			for (Map map : game.maps) {
//				map.groundY = 48*4;
//				map.groundImageName = "ground.png";
//				map.skyImageName = "sky.png";
				map.midGrounds = new LinkedList<String>();
				map.midGrounds.add("whiteclouds.png");
				map.midGrounds.add("mountain.png");
				map.midGrounds.add("trees.png");
//				map.groundImageName = "bgnitegrnd.png";
//				map.skyImageName = "bgnitesky.jpg";
//				
//				game.objects[0].imageName = "rock.png";
//				game.objects[0].zoom = 0.5f;
				map.actorLayer.defaultValue = -1;
			}
			//game.tilesets[0].bitmapName = "grass.png";
			game.tilesets = new Tileset[3];
			game.tilesets[0] = new Tileset("Default", "tiles.png", 48, 48, 8, 8);
			game.tilesets[1] = new Tileset("Ice", "ice.png", 48, 48, 8, 8);
			game.tilesets[2] = new Tileset("Grass", "grass.png", 48, 48, 8, 8);
			//upgraded(game);
		}
		
		if (version == 2) {
			game.mapBehaviors = new LinkedList<Behavior>();
			game.objectBehaviors = new LinkedList<Behavior>();
			game.actorBehaviors = new LinkedList<Behavior>();
			int i = 0;
			for (Map map : game.maps) {
				map.behaviors = new LinkedList<BehaviorInstance>();
			}
			for (ActorClass actor : game.actors) {
				actor.behaviors = new LinkedList<BehaviorInstance>();
			}
			for (ObjectClass object : game.objects) {
				object.behaviors = new LinkedList<BehaviorInstance>();
			}
			upgraded(game);
		}
	}
	
	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Game.debug("Upgraded game from v%d to v%d...", from, to);
	}
}
