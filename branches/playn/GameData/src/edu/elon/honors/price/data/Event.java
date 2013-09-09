package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.elon.honors.price.data.field.DataObject;
import edu.elon.honors.price.data.field.FieldData;
import edu.elon.honors.price.data.field.FieldData.ParseDataException;
import edu.elon.honors.price.data.types.ActorClassPointer;
import edu.elon.honors.price.data.types.ObjectClassPointer;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.game.DataDebug;

/**
 * Represents an Event, including its triggers and actions. Events
 * will cue the interpreter to performs a series of actions in-game,
 * once one of its triggers' conditions are met.
 *
 */
public class Event extends GameData {
	private static final long serialVersionUID = 2L;

	public String name = "";
	public boolean disabled;
	public final ArrayList<Action> actions;
	public final ArrayList<Trigger> triggers;

	@Override
	public void addFields(FieldData fields) throws ParseDataException,
			NumberFormatException {
		name = fields.add(name);
		disabled = fields.add(disabled);
		fields.addList(actions);
		fields.addList(triggers);
	}

	/**
	 * Creates a new event with the given list of Actions.
	 * @param actions The actions this event performs when triggered.
	 */
	public Event(ArrayList<Action> actions) {
		this.actions = actions;
		this.triggers = new ArrayList<Event.Trigger>();
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
		this.actions = new ArrayList<Event.Action>();
		this.triggers = new ArrayList<Event.Trigger>();
	}

	public Event() {
		this("New Event");
	}
	
	/**
	 * Important for SelectorEvent to work
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Represents an action carried out by this event. Actions have an
	 * id, chosen from the constants in the EventIds class, as well as a
	 * set of Parameters. The parameters are interpreted in the context of
	 * the type of action this is. The constant will have a description
	 * explaining how to set the parameters for a given id.
	 *
	 */
	public static class Action extends GameData implements Serializable {
		private static final long serialVersionUID = 1L;

		public Parameters params;
		public int id;
		public int indent;
		public String description;
		//TODO: prevent circular references?
		public Action dependsOn; 

		@Override
		public void addFields(FieldData fields) throws ParseDataException,
				NumberFormatException {
			params = fields.add(params);
			id = fields.add(id);
			indent = fields.add(indent);
			description = fields.add(description);
			dependsOn = fields.add(dependsOn);
		}

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
		
		public Action copy() {
			Action a = new Action(id, params.copy());
			a.indent = indent;
			a.description = description;
			return a;
		}
		
		@Override
		public String toString() {
			return "Action #" + id + ":  " + params.toString();  
		}
	}
	
	/**
	 * Represents a set of parameters for an action.
	 *
	 */
	public static class Parameters implements Serializable, DataObject {//, Iterable<Object> {
		private static final long serialVersionUID = 1L;

		private ArrayList<Object> params = new ArrayList<Object>();
		
		public int version = 0;

		@Override
		public void addFields(FieldData fields) throws ParseDataException,
				NumberFormatException {
			if (fields.writeMode()) {
				fields.add(params.size());
				for (Object o : params) {
					fields.add(o == null ? null : o.getClass().toString());
					if (o instanceof Integer) {
						fields.add((Integer) o);
					} else if (o instanceof Boolean) {
						fields.add((Boolean) o);
					} else if (o instanceof Double) {
						fields.add((Double) o);
					} else if (o instanceof Float) {
						fields.add((Float) o);
					} else if (o instanceof String) {
						fields.add((String) o);
					} else if (o instanceof DataObject) {
						fields.add((DataObject) o);
					} else {
						throw new ParseDataException("Invalid object in Parameters: " + o.toString());
					}
				}
			} else {
				int length = fields.add(0);
				for (int i = 0; i < length; i++) {
					String clazz = fields.add((String) null);
					if (clazz.equals(Integer.class.getName())) {
						params.add(fields.add(0));
					} else if (clazz.equals(Boolean.class.getName())) {
						params.add(fields.add(false));
					} else if (clazz.equals(Double.class.getName())) {
						params.add(fields.add(0.0));
					} else if (clazz.equals(Float.class.getName())) {
						params.add(fields.add(0.0f));
					} else if (clazz.equals(String.class.getName())) {
						params.add(fields.add(""));
					} else if (clazz.equals(DataObject.class.getName())) {
						params.add(fields.add(null, clazz));
					}
				}
			}
			version = fields.add(version);
		}
		
		public <T> List<T> getAllByClass(Class<T> c) {
			ArrayList<T> list = new ArrayList<T>();
			addAllByClass(c, list);
			return list;
		}
		
