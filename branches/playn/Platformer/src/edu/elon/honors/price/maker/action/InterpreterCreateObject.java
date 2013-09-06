package edu.elon.honors.price.maker.action;

import com.twp.platform.PlatformLogic.ObjectAddable;

import edu.elon.honors.price.data.ObjectClass;

public class InterpreterCreateObject extends ActionInterpreter<ActionCreateObject>{

	@Override
	protected void interperate(ActionCreateObject action,
			PlatformGameState gameState) throws ParameterException {
		//Read the point where the Object should be created
		Point point = action.readPoint(gameState);
		//Read the type of Object to create
		ObjectClass objectClass = action.readObjectClass(gameState);
		//Tell the physics handler to add a new ObjectBody at
		//the next good opportunity.
		gameState.getPhysics().addObjectBody(
				new ObjectAddable(objectClass, point.x, point.y));
	}
}
