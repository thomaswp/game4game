package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.data.types.Switch;

public class InterpreterSetSwitch extends ActionInterpreter<ActionSetSwitch> {

	@Override
	protected void interperate(ActionSetSwitch action,
			PlatformGameState gameState) throws ParameterException {
		if (action.setOneSwitch) {
			Switch s = action.setOneSwitchData.aSwitch;
			boolean value = operate(gameState.readSwitch(s), 
					action, gameState);
			gameState.setSwitch(s, value);
		} else {
			int from = action.setAllSwitchesFromData.from.id;
			int to = action.setAllSwitchesFromData.to.id;

			for (int i = from; i <= to; i++) {
				boolean startValue = PlatformGameState.readGlobalSwitch(i);
				boolean value = operate(startValue, action, gameState);
				PlatformGameState.setGlobalSwitch(i, value);
			}
		}
	}
	
	private boolean operate(boolean startValue, ActionSetSwitch action,
			PlatformGameState gameState) throws ParameterException {
		
		ActionSetSwitch.ActionSetItToData data = 
				action.actionSetItToData;
		boolean argument;
		
		if (action.actionSetItTo) {
			if (data.setToOn) {
				argument = true;
			} else if (data.setToOff) {
				argument = false;
			} else if (data.setToARandomValue) {
				argument = rand.nextBoolean();
			} else {
				argument = data.setToASwitchsValueData.readASwitch(gameState);
			}
		} else {
			argument = !startValue;
		}
		
		return argument;
	}

}
