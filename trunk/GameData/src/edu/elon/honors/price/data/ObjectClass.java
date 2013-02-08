package edu.elon.honors.price.data;

public class ObjectClass extends MapClass {
	private static final long serialVersionUID = 1L;
	
	
	public float density = 0.5f;
	public boolean moves = true;
	public boolean rotates = true;
	public float restitution = 0.4f;
	
	@Override
	protected String getDefaultImageName() {
		return "rock.png";
	}
}

