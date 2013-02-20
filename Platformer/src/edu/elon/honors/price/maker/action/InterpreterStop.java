package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.data.Event.Action;

public class InterpreterStop extends ActionInterpreter<ActionStop> {

	@Override
	protected void interperate(ActionStop action, PlatformGameState gameState)
			throws ParameterException {
		
	}

	@Override
	protected void updateControl(ActionControl control, Action action) {
		control.actionIndex = control.getActions().size();
	}

}
