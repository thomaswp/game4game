package edu.elon.honors.price.data;

public class ObjectClass extends MapClass {
	private static final long serialVersionUID = 1L;
	
	
	public boolean fixedRotation = false;
	public float density = 0.5f;
	@Override
	protected String getDefaultImageName() {
		return "rock.png";
	}
}

