package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Map extends GameData {
	private static final long serialVersionUID = 9L;
	
	public static final int HERO_ID = 1;

	public String name;
	
	public Event[] events;
	public int tilesetId;
	public MapLayer[] layers;
	@Deprecated
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
	
	private int getNextActorId() {
		return actors.size() == 0 ? HERO_ID : 
			(actors.get(actors.size() - 1).id + 1);
	}
	
	private int getNextObjectId() {
		return objects.size() == 0 ? 0 : 
			(objects.get(objects.size() - 1).id + 1);
	}

	public Map(PlatformGame game) {

		name = "New Map";
		
		rows = 14;
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
//		actorLayer = new MapLayer("actors", rows, columns, false, -1);
//		actorLayer.defaultValue = -1;

		actors = new ArrayList<ActorInstance>();
		actors.add(new ActorInstance(HERO_ID, 0));

		objects = new ArrayList<ObjectInstance>();


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
		
		behaviors = new LinkedList<BehaviorInstance>();
	}

	public void resize(int dRow, int dCol, boolean anchorTop, boolean anchorLeft,
			int tileWidth, int tileHeight) {

		LinkedList<MapLayer> allLayers = new LinkedList<MapLayer>();
		for (MapLayer layer : layers) {
			allLayers.add(layer);
		}

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
				int[] tiles = layer.tiles[i];
				if (dCol > 0) {
					int[] newTiles = new int[tiles.length + dCol];
					for (int j = 0; j < newTiles.length; j++) {
						boolean deflt = anchorLeft ? 
								j < dCol : j >= tiles.length;
						int offset = anchorLeft ? -dCol : 0;
						int value = deflt ? layer.defaultValue :
							layer.tiles[i][j + offset];
						newTiles[j] = value;
					}
					layer.tiles[i] = newTiles;
				} else if (dCol < 0) {
					if (anchorLeft) {
						int start = -dCol;
						int end = tiles.length;
						layer.tiles[i] = Arrays.copyOfRange(tiles, start, end);
					} else {
						layer.tiles[i] = Arrays.copyOf(tiles, columns + dCol);	
					}
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
		for (ActorInstance o : actors) {
			if (anchorLeft)
				o.column += dCol;
			if (anchorTop)
				o.row += dRow;
		}

		rows += dRow;
		columns += dCol;
		//TODO: SHIFT EVENTS!!

//		for (int[] a : layers[1].tiles) {
//			Debug.write(Arrays.toString(a));
//		}
	}
	
	public int addObject(int classIndex, int startX, int startY) {
		return addObject(classIndex, startX, startY, getNextObjectId());
	}

	public int addObject(int classIndex, int startX, int startY, int id) {
		objects.add(new ObjectInstance(id, classIndex, startX, startY));
		Collections.sort(objects);
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

//	public int getActorType(int row, int column) {
//		if (!inMap(row, column)) return 0;
//		int id = actorLayer.tiles[row][column];
//		if (id == -1) return -1;
//		return id > 0 ? actors.get(id).classIndex : 0;
//	}

	public ActorInstance getActorInstance(int row, int column) {
		for (int i = 0; i < actors.size(); i++) {
			ActorInstance actor = actors.get(i);
			if (actor.row == row && actor.column == column) {
				return actor;
			}
		}
		return null;
	}
	
	public ActorInstance getActorInstanceById(int id) {
		for (int i = 0; i < actors.size(); i++) {
			ActorInstance actor = actors.get(i);
			if (actor.id == id) {
				return actor;
			}
		}
		return null;
	}
	
	public ObjectInstance getObjectInstanceById(int id) {
		for (int i = 0; i < objects.size(); i++) {
			ObjectInstance object = objects.get(i);
			if (object.id == id) {
				return object;
			}
		}
		return null;
	}

	public int setActor(int row, int column, int type) {
		return setActor(row, column, type, getNextActorId());
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
	public int setActor(int row, int column, int type, int id) {
		if (!inMap(row, column)) return 0;
		if (type == -1) {
			ActorInstance instance = getActorInstance(row, column);
			if (instance != null && instance.id != HERO_ID) {
				actors.remove(instance);
			}
			return 0;
		} else if (type == 0) {
			ActorInstance oldInstance = getActorInstance(row, column);
			if (oldInstance != null) actors.remove(oldInstance);
			ActorInstance hero = actors.get(0);
			hero.row = row;
			hero.column = column;
			return hero.id;
		} else {
			ActorInstance oldInstance = getActorInstance(row, column);
			if (oldInstance != null) {
				if (oldInstance.classIndex == type ||
						oldInstance.id == HERO_ID) {
					return oldInstance.id;
				} else {
					actors.remove(oldInstance);
				}
			}

			ActorInstance actor = new ActorInstance(id, type);
			actor.row = row; actor.column = column;
			actors.add(actor);
			Collections.sort(actors);
			return actor.id;
		}
	}
	
	public boolean inMap(int row, int column) {
		return !(row < 0 || row >= rows || column < 0 || column >= columns);
	}

	public int getHeroRow() {
		return actors.get(0).row;
	}

	public int getHeroCol() {
		return actors.get(0).column;
	}
	
	public int width(PlatformGame game) {
		return game.tilesets[tilesetId].tileWidth * columns;
	}
	
	public int height(PlatformGame game) {
		return game.tilesets[tilesetId].tileHeight * rows;
	}
	
	public Tileset getTileset(PlatformGame game) {
		return game.tilesets[tilesetId];
	}
}
