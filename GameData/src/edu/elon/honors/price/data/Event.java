package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Rect;

public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Action> actions;
	public ArrayList<SwitchTrigger> switchTriggers = new ArrayList<SwitchTrigger>();
	public ArrayList<VariableTrigger> variableTriggers = new ArrayList<Event.VariableTrigger>();
	public ArrayList<ActorTrigger> actorTriggers = new ArrayList<Event.ActorTrigger>();
	public ArrayList<RegionTrigger> regionTriggers = new ArrayList<Event.RegionTrigger>();
	
	public Event(ArrayList<Action> actions) {
		this.actions = actions;
	}
	
	public Event(Action action) {
		this(new ArrayList<Action>());
		actions.add(action);
	}

	public static class Action extends EventIds implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public Parameters params;
		public int id;
		
		public Action(int id, Parameters params) {
			this.id = id;
			this.params = params;
		}
	}
	
	public static class Parameters implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Object[] params;
		
		public Parameters(Object... params) {
			this.params = params;
		}
		
		public int getSize() {
			return params.length;
		}
		
		public boolean getBoolean() { return getBoolean(0); }
		public boolean getBoolean(int index) {
			return (Boolean)params[index];
		}
		
		public String getString() { return getString(0); }
		public String getString(int index) {
			return (String)params[index];
		}
		
		public int getInt() { return getInt(0); }
		public int getInt(int index) {
			return (Integer)params[index];
		}
		
		public float getFloat() { return getFloat(0); }
		public float getFloat(int index) {
			return (Float)params[index];
		}
		
		public Parameters getParameters() { return getParameters(0); }
		public Parameters getParameters(int index) {
			return (Parameters)params[index];
		}
	}
	
	public static class SwitchTrigger implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public int switchId;
		public boolean value;
		
		public SwitchTrigger(int switchId, boolean value) {
			this.switchId = switchId;
			this.value = value;
		}
	}
	
	public static class VariableTrigger implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public static final int TEST_EQUALS = 0;
		public static final int TEST_NOT_EQUALS = 1;
		public static final int TEST_GT = 2;
		public static final int TEST_LT = 3;
		public static final int TEST_GEQ = 4;
		public static final int TEST_LEQ = 5;
		public static final int TEST_DIVISIBLE = 6;
		
		public static final int WITH_VARIABLE = 0;
		public static final int WITH_VALUE = 1;
		
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
	}
	
	public static class ActorTrigger implements Serializable {
		private static final long serialVersionUID = 1L;
		
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
	}
	
	public static class RegionTrigger implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public static final int MODE_TOUCH = 3;
		public static final int MODE_CONTAIN = 5;
		public static final int MODE_LOSE_TOUCH = 0;
		public static final int MODE_LOSE_CONTAIN = 2;
		
		public transient ArrayList<Contact> contacts = new ArrayList<Event.RegionTrigger.Contact>();
		
		public int left, right, top, bottom;
		public int mode;
		public boolean onlyHero;
		
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
