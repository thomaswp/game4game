package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.game.Game;

public class InterpreterDebugBox extends ActionInterpreter<ActionDebugBox> {

	@Override
	public void interperate(ActionDebugBox action, PlatformGameState gameState) throws ParameterException {
		String message = "";
		if (action.showTheMessage) {
			message = action.showTheMessageData.string;
		} else if (action.showTheSwitch) {
			String name = action.showTheSwitchData.aSwitch.getName(
					gameState.getGame(), gameState.getNullableBehavior());
			message = String.format("%s = %s", name,
					action.showTheSwitchData.readASwitch(gameState) ? "On" : "Off");
		} else if (action.showTheVariable) {
			String name = action.showTheVariableData.variable.getName(
					gameState.getGame(), gameState.getNullableBehavior());
			message = String.format("%s = %d", name,
					action.showTheVariableData.readVariable(gameState));
		}
		Game.getCurrentGame().showToast(message);
	}
}
