package edu.elon.honors.price.maker.action;

public class InterpreterDestroyObject extends ActionInterpreter<ActionDestroyObject> {

	@Override
	protected void interperate(ActionDestroyObject action,
			PlatformGameState gameState) throws ParameterException {
		action.readObjectInstance(gameState).dispose();
	}

}
