package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import edu.elon.honors.price.game.Game;

public class Map extends GameData {
	private static final long serialVersionUID = 9L;

	
	
	public Event[] events;
	public int tilesetId;
	public MapLayer[] layers;
	public MapLayer actorLayer;
	public ArrayList<ActorInstance> actors; 
	public ArrayList<ObjectInstance> objects;
	public LinkedList<BehaviorInstance> behaviors;
	public int rows, columns;
	public Serializable editorData;
	public int groundY;
	public String groundImageName;
	public String skyImageName;
	public LinkedList<String> midGrounds = new LinkedList<String>();

	public Map(PlatformGame game) {

		rows = 8;
		columns = 30;

		groundY = 4 * game.tilesets[tilesetId].tileHeight;
		groundImageName = "ground.png";
		skyImageName = "sky.png";

		midGrounds = new LinkedList<String>();
		midGrounds.add("whiteclouds.png");
		midGrounds.add("mountain.png");
		midGrounds.add("trees.png");
		
		tilesetId = 0;

		//layer with tiles representing indices in the actors ArrayList 
		actorLayer = new MapLayer("actors", rows, columns, false, -1);
		actorLayer.defaultValue = -1;

		actors = new ArrayList<ActorInstance>();
		actors.add(null);
		setActor(0, 0, 0);

		//		objectLayer = new MapLayer("objects", rows, columns, false);
		//		objectLayer.setAll(-1);

		objects = new ArrayList<ObjectInstance>();
		//addObject(0, 20, 20);


		events = new Event[3];
		for (int i = 0; i < events.length; i++) 
			events[i] = new Event(String.format("Event%d", i));

		layers = new MapLayer[3];
		MapLayer layer = new MapLayer("background", rows, columns, false, 0);
		layers[0] = layer;
		layer = new MapLayer("l1", rows, columns, true, 0);
		layers[1] = layer;
		layer = new MapLayer("l2", rows, columns, false, 0);
		layers[2] = layer;
	}

	public void resize(int dRow, int dCol, boolean anchorTop, boolean anchorLeft,
			int tileWidth, int tileHeight) {

		LinkedList<MapLayer> allLayers = new LinkedList<MapLayer>();
		for (MapLayer layer : layers) {
			allLayers.add(layer);
		}
		allLayers.add(actorLayer);

		for (MapLayer layer : allLayers) {
			if (dRow > 0) {
				int[][] newTiles = new int[rows + dRow][];
				for (int i = 0; i < newTiles.length; i++) {
					int oldIndex = anchorTop ? i - dRow : i;
					if (oldIndex >= 0 && oldIndex < rows) {
						newTiles[i] = layer.tiles[oldIndex];
					} else {
						newTiles[i] = new int[columns];
						if (layer.defaultValue != 0) {
							for (int j = 0; j < newTiles[i].length; j++) {
								newTiles[i][j] = layer.defaultValue;
							}
						}
					}
				}
				layer.tiles = newTiles;
			} else if (dRow < 0) {
				if (anchorTop) {
					layer.tiles = Arrays.copyOfRange(layer.tiles, -dRow, rows);
				} else {
					layer.tiles = Arrays.copyOfRange(layer.tiles, 0, rows + dRow);
				}
			}

			layer.rows += dRow;

			for (int i = 0; i < layer.rows; i++) {
				if (dCol > 0) {
					layer.tiles[i] = Arrays.copyOf(layer.tiles[i], columns + dCol);
					if (layer.defaultValue != 0) {
						for (int j = columns; j < columns + dCol; j++) {
							layer.tiles[i][j] = layer.defaultValue;
						}
					}
				} else if (dCol < 0) {
					layer.tiles[i] = Arrays.copyOf(layer.tiles[i], columns + dCol);
				}
			}

			layer.columns += dCol;
		}

		for (ObjectInstance o : objects) {
			if (anchorLeft)
				o.startX += dCol * tileWidth;
			if (anchorTop)
				o.startY += dRow * tileHeight;
		}

		rows += dRow;
		columns += dCol;
		//TODO: SHIFT EVENTS!!

		for (int[] a : layers[1].tiles) {
			Game.debug(Arrays.toString(a));
		}
	}

