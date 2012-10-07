package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.data.types.Switch;

public class InterpreterSetSwtich extends ActionInterpreter<ActionSetSwitch> {

	@Override
	protected void interperate(ActionSetSwitch action,
			PlatformGameState gameState) throws ParameterException {
		int from, to;
		if (action.setOneSwitch) {
			Switch s = action.setOneSwitchData.aSwitch;
			setOneSwitch(s.id, action, gameState);
		} else {
			from = action.setAllSwitchesFromData.group.from.id;
			to = action.setAllSwitchesFromData.group.to.id;

			for (int i = from; i <= to; i++) {
				setOneSwitch(i, action, gameState);
			}
		}
	}
	
	private void setOneSwitch(int id, ActionSetSwitch action,
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
			argument = !PlatformGameState.readGlobalSwitch(id);
		}
		
		PlatformGameState.setGlobalSwitch(id, argument);
	}

}
