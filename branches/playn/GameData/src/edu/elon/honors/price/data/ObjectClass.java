package edu.elon.honors.price.data;

public class ObjectClass extends MapClass {
	private static final long serialVersionUID = 1L;
	
	public final static int MAX_FRICTION = 5;
	
	public float density = 0.5f;
	public float friction = 1;
	public boolean moves = true;
	public boolean rotates = true;
	public float restitution = 0.4f;
	public boolean isPlatform;
	
	@Override
	protected String getDefaultImageName() {
		return "rock.png";
	}
}

