package edu.elon.honors.price.data;

import java.util.LinkedList;
import java.util.List;


public abstract class MapClass extends GameData {
	private static final long serialVersionUID = 1L;

	public enum CollidesWith {
		Hero, Actors, Objects, Terrain 
	}
	
	protected abstract String getDefaultImageName();

	public float zoom = 1;
	public List<BehaviorInstance> behaviors = 
			new LinkedList<BehaviorInstance>();
	public String imageName = getDefaultImageName();
	public String name = "";
	public boolean[] collidesWith = new boolean[] {
			true, true, true, true	
	};

	public boolean collidesWith(CollidesWith with) {
		return collidesWith[with.ordinal()];
	}

	public void setCollidesWith(CollidesWith with, boolean collides) {
		collidesWith[with.ordinal()] = collides;
	}

}
