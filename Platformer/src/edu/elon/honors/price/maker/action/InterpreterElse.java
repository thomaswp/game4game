package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.data.Event.Action;

public class InterpreterElse extends ActionInterpreter<ActionElse> {

	@Override
	protected void interperate(ActionElse action, PlatformGameState gameState)
			throws ParameterException {
	}

	@Override
	protected void updateControl(ActionControl control, Action action) {
		super.updateControl(control, action);
		
		int indent = action.indent;
		boolean result = (Boolean)control.getExecutionData(action.dependsOn);
		if (result) {
			while (control.hasNextAction() && 
					control.getNextAction().indent > indent) {
				control.actionIndex++;
			}
		}
	}
}
