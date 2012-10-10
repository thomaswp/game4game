package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.game.Game;

public class InterpreterDebugMessage extends ActionInterpreter<ActionDebugMessage> {

	@Override
	protected void interperate(ActionDebugMessage action,
			PlatformGameState gameState) throws ParameterException {
		String message = "";
		if (action.showTheMessage) {
			message = action.showTheMessageData.string;
		} else if (action.showTheSwitch) {
			String name = gameState.getSwitchName(action.showTheSwitchData.aSwitch);
			message = String.format("%s = %s", name,
					action.showTheSwitchData.readASwitch(gameState) ? "On" : "Off");
		} else if (action.showTheVariable) {
			String name = gameState.getVariableName(action.showTheVariableData.variable);
			message = String.format("%s = %d", name,
					action.showTheVariableData.readVariable(gameState));
		}
		Game.showMessage(message);
	}
}
