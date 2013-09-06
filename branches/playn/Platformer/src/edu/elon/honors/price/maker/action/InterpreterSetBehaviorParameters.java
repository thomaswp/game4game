package edu.elon.honors.price.maker.action;

import com.twp.platform.PlatformBody;

import edu.elon.honors.price.data.Event.Parameters;

public class InterpreterSetBehaviorParameters extends ActionInterpreter<ActionSetBehaviorParameters> {

	@Override
	protected void interperate(ActionSetBehaviorParameters action,
			PlatformGameState gameState) throws ParameterException {
		PlatformBody body;
		Parameters behavior;
		if (action.ofTheActor) {
			body = action.ofTheActorData.readActorInstance(gameState);
			behavior = action.ofTheActorData.behavior;
		} else if (action.ofTheObject) {
			body = action.ofTheObjectData.readObjectInstance(gameState);
			behavior = action.ofTheObjectData.behavior;
		} else {
			throw new UnsupportedException();
		}
		gameState.setBehaviorParameters(body, behavior);
	}

}
