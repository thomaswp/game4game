package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.elon.honors.price.data.types.DataScope;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import android.graphics.Rect;

/**
 * Represents an Event, including its triggers and actions. Events
 * will cue the interpreter to performs a series of actions in-game,
 * once one of its triggers' conditions are met.
 *
 */
public class Event extends GameData {
	private static final long serialVersionUID = 2L;

	public String name = "";
	public ArrayList<Action> actions = new ArrayList<Event.Action>();
	public ArrayList<Trigger> triggers = new ArrayList<Event.Trigger>();
	
	public transient Object tActor, tObject, tVector, behaving;
	public transient int behaviorIndex = -1;

	/**
	 * Creates a new event with the given list of Actions.
	 * @param actions The actions this event performs when triggered.
	 */
	public Event(ArrayList<Action> actions) {
		this.actions = actions;
	}

	/**
	 * Creates a new event with the given Action. More actions
	 * can be added later.
	 * @param action The action this event performs when triggered.
	 */
	public Event(Action action) {
		this(new ArrayList<Action>());
		actions.add(action);
	}

	public Event(String name) {
		this.name = name;
	}

	public Event() {
		this("New Event");
	}

	public void clearTriggerInfo() {
		tActor = null;
		tObject = null;
		tVector = null;
	}
	
	public void clearBehavingInfo() {
		behaving = null;
		behaviorIndex = -1;
	}
	
	/**
	 * Represents an action carried out by this event. Actions have an
	 * id, chosen from the constants in the EventIds class, as well as a
	 * set of Parameters. The parameters are interpreted in the context of
	 * the type of action this is. The constant will have a description
	 * explaining how to set the parameters for a given id.
	 *
	 */
	public static class Action extends ActionIds implements Serializable {
		private static final long serialVersionUID = 1L;

		public Parameters params;
		public int id;
		public int indent;
		public String description;

		/**
		 * Creates a new Action with the given id and parameters. See the
		 * class' javadoc for more.
		 * @param id The id of the Action. Should be a constant from this
		 * class.
		 * @param params The parameters for this action.
		 */
		public Action(int id, Parameters params) {
			this.id = id;
			this.params = params;
		}
		
		public boolean canHaveChildren() {
			return id == ID_IF ||
				id == ID_LOOP;
		}
		
		public Action copy() {
			Action a = new Action(id, params.copy());
			a.indent = indent;
			a.description = description;
			return a;
		}
		
		@Override
		public String toString() {
			return String.format("[%d] %s", indent, ActionIds.ACTION_NAMES[id]);
		}
	}
	
	/**
	 * Represents a set of parameters for an action.
	 *
	 */
	public static class Parameters implements Serializable, Iterable<Object> {
		private static final long serialVersionUID = 1L;

		private ArrayList<Object> params = new ArrayList<Object>();
		private Object tag;
		
		public void setTag(Object tag) {
			this.tag = tag;
		}
		
		public Object getTag() {
			return tag;
		}
		
		public <T> List<T> getAllByClass(Class<T> c) {
			ArrayList<T> list = new ArrayList<T>();
			addAllByClass(c, list);
			return list;
		}
		
		private <T> void addAllByClass(Class<T> c, List<T> list) {
			for (Object param : params) {
				if (param instanceof Parameters) {
					((Parameters) param).addAllByClass(c, list);
				} else if (c.isInstance(param)) {
					list.add(c.cast(param));
				}
			}
		}
		
		public void set(int index, Object object) {
			params.set(index, object);
		}
		
		/**
		 * Gets the number of parameters in this set.
		 * @return The size
		 */
		public int getSize() {
			return params.size();
		}

		public void addParam(Object param) {
			params.add(param);
		}

		public Object getObject() { return getObject(0); }

		public Object getObject(int index) {
			return params.get(index);
		}

		/**
		 * Gets the first parameter, cast as a boolean.
		 * @return The parameter
		 */
		public boolean getBoolean() { return getBoolean(0); }
		/**
		 * Gets the parameter at the given index, cast as a boolean.
		 * @param index The index
		 * @return The parameter
		 */
		public boolean getBoolean(int index) {
			return (Boolean)params.get(index);
		}

		/**
		 * Gets the first parameter, cast as a String.
		 * @return The parameter
		 */
		public String getString() { return getString(0); }
		/**
		 * Gets the parameter at the given index, cast as a String.
		 * @param index The index
		 * @return The parameter
		 */
		public String getString(int index) {
			return (String)params.get(index);
		}

		/**
		 * Gets the first parameter, cast as an int.
		 * @return The parameter
		 */
		public int getInt() { return getInt(0); }
		/**
		 * Gets the parameter at the given index, cast as an int.
		 * @param index The index
		 * @return The parameter
		 */
		public int getInt(int index) {
			return (Integer)params.get(index);
		}

		/**
		 * Gets the first parameter, cast as a float.
		 * @return The parameter
		 */
		public float getFloat() { return getFloat(0); }
		/**
		 * Gets the parameter at the given index, cast as a float.
		 * @param index The index
		 * @return The parameter
		 */
		public float getFloat(int index) {
			return (Float)params.get(index);
		}

