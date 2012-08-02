package edu.elon.honors.price.data;

import java.util.HashMap;
import java.util.LinkedList;

public class BehaviorInstance {
	public Behavior behavior;
	public HashMap<String, Object> parameters = new HashMap<String, Object>();
	public transient LinkedList<Integer> variables = new LinkedList<Integer>();
	public transient LinkedList<Boolean> switches = new LinkedList<Boolean>();
	
	public BehaviorInstance(Behavior behavior) {
		this.behavior = behavior;
	}
}
