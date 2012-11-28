package edu.elon.honors.price.maker.action;

public class InterpreterTestAction extends ActionInterpreter<ActionTestAction> {

	@Override
	protected void interperate(ActionTestAction action,
			PlatformGameState gameState) throws ParameterException {
		if (action.happyYes) {
			action.happyYesData.readHappyActor(gameState);
		}
	}

}
