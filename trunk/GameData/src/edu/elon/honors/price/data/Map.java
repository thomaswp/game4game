package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable{
	private static final long serialVersionUID = 9L;
	
	public int tilesetId;
	public MapLayer[] layers;
	public MapLayer actorLayer;
	public ArrayList<ActorInstance> actors; 
	public Event[] events;
	public int rows, columns;
	
	public Map() {
		
		rows = 8;
		columns = 30;
		
		tilesetId = 0;
		
		//layer with tiles representing indices in the actors ArrayList 
		actorLayer = new MapLayer("actors", rows, columns, false);
		
		events = new Event[3];
		for (int i = 0; i < events.length; i++) 
			events[i] = new Event(String.format("Event%d", i));
		
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
		return id > 0 ? actors.get(id).classIndex : 0;
	}
	
	public ActorInstance getActorInstance(int row, int column) {
		return actors.get(actorLayer.tiles[row][column]);
	}
	
	/**
	 * Sets the actor at the given row and column to the type give.
	 * If the type is 0, it clears the actor.
	 * If the type is -1, it sets the actor to the hero.
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
		if (type == 0) {
			if (actorLayer.tiles[row][column] != 0) {
				//Dispose actor?
			}
			actorLayer.tiles[row][column] = 0;
			return 0;
		} else if (type == -1 && actors.size() > 1) {
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
}
