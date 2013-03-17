package edu.elon.honors.price.maker.action;

import com.twp.platform.PlatformBody;

public class InterpreterChangeColor extends ActionInterpreter<ActionChangeColor> {

	@Override
	protected void interperate(ActionChangeColor action,
			PlatformGameState gameState) throws ParameterException {
		PlatformBody body;
		if (action.colorTheActor) {
			body = action.colorTheActorData.readActorInstance(gameState);
		} else if (action.colorTheObject) {
			body = action.colorTheObjectData.readObjectInstance(gameState);
		} else {
			throw new UnsupportedException();
		}
		
		body.getSprite().setBaseColor(action.newColor);
	}
}
