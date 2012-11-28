package edu.elon.honors.price.maker.action;

import android.graphics.Rect;
import android.graphics.RectF;

import com.twp.platform.ActorBody;

import edu.elon.honors.price.maker.action.ActionIf.CheckIfTheActorData;
import edu.elon.honors.price.maker.action.ActionIf.CheckIfTheSwitchData;
import edu.elon.honors.price.maker.action.ActionIf.CheckIfTheVariableData;
import edu.elon.honors.price.physics.Vector;

public class InterpreterIf extends ActionInterpreter<ActionIf> {

	boolean result;
	RectF regionF = new RectF();
	
	@Override
	protected void interperate(ActionIf action, PlatformGameState gameState)
			throws ParameterException {
		
		result = false;
		if (action.checkIfTheSwitch) {
			result = checkIfTheSwitch(action.checkIfTheSwitchData, gameState);
		} else if (action.checkIfTheVariable) {
			result = checkIfTheVariable(action.checkIfTheVariableData, gameState);
		} else if (action.checkIfTheActor) {
			result = checkIfTheActor(action.checkIfTheActorData, gameState);
		} else {
			throw new UnsupportedException();
		}
	}
	
	private boolean checkIfTheSwitch(CheckIfTheSwitchData data,
			PlatformGameState gameState) throws ParameterException {
		boolean op1 = data.readASwitch(gameState);
		boolean op2 = false;
		if (data.withOn) {
			op2 = true;
		} else if (data.withOff) {
			op2 = false;
		} else if (data.withTheSwitch) {
			op2 = data.withTheSwitchData.readASwitch(gameState);
		} else {
			throw new UnsupportedException();
		}
		if (data.operatorDoesNotEqual) op2 = !op2;
		return op1 == op2;
	}
	
	private boolean checkIfTheVariable(CheckIfTheVariableData data,
			PlatformGameState gameState) throws ParameterException {
		int op1 = data.readVariable(gameState);
		int op2 = data.readNumber(gameState);
		if (data.operatorEquals) {
			return op1 == op2;
		} else if (data.operatorNotEquals) {
			return op1 != op2;
		} else if (data.operatorGreater) {
			return op1 > op2;
		} else if (data.operatorGreaterOrEqual) {
			return op1 >= op2;
		} else if (data.operatorLess) {
			return op1 < op2;
		} else if (data.operatorGreater) {
			return op1 <= op2;
		} else {
			throw new UnsupportedException();
		}
	}
	
	private boolean checkIfTheActor(CheckIfTheActorData data,
			PlatformGameState gameState) throws ParameterException {
		
		ActorBody body = null;
		try {
			body = data.readActorInstance(gameState);
		} catch (ParameterException e) { }
		
		if (data.checkRegion) {
			if (body == null) return false;
			
			//data.checkRegionData.region
			Rect region = data.checkRegionData.readRegion(gameState);
			regionF.set(region); 
			Vector offset = gameState.getLogic().getOffset();
			regionF.offset(offset.getX(), offset.getY());
			RectF bodyRect = body.getSprite().getRect();
			
			if (data.checkRegionData.checkIsInside) {
				return regionF.contains(bodyRect);
			} else if (data.checkRegionData.checkIsOutside) {
				return !RectF.intersects(regionF, bodyRect);
			} else if (data.checkRegionData.checkIsTouching) {
				return RectF.intersects(regionF, bodyRect);
			} else {
				throw new UnsupportedException();
			}
			
		} else if (data.checkProperty) {
			if (body == null) {
				return data.checkPropertyData.propertyIsDead;
			}
			return data.checkPropertyData.propertyIsAlive;
		} else {
			throw new UnsupportedException();
		}
	}

	@Override
	protected void updateControl(ActionControl control) {
		int indent = control.getNextAction().indent;
		super.updateControl(control);
		if (!result) {
			while (control.hasNextAction() && 
					control.getNextAction().indent > indent) {
				control.actionIndex++;
			}
		}
	}
}
