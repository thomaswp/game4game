package edu.elon.honors.price.data.field;

import edu.elon.honors.price.data.field.FieldData.ParseDataException;

public interface DataObject {

	public abstract class Constructor {
		public abstract DataObject construct();
		
		static DataObject construct(String type) {
			return null;
		}
	}
	
	void addFields(FieldData fields) throws ParseDataException, NumberFormatException;
}
