package edu.elon.honors.price.maker.action;

import com.twp.platform.PlatformLogic.ActorAddable;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Hero;


public class InterpreterCreateActor extends ActionInterpreter<ActionCreateActor> {

	@Override
	protected void interperate(ActionCreateActor action,
			PlatformGameState gameState) throws ParameterException {
		Point loc = action.readPoint(gameState);
		int dir = action.facingLeft ? -1 : 1;
		ActorClass actorClass = action.readActorClass(gameState);
		if (actorClass instanceof Hero) {
			throw new ParameterException("Cannot create a hero!");
		} else {
			gameState.getPhysics().addActorBody(new ActorAddable(
					actorClass, loc.x, loc.y, dir));
		}
	}
	

}
