package edu.elon.honors.price.maker.action;

import java.util.List;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;

public class ActionControl {
	protected int actionIndex;
	private Event event;
	
	public List<Action> getActions() {
		return event.actions;
	}
	
	public void setEvent(Event event) {
		actionIndex = 0;
		this.event = event;
	}
	
	public void nextAction(PlatformGameState gameState) 
			throws ParameterException {
//		if (!hasNextAction()) {
//			throw new ParameterException("No next action!");
//		}
		gameState.setEvent(event);
		ActionInterpreter.interperate(
				event.actions.get(actionIndex), gameState, this);
	}
	
	public boolean hasNextAction() {
		return actionIndex < event.actions.size();
	}
	
	public Action getNextAction() {
		return event.actions.get(actionIndex);
	}
}
