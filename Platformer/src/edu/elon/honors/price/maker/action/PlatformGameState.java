package edu.elon.honors.price.maker.action;

import java.util.List;

import android.graphics.Rect;

import com.eujeux.data.GameList;
import com.twp.platform.ActorBody;
import com.twp.platform.BehaviorRuntime;
import com.twp.platform.Globals;
import com.twp.platform.IBehaving;
import com.twp.platform.ObjectBody;
import com.twp.platform.PhysicsHandler;
import com.twp.platform.PlatformBody;
import com.twp.platform.PlatformLogic;
import com.twp.platform.TriggeringInfo;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.MapClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.types.ActorClassPointer;
import edu.elon.honors.price.data.types.DataScope;
import edu.elon.honors.price.data.types.EventPointer;
import edu.elon.honors.price.data.types.ObjectClassPointer;
import edu.elon.honors.price.data.types.ScopedData;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.input.UIControl;
import edu.elon.honors.price.physics.Vector;

public class PlatformGameState implements GameState {

	private PlatformLogic logic;
	private PhysicsHandler physics;
	private PlatformGame game;
	private Event event;
	private TriggeringInfo triggeringInfo;

	private Point point = new Point();
	private Vector vector = new Vector();
	private Rect rect = new Rect();
	
	public Event getEvent() {
		return event;
	}

	public void setTriggeringContext(Event event, TriggeringInfo info) {
		this.event = event;
		this.triggeringInfo = info;
	}

	public PlatformLogic getLogic() {
		return logic;
	}

	public PhysicsHandler getPhysics() {
		return physics;
	}

	public PlatformGame getGame() {
		return game;
	}
	
	public long getGameTime() {
		return logic.getGameTime();
	}
	
	public BehaviorInstance getBehaviorInstance() throws ParameterException {
		return getBehavingInstance(triggeringInfo);
	}
	
	public IBehaving getBehaving() throws ParameterException {
		return getBehaving(triggeringInfo);
	}
	
	public Behavior getBehavior() throws ParameterException {
		BehaviorInstance instance = getBehaviorInstance();
		Behavior behavior = instance.getBehavior(game); 
		return behavior;
	}
	
	public Behavior getNullableBehavior() throws ParameterException {
		try {
			BehaviorInstance instance = getBehaviorInstance();
			Behavior behavior = instance.getBehavior(game);
			return behavior;
		} catch (Exception e) {
			return null;
		}
	}
	
	public PlatformGameState(PlatformLogic logic) {
		this.logic = logic;
		this.physics = logic.getPhysics();
		this.game = logic.getGame();
	}

	@Override
	public JoyStick readJoystick(int index) throws ParameterException {
		JoyStick joy = logic.getJoystick(index);
		if (joy == null) throw new ParameterException(
				"No joystick found with id " + index);
		return joy;
	}

	@Override
	public Button readButton(int index) throws ParameterException {
		Button button = logic.getButton(index);
		if (button == null) throw new ParameterException(
				"No button found with id " + index);
		return button;
	}
	
	@Override
	public UIControl readUi(Parameters params) throws ParameterException {
		int type = params.getInt(0);
		if (type == 0) {
			return logic.getButton(params.getInt(1));
		} else if (type == 1) {
			return logic.getJoystick(params.getInt(1));
		} else {
			assertThat(triggeringInfo.triggeringControl != null, "No triggering control");
			return triggeringInfo.triggeringControl;
		}
	}
	
	@Override
	public Point readPoint(Parameters params) throws ParameterException {
		int choice = params.getInt();
		if (choice == 0) {
			point.set(params.getInt(1), params.getInt(2));
		} else if (choice == 1) {
			Parameters ps = params.getParameters(1);
			point = readVariablePoint(ps);
		} else {
			assertThat(triggeringInfo.triggeringPoint!= null, "No triggering point");
			point.set(triggeringInfo.triggeringPoint);
		}
		return point;
	}
	
	public Point readVariablePoint(Parameters ps) throws ParameterException {
		point.set(readVariable(ps.getVariable(0), event, triggeringInfo), 
				readVariable(ps.getVariable(1), event, triggeringInfo));
		return point;
	}
	
