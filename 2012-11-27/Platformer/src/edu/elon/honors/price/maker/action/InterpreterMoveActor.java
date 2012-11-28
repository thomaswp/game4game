package edu.elon.honors.price.maker.action;

import com.twp.platform.ActorBody;

public class InterpreterMoveActor extends ActionInterpreter<ActionMoveActor> {

	@Override
	protected void interperate(ActionMoveActor action,
			PlatformGameState gameState) throws ParameterException {
		Point point = action.readPoint(gameState);
		int dir = action.facingLeft ? -1 : 1;
		ActorBody body = action.readActorInstance(gameState);
		body.setScaledPosition(point.x, point.y);
		if (!action.facingTheSameDirection) {
			body.setDirectionX(dir);
		}
	}

}
