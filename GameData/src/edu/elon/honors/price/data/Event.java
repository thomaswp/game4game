package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import android.graphics.Rect;

/**
 * Represents an Event, including its triggers and actions. Events
 * will cue the interpreter to performs a series of actions in-game,
 * once one of its triggers' conditions are met.
 *
 */
public class Event implements Serializable {
	private static final long serialVersionUID = 2L;

	public String name = "";
	public ArrayList<Action> actions = new ArrayList<Event.Action>();
	public ArrayList<Trigger> triggers = new ArrayList<Event.Trigger>();

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
	}

	/**
	 * Represents a set of parameters for an action.
	 *
	 */
	public static class Parameters implements Serializable {
		private static final long serialVersionUID = 1L;

		private ArrayList<Object> params = new ArrayList<Object>();

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
			return params.get(0);
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

			boolean equal = true;
			try {
				for (int i = 0; i < o.params.size(); i++) {
					if (params.get(i) instanceof Parameters) {
						equal &= getParameters(i).equals(o.getParameters(i));
					} else {
						equal &= getObject(i).equals(o.getObject(i));
					}
				}
			}
			catch (Exception e) {
				return false;
			}

			return equal;
		}
	}

	public abstract static class Trigger implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Represents an Event trigger which is triggered by a switch
	 * taking on a certain value.
	 *
	 */
	public static class SwitchTrigger extends Trigger {
		private static final long serialVersionUID = 1L;

		public int switchId;
		public boolean value;

		/**
		 * Creates a SwitchTrigger, activated when the switch with
		 * the given id is set to the given value.
		 * @param switchId The id of the switch
		 * @param value The value 
		 */
		public SwitchTrigger(int switchId, boolean value) {
			this.switchId = switchId;
			this.value = value;
		}

		public SwitchTrigger() {
			this(0, true);
		}

		public boolean equals(SwitchTrigger o) {
			return o.switchId == switchId &&
			o.value == value;
		}
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

		public int variableId;
		public int test;
		public int with;
		public int valueOrId;

		public VariableTrigger(int variableId, int test, int with, int valueOrId) {
			this.variableId = variableId;
			this.test = test;
			this.with = with;
			this.valueOrId = valueOrId;
		}

		public VariableTrigger() {
			this(0, 0, 0, 0);
		}

		public boolean equals(VariableTrigger o) {
			return o.variableId == variableId &&
			o.test == test &&
			o.with == with &&
			o.valueOrId == valueOrId;
		}
	}

	public static class ActorTrigger extends Trigger {
		private static final long serialVersionUID = 1L;

		public static final String[] ACTIONS = new String[] {
			"collides with an actor",
			"collides with the Hero",
			"collides with a wall",
			"dies"
		};

		public static final int ACTION_COLLIDES_ACTOR = 0;
		public static final int ACTION_COLLIDES_HERO = 1;
		public static final int ACTION_COLLIDES_WALL = 2;
		public static final int ACTION_DIES = 3;

		public int id;
		public int action;
		public boolean forInstance;

		public ActorTrigger(boolean forInstance, int id, int action) {
			this.forInstance = forInstance;
			this.id = id;
			this.action = action;
		}

		public ActorTrigger() {
			this(true, 1, 0);
		}

		public boolean equals(ActorTrigger o) {
			return o.id == id &&
			o.action == action &&
			o.forInstance == forInstance;
		}
	}

	public static class RegionTrigger extends Trigger {
		private static final long serialVersionUID = 1L;

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

		public transient ArrayList<Contact> contacts = new ArrayList<Event.RegionTrigger.Contact>();

		public int left, right, top, bottom;
		public int mode;
		public boolean onlyHero;

		public int getTriggerState() {
			switch (mode) {
			case MODE_LOSE_TOUCH: return 0;
			case MODE_LOSE_CONTAIN: return 2;
			case MODE_TOUCH: return 3;
			case MODE_CONTAIN: return 5;
			}
			return -1;
		}

		public RegionTrigger(Rect rect, int mode, boolean onlyHero) {
			this(rect.left, rect.top, rect.right, rect.bottom, mode, onlyHero);
		}

		public RegionTrigger(int left, int top, int right, int bottom, int mode, boolean onlyHero) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			this.onlyHero = onlyHero;
			this.mode = mode;
		}

		public RegionTrigger() {
			this(0, 0, 0, 0, 0, false);
		}

		public boolean equals(RegionTrigger o) {
			return o.left == left &&
			o.top == top &&
			o.right == right &&
			o.bottom == bottom && 
			o.mode == mode &&
			o.onlyHero == onlyHero;
		}

		public static class Contact {

			public int state;
			public Object object;

			public Contact(Object object, int state) {
				this.object = object;
				this.state = state;
			}
		}
	}
}
