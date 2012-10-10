package com.twp.platform;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.action.ActionControl;
import edu.elon.honors.price.maker.action.ActionFactory;
import edu.elon.honors.price.maker.action.PlatformGameState;
import edu.elon.honors.price.maker.action.ParameterException;;

public class Interpreter {

	
	private ActionControl control = new ActionControl();
	private PlatformGameState gameState;

	public Interpreter(PlatformLogic logic) {
		gameState = new PlatformGameState(logic);
	}

	public void doEvent(Event event) {
		if (event == null)
			return;

		control.setEvent(event);
		Game.debug("Event: %s", event.name);
		
		while (control.hasNextAction()) {
			try {
				Action action = control.getNextAction();
				Game.debug("Action (%s): %s", 
						ActionFactory.ACTION_NAMES[action.id],
						action.params.toString());
				control.nextAction(gameState);
			} catch (ParameterException e) {
				Game.debug("Param Exception!: %s", e.getMessage());
				break;
			}
		}
	}

	public void update() {

	}
}