	@Override
	public Vector readVector(Parameters ps) throws ParameterException {
		int type = ps.getInt();
		if (type == 0) {
			vector.set(ps.getFloat(1), ps.getFloat(2));
		} else if (type == 1 || type == 2){ 
			if (type == 1) {
				if (triggeringInfo.triggeringVector == null) {
					throw new ParameterException("No triggering vector");
				}
				vector.set(triggeringInfo.triggeringVector);
			} else {
				JoyStick joy = readJoystick(ps.getInt(1));
				vector.set(joy.getLastPull());
			}
			if (vector.magnitude() == 0) {
				vector.set(0, 0);
			} else {
				vector.multiply(1f / vector.magnitude());
			}
			Debug.write(vector);
		}
		return vector;
	}

	@Override
	public ActorClass readActorClass(ActorClassPointer pointer) throws ParameterException {
		return readMapClass(pointer, game.actors);
	}

	@Override
	public ObjectClass readObjectClass(ObjectClassPointer pointer) throws ParameterException {
		return readMapClass(pointer, game.objects);
	}
	
	private <T extends MapClass> T readMapClass(ScopedData<?> pointer, T[] list) throws ParameterException {
		if (pointer.scope == DataScope.Global) {
			int i = pointer.id;
			assertThat(inArray(i, list), "MapClass index out of bounds: " + i);
			return list[i];
		} else if (pointer.scope == DataScope.Param) {
			BehaviorInstance instance = getBehaviorInstance();
			assertThat(inArray(pointer.id, instance.parameters), 
					"Parameter out of bounds");
			Object o = instance.parameters.get(pointer.id);
			assertThat(o instanceof Parameters, "Param not a param!");
			Object mapClass = ((Parameters)o).getObject();
			return readMapClass((ScopedData<?>) mapClass, list);
		} else {
			throw new ParameterException("Wrong scope for ActorClass");
		}
	}

	@Override
	public ActorBody readActorInstance(Parameters ps) throws ParameterException {
		int mode = ps.getInt();
		if (mode == 0) {
			int id = ps.getInt(1);
			ActorBody body = physics.getActorBodyFromId(id);
			if (body == null) {
				throw new ParameterException("Actor Instance not found: " + id);
			}
			return body;
		} else if (mode == 1) {
			if (triggeringInfo.triggeringActor == null) {
				throw new ParameterException("No triggering actor");
			}
			return triggeringInfo.triggeringActor;
		} else if (mode == 2) {
			ActorBody actor = physics.getLastCreatedActor();
			assertThat(actor != null, "No last created actor");
			return actor;
		} else {
			assertThat(triggeringInfo.behaving != null && 
					triggeringInfo.behaving instanceof ActorBody,
					"Behaving object is not an Actor");
			return (ActorBody)triggeringInfo.behaving;
		}
	}
	
	@Override
	public Rect readRegion(Parameters params) {
		rect.set(params.getInt(0),
				params.getInt(1),
				params.getInt(2),
				params.getInt(3));
		return rect;
	}

	@Override
	public ObjectBody readObjectInstance(Parameters ps) throws ParameterException {
		int mode = ps.getInt();
		if (mode == 0) {
			int id = ps.getInt(1);
			ObjectBody body = physics.getObjectBodyFromId(id);
			if (body == null) {
				throw new ParameterException("Object Instance not found: " + id);
			}
			return body;
		} else if (mode == 1) {
			if (triggeringInfo.triggeringObject == null) {
				throw new ParameterException("No triggering object");
			}
			return triggeringInfo.triggeringObject;
		} else if (mode == 2) {
			ObjectBody last = physics.getLastCreatedObject();
			if (last == null || last.isDisposed()) {
				throw new ParameterException("No last created object");
			}
			return last;
		} else {
			assertThat(triggeringInfo.behaving != null && 
					triggeringInfo.behaving instanceof ObjectBody,
					"Behaving object is not an Object");
			return (ObjectBody)triggeringInfo.behaving;
		}
	}

	@Override
	public boolean readSwitch(Switch params) throws ParameterException {
		return readSwitch(params, event, triggeringInfo);
	}
	
	public void setSwitch(Switch s, boolean value) throws ParameterException {
		setSwitch(s, event, triggeringInfo, value);
	}
	
	public String getSwitchName(Switch s) throws ParameterException {
		return s.getName(game, getNullableBehavior());
	}

	@Override
	public int readVariable(Variable params) throws ParameterException {
		return readVariable(params, event, triggeringInfo);
	}

	public void setVariable(Variable variable, int value) throws ParameterException {
		setVariable(variable, event, triggeringInfo, value);
	}
	
