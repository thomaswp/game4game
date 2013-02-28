package edu.elon.honors.price.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.data.Event.Parameters;

public class BehaviorInstance extends GameData {
	private static final long serialVersionUID = 1L;
	
	public int behaviorId;
	public BehaviorType type;
	public List<Parameters> parameters = new LinkedList<Parameters>();
	
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
