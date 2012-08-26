package edu.elon.honors.price.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Behavior.BehaviorType;

public class BehaviorInstance extends GameData {
	private static final long serialVersionUID = 1L;
	
	public int behaviorId;
	public BehaviorType type;
	public List<Object> parameters = new LinkedList<Object>();
	public transient LinkedList<Integer> variables = new LinkedList<Integer>();
	public transient LinkedList<Boolean> switches = new LinkedList<Boolean>();
	
	public BehaviorInstance(int behaviorId, BehaviorType type) {
		this.behaviorId = behaviorId;
		this.type = type;
	}
	
	public Behavior getBehavior(PlatformGame game) {
		return game.getBehaviors(type).get(behaviorId);
	}
	
	@Override
	public String toString() {
		return String.format("%d (%s): %s",
				behaviorId, type.toString(),
				Arrays.toString(parameters.toArray()));
	}
}
