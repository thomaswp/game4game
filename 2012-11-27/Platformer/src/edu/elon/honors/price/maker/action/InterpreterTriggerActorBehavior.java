package edu.elon.honors.price.maker.action;

public class InterpreterTriggerActorBehavior 
extends ActionInterpreter<ActionTriggerActorBehavior> {

	@Override
	protected void interperate(ActionTriggerActorBehavior action,
			PlatformGameState gameState) throws ParameterException {
		action.readActorInstance(gameState).doBehavior(
				action.actorBehavior, null);
	}
}
