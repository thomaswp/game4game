package edu.elon.honors.price.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import android.graphics.Color;

import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.physics.Vector;

@SuppressWarnings("unused")
public class Upgrader {

	public final static int LATEST_VERSION = 12;

	@SuppressWarnings("deprecation")
	public static void upgrade(PlatformGame game) {
		int version = game._VERSION_;
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
		if (version < 5) {
			for (ActorClass actor : game.actors) {
				actor.imageName = Data.ACTOR_7 + actor.imageName;
				Debug.write(actor.imageName);
			}
			upgraded(game);
		}
		if (version < 6) {
			game.name = "New Game";
			upgraded(game);
		}
		if (version < 7) {
			game.tilesets = Arrays.copyOf(game.tilesets, 2);
			upgraded(game);
		}
		if (version < 8) {
			for (ObjectClass object : game.objects) {
				object.moves = true;
				object.rotates = true;
			}
			upgraded(game);
		}
		if (version < 9) {
			for (Event event : game.getAllEvents()) {
				event.triggers.clear();
			}
			upgraded(game);
		}
		if (version < 10) {
			for (Event event : game.getAllEvents()) {
				for (int i = 0; i < event.actions.size(); i++) {
					Action action = event.actions.get(i); 
					if (action.id == 7) { //If
						Action actionElse = new Action(22, new Parameters()); //Else
						actionElse.description = "<i><font color='#8800FF'>Else</font></i>";
						actionElse.dependsOn = action;
						actionElse.indent = action.indent;
						int j = i + 1;
						while (j < event.actions.size() && 
								event.actions.get(j).indent > action.indent) j++;
						event.actions.add(j, actionElse);
					}
				}
			}
			upgraded(game);
		}
		if (version < 11) {
			for (Map map : game.maps) {
				map.gravity = new Vector(0, 10);
			}
			upgraded(game);
		}
		if (version < 12) {
			for (ActorClass actor : game.actors) {
				actor.color = Color.WHITE;
			}
			for (ObjectClass object : game.objects) {
				object.color = Color.WHITE;
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

