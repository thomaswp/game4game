package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.data.Event.Parameters;

public abstract class ScriptableFragment {

	protected Parameters params;
	
	protected abstract void readParams(Parameters.Iterator iterator);
	
	public Parameters getParameters() {
		return params;
	}
	
	public void setParameters(Parameters params) {
		this.params = params;
		readParams(params.iterator());
	}
	
	public ScriptableFragment() {
	}
}
