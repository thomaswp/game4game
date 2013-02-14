package com.twp.platform;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.graphics.RectF;

import com.badlogic.gdx.math.Vector2;
import com.twp.platform.PhysicsHandler.BodyCallback;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Trigger.Contact;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.maker.action.GameState;
import edu.elon.honors.price.maker.action.ParameterException;
import edu.elon.honors.price.maker.action.PlatformGameState;
import edu.elon.honors.price.maker.action.Point;
import edu.elon.honors.price.maker.action.ScriptableInstance;
import edu.elon.honors.price.maker.action.TriggerActorOrObjectTrigger;
import edu.elon.honors.price.maker.action.TriggerRegionTrigger;
import edu.elon.honors.price.maker.action.TriggerSwitchTrigger;
import edu.elon.honors.price.maker.action.TriggerUserInputTrigger;
import edu.elon.honors.price.maker.action.TriggerVariableTrigger;
import edu.elon.honors.price.physics.Vector;

public class TriggerHandler {

	public static final float SCALE = PhysicsHandler.SCALE;

	private PlatformLogic logic;
	private PlatformGame game;
	private Map map;
	private PhysicsHandler physics;
	private Interpreter interpreter;
	private Vector vector = new Vector();
	private Point point = new Point();

	private int touchPID;
	private RectF regionRect = new RectF();
	
