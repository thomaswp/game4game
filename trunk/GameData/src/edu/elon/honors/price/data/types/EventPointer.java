package edu.elon.honors.price.data.types;

import java.util.List;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.ICopyable;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.BehaviorType;

public class EventPointer extends GameData implements ICopyable<EventPointer>, Cloneable {
	private static final long serialVersionUID = 1L;

	private BehaviorType behaviorType;
	private int behaviorIndex = -1;
	private int eventIndex = -1;
	
	public void setEvent(PlatformGame game, Behavior behavior, int index) {
		if (behavior != null) {
			behaviorType = behavior.type;
			behaviorIndex = game.getBehaviors(behaviorType).indexOf(behavior);
		} else {
			behaviorType = null;
			behaviorIndex = -1;
		}
		eventIndex = index;
	}
	
	public int getEventIndex() {
		return eventIndex;
	}
	
	public Behavior getBehavior(PlatformGame game) {
		if (behaviorType != null) {
			return game.getBehaviors(behaviorType).get(behaviorIndex);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("Event[%d,%s:%d]", eventIndex, behaviorType, behaviorIndex);
	}
	
//	private List<Event> getEvents(PlatformGame game) {
//		if (behaviorType == null) {
//			
//		}
//	}
//	
//	public Event getEvent(PlatformGame game) {
//		List<Event> events;
//		if (behaviorIndex >= 0) {
//			events = 
//		} else {
//			
//		}
//	}
	
	@Override
	public EventPointer copy() {
		try {
			return (EventPointer)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