	public int addObject(int classIndex, int startX, int startY) {
		int id = objects.size();
		objects.add(new ObjectInstance(id, classIndex, startX, startY));
		return id;
	}

	//	public int getObjectType(int row, int column) {
	//		int id = objectLayer.tiles[row][column];
	//		return id >= 0 ? objects.get(id).classIndex : -1;
	//	}
	//	
	//	public ObjectInstance getObjectInstance(int row, int column) {
	//		return objects.get(objectLayer.tiles[row][column]);
	//	}
	//	
	//	public int setObject(int row, int column, int type) {
	//		int previousId = objectLayer.tiles[row][column];
	//		if (type == -1) {
	//			if (previousId >= 0) {
	//				//Remove old instance?
	//			}
	//			objectLayer.tiles[row][column] = -1;
	//			return -1;
	//		} else {
	//			if (previousId > -1) {
	//				if (objects.get(previousId).classIndex == type) {
	//					return previousId;
	//				}
	//			}
	//			ObjectInstance instance = new ObjectInstance(objects.size(), type);
	//			objects.add(instance);
	//			objectLayer.tiles[row][column] = instance.id;
	//			return instance.id;
	//		}
	//	}

	public int getActorType(int row, int column) {
		if (!inMap(row, column)) return 0;
		int id = actorLayer.tiles[row][column];
		if (id == -1) return -1;
		return id > 0 ? actors.get(id).classIndex : 0;
	}

	public ActorInstance getActorInstance(int row, int column) {
		if (!inMap(row, column)) return null;
		return actors.get(actorLayer.tiles[row][column]);
	}

	/**
	 * Sets the actor at the given row and column to the type given.
	 * If the type is -1, it clears the actor.
	 * If the type is 0, it sets the actor to the hero.
	 * If the type is greater than 0, it creates a new actor instance
	 * with its class' id given by the type
	 * 
	 * Note that if the actor in this position is
	 * already of the class indicated by type, nothing
	 * will happen and the id of the actor will be returned.
	 * 
	 * @param row The row
	 * @param column The column
	 * @param type The type
	 * @return The id of the new instance in the row and column.
	 * 1 is always the hero, and 0 indicates the actor was cleared.
	 */
	public int setActor(int row, int column, int type) {
		if (!inMap(row, column)) return 0;
		if (type == -1) {
			if (actorLayer.tiles[row][column] > 0) {
				//Dispose actor?
				if (actorLayer.tiles[row][column] == actors.size() - 1) {
					actors.remove(actors.size() - 1);
				}
			}
			actorLayer.tiles[row][column] = -1;
			return 0;
		} else if (type == 0 && actors.size() > 1) {
			actorLayer.tiles[row][column] = 1;
			return 1;
		} else {
			int oldId = actorLayer.tiles[row][column];
			//If it's the same class of actor, don't do anything
			if (oldId > 0 && actors.get(oldId).classIndex == type) return oldId;

			ActorInstance actor = new ActorInstance(actors.size(), type);
			actors.add(actor);
			actorLayer.tiles[row][column] = actor.id;
			return actor.id;
		}
	}
	
	public boolean inMap(int row, int column) {
		return !(row < 0 || row >= rows || column < 0 || column >= columns);
	}

	public int getHeroRow() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (getActorType(i,j) == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	public int getHeroCol() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (getActorType(i,j) == 0) {
					return j;
				}
			}
		}
		return -1;
	}
	
	public int width(PlatformGame game) {
		return game.tilesets[tilesetId].tileWidth * columns;
	}
	
	public int height(PlatformGame game) {
		return game.tilesets[tilesetId].tileHeight * rows;
	}
}