		//TODO: test!
		private <T> void addAllByClass(Class<T> c, List<T> list) {
			for (Object param : params) {
				if (param instanceof Parameters) {
					((Parameters) param).addAllByClass(c, list);
				} else {
					try {
						@SuppressWarnings("unchecked")
						T t = (T) param;
						list.add(t);
					} catch (Exception e) { }
				}
			}
		}
		
//		protected <T> List<T> getAll(Condition condition, Class<T> clazz) {
//			ArrayList<T> list = new ArrayList<T>();
//			addAll(condition, list);
//			return list;
//		}
//
//		@SuppressWarnings("unchecked")
//		private <T> void addAll(Condition condition, List<T> list) {
//			for (Object param : params) {
//				if (param instanceof Parameters) {
//					((Parameters) param).addAll(condition, list);
//				} else if (condition.meetsCondition(param)) {
//					list.add((T) param);
//				}
//			}
//		}
		
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
		
		public ActorClassPointer getActorClassPointer(int index) {
			return (ActorClassPointer)params.get(index);
		}
		
		public ObjectClassPointer getObjectClassPointer(int index) {
			return (ObjectClassPointer)params.get(index);
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
						param = ((ICopyable<?>) param).copy();
					}
					o.addParam(param);
				}
			}
			o.version = version;
			return o;
		}
		
		private transient ArrayList<Iterator> toReuse;
		private final static int MAX_REUSE = 10;
		
		/**
		 * Returns an iterator for these parameters. To call in an allocation-
		 * minimised environment, indicate your intention to dispose after use.
		 * 
		 * Note that the iterator assumes the Paramteters will remain unchanged
		 * for the duration of the iteration.
		 */
		public Iterator iterator(boolean IWillDisposeThis) {
			if (toReuse == null) {
				toReuse = new ArrayList<Event.Parameters.Iterator>();
			}
			if (!IWillDisposeThis || toReuse.size() == 0) {
				return new Iterator();
			} else {
				Iterator iterator = toReuse.remove(toReuse.size() - 1);
				iterator.reset();
				return iterator;
			}
		}
		
		public class Iterator implements java.util.Iterator<Object>{
			private int index = 0;
			private Parameters params = Parameters.this; //= copy();
			
			public void reset() {
				index = 0;
			}
			
			public void dispose() {
				if (toReuse.contains(this)) {
					//If dispose it called twice
					DataDebug.write("Double disposed iterator!!");
				} else if (toReuse.size() >= MAX_REUSE) {
					//Likely if dispose is not called
					DataDebug.write("Max iterator reuse occurred");
				} else {
					toReuse.add(this);
				}
			}

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
			public ActorClassPointer getActorClassPointer() { return params.getActorClassPointer(index++); }
			public ObjectClassPointer getObjectClassPointer() { return params.getObjectClassPointer(index++); }

			@Override
			public boolean hasNext() {
				boolean hasNext = index < getSize();
				return hasNext;
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

	public static class Trigger extends GameData 
	implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public final static int ID_SWITCH = 0,
				ID_VARIABLE = 1,
				ID_ACTOR_OBJECT = 2,
				ID_REGION = 3,
				ID_UI = 4,
				ID_TIME = 5;

		
		public int id;
		public Parameters params;
		public String description;
		public transient Object gameData;

		@Override
		public void addFields(FieldData fields) throws ParseDataException,
				NumberFormatException {
			id = fields.add(id);
			params = fields.add(params);
			description = fields.add(description);
		}
		
		public Trigger(int id, Parameters params) {
			this.id = id;
			this.params = params;
		}
		
		public Trigger() { };
		
		public static class Contact {

			public final static int STATE_TOUCHING = 0;
			public final static int STATE_CONTAINED = 1;

			public int state;
			public Object object;
			public int event;
			public boolean checked = true;
			
			private static ArrayList<Contact> toReuse = 
					new ArrayList<Event.Trigger.Contact>();

			private Contact() {	}
			
			public void set(Object object, int state, int event) {
				this.object = object;
				this.state = state;
				this.event = event;
			}
			
			public static Contact create(Object object, int state, int event) {
				Contact contact;
				if (toReuse.size() == 0) {
					contact = new Contact();
				} else {
					contact = toReuse.remove(toReuse.size() - 1);
				}
				contact.set(object, state, event);
				return contact;
			}
			
			public void dispose() {
				toReuse.add(this);
			}
		}
	}
}
