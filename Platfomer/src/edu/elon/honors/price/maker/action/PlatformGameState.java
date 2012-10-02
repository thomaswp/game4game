package edu.elon.honors.price.maker.action;

import java.util.List;

import com.twp.platform.ActorBody;
import com.twp.platform.BehaviorRuntime;
import com.twp.platform.Globals;
import com.twp.platform.IBehaving;
import com.twp.platform.ObjectBody;
import com.twp.platform.PhysicsHandler;
import com.twp.platform.PlatformLogic;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.types.DataScope;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.physics.Vector;

public class PlatformGameState implements GameState {

	private PlatformLogic logic;
	private PhysicsHandler physics;
	private PlatformGame game;

	private Point point = new Point();
	private Vector vector = new Vector();
	
	private Event event;


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
	public Point readPoint(Parameters ps) throws ParameterException {
		if (ps.getInt() == 0) {
			point.set(ps.getInt(1), ps.getInt(2));
		} else {
			point.set(readVariable(ps.getVariable(1), event), readVariable(ps.getVariable(2), event));
		}
		return point;
	}
	
	@Override
	public Vector readVector(Parameters ps) throws ParameterException {
		int type = ps.getInt();
		if (type == 0) {
			vector.set(ps.getFloat(1), ps.getFloat(2));
		} else if (type == 1) {
			if (event.tVector == null) {
				throw new ParameterException("No triggering vector");
			}
			vector.set((Vector)event.tVector);
		} else {
			JoyStick joy = readJoystick(ps.getInt(1));
			vector.set(joy.getLastPull());
			vector.multiply(1f / joy.getLastPull().magnitude());
			Game.debug(vector);
		}
		return vector;
	}

	@Override
	public ActorClass readActorClass(int i) throws ParameterException {
		if (i < 0 || i >= game.actors.length) {
			throw new ParameterException("Actor Class index out of bounds: " + i);
		}
		return game.actors[i];
	}