	private TriggeringInfo triggeringInfo = new TriggeringInfo();

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
			triggeringInfo.behaving = behaving;
			triggeringInfo.behaviorIndex = behavingIndex;
			checkEvent(event);
			triggeringInfo.clearBehavingInfo();
		}
	}
	
	private void checkEvent(Event event) {
		for (int j = 0; j < event.triggers.size(); j++) {
			Trigger generic = event.triggers.get(j);

			PlatformGameState gameState = new PlatformGameState(logic);
			gameState.setTriggeringContext(event, triggeringInfo);
			
			try {
				if (generic.id == Trigger.ID_SWITCH) { 
					TriggerSwitchTrigger instance = new TriggerSwitchTrigger(); 
					instance.setParameters(generic.params);
					checkTrigger(instance, event, gameState);
				} else if (generic.id == Trigger.ID_VARIABLE) { 
					TriggerVariableTrigger instance = new TriggerVariableTrigger(); 
					instance.setParameters(generic.params);
					checkTrigger(instance, event, gameState);
				} else if (generic.id == Trigger.ID_ACTOR_OBJECT) {
					TriggerActorOrObjectTrigger instance = new TriggerActorOrObjectTrigger();
					instance.setParameters(generic.params);
					checkTrigger(instance, event, gameState);
				} else if (generic.id == Trigger.ID_REGION) {
					TriggerRegionTrigger instance = new TriggerRegionTrigger(); 
					instance.setParameters(generic.params);
					checkTrigger(instance, event, gameState, generic);
				} else if (generic.id == Trigger.ID_UI) {
					TriggerUserInputTrigger instance = new TriggerUserInputTrigger(); 
					instance.setParameters(generic.params);
					checkTrigger(instance, event, gameState);
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkTrigger(TriggerSwitchTrigger trigger, Event event, GameState gameState)
			throws ParameterException {
		if (trigger.readASwitch(gameState) == trigger.readValue(gameState)) {
			trigger(event);
		}
	}

	private void checkTrigger(TriggerVariableTrigger trigger, Event event,
			GameState gameState) throws ParameterException {
		
		int value1 = trigger.readVariable(gameState);
		int value2 = trigger.readValue(gameState);

		boolean result = false;
		if (trigger.operatorEquals) result = value1 == value2;
		if (trigger.operatorNotEquals) result = value1 != value2;
		if (trigger.operatorGreater) result = value1 > value2;
		if (trigger.operatorLess) result = value1 < value2;
		if (trigger.operatorGreaterOrEqual) result = value1 >= value2;
		if (trigger.operatorLessOrEqual) result = value1 <= value2;
		
		if (result) {
			trigger(event);
		}
	}


	private void checkTrigger(TriggerActorOrObjectTrigger trigger, Event event,
			PlatformGameState gameState) throws ParameterException {
		List<PlatformBody> platformBodies = physics.getPlatformBodies();
		for (int k = 0; k < platformBodies.size(); k++) {
			PlatformBody body = platformBodies.get(k);

			if (!gameState.isBody(trigger.body, body)) continue;
			
			for (int l = 0; l < body.getCollidedBodies().size(); l++) {
				PlatformBody collided = body.getCollidedBodies().get(l);
				if (trigger.collidesWithHero) {
					if (collided instanceof ActorBody && 
							((ActorBody)collided).isHero()) {
						trigger(event, body, collided);
					}
				} else if (trigger.collidesWithActor) {
					if (collided instanceof ActorBody) {
						trigger(event, body, collided);
					}
				} else if (trigger.collidesWithObject) {
					if (collided instanceof ObjectBody) {
						trigger(event, body, collided);
					}
				}
			}
			
				
			if (trigger.collidesWithWall) {
				if (body.isCollidedWall()) {
					trigger(event, body);
				}
			}
		}
		//TODO: Add death
	}

	private void checkTrigger(final TriggerRegionTrigger trigger, Event event,
			PlatformGameState gameState, Trigger eventTrigger) throws ParameterException {
		
		Rect rect = trigger.readRegion(gameState);
		final float left = rect.left / SCALE;
		final float top = rect.top / SCALE;
		final float right = rect.right / SCALE;
		final float bottom = rect.bottom / SCALE;

		if (eventTrigger.gameData == null) {
			eventTrigger.gameData = new ArrayList<Event.Trigger.Contact>();
		}
		@SuppressWarnings("unchecked")
		final ArrayList<Event.Trigger.Contact> contacts = 
			(ArrayList<Event.Trigger.Contact>)eventTrigger.gameData;
		
		for (int k = 0; k < contacts.size(); k++) {
			contacts.get(k).event = -1;
			contacts.get(k).checked = false;
		}
		
		final boolean[] events = new boolean[] { 
			trigger.actionBeginsToEnter,
			trigger.actionFullyEnters,
			trigger.actionBeginsToLeave,
			trigger.actionFullyLeaves
		};
		
		
		final int BEGIN_ENTER = 0,
				FULLY_ENTER = 1,
				BEGIN_LEAVE = 2,
				FULLY_LEAVE = 3;

		physics.regionCallback(new BodyCallback() {
			@Override
			public boolean doCallback(PlatformBody body) {
				int index = -1;
				boolean inRegion = inRegion(body, left, top, right, bottom);
				for (int k = 0; k < contacts.size(); k++) {
					if (contacts.get(k).object == body) index = k;
				}
				if (index < 0) {
					int state = inRegion ? Contact.STATE_CONTAINED :
						Contact.STATE_TOUCHING;
					int event = inRegion ? FULLY_ENTER : BEGIN_ENTER;
					contacts.add(new Contact(body, state, event));
				} else {
					Contact contact = contacts.get(index);
					if (inRegion) {
						int newState = Contact.STATE_CONTAINED;
						if (contact.state != newState) {
							contact.event = FULLY_ENTER;
							contact.state = newState;
						}
					} else {
						int newState = Contact.STATE_TOUCHING;
						if (contact.state != newState) {
							contact.event = 
								contact.state == Contact.STATE_CONTAINED ?
										BEGIN_LEAVE : BEGIN_ENTER;
							contact.state = newState;
						}
					}
					contact.checked = true;
				}
				return true;
			}
		}, left, top, right, bottom);

		for (int k = 0; k < contacts.size(); k++) {
			Contact contact = contacts.get(k);

			int contactEvent = contact.event;

			if (!contact.checked) {
				contactEvent = FULLY_LEAVE;
				contacts.remove(k);
				k--;
			}

			if (contactEvent >= 0 && events[contactEvent]) {
				PlatformBody body = (PlatformBody)contact.object;
				if (gameState.isBody(trigger.body, body)) {
					trigger(event, body);
				}
			}
		}
	}

	private void checkTrigger(TriggerUserInputTrigger trigger, Event event,
			PlatformGameState gameState) throws ParameterException {
		
		boolean triggered = false;
		if (trigger.inputTheButton) {
			Button button = trigger.inputTheButtonData.readButton(gameState);
			if (button.isTapped() && trigger.actionIsPressed) {
				triggered = true;
			} else if (button.isReleased() && trigger.actionIsReleased) {
				triggered = true;
			}
		} else if (trigger.inputTheJoystick) {
			JoyStick joy = trigger.inputTheJoystickData.readJoystick(gameState);
			if (joy.isTapped() && trigger.actionIsPressed) {
				triggered = true;
			} else if (joy.isReleased() && trigger.actionIsReleased) {
				triggered = true;
			} else if (joy.isPressed() && trigger.actionIsDragged) {
				triggered = true;
			}
			
			if (triggered) {
				vector.set(joy.getLastPull());
				triggeringInfo.triggeringVector = vector;
			}
		} else {
			if (Input.isTapped()) {
				int pid = Input.getTappedPointer();
				List<Button> buttons = logic.getButtons();
				List<JoyStick> joysticks = logic.getJoysticks();
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
					if (trigger.actionIsPressed) {
						point.setF(Input.getLastTouchX() - logic.getOffset().getX(), 
								Input.getLastTouchY() - logic.getOffset().getY());
						triggeringInfo.triggeringPoint = point;
						triggered = true;	
					}
				}
			}
			if (touchPID >= 0) {
				if (Input.isTouchDown(touchPID)) {
					if (trigger.actionIsDragged) {
						triggered = true;
					}
				} else {
					if (trigger.actionIsReleased) {
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
		interpreter.doEvent(event, triggeringInfo);
		triggeringInfo.clearTriggerInfo();
	}
	
	private void trigger(Event event, PlatformBody body) {
		if (body instanceof ActorBody) {
			triggeringInfo.triggeringActor = (ActorBody)body;
		} else {
			triggeringInfo.triggeringObject = (ObjectBody)body;
		}
		trigger(event);
	}
	
	private void trigger(Event event, PlatformBody body, PlatformBody collided) {
		Vector2 point = body.getPosition().add(
				collided.getPosition()).mul(0.5f);
		if (collided instanceof ActorBody) {
			triggeringInfo.triggeringActor = (ActorBody)collided;
		} else {
			triggeringInfo.triggeringObject = (ObjectBody)collided;
		}
		this.point.setF(point.x * SCALE, point.y * SCALE);
		triggeringInfo.triggeringPoint = this.point;
		trigger(event, body);
	}

	private boolean inRegion(PlatformBody body, float left, 
			float top, float right, float bottom) {
		Vector offset = logic.getOffset();
		regionRect.set(left, top, right, bottom);
		regionRect.offset(offset.getX(), offset.getY());
		return regionRect.contains(body.getSprite().getRect());
	}
}
