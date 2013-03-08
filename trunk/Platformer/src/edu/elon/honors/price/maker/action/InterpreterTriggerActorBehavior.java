package edu.elon.honors.price.maker.action;

import com.twp.platform.ActorBody;
import com.twp.platform.IBehaving;

import edu.elon.honors.price.game.Debug;

public class InterpreterTriggerActorBehavior 
extends ActionInterpreter<ActionTriggerActorBehavior> {

	@Override
	protected void interperate(ActionTriggerActorBehavior action,
			PlatformGameState gameState) throws ParameterException {
		//TODO: Make a better way for "cause" to be passed
		IBehaving behaving = gameState.getBehaving();
		ActorBody cause = null;
		if (behaving instanceof ActorBody) {
			cause = (ActorBody)behaving;
			//Debug.write(cause.getActor().name);
		}
		
		action.readActorInstance(gameState).doBehavior(
				action.actorBehavior, cause);
	}
}
