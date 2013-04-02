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

	public final static int LATEST_VERSION = 18;

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
		if (version < 13) {
			for (Event e : game.getAllEvents()) {
				for (Action action : e.actions) {
					if (action.id == 20) { // Wait
						int time = action.params.getInt();
						Parameters params = new Parameters();
						params.addParam(0); params.addParam(time);
						action.params = new Parameters();
						action.params.addParam(params);
						
					}
				}
			}
			upgraded(game);
		}
		if (version < 14) {
			for (ObjectClass o : game.objects) {
				o.friction = 1;
			}
			upgraded(game);
		}
		if (version < 15) {
			for (ActorClass actor : game.actors) {
				if (actor.imageName.contains("hero")) {
					actor.imageName = "a5/ninja.png";
				}
				if (actor.imageName.contains("StickMan") ||
						actor.imageName.contains("Skull")) {
					actor.imageName = "a2/skeleton.png";
				}
				if (actor.imageName.contains("Gloop")) {
					actor.imageName = "a2/gloop.png";
				}
				if (actor.imageName.contains("ghost")) {
					actor.imageName = "a2/ghost.png";
				}
			}
			upgraded(game);
		}
		if (version < 16) {
			game.tilesets = Arrays.copyOf(game.tilesets, 3);
			game.tilesets[2] = new Tileset("Castle", "castle.png", 64, 64, 8, 8);
			
			upgraded(game);
		}
		if (version < 17) {
			for (Event event : game.getAllEvents()) {
				for (Action action : event.actions) {
					if (action.id == 24) {
						action.params.addParam(0);
					}
				}
			}
			
			upgraded(game);
		}
		if (version < 18) {
			game.tilesets = Arrays.copyOf(game.tilesets, game.tilesets.length + 1);
			game.tilesets[game.tilesets.length - 1] = new Tileset("Night", "night.png", 64, 64, 8, 8);
			
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

