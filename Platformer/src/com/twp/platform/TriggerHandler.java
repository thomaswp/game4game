package com.twp.platform;

import java.util.ArrayList;
import java.util.LinkedList;
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
import edu.elon.honors.price.data.Event.Parameters;
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
import edu.elon.honors.price.maker.action.TriggerTimeTrigger;
import edu.elon.honors.price.maker.action.TriggerActorOrObjectTrigger.WhenCollidesWithData;
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
	private ArrayList<PlatformBody> destroyedBodiesFrame =
			new ArrayList<PlatformBody>();
	private ArrayList<PlatformBody> createdBodiesFrame =
			new ArrayList<PlatformBody>();
	private ArrayList<PlatformBody> bodiesFrame = 
			new ArrayList<PlatformBody>();

	private int touchPID;
	
	private TriggeringInfo triggeringInfo = new TriggeringInfo();

	public TriggerHandler(PlatformLogic logic) {
		this.logic = logic;
		this.game = logic.getGame();
		this.map = game.getSelectedMap();
		this.physics = logic.getPhysics();
		this.interpreter = logic.getInterpreter();
	}

	public void checkTriggers() {
		destroyedBodiesFrame.addAll(physics.getDestroyedBodies());
		createdBodiesFrame.addAll(physics.getCreatedBodies());
		
		for (int i = 0; i < map.events.length; i++) {
			checkEvent(map.events[i]);
		}
		//TODO: figure out how to represent map runtimes
//		for (int i = 0; i < map.behaviors.size(); i++) {
//			checkBehavior(map.behaviors.get(i));
//		}
		bodiesFrame.addAll(physics.getPlatformBodies());
		for (int i = 0; i < bodiesFrame.size(); i++) {
			PlatformBody body = bodiesFrame.get(i);
			if (physics.getPlatformBodies().contains(body)) {
				checkBehaving(body);
			}
		}
		bodiesFrame.clear();
		
		physics.getDestroyedBodies().removeAll(destroyedBodiesFrame);
		destroyedBodiesFrame.clear();
		
		physics.getCreatedBodies().removeAll(createdBodiesFrame);
		createdBodiesFrame.clear();
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
	
	protected TriggerSwitchTrigger triggerSwitch = new TriggerSwitchTrigger();
	protected TriggerVariableTrigger triggerVariable = new TriggerVariableTrigger();
	protected TriggerActorOrObjectTrigger triggerActorObject = new TriggerActorOrObjectTrigger();
	protected TriggerRegionTrigger triggerRegion = new TriggerRegionTrigger();
	protected TriggerUserInputTrigger triggerUI = new TriggerUserInputTrigger();
	protected TriggerTimeTrigger triggerTime = new TriggerTimeTrigger();
	
	protected PlatformGameState gameState;
	
	private void checkEvent(Event event) {
		for (int j = 0; j < event.triggers.size(); j++) {
			Trigger generic = event.triggers.get(j);

			if (gameState == null) {
				gameState = new PlatformGameState(logic);
			}
			gameState.setTriggeringContext(event, triggeringInfo);
			
			try {
				if (generic.id == Trigger.ID_SWITCH) { 
					triggerSwitch.setParameters(generic.params);
					checkTrigger(triggerSwitch, event, gameState);
				} else if (generic.id == Trigger.ID_VARIABLE) {  
					triggerVariable.setParameters(generic.params);
					checkTrigger(triggerVariable, event, gameState);
				} else if (generic.id == Trigger.ID_ACTOR_OBJECT) {
					triggerActorObject.setParameters(generic.params);
					checkTrigger(triggerActorObject, event, gameState);
				} else if (generic.id == Trigger.ID_REGION) { 
					triggerRegion.setParameters(generic.params);
					checkTrigger(triggerRegion, event, gameState, generic);
				} else if (generic.id == Trigger.ID_UI) { 
					triggerUI.setParameters(generic.params);
					checkTrigger(triggerUI, event, gameState);
				} else if (generic.id == Trigger.ID_TIME) {
					triggerTime.setParameters(generic.params);
					checkTrigger(triggerTime, event, gameState);
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkTrigger(TriggerTimeTrigger trigger, Event event,
			PlatformGameState gameState) {
		int time = trigger.exactNumber;
		if (trigger.inTenthsOfASecond) {
			time *= 100;
		} else if (trigger.inSeconds) {
			time *= 1000;
		} else if (trigger.inMinutes) {
			time *= 60000;
		}
		long timeNow = logic.getGameTime();
		long timeBefore = logic.getLastGameTime();
		if (trigger.triggerAfter) {
			if (timeNow >= time && timeBefore < time) {
				trigger(event);
			}
		} else if (trigger.triggerEvery) {
			if (timeNow / time > timeBefore / time) {
				trigger(event);
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
				if (trigger.whenCollidesWith) {
					WhenCollidesWithData data = trigger.whenCollidesWithData;
					if (data.collidesWithHero) {
						if (collided instanceof ActorBody && 
								((ActorBody)collided).isHero()) {
							trigger(event, body, collided);
						}
					} else if (data.collidesWithActor) {
						if (collided instanceof ActorBody) {
							trigger(event, body, collided);
						}
					} else if (data.collidesWithObject) {
						if (collided instanceof ObjectBody) {
							trigger(event, body, collided);
						}
					}
				}
			}
			
			if (trigger.whenCollidesWith) {
				WhenCollidesWithData data = trigger.whenCollidesWithData;
				if (data.collidesWithWall) {
					if (body.isCollidedWall()) {
						trigger(event, body);
					}
				} else if (data.collidesWithLedge) {
					if (body instanceof ActorBody &&
							((ActorBody)body).isCollidedEdge()) {
						trigger(event, body);
					}
				} 
			}
		}
		if (trigger.whenIsDestroyed) {
			for (int i = 0; i < destroyedBodiesFrame.size(); i++) {
				PlatformBody body = destroyedBodiesFrame.get(i);
				if (gameState.isBody(trigger.body, body)) {
					trigger(event, body);
				}
			}
		} else if (trigger.whenIsCreated) {
			for (int i = 0; i < createdBodiesFrame.size(); i++) {
				PlatformBody body = createdBodiesFrame.get(i);
				if (gameState.isBody(trigger.body, body)) {
					trigger(event, body);
				}
			}
		}
	}

	private boolean[] triggerEvents = new boolean[4];
	private RegionCallback regionCallback = new RegionCallback();
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
		
		final boolean[] events = triggerEvents; 
		
		events[0] =	trigger.actionBeginsToEnter;
		events[1] =	trigger.actionFullyEnters;
		events[2] =	trigger.actionBeginsToLeave;
		events[3] =	trigger.actionFullyLeaves;
		
		regionCallback.set(logic, contacts, left,  top, right, bottom);
		physics.regionCallback(regionCallback, left, top, right, bottom);

		for (int k = 0; k < contacts.size(); k++) {
			Contact contact = contacts.get(k);

			int contactEvent = contact.event;

			if (!contact.checked) {
				contactEvent = RegionCallback.FULLY_LEAVE;
				contacts.remove(k);
				contact.dispose();
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
	
	private static class RegionCallback extends BodyCallback {

		private RectF regionRect = new RectF();
		public final static int BEGIN_ENTER = 0,
				FULLY_ENTER = 1,
				BEGIN_LEAVE = 2,
				FULLY_LEAVE = 3;
		
		public PlatformLogic logic;
		public float left, top, right, bottom;
		public List<Contact> contacts;
		
		public void set(PlatformLogic logic, List<Contact> contacts, 
				float left, float top, float right, float bottom) {
			this.logic = logic;
			this.contacts = contacts;
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
		
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
				contacts.add(Contact.create(body, state, event));
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

		private boolean inRegion(PlatformBody body, float left, 
				float top, float right, float bottom) {
			Vector offset = logic.getOffset();
			regionRect.set(left, top, right, bottom);
			regionRect.offset(offset.getX(), offset.getY());
			return regionRect.contains(body.getSprite().getRect());
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
		
		PlatformBody primaryTrigger = body;
		//Debug.write("body: %s, collided: %s", body.getMapClass().name, collided.getMapClass().name);
//		//TODO: Fix this dumb workaround
		if (body == triggeringInfo.behaving && body.getClass() == collided.getClass()) {
			primaryTrigger = collided;
		}
		
		this.point.setF(point.x * SCALE, point.y * SCALE);
		triggeringInfo.triggeringPoint = this.point;
		trigger(event, primaryTrigger);
	}
}
