package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class PlatformMap implements Serializable{
	private static final long serialVersionUID = 9L;
	
	public int tilesetId;
	public PlatformLayer[] layers;
	public PlatformLayer actorLayer;
	public ArrayList<PlatformActorInstance> actors; 
	public int rows, columns;
	
	public PlatformMap() {
		
		rows = 8;
		columns = 30;
		
		tilesetId = 0;
		
		actorLayer = new PlatformLayer("actors", rows, columns, false);
		
		actors = new ArrayList<PlatformActorInstance>();
		actors.add(null);
		setActor(0, 0, -1);
		
		layers = new PlatformLayer[3];
		PlatformLayer layer = new PlatformLayer("background", rows, columns, false);
		layers[0] = layer;
		layer = new PlatformLayer("l1", rows, columns, true);
		layers[1] = layer;
		layer = new PlatformLayer("l2", rows, columns, false);
		layers[2] = layer;
	}
	
	public int getActorType(int row, int column) {
		int id = actorLayer.tiles[row][column];
		return id > 0 ? actors.get(id).actorType : 0;
	}
	
	public PlatformActorInstance getActorInstance(int row, int column) {
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
			
			PlatformActorInstance actor = new PlatformActorInstance(actors.size(), type);
			actors.add(actor);
			actorLayer.tiles[row][column] = actor.id;
			return actor.id;
		}
	}
}
