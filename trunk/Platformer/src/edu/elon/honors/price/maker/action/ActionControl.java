package edu.elon.honors.price.maker.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.twp.platform.TriggeringInfo;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.maker.action.ActionInterpreter.WaitChecker;

public class ActionControl {
	protected int actionIndex;
	private Event event;
	private WaitChecker waitChecker;
	private TriggeringInfo triggeringInfo;
	private ArrayList<Object> executionData = new ArrayList<Object>();
	
	public List<Action> getActions() {
		return event.actions;
	}
	
	public void setEvent(Event event) {
		actionIndex = 0;
		this.event = event;
	}

	/**
	 * Will not be cleared between actions!
	 * Only read if it has been set FOR SURE.
	 */
	public Object getExecutionData(Action action) {
		int index = event.actions.indexOf(action);
		while (executionData.size() <= index) executionData.add(null);
		return executionData.get(index);
	}
	
	public void setExecutionData(Action action, Object data) {
		int index = event.actions.indexOf(action);
		while (executionData.size() <= index) executionData.add(null);
		executionData.set(index, data);
	}
	
	public void setTriggeringInfo(TriggeringInfo info) {
		this.triggeringInfo = info;
	}
	
	public void nextAction(PlatformGameState gameState) 
			throws ParameterException {
		gameState.setTriggeringContext(event, triggeringInfo);
		waitChecker = ActionInterpreter.interperate(
				event.actions.get(actionIndex), gameState, this);
	}
	
	public boolean hasNextAction() {
		return actionIndex < event.actions.size();
	}
	
	public boolean isWaiting(PlatformGameState gameState) {
		return waitChecker != null && waitChecker.isWaiting(gameState);
	}
	
	public Action getNextAction() {
		return event.actions.get(actionIndex);
	}

	public void makePersistent() {
		triggeringInfo = triggeringInfo.clone();
	}
}
