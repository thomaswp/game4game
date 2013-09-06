package com.twp.platform;

import java.util.LinkedList;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.action.ActionControl;
import edu.elon.honors.price.maker.action.ActionFactory;
import edu.elon.honors.price.maker.action.PlatformGameState;
import edu.elon.honors.price.maker.action.ParameterException;;

public class Interpreter {

	private LinkedList<ActionControl> pendingActions = new LinkedList<ActionControl>();
	private LinkedList<ActionControl> reusableControls = new LinkedList<ActionControl>();
	private ActionControl control = new ActionControl();
	private PlatformGameState gameState;

	public Interpreter(PlatformLogic logic) {
		gameState = new PlatformGameState(logic);
	}

	public void doEvent(Event event, TriggeringInfo info) {
		if (event == null)
			return;

		control.setEvent(event);
		control.setTriggeringInfo(info);
		Debug.write("Event: %s", event.name);
		
		if (!doEvent(control)) {
			control.makePersistent();
			pendingActions.add(control);
			
			if (reusableControls.size() > 0) {
				control = reusableControls.remove();
			} else {
				control = new ActionControl();
			}
		}
	}
	
	public boolean doEvent(ActionControl control) {
		while (control.hasNextAction()) {
			if (control.isWaiting(gameState)) {
				return false;
			} else {
				try {
					Action action = control.getNextAction();
					Debug.write("Action (%s): %s", 
							ActionFactory.ACTION_NAMES[action.id],
							action.params.toString());
					control.nextAction(gameState);
				} catch (ParameterException e) {
					Debug.write("Param Exception!: %s", e.getMessage());
					break;
				}
			}
		}
		return true;
	}

	public void update() {
		for (int i = 0; i < pendingActions.size(); i++) {
			ActionControl control = pendingActions.get(i);
			if (doEvent(control)) {
				ActionControl c = pendingActions.remove(i);
				reusableControls.add(c);
				i--;
			}
		}
	}
}
