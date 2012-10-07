package com.twp.platform;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;

import com.twp.platform.PhysicsHandler.BodyCallback;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.Event.ActorOrObjectTrigger;
import edu.elon.honors.price.data.Event.RegionTrigger;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.Event.UITrigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.maker.action.ParameterException;
import edu.elon.honors.price.physics.Vector;

public class TriggerHandler {

	public static final float SCALE = PhysicsHandler.SCALE;

	private PlatformLogic logic;
	private PlatformGame game;
	private Map map;
	private PhysicsHandler physics;
	private Interpreter interpreter;

	private int touchPID;
	private RectF regionRect = new RectF();

	public TriggerHandler(PlatformLogic logic) {
		this.logic = logic;
		this.game = logic.getGame();
		this.map = game.getSelectedMap();
		this.physics = logic.getPhysics();
		this.interpreter = logic.getInterpreter();
		
		
	}

	public void checkTriggers() {
		for (int i = 0; i < map.events.length; i++) {
			checkEvent(map.events[i]);
		}
		//TODO: figure out how to represent map runtimes
//		for (int i = 0; i < map.behaviors.size(); i++) {
//			checkBehavior(map.behaviors.get(i));
//		}
		for (int i = 0; i < physics.getPlatformBodies().size(); i++) {
			PlatformBody body = physics.getPlatformBodies().get(i);
			checkBehaving(body);
		}
	}
	
	private void checkBehaving(IBehaving behaving) {
		if (behaving != null) {
			for (int j = 0; j < behaving.getBehaviorCount(); j++) {
				checkBehavior(behaving.getBehaviorInstances().get(j), 
						behaving, j);
			}
		}
	}
	
	private void checkBehavior(BehaviorInstance instance, 
			IBehaving behaving, int behavingIndex) {
		Behavior behavior = instance.getBehavior(game);
		for (int i = 0; i < behavior.events.size(); i++) {
			Event event = behavior.events.get(i);
			event.behaving = behaving;
			event.behaviorIndex = behavingIndex;
			checkEvent(behavior.events.get(i));
			event.clearBehavingInfo();
		}
	}
	
