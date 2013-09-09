package edu.elon.honors.price.data;

import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;

/**
 * Represents an instance of an actor. This instance can be
 * tied to specific events, but inherits most of its ActorClass.
 *
 */
public class ActorInstance extends GameData implements Comparable<ActorInstance> {
	private static final long serialVersionUID = 1L;

	public int id;
	public int classIndex;
	public int row, column;

	@Override
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		id = fields.add(id);
		classIndex = fields.add(classIndex);
		row = fields.add(row);
		column = fields.add(column);
	}
	
	/**
	 * Constructs the ActorInstance with the given id and
	 * class index.
	 * @param id The unique id for this instance
	 * @param classIndex The index of the ActorClass of this instance.
	 */
	public ActorInstance(int id, int classIndex) {
		this.id = id;
		this.classIndex = classIndex;
	}
	
	public ActorClass getActorClass(PlatformGame game) {
		return game.actors[classIndex];
	}

	@Override
	public int compareTo(ActorInstance another) {
		return id - another.id;
	}
}
