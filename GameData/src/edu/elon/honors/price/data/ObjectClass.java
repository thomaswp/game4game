package edu.elon.honors.price.data;

import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;

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
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		super.addFields(fields);
		density = fields.add(density);
		friction = fields.add(friction);
		moves = fields.add(moves);
		rotates = fields.add(rotates);
		restitution = fields.add(restitution);
		isPlatform = fields.add(isPlatform);
	}
	
	@Override
	protected String getDefaultImageName() {
		return "rock.png";
	}
}

