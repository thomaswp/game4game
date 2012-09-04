package edu.elon.honors.price.data.types;

import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.ICopyable;

public abstract class ScopedData extends GameData
implements ICopyable {
	private static final long serialVersionUID = 1L;

	public int id;
	public DataScope scope;

	public ScopedData(int id, DataScope scope) {
		this.id = id; this.scope = scope;
	}
}
