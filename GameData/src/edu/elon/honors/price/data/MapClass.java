package edu.elon.honors.price.data;

import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;
import edu.elon.honors.price.game.Color;

public abstract class MapClass extends GameData {
	private static final long serialVersionUID = 1L;

	public enum CollidesWith {
		Hero, Actors, Objects, Terrain 
	}
	
	protected abstract String getDefaultImageName();

	public float zoom = 1;
	public int color = Color.WHITE;
	public String imageName = getDefaultImageName();
	public String name = "";
	public List<BehaviorInstance> behaviors = 
			new LinkedList<BehaviorInstance>();
	public final boolean[] collidesWith = new boolean[] {
			true, true, true, true	
	};
	
	@Override
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		zoom = fields.add(zoom);
		color = fields.add(color);
		imageName = fields.add(imageName);
		name = fields.add(name);
		behaviors = fields.addList(behaviors);
		fields.addArray(collidesWith);
	}

	public boolean collidesWith(CollidesWith with) {
		return collidesWith[with.ordinal()];
	}

	public void setCollidesWith(CollidesWith with, boolean collides) {
		collidesWith[with.ordinal()] = collides;
	}

}
