package edu.elon.honors.price.data.types;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.ParameterType;

public class Variable extends GameData {
	private static final long serialVersionUID = 1L;

	public int id;
	public DataScope scope;
	
	public Variable(int id, DataScope scope) {
		this.id = id; this.scope = scope;
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
			return "[" + behavior.variableNames.get(id) + "]";
		} else if (scope == DataScope.Param){
			return "{" + behavior.getParamters(
					ParameterType.Variable).get(id).name + "}";
		}
		return "";
	}
}
