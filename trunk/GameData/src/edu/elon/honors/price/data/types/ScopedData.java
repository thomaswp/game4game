package edu.elon.honors.price.data.types;

import edu.elon.honors.price.data.GameData;

public abstract class ScopedData extends GameData {
	private static final long serialVersionUID = 1L;
	
	public int id;
	public DataScope scope;
	
	public ScopedData(int id, DataScope scope) {
		this.id = id; this.scope = scope;
	}
}
