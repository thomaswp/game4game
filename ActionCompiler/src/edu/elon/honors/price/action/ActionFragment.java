package edu.elon.honors.price.action;

import edu.elon.honors.price.data.Event.Parameters;

public abstract class ActionFragment {

	protected Parameters params;
	
	protected abstract void readParams();
	
	public Parameters getParameters() {
		return params;
	}
	
	public void setParameters(Parameters params) {
		this.params = params;
		readParams();
	}
	
	public ActionFragment() {
	}
}
