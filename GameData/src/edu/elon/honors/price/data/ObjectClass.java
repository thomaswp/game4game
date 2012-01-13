package edu.elon.honors.price.data;

import java.io.Serializable;

public class ObjectClass implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String name;
	public String imageName;
	public float zoom = 1;
	public boolean fixedRotation = false;
}
