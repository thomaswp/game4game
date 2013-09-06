package edu.elon.honors.price.maker.action;

public class InterpreterTriggerActorAnimation extends ActionInterpreter<ActionTriggerActorAnimation> {

	@Override
	protected void interperate(ActionTriggerActorAnimation action,
			PlatformGameState gameState) throws ParameterException {
		action.readActorInstance(gameState).triggerAction();
	}

}