	private void checkEvent(Event event) {
		for (int j = 0; j < event.triggers.size(); j++) {
			Trigger generic = event.triggers.get(j);

			try {
				if (generic instanceof SwitchTrigger) {
					checkTrigger((SwitchTrigger)generic, event);
				} else if (generic instanceof VariableTrigger) {
					checkTrigger((VariableTrigger)generic, event);
				} else if (generic instanceof ActorOrObjectTrigger) {
					checkTrigger((ActorOrObjectTrigger)generic, event);
				} else if (generic instanceof RegionTrigger) {
					checkTrigger((RegionTrigger)generic, event);
				} else if (generic instanceof UITrigger) {
					checkTrigger((UITrigger)generic, event);
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkTrigger(SwitchTrigger trigger, Event event)
			throws ParameterException {
		Switch tSwitch = trigger.triggerSwitch;
		if (Interpreter.readSwitch(tSwitch, event) == trigger.value) {
			if (Globals.getSwitches()[tSwitch.id] == trigger.value) {
				trigger(event);
			}			
		}
	}

	private void checkTrigger(VariableTrigger trigger, Event event)
			throws ParameterException {
		Variable variable = trigger.variable;

		int value1 = Interpreter.readVariable(variable, event);
		
		int value2;
		if (trigger.with == VariableTrigger.WITH_VALUE) {
			value2 = trigger.withValue;
		} else {
			variable = trigger.withVariable;
			value2 = Interpreter.readVariable(variable, event);
		}

		boolean result = false;
		switch (trigger.test) {
		case VariableTrigger.TEST_EQUALS: result = value1 == value2; break;
		case VariableTrigger.TEST_NOT_EQUALS: result = value1 != value2; break;
		case VariableTrigger.TEST_GT: result = value1 > value2; break;
		case VariableTrigger.TEST_LT: result = value1 < value2; break;
		case VariableTrigger.TEST_GEQ: result = value1 >= value2; break;
		case VariableTrigger.TEST_LEQ: result = value1 <= value2; break;
		}
		
		if (result) {
			trigger(event);
		}
	}


	private void checkTrigger(ActorOrObjectTrigger trigger, Event event) {
		//TODO: support for "this actor/object"
		List<PlatformBody> platformBodies = physics.getPlatformBodies();
		for (int k = 0; k < platformBodies.size(); k++) {
			PlatformBody body = platformBodies.get(k);

			if (body == null) continue;
			if (trigger.mode == ActorOrObjectTrigger.MODE_ACTOR_INSTANCE) {
				if (!(body instanceof ActorBody)) continue;
				if (body.getId() != trigger.id) continue;
			}
			if (trigger.mode == ActorOrObjectTrigger.MODE_ACTOR_CLASS) {
				if (!(body instanceof ActorBody)) continue;
				if (((ActorBody)body).getActor() != game.actors[trigger.id])
					continue;
			}
			if (trigger.mode == ActorOrObjectTrigger.MODE_OBJECT_INSTANCE) {
				if (!(body instanceof ObjectBody)) continue;
				if (body.getId() != trigger.id) continue;
			}
			if (trigger.mode == ActorOrObjectTrigger.MODE_OBJECT_CLASS) {
				if (!(body instanceof ObjectBody)) continue;
				if (((ObjectBody)body).getObject() != game.objects[trigger.id])
					continue;
			}
			
			if (trigger.action == ActorOrObjectTrigger.ACTION_COLLIDES_HERO) {
				for (int l = 0; l < body.getCollidedBodies().size(); l++) {
					PlatformBody collided = body.getCollidedBodies().get(l);
					if (collided instanceof ActorBody && 
							((ActorBody)collided).isHero()) {
						trigger(event, body);
					}
				}
			} else if (trigger.action == ActorOrObjectTrigger.ACTION_COLLIDES_ACTOR) {
				for (int l = 0; l < body.getCollidedBodies().size(); l++) {
					PlatformBody collided = body.getCollidedBodies().get(l);
					if (collided instanceof ActorBody) {
						trigger(event, body);
					}
				}
			} else if (trigger.action == ActorOrObjectTrigger.ACTION_COLLIDES_WALL) {
				if (body.isCollidedWall()) {
					trigger(event, body);
				}
			}
		}
	}

	private void checkTrigger(final RegionTrigger trigger, Event event) {
		float left = trigger.left / SCALE;
		float top = trigger.top / SCALE;
		float right = trigger.right / SCALE;
		float bottom = trigger.bottom / SCALE;

		if (trigger.contacts == null) {
			trigger.contacts = new ArrayList<Event.RegionTrigger.Contact>();
		}
		for (int k = 0; k < trigger.contacts.size(); k++) {
			trigger.contacts.get(k).event = -1;
			trigger.contacts.get(k).checked = false;
		}

		physics.regionCallback(new BodyCallback() {
			@Override
			public boolean doCallback(PlatformBody body) {
				int index = -1;
				boolean inRegion = inRegion(body, trigger);
				for (int k = 0; k < trigger.contacts.size(); k++) {
					if (trigger.contacts.get(k).object == body) index = k;
				}
				if (index < 0) {
					int state = inRegion ? RegionTrigger.Contact.STATE_CONTAINED :
						RegionTrigger.Contact.STATE_TOUCHING;
					int event = inRegion ? RegionTrigger.MODE_CONTAIN :
						RegionTrigger.MODE_TOUCH;
					trigger.contacts.add(new RegionTrigger.Contact(body, state, event));
				} else {
					RegionTrigger.Contact contact = trigger.contacts.get(index);
					if (inRegion) {
						int newState = RegionTrigger.Contact.STATE_CONTAINED;
						if (contact.state != newState) {
							contact.event = RegionTrigger.MODE_CONTAIN;
							contact.state = newState;
						}
					} else {
						int newState = RegionTrigger.Contact.STATE_TOUCHING;
						if (contact.state != newState) {
							contact.event = 
								contact.state == RegionTrigger.Contact.STATE_CONTAINED ?
										RegionTrigger.MODE_LOSE_CONTAIN :
											RegionTrigger.MODE_TOUCH;
							contact.state = newState;
						}
					}
					contact.checked = true;
				}
				return true;
			}
		}, left, top, right, bottom);

		for (int k = 0; k < trigger.contacts.size(); k++) {
			RegionTrigger.Contact contact = trigger.contacts.get(k);

			int contactEvent = contact.event;

			if (!contact.checked) {
				contactEvent = RegionTrigger.MODE_LOSE_TOUCH;
				trigger.contacts.remove(k);
				k--;
			}

			if (trigger.mode == contactEvent) {
				if (contact.object instanceof ActorBody) {
					ActorBody body = (ActorBody)contact.object;
					if (trigger.who == RegionTrigger.WHO_ACTOR) {
						trigger(event, body);
					} else if (trigger.who == RegionTrigger.WHO_HERO &&
							body.isHero()) {
						trigger(event, body);
					}
				} else {
					if (trigger.who == RegionTrigger.WHO_OBJECT) {
						ObjectBody body = (ObjectBody)contact.object;
						trigger(event, body);
					}
				}
			}
		}
	}

	private void checkTrigger(UITrigger trigger, Event event) {
		List<Button> buttons = logic.getButtons();
		List<JoyStick> joysticks = logic.getJoysticks();
		
		boolean triggered = false;
		if (trigger.controlType == UITrigger.CONTROL_BUTTON) {
			Button button = buttons.get(trigger.index);
			if (button.isTapped() && trigger.condition == 
				UITrigger.CONDITION_PRESS) {
				triggered = true;
			} else if (button.isReleased() && trigger.condition == 
				UITrigger.CONDITION_RELEASE) {
				triggered = true;
			}
		} else if (trigger.controlType == UITrigger.CONTROL_JOY) {
			JoyStick joy = joysticks.get(trigger.index);
			if (joy.isTapped() && trigger.condition == 
				UITrigger.CONDITION_PRESS) {
				triggered = true;
			} else if (joy.isReleased() && trigger.condition == 
				UITrigger.CONDITION_RELEASE) {
				triggered = true;
			} else if (joy.isPressed() && trigger.condition == 
				UITrigger.CONDITION_MOVE) {
				triggered = true;
			}
			
			if (triggered) {
				event.tVector = joy.getPull();
			}
		} else {
			if (Input.isTapped()) {
				int pid = Input.getTappedPointer();
				boolean inUse = false;
				for (int k = 0; k < buttons.size(); k++) {
					if (buttons.get(k).getPID() == pid) {
						inUse = true;
					}
				}
				for (int k = 0; k < joysticks.size(); k++) {
					if (joysticks.get(k).getPID() == pid) {
						inUse = true;
					}
				}
				if (!inUse) {
					touchPID = pid;
					if (trigger.condition == UITrigger.CONDITION_PRESS) {
						triggered = true;	
					}
				}
			}
			if (touchPID >= 0) {
				if (Input.isTouchDown(touchPID)) {
					if (trigger.condition == UITrigger.CONDITION_MOVE) {
						triggered = true;
					}
				} else {
					if (trigger.condition == UITrigger.CONDITION_RELEASE) {
						triggered = true;	
					}
					touchPID = -1;
				}
			}
		}
		
		if (triggered) {
			trigger(event);
		}
	}

	private void trigger(Event event) {
		interpreter.doEvent(event);
		event.clearTriggerInfo();
	}
	
	private void trigger(Event event, PlatformBody body) {
		if (body instanceof ActorBody) {
			event.tActor = body;
		} else {
			event.tObject = body;
		}
		trigger(event);
	}

	private boolean inRegion(PlatformBody body, RegionTrigger trigger) {
		Vector offset = logic.getOffset();
		regionRect.set(trigger.left, trigger.top, trigger.right, trigger.bottom);
		regionRect.offset(offset.getX(), offset.getY());
		return regionRect.contains(body.getSprite().getRect());
	}
}
