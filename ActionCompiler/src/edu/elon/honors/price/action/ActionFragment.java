package edu.elon.honors.price.action;

import edu.elon.honors.price.data.Event.Parameters;

public class ActionFragment {

	protected Parameters params;
	
	public Parameters getParameters() {
		return params;
	}
	
	public ActionFragment(Parameters params) {
		this.params = params;
	}
}
