package edu.elon.honors.price.maker.action;

public class InterpreterTriggerActorAction extends ActionInterpreter<ActionTriggerActorAction> {

	@Override
	protected void interperate(ActionTriggerActorAction action,
			PlatformGameState gameState) throws ParameterException {
		action.readActorInstance(gameState).triggerAction();
	}

}
