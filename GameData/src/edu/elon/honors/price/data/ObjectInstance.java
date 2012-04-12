package edu.elon.honors.price.data;

import java.io.Serializable;

public class ObjectInstance implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int id;
	public int classIndex;
	public int startX, startY;
	
	public ObjectInstance(int id, int classIndex, int startX, int startY) {
		this.id = id;
		this.classIndex = classIndex;
		this.startX = startX;
		this.startY = startY;
	}
}