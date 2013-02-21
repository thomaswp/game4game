package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;

public class InterpreterTriggerEvent extends ActionInterpreter<ActionTriggerEvent> {

	private Event nextEvent;
	
	@Override
	protected void interperate(ActionTriggerEvent action,
			PlatformGameState gameState) throws ParameterException {
		nextEvent = action.readEvent(gameState);
	}

	@Override
	protected void updateControl(ActionControl control, Action action) {
		super.updateControl(control, action);
		try {
			control.pushEvent(nextEvent);
		} catch (ParameterException e) {
			e.printStackTrace();
		}
	}
}
