package edu.elon.honors.price.data.types;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.PlatformGame;

public class ActorClassPointer extends ScopedData<ActorClassPointer> {
	private static final long serialVersionUID = 1L;

	public ActorClassPointer(int id, DataScope scope) {
		super(id, scope);
		
	}
	
	public ActorClassPointer() {
		super(0, DataScope.Global);
	}
	
	public String getName(PlatformGame game, Behavior behavior) {
		if (scope == DataScope.Global) {
			if (game != null) {
				return game.actors[id].name;
			}
		} else if (scope == DataScope.Param){
			if (behavior != null) {
				return "{" + behavior.parameters.get(id).name + "}";
			}
		}
		return "<None>";
	}

	@Override
	public ActorClassPointer copy() {
		return new ActorClassPointer(id, scope);
	}
}
