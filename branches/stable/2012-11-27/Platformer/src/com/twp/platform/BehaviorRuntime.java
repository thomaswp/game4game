package com.twp.platform;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.PlatformGame;

public class BehaviorRuntime {
	public int[] variables;
	public boolean[] switches;
	public BehaviorInstance instance;
	
	public Object getParameter(int index) {
		return instance.parameters.get(index);
	}
	
	public BehaviorRuntime(BehaviorInstance instance, PlatformGame game) {
		this.instance = instance;
		Behavior behavior = instance.getBehavior(game);
		switches = new boolean[behavior.switches.size()];
		for (int i = 0; i < switches.length; i++) {
			switches[i] = behavior.switches.get(i);
		}
		variables = new int[behavior.variables.size()];
		for (int i = 0; i < variables.length; i++) {
			variables[i] = behavior.variables.get(i);
		}
	}
}
