package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.input.UIControl;

public class InterpreterUIAction extends ActionInterpreter<ActionUIAction> {

	@Override
	protected void interperate(ActionUIAction action,
			PlatformGameState gameState) throws ParameterException {
		
		UIControl control = action.readUi(gameState);
		boolean to = action.readTo(gameState);
		
		if (action.setItsVisibility) {
			control.setVisible(to);
		} else if (action.setItsDefaultBehavior) {
			control.setActive(to);
		} else {
			throw new UnsupportedException();
		}
	}

}