	@Override
	public ObjectClass readObjectClass(int i) throws ParameterException {
		if (i < 0 || i >= game.objects.length) {
			throw new ParameterException("Object Class index out of bounds: " + i);
		}
		return game.objects[i];
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
			if (event.tActor == null) {
				throw new ParameterException("No triggering actor");
			}
			return (ActorBody)event.tActor;
		} else {
			assertThat(event.behaving != null && 
					event.behaving instanceof ActorBody,
					"Behaving object is not an Actor");
			return (ActorBody)event.behaving;
		}
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
			if (event.tObject == null) {
				throw new ParameterException("No triggering object");
			}
			return (ObjectBody)event.tObject;
		} else if (mode == 2) {
			ObjectBody last = physics.getLastCreatedObject();
			if (last == null || last.isDisposed()) {
				throw new ParameterException("No last created object");
			}
			return last;
		} else {
			assertThat(event.behaving != null && 
					event.behaving instanceof ObjectBody,
					"Behaving object is not an Object");
			return (ObjectBody)event.behaving;
		}
	}

	@Override
	public boolean readSwitch(Switch params) throws ParameterException {
		return readSwitch(params, event);
	}

	@Override
	public int readVariable(Variable params) throws ParameterException {
		return readVariable(params, event);
	}

	@Override
	public boolean readBoolean(Parameters ps) throws ParameterException {
		return readBoolean(ps, event);
	}

	@Override
	public int readNumber(Parameters ps) throws ParameterException {
		return readNumber(ps, event);
	}
	
	/* -- Static Members -- */
	
	private static void assertThat(boolean condition, String message)
			throws ParameterException {
		if (!condition) {
			throw new ParameterException(message);
		}
	}

	private static void assertThat(boolean condition, String message, 
			Object... args) throws ParameterException {
		assertThat(condition, String.format(message, args));
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

	public static IBehaving getBehaving(Event event) throws ParameterException {
		assertThat(event.behaving != null && 
				event.behaving instanceof IBehaving,
				"No behaving object!");
		return (IBehaving)event.behaving;
	}

	public static BehaviorInstance getBehavingInstance(Event event) 
			throws ParameterException {
		List<BehaviorInstance> instances = 
				getBehaving(event).getBehaviorInstances();
		assertThat(inArray(event.behaviorIndex, instances),
				"Behavior out of range!");
		return instances.get(event.behaviorIndex);		
	}

	public static BehaviorRuntime getBehavingRuntime(Event event) 
			throws ParameterException {
		BehaviorRuntime[] runtimes = 
				getBehaving(event).getBehaviorRuntimes();
		assertThat(inArray(event.behaviorIndex, runtimes),
				"Behavior out of range!");
		return runtimes[event.behaviorIndex];		
	}

	public static boolean readSwitch(Switch s, Event event) 
			throws ParameterException {
		if (s.scope == DataScope.Global) {
			int i = s.id;
			assertThat(inArray(i, Globals.getSwitches().length), 
					"Switch index out of bounds: %d", i);
			return Globals.getSwitches()[i];
		} else if (s.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(event);
			assertThat(inArray(s.id, runtime.switches.length),
					"Switch index out of bounds: %d", s.id);
			return runtime.switches[s.id];
		} else {
			BehaviorInstance instance = getBehavingInstance(event);
			assertThat(inArray(s.id, instance.parameters),
					"Switch index is out of bounds: %d", s.id);
			Object o = instance.parameters.get(s.id);
			assertThat(o instanceof Parameters, "Invalid parameter");
			return readBoolean((Parameters)o, event);
		}
	}

	public static void setSwitch(Switch s, Event event, boolean value) 
			throws ParameterException {
		if (s.scope == DataScope.Global) {
			int i = s.id;
			assertThat(inArray(i, Globals.getSwitches().length), 
					"Switch index out of bounds: %d", i);
			Globals.getSwitches()[i] = value;
		} else if (s.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(event);
			assertThat(inArray(s.id, runtime.switches.length),
					"Switch index out of bounds: %d", s.id);
			runtime.switches[s.id] = value;
		} else {
			throw new ParameterException("Cannot set a parameter!");
		}
	}

	public static int readVariable(Variable v, Event event) 
			throws ParameterException {
		if (v.scope == DataScope.Global) {
			int i = v.id;
			assertThat(inArray(i, Globals.getVariables().length),
					"Variable index out of bounds: %d", i);
			return Globals.getVariables()[i];
		}  else if (v.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(event);
			assertThat(inArray(v.id, runtime.variables.length),
					"Variable index out of bounds: %d", v.id);
			return runtime.variables[v.id];
		} else {
			BehaviorInstance instance = getBehavingInstance(event);
			assertThat(inArray(v.id, instance.parameters.size()),
					"Variable index out of bounds: %d", v.id);
			Object o = instance.parameters.get(v.id);
			assertThat(o instanceof Parameters, "Invalid parameter");
			return readNumber((Parameters)o, 0, event);
		}
	}

	public static void setVariable(Variable v, Event event, int value) 
			throws ParameterException {
		if (v.scope == DataScope.Global) {
			int i = v.id;
			assertThat(inArray(i, Globals.getVariables().length),
					"Variable index out of bounds: %d", i);
			Globals.getVariables()[i] = value;
		}  else if (v.scope == DataScope.Local) {
			BehaviorRuntime runtime = getBehavingRuntime(event);
			assertThat(inArray(v.id, runtime.variables.length),
					"Variable index out of bounds: %d", v.id);
			runtime.variables[v.id] = value;
		} else {
			throw new ParameterException("Cannot set a parameter!");
		}
	}
	
	private static int readNumber(Parameters ps, Event event) 
			throws ParameterException {
		int mode = ps.getInt();
		if (mode == 0) {
			return ps.getInt(1);
		} else {
			return readVariable(ps.getVariable(1), event);
		}
	}

	private static int readNumber(Parameters params, int index, Event event) 
			throws ParameterException {
		Parameters ps = params.getParameters(index);
		return readNumber(ps, event);
	}
	
	public static boolean readBoolean(Parameters ps, Event event)
			throws ParameterException {
		int mode = ps.getInt();
		if (mode == 0) {
			return true;
		} else if (mode == 1) {
			return false;
		} else {
			return readSwitch(ps.getSwitch(1), event);
		}
	}

	public static boolean readBoolean(Parameters params, int index, Event event)
			throws ParameterException {
		Parameters ps = params.getParameters(index);
		return readBoolean(ps, event);
	}
}
