package edu.elon.honors.price.maker.action;

import java.io.Serializable;
import java.util.LinkedList;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Trigger;

public class EventContext implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Scope {
		MapBehavior,
		ActorBehavior,
		ObjectBehavior,
		MapEvent
	}
	
	public enum TriggerType {
		ActorTrigger,
		ObjectTrigger,
		RegionTrigger,
		SwitchTrigger,
		VariableTrigger,
		UITrigger
	}
	
	private Scope scope;
	private LinkedList<TriggerType> triggers = 
		new LinkedList<TriggerType>();
	private Behavior behavior;
	
	public boolean hasBehavior() {
		return behavior != null;
	}
	
	public Behavior getBehavior() {
		return behavior;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public boolean hasTrigger(TriggerType type) {
		return triggers.contains(type);
	}
	
	public EventContext(Event event) {
		this(event, null);
	}
	
	public EventContext(Event event, Behavior behavior) {
		this.behavior = behavior;
		if (behavior == null) {
			scope = Scope.MapEvent;
		} else {
			if (behavior.type == BehaviorType.Map) {
				scope = Scope.MapBehavior;
			} else if (behavior.type == BehaviorType.Actor) {
				scope = Scope.ActorBehavior;
			} else if (behavior.type == BehaviorType.Object) {
				scope = Scope.ObjectBehavior;
			}
		}
		for (Trigger trigger : event.triggers) {
			if (trigger.id == Trigger.ID_ACTOR_OBJECT) {
				//TODO: FIX this
				//if (((Event.ActorOrObjectTrigger) trigger).isActorTrigger()) {
					triggers.add(TriggerType.ActorTrigger);
				//} else {
					triggers.add(TriggerType.ObjectTrigger);
				//}
			} else if (trigger.id == Trigger.ID_REGION) {
				triggers.add(TriggerType.RegionTrigger);
			} else if (trigger.id == Trigger.ID_SWITCH) {
				triggers.add(TriggerType.SwitchTrigger);
			} else if (trigger.id == Trigger.ID_VARIABLE) {
				triggers.add(TriggerType.VariableTrigger);
			} else if (trigger.id == Trigger.ID_UI) {
				triggers.add(TriggerType.UITrigger);
			}
		}
	}
}