	public String getVariableName(Variable v) throws ParameterException {
		return v.getName(game, getNullableBehavior());
	}
	
	@Override
	public boolean readBoolean(Parameters ps) throws ParameterException {
		return readBoolean(ps, event, triggeringInfo);
	}

	@Override
	public int readNumber(Parameters ps) throws ParameterException {
		return readNumber(ps, event, triggeringInfo);
	}
	
	public boolean isBody(Parameters params, PlatformBody body) throws ParameterException {
		if (body == null) return false;
		
		int mode = params.getInt(0);
		if (mode == 0) {
			return body instanceof ActorBody && body.getId() == params.getInt(1);
		} else if (mode == 1) {
			return body.getMapClass() == readActorClass(params.getActorClassPointer(1));
		} else if (mode == 2) {
			return body instanceof ObjectBody && body.getId() == params.getInt(1);
		} else if (mode == 3) {
			return body.getMapClass() == readObjectClass(params.getObjectClassPointer(1));
		} else if (mode == 4) {
			return body == triggeringInfo.behaving;
		} else if (mode == 5) {
			return body == triggeringInfo.behaving;
		}
		
		return false;
	}

	@Override
	public Event readEvent(Object params) throws ParameterException {
		assertThat(params instanceof EventPointer, "Not an EventPointer!");
		EventPointer pointer = (EventPointer) params;
		Behavior behavior = pointer.getBehavior(game);
		int index = pointer.getEventIndex();
		if (behavior != null) {
			assertThat(behavior == getBehavior(), 
					"Cannot trigger another behavior's event");
			List<Event> events = behavior.events;
			assertThat(inArray(index, events), "No event at that index");
			return events.get(index);
		} else {
			Event[] events = game.getSelectedMap().events;
			assertThat(inArray(index, events), "No event at that index");
			return events[index];
		}
	}
	
	/* -- Static Members -- */

	private static void assertThat(boolean condition, String message)
			throws ParameterException {
		if (!condition) {
			throw new ParameterException(message);
		}
	}

	//Implement without Object... to avoid unnecessary array allocation
	private static void assertThat(boolean condition, String message, 
			Object arg0) throws ParameterException {
		if (!condition) {
			throw new ParameterException(String.format(message, arg0));
		}
	}
	
	@SuppressWarnings("unused")
	private static void assertThat(boolean condition, String message, 
			Object arg0, Object arg1) throws ParameterException {
		if (!condition) {
			throw new ParameterException(String.format(message, arg0, arg1));
		}
	}

	@SuppressWarnings("unused")
	private static void assertThat(boolean condition, String message, 
			Object arg0, Object arg1, Object arg2) throws ParameterException {
		if (!condition) {
			throw new ParameterException(String.format(message, arg0, arg1, arg2));
		}
	}

	private static boolean inArray(int index, int length) {
		return index >= 0 && index < length;
	}

	private static boolean inArray(int index, List<?> list) {
		return index >= 0 && index < list.size();
	}

	private static <T> boolean inArray(int index, T[] array) {
		return inArray(index, array.length);
	}

	public static IBehaving getBehaving(TriggeringInfo triggeringInfo) throws ParameterException {
		assertThat(triggeringInfo.behaving != null && 
				triggeringInfo.behaving instanceof IBehaving,
				"No behaving object!");
		return (IBehaving)triggeringInfo.behaving;
	}

	public static BehaviorInstance getBehavingInstance(TriggeringInfo triggeringInfo) 
			throws ParameterException {
		List<BehaviorInstance> instances = 
				getBehaving(triggeringInfo).getBehaviorInstances();
		assertThat(inArray(triggeringInfo.behaviorIndex, instances),
				"Behavior out of range!");
		return instances.get(triggeringInfo.behaviorIndex);		
	}

	public static BehaviorRuntime getBehavingRuntime(TriggeringInfo triggeringInfo) 
			throws ParameterException {
		BehaviorRuntime[] runtimes = 
				getBehaving(triggeringInfo).getBehaviorRuntimes();
		assertThat(inArray(triggeringInfo.behaviorIndex, runtimes),
				"Behavior out of range!");
		return runtimes[triggeringInfo.behaviorIndex];		
	}

