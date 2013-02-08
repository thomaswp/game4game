package edu.elon.honors.price.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;

@SuppressWarnings("unused")
public class Upgrader {

	public final static int LATEST_VERSION = 8;

	@SuppressWarnings("deprecation")
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;

		
		if (version < 8) {
			for (ObjectClass object : game.objects) {
				object.moves = true;
				object.rotates = true;
			}
			upgraded(game);
		}
		if (version < 7) {
			game.tilesets = Arrays.copyOf(game.tilesets, 2);
			upgraded(game);
		}
		if (version < 6) {
			game.name = "New Game";
			upgraded(game);
		}
		if (version < 5) {
			for (ActorClass actor : game.actors) {
				actor.imageName = Data.ACTOR_7 + actor.imageName;
				Debug.write(actor.imageName);
			}
			upgraded(game);
		}
		if (version  < 4) {
			//Shift from actorLayer to plain actors list
			for (Map map : game.maps) {
				if (map.actorLayer != null) {
					int[][] tiles = map.actorLayer.tiles;
					ArrayList<ActorInstance> newActors = new ArrayList<ActorInstance>();
					for (int i = 0; i < tiles.length; i++) {
						for (int j = 0; j < tiles[i].length; j++) {
							if (tiles[i][j] >= 0) {
								ActorInstance instance = map.actors.get(tiles[i][j]);
								if (instance != null) {
									instance.row = i;
									instance.column = j;
									instance.id = tiles[i][j];
									newActors.add(instance);
								}
							}
						}
					}
					Collections.sort(newActors);
					map.actors = newActors;
					map.actorLayer = null;
				}
			}
			upgraded(game);
		}
	}

	private static void upgraded(PlatformGame game) {
		int from = game._VERSION_;
		game._VERSION_++;
		int to = game._VERSION_;
		Debug.write("Upgraded game from v%d to v%d...", from, to);
	}
}