		public Double getDouble() {
			return (Double)params.get(0);
		}
		
		public Double getDouble(int index) {
			return (Double)params.get(index);
		}
		
		public Switch getSwitch() {
			return getSwitch(0);
		}
		
		public Switch getSwitch(int index) {
			return (Switch)params.get(index);
		}
		
		public Variable getVariable() {
			return getVariable(0);
		}
		
		public Variable getVariable(int index) {
			return (Variable)params.get(index);
		}
		
		/**
		 * Gets the first parameter, cast as another set of Parameters.
		 * This is used when a list or complex data is passed as a parameter.
		 * @return The parameter
		 */
		public Parameters getParameters() { return getParameters(0); }
		/**
		 * Gets the parameter at the given index, cast as another set of Parameters.
		 * This is used when a list or complex data is passed as a parameter.
		 * @param index The index
		 * @return The parameter
		 */
		public Parameters getParameters(int index) {
			return (Parameters)params.get(index);
		}
		
		@Override
		public String toString() {
			return Arrays.toString(params.toArray());
		}

		public boolean equals(Parameters o) {
			if (o.getSize() != getSize())
				return false;

			try {
				for (int i = 0; i < o.params.size(); i++) {
					if (params.get(i) instanceof Parameters) {
						if (!getParameters(i).equals(o.getParameters(i))) {
							return false;
						}
					} else {
						Object objectA = getObject(i);
						Object objectB = o.getObject(i);
						if (objectA != null && objectA instanceof GameData
								&& objectB instanceof GameData) {
							return ((GameData)objectA).equals((GameData)objectB);
						} else if (!getObject(i).equals(o.getObject(i))) {
							return false;
						}
					}
				}
			}
			catch (Exception e) {
				return false;
			}

			return true;
		}

