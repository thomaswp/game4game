package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable{
	private static final long serialVersionUID = 9L;
	
	public int tilesetId;
	public MapLayer[] layers;
	public MapLayer actorLayer;
	public ArrayList<ActorInstance> actors; 
	public ArrayList<Event> events;
	public int rows, columns;
	
	public Map() {
		
		rows = 8;
		columns = 30;
		
		tilesetId = 0;
		
		actorLayer = new MapLayer("actors", rows, columns, false);
		
		events = new ArrayList<Event>();
		
		actors = new ArrayList<ActorInstance>();
		actors.add(null);
		setActor(0, 0, -1);
		
		layers = new MapLayer[3];
		MapLayer layer = new MapLayer("background", rows, columns, false);
		layers[0] = layer;
		layer = new MapLayer("l1", rows, columns, true);
		layers[1] = layer;
		layer = new MapLayer("l2", rows, columns, false);
		layers[2] = layer;
	}
	
	public int getActorType(int row, int column) {
		int id = actorLayer.tiles[row][column];
		return id > 0 ? actors.get(id).actorType : 0;
	}
	
	public ActorInstance getActorInstance(int row, int column) {
		return actors.get(actorLayer.tiles[row][column]);
	}
	
	public int setActor(int row, int column, int type) {
		if (type == 0) {
			if (actorLayer.tiles[row][column] != 0) {
				//Delete actor?
			}
			actorLayer.tiles[row][column] = 0;
			return 0;
		} else if (type == -1 && actors.size() > 1) {
			actorLayer.tiles[row][column] = 1;
			return 1;
		} else {
			int oldId = actorLayer.tiles[row][column];
			if (oldId > 0 && actors.get(oldId).actorType == type) return oldId;
			
			ActorInstance actor = new ActorInstance(actors.size(), type);
			actors.add(actor);
			actorLayer.tiles[row][column] = actor.id;
			return actor.id;
		}
	}
}
