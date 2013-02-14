package com.twp.platform;

import edu.elon.honors.price.input.UIControl;
import edu.elon.honors.price.maker.action.Point;
import edu.elon.honors.price.physics.Vector;

public class TriggeringInfo implements Cloneable {

	public ActorBody triggeringActor;
	public ObjectBody triggeringObject;
	public Vector triggeringVector;
	public UIControl triggeringControl;
	public Point triggeringPoint;
	public IBehaving behaving;
	public int behaviorIndex = -1;
	
	public void clearTriggerInfo() {
		triggeringActor = null;
		triggeringObject = null;
		triggeringVector = null;
		triggeringControl = null;
		triggeringPoint = null;
	}
	
	public void clearBehavingInfo() {
		behaving = null;
		behaviorIndex = -1;
	}
	
	public TriggeringInfo clone() {
		try {
			return (TriggeringInfo)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
