package edu.elon.honors.price.data.types;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.ParameterType;

public class Switch extends GameData {
	private static final long serialVersionUID = 1L;

	public int id;
	public DataScope scope;
	
	public Switch(int id, DataScope scope) {
		this.id = id; this.scope = scope;
	}
	
	public Switch() {
		this(0, DataScope.Global);
	}
	
	public String getName(PlatformGame game, Behavior behavior) {
		if (scope == DataScope.Global) {
			if (game != null) {
				return String.format("%03d: %s", id, 
						game.switchNames[id]);
			}
		} else if (scope == DataScope.Local) {
			return "[" + behavior.switchNames.get(id) + "]";
		} else if (scope == DataScope.Param){
			return "{" + behavior.getParamters(
					ParameterType.Switch).get(id).name + "}";
		}
		return "";
	}
}
