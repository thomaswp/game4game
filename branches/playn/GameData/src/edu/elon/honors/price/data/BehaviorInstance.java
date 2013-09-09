package edu.elon.honors.price.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;
import edu.elon.honors.price.game.Formatter;

public class BehaviorInstance extends GameData {
	private static final long serialVersionUID = 1L;
	
	public int behaviorId;
	public BehaviorType type;
	public final List<Parameters> parameters = new LinkedList<Parameters>();

	@Override
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		behaviorId = fields.add(behaviorId);
		type = BehaviorType.values()[fields.add(type.ordinal())];
		fields.addList(parameters);
	}
	
	public BehaviorInstance(int behaviorId, BehaviorType type) {
		this.behaviorId = behaviorId;
		this.type = type;
	}
	
	public Behavior getBehavior(PlatformGame game) {
		return game.getBehaviors(type).get(behaviorId);
	}
	
	@Override
	public String toString() {
		return Formatter.format("%d (%s): %s",
				behaviorId, type.toString(),
				Arrays.toString(parameters.toArray()));
	}
}
