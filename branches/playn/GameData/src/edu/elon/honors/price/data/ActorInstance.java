package edu.elon.honors.price.data;

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
