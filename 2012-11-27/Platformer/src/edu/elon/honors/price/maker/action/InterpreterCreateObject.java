package edu.elon.honors.price.maker.action;

import com.twp.platform.PlatformLogic.ObjectAddable;

import edu.elon.honors.price.data.ObjectClass;

public class InterpreterCreateObject extends ActionInterpreter<ActionCreateObject>{

	@Override
	protected void interperate(ActionCreateObject action,
			PlatformGameState gameState) throws ParameterException {
		Point point = action.readPoint(gameState);
		ObjectClass objectClass = action.readObjectClass(gameState);
		gameState.getPhysics().addObjectBody(
				new ObjectAddable(objectClass, point.x, point.y));
	}
}
