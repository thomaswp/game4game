package edu.elon.honors.price.data;

import java.io.Serializable;

public class ActorInstance implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ActorInstance(int id, int actorType) {
		this.id = id;
		this.actorType = actorType;
	}
	
	public int id;
	public int actorType;
}
