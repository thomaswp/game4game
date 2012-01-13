package edu.elon.honors.price.data;

import java.io.Serializable;

public class ObjectInstance implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int id;
	public int classIndex;
	
	public ObjectInstance(int id, int classIndex) {
		this.id = id;
		this.classIndex = classIndex;
	}
}
