package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.data.types.Variable;

public class InterpreterDebugBox extends ActionInterpreter<ActionDebugBox> {

	@Override
	public void interperate(ActionDebugBox action, PlatformGameState gameState) throws ParameterException {
		String message;
		if (action.showTheMessage) {
			message = action.showTheMessageData.string;
		} else if (action.showTheSwitch) {
			message = action.showTheSwitchData.readASwitch(gameState) ? "On" : "Off";
		} else if (action.showTheVariable) {
			message = "" + action.showTheVariableData.readVariable(gameState);
		}
	}
}