	public static boolean readSwitch(Switch s, Event event, TriggeringInfo triggeringInfo) 
			throws ParameterException {
		if (s.scope == DataScope.Global) {
			return readGlobalSwitch(s.id);
		} else if (s.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(triggeringInfo);
			assertThat(inArray(s.id, runtime.switches.length),
					"Switch index out of bounds: %d", s.id);
			return runtime.switches[s.id];
		} else {
			BehaviorInstance instance = getBehavingInstance(triggeringInfo);
			assertThat(inArray(s.id, instance.parameters),
					"Switch index is out of bounds: %d", s.id);
			Object o = instance.parameters.get(s.id);
			assertThat(o instanceof Parameters, "Invalid parameter");
			return readBoolean((Parameters)o, 0, event, triggeringInfo);
		}
	}
	
	public static boolean readGlobalSwitch(int id) throws ParameterException {
		assertThat(inArray(id, Globals.getSwitches().length), 
				"Switch index out of bounds: %d", id);
		return Globals.getSwitches()[id];
	}

	public static void setSwitch(Switch s, Event event, TriggeringInfo triggeringInfo, 
			boolean value) throws ParameterException {
		if (s.scope == DataScope.Global) {
			setGlobalSwitch(s.id, value);
		} else if (s.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(triggeringInfo);
			assertThat(inArray(s.id, runtime.switches.length),
					"Switch index out of bounds: %d", s.id);
			runtime.switches[s.id] = value;
		} else {
			throw new ParameterException("Cannot set a parameter!");
		}
	}
	
	public static void setGlobalSwitch(int id, boolean value) throws ParameterException {
		assertThat(inArray(id, Globals.getSwitches().length), 
				"Switch index out of bounds: %d", id);
		Globals.getSwitches()[id] = value;
	}

	public static int readVariable(Variable v, Event event, TriggeringInfo triggeringInfo) 
			throws ParameterException {
		if (v.scope == DataScope.Global) {
			return readGlobalVariable(v.id);
		}  else if (v.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(triggeringInfo);
			assertThat(inArray(v.id, runtime.variables.length),
					"Variable index out of bounds: %d", v.id);
			return runtime.variables[v.id];
		} else {
			BehaviorInstance instance = getBehavingInstance(triggeringInfo);
			assertThat(inArray(v.id, instance.parameters.size()),
					"Variable index out of bounds: %d", v.id);
			Object o = instance.parameters.get(v.id);
			assertThat(o instanceof Parameters, "Invalid parameter");
			return readNumber((Parameters)o, 0, event, triggeringInfo);
		}
	}
	
	public static int readGlobalVariable(int id) throws ParameterException {
		assertThat(inArray(id, Globals.getVariables().length), 
				"Variable index out of bounds: %d", id);
		return Globals.getVariables()[id];
	}

	public static void setVariable(Variable v, Event event, TriggeringInfo triggeringInfo,
			int value) throws ParameterException {
		if (v.scope == DataScope.Global) {
			setGlobalVariable(v.id, value);
		}  else if (v.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(triggeringInfo);
			assertThat(inArray(v.id, runtime.variables.length),
					"Variable index out of bounds: %d", v.id);
			runtime.variables[v.id] = value;
		} else {
			throw new ParameterException("Cannot set a parameter!");
		}
	}
	
	public static void setGlobalVariable(int id, int value) throws ParameterException {
		assertThat(inArray(id, Globals.getVariables().length), 
				"Variable index out of bounds: %d", id);
		Globals.getVariables()[id] = value;
	}
	
	private static int readNumber(Parameters ps, Event event, TriggeringInfo triggeringInfo) 
			throws ParameterException {
		int mode = ps.getInt();
		if (mode == 0) {
			return ps.getInt(1);
		} else {
			return readVariable(ps.getVariable(1), event, triggeringInfo);
		}
	}

	private static int readNumber(Parameters params, int index, Event event, 
			TriggeringInfo triggeringInfo) throws ParameterException {
		Parameters ps = params.getParameters(index);
		return readNumber(ps, event, triggeringInfo);
	}
	
	public static boolean readBoolean(Parameters ps, Event event,
			TriggeringInfo triggeringInfo) throws ParameterException {
		int mode = ps.getInt();
		if (mode == 0) {
			return true;
		} else if (mode == 1) {
			return false;
		} else {
			return readSwitch(ps.getSwitch(1), event, triggeringInfo);
		}
	}

	public static boolean readBoolean(Parameters params, int index, Event event,
			TriggeringInfo triggeringInfo) throws ParameterException {
		Parameters ps = params.getParameters(index);
		return readBoolean(ps, event, triggeringInfo);
	}
}
