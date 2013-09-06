package edu.elon.honors.price.maker.action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.twp.platform.TriggeringInfo;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.maker.action.ActionInterpreter.WaitChecker;

public class ActionControl {
	private final int MAX_STACK_SIZE = 20;
	
	protected int actionIndex;
	private Event event;
	private WaitChecker waitChecker;
	private TriggeringInfo triggeringInfo;
	
	private int executionDataOffset = 0;
	private ArrayList<Object> executionData = new ArrayList<Object>();
	
	private Stack<Event> eventStack = new Stack<Event>();
	private Stack<Integer> indexStack = new Stack<Integer>();
	
	public List<Action> getActions() {
		return event.actions;
	}
	
	public void setEvent(Event event) {
		actionIndex = 0;
		this.event = event;
		
		eventStack.clear();
		indexStack.clear();
		executionDataOffset = 0;
	}

	/**
	 * Will not be cleared between events!
	 * Only read if it has been set FOR SURE.
	 */
	public Object getExecutionData(Action action) {
		int index = event.actions.indexOf(action) + executionDataOffset;
		while (executionData.size() <= index) executionData.add(null);
		return executionData.get(index);
	}
	
	public void setExecutionData(Action action, Object data) {
		int index = event.actions.indexOf(action) + executionDataOffset;
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
		while (actionIndex >= event.actions.size() && eventStack.size() > 0) {
			popEvent();
		}
		return hasNextActionInEvent();
	}
	
	public boolean hasNextActionInEvent() {
		return actionIndex < event.actions.size();
	}
	
	public boolean isWaiting(PlatformGameState gameState) {
		return waitChecker != null && waitChecker.isWaiting(gameState);
	}
	
	public Action getNextAction() {
		return event.actions.get(actionIndex);
	}
	
	public void popEvent() {
		event = eventStack.pop();
		actionIndex = indexStack.pop();
		executionDataOffset -= event.actions.size();
	}
	
	public void pushEvent(Event event) throws ParameterException {
		if (eventStack.size() == MAX_STACK_SIZE) {
			throw new ParameterException("Stack overflow!!");
		}
		
		executionDataOffset += this.event.actions.size();
		eventStack.push(this.event);
		indexStack.push(actionIndex);
		
		this.event = event;
		actionIndex = 0;
	}

	public void makePersistent() {
		triggeringInfo = triggeringInfo.clone();
	}
}
