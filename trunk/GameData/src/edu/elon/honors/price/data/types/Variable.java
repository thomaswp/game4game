package edu.elon.honors.price.data.types;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.ParameterType;

public class Variable extends ScopedData {
	private static final long serialVersionUID = 1L;

	public Variable(int id, DataScope scope) {
		super(id, scope);
	}
	
	public Variable() {
		this(0, DataScope.Global);
	}
	
	public String getName(PlatformGame game, Behavior behavior) {
		if (scope == DataScope.Global) {
			if (game != null) {
				return String.format("%03d: %s", id, 
						game.variableNames[id]);
			}
		} else if (scope == DataScope.Local) {
			if (behavior != null) {
				return "[" + behavior.variableNames.get(id) + "]";
			}
		} else if (scope == DataScope.Param){
			if (behavior != null) {
				return "{" + behavior.parameters.get(id).name + "}";
			}
		}
		return "<None>";
	}
	
	@Override 
	public String toString() {
		return String.format("[V[%d,%d]", id, scope.toInt());
	}
}
