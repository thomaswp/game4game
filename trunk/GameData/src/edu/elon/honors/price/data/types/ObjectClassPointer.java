package edu.elon.honors.price.data.types;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.PlatformGame;

public class ObjectClassPointer extends ScopedData<ObjectClassPointer> {
	private static final long serialVersionUID = 1L;

	public ObjectClassPointer(int id, DataScope scope) {
		super(id, scope);
		
	}
	
	public ObjectClassPointer() {
		super(0, DataScope.Global);
	}
	
	public String getName(PlatformGame game, Behavior behavior) {
		if (scope == DataScope.Global) {
			if (game != null) {
				return game.objects[id].name;
			}
		} else if (scope == DataScope.Param){
			if (behavior != null) {
				return "{" + behavior.parameters.get(id).name + "}";
			}
		}
		return "<None>";
	}

	@Override
	public ObjectClassPointer copy() {
		return new ObjectClassPointer(id, scope);
	}
}
