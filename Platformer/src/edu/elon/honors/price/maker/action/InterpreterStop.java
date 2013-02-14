package edu.elon.honors.price.maker.action;

public class InterpreterStop extends ActionInterpreter<ActionStop> {

	@Override
	protected void interperate(ActionStop action, PlatformGameState gameState)
			throws ParameterException {
		
	}

	@Override
	protected void updateControl(ActionControl control) {
		control.actionIndex = control.getActions().size();
	}

}
