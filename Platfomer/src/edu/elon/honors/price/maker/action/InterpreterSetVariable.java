package edu.elon.honors.price.maker.action;

import com.twp.platform.ActorBody;

import edu.elon.honors.price.physics.Vector;

public class InterpreterSetVariable extends ActionInterpreter<ActionSetVariable> {

	@Override
	protected void interperate(ActionSetVariable action,
			PlatformGameState gameState) throws ParameterException {
		if (action.setOneVariable) {
			int operandA = action.setOneVariableData.readVariable(gameState);
			int value = operate(operandA, action, gameState);
			gameState.setVariable(action.setOneVariableData.variable, value);
		} else if (action.setAllVariablesFrom) {
			int from = action.setAllVariablesFromData.from.id;
			int to = action.setAllVariablesFromData.to.id;
			for (int i = from; i <= to; i++) {
				int operandA = PlatformGameState.readGlobalVariable(i);
				int value = operate(operandA, action, gameState);
				PlatformGameState.setGlobalVariable(i, value);
			}
		}
	}
	
	private int operate(int operandA, ActionSetVariable action,
			PlatformGameState gameState) throws ParameterException {
		int operandB = 0;
		
		if (action.withTheValue) {
			operandB = action.withTheValueData.exactNumber;
		} else if (action.withAVariable) {
			operandB = action.withAVariableData.readVariable(gameState);
		} else if (action.withARandomNumber) {
			int min = action.withARandomNumberData.group.exactNumber;
			int max = action.withARandomNumberData.group.exactNumber2;
			operandB = rand.nextInt(max - min + 1) + min;
		} else if (action.withAnActorProperty) {
			ActorBody body = action.withAnActorPropertyData
					.readActorInstance(gameState);
			Vector pos = body.getScaledPosition();
			if (action.withAnActorPropertyData.coordinateX) {
				operandB = (int)pos.getX();
			} else {
				operandB = (int)pos.getY();
			}
		}
		
		int result = 0;
		if (action.operationAdd) {
			result = operandA + operandB;
		} else if (action.operationSubtract) {
			result = operandA - operandB;
		} else if (action.operationMultiply) {
			result = operandA * operandB;
		} else if (action.operationModBy) {
			result = operandA % operandB;
		} else if (action.operationDivideBy) {
			if (operandB == 0) throw new ParameterException("Divide by 0!");
			result = operandA / operandB;
		} else if (action.operationSetItTo) {
			if (operandB == 0) throw new ParameterException("Divide by 0!");
			result = operandB;
		}
		
		return result;
	}

}
