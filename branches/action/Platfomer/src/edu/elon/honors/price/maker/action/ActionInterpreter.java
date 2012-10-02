package edu.elon.honors.price.maker.action;

public abstract class ActionInterpreter<T extends Action> {
	public abstract void interperate(T action, PlatformGameState gameState) throws ParameterException;
}
