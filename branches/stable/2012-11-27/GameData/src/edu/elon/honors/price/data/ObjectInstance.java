package edu.elon.honors.price.data;

public class ObjectInstance extends GameData {
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