		public Parameters copy() {
			Parameters o = new Parameters();
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i) instanceof Parameters) {
					o.addParam(getParameters(i).copy());
				} else {
					Object param = params.get(i);
					if (param instanceof ICopyable) {
						param = ((ICopyable) param).copy();
					}
					o.addParam(param);
				}
			}
			return o;
		}
		
		@Override
		public Iterator iterator() {
			return new Iterator();
		}
		
		public class Iterator implements java.util.Iterator<Object>{
			private int index = 0;
			private Parameters params = copy();

			public int getSize() {
				return params.getSize();
			}

			public Object getObject() { return params.getObject(index++); }
			public boolean getBoolean() { return params.getBoolean(index++); }
			public String getString() { return params.getString(index++); }
			public int getInt() { return params.getInt(index++); }
			public float getFloat() { return params.getFloat(index++); }
			public Double getDouble() {	return params.getDouble(index++); }
			public Parameters getParameters() { return params.getParameters(index++); }
			public Switch getSwitch() { return params.getSwitch(index++); }
			public Variable getVariable() { return params.getVariable(index++); }

			@Override
			public boolean hasNext() {
				return index < getSize();
			}

			@Override
			public Object next() {
				return getObject();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}

	public abstract static class Trigger extends GameData 
	implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Represents an Event trigger which is triggered by a switch
	 * taking on a certain value.
	 *
	 */
	public static class SwitchTrigger extends Trigger {
		private static final long serialVersionUID = 1L;

		public Switch triggerSwitch;
		public boolean value;

		/**
		 * Creates a SwitchTrigger, activated when the switch with
		 * the given id is set to the given value.
		 * @param switchId The id of the switch
		 * @param value The value 
		 */
		public SwitchTrigger(int switchId, DataScope switchScope, 
				boolean value) {
			this.triggerSwitch = new Switch(switchId, switchScope);
			this.value = value;
		}

		public SwitchTrigger() {
			this(0, DataScope.Global, true);
		}

//		public boolean equals(SwitchTrigger o) {
//			return o.triggerSwitch.equals(triggerSwitch) &&
//			o.value == value;
//		}
	}

	/**
	 * Represents an Event trigger which is triggered by a variable
	 * falling under a given condition, such as equalling a value
	 * or exceeding it.
	 *
	 */
	public static class VariableTrigger extends Trigger {
		private static final long serialVersionUID = 1L;

		/**
		 * Tests if the variable is equal to the value.
		 */
		public static final int TEST_EQUALS = 0;
		/**
		 * Tests if the variable is not equal to the value.
		 */
		public static final int TEST_NOT_EQUALS = 1;
		/**
		 * Tests if the variable is greater than the value.
		 */
		public static final int TEST_GT = 2;
		/**
		 * Tests if the variable is less than the value.
		 */
		public static final int TEST_LT = 3;
		/**
		 * Tests if the variable is greater than or equal to the value.
		 */
		public static final int TEST_GEQ = 4;
		/**
		 * Tests if the variable is less than or equal to the value.
		 */
		public static final int TEST_LEQ = 5;
		/**
		 * Tests if the variable is divisible by the value.
		 */
		public static final int TEST_DIVISIBLE = 6;

		/**
		 * Tests against another variable
		 */
		public static final int WITH_VARIABLE = 0;
		/**
		 * Tests against a literal value
		 */
		public static final int WITH_VALUE = 1;

		public static final String[] OPERATORS = new String[] {
			"equal to",
			"not equal to",
			"greater than",
			"less than",
			"greater than or equal to",
			"less than or equal to",
			"divisible by"
		};

		public Variable variable;
		public int test;
		public int with;
		public int withValue;
		public Variable withVariable;

		public VariableTrigger(int variableId, DataScope variableScope, 
				int test, int with) {
			this.variable = new Variable(variableId, variableScope);
			this.test = test;
			this.with = with;
			withValue = 0;
			withVariable = new Variable();
		}

		public VariableTrigger() {
			this(0, DataScope.Global, 0, 0);
		}

//		public boolean equals(VariableTrigger o) {
//			return o.variable.equals(variable) &&
//			o.test == test &&
//			o.with == with &&
//			o.withValue == withValue &&
//			o.withVariableScope == withVariableScope;
//		}
	}

	public static class ActorOrObjectTrigger extends Trigger {
		private static final long serialVersionUID = 1L;

		public static final String[] ACTIONS = new String[] {
			"collides with an actor",
			"collides with the Hero",
			"collides with an object",
			"collides with a wall",
			"is destroyed"
		};

		public static final int ACTION_COLLIDES_ACTOR = 0;
		public static final int ACTION_COLLIDES_HERO = 1;
		public static final int ACTION_COLLIDES_OBJECT = 2;
		public static final int ACTION_COLLIDES_WALL = 3;
		public static final int ACTION_DIES = 4;
		
		public static final int MODE_ACTOR_INSTANCE = 0;
		public static final int MODE_ACTOR_CLASS = 1;
		public static final int MODE_OBJECT_INSTANCE = 2;
		public static final int MODE_OBJECT_CLASS= 3;

		public int id;
		public int action;
		public int mode;

		public ActorOrObjectTrigger(int mode, int id, int action) {
			this.mode = mode;
			this.id = id;
			this.action = action;
		}

		public ActorOrObjectTrigger() {
			this(0, 1, 0);
		}
		
		public boolean isActorTrigger() {
			return mode == MODE_ACTOR_CLASS || 
			mode == MODE_ACTOR_INSTANCE;
		}
		
		public boolean isObjectTrigger() {
			return !isActorTrigger();
		}

		public boolean equals(ActorOrObjectTrigger o) {
			return o.id == id &&
			o.action == action &&
			o.mode == mode;
		}
	}

	public static class RegionTrigger extends Trigger {
		private static final long serialVersionUID = 2L;

		public static final String[] MODES = new String[] {
			"begins to enter",
			"fully enters",
			"begins to leave",
			"fully leaves"
		};

		public static final int MODE_TOUCH = 0;
		public static final int MODE_CONTAIN = 1;
		public static final int MODE_LOSE_CONTAIN = 2;
		public static final int MODE_LOSE_TOUCH = 3;
		
		public static final int WHO_ACTOR = 0;
		public static final int WHO_OBJECT = 1;
		public static final int WHO_HERO = 2;

		public transient ArrayList<Contact> contacts;

		public int left, right, top, bottom;
		public int mode;
		public int who;

		public RegionTrigger(Rect rect, int mode, int who) {
			this(rect.left, rect.top, rect.right, rect.bottom, mode, who);
		}

		public RegionTrigger(int left, int top, int right, int bottom, int mode, int who) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			this.who = who;
			this.mode = mode;
		}

		public RegionTrigger() {
			this(0, 0, 0, 0, 0, 0);
		}

		public boolean equals(RegionTrigger o) {
			return o.left == left &&
			o.top == top &&
			o.right == right &&
			o.bottom == bottom && 
			o.mode == mode &&
			o.who == who;
		}

		public static class Contact {

			public final static int STATE_TOUCHING = 0;
			public final static int STATE_CONTAINED = 1;
			
			public int state;
			public Object object;
			public int event;
			public boolean checked = true;

			public Contact(Object object, int state, int event) {
				this.object = object;
				this.state = state;
				this.event = event;
			}
		}
	}
	
	public static class UITrigger extends Trigger {
		private static final long serialVersionUID = 1L;
		
		public static final int CONTROL_BUTTON = 0;
		public static final int CONTROL_JOY = 1;
		public static final int CONTROL_TOUCH = 2;
		
		public static final int CONDITION_PRESS = 0;
		public static final int CONDITION_RELEASE = 1;
		public static final int CONDITION_MOVE = 2;
		
		public int controlType;
		public int index;
		public int condition;
		
		public UITrigger() {
			this(2, 0);
		}
		
		public UITrigger(int controlType, int condition) {
			this.controlType = controlType;
			this.condition = condition;
		}
		
		public transient boolean lastButtonDown, lasyJoyDown;
		public transient float lastJoyX, lastJoyY;
		
		public boolean equals(UITrigger o) {
			return controlType == o.controlType &&
			index == o.index &&
			condition == o.condition;
		}
	}
}
