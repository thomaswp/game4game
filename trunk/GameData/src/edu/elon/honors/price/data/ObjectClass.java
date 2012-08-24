package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.LinkedList;

public class ObjectClass extends GameData {
	private static final long serialVersionUID = 1L;
	
	public String name = "New Object";
	public String imageName = "rock.png";
	public float zoom = 1;
	public boolean fixedRotation = false;
	public LinkedList<BehaviorInstance> behaviors;
}
