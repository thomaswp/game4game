package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

public class PlatformEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Action> actions;
	
	public PlatformEvent(ArrayList<Action> actions) {
		this.actions = actions;
	}
	
	public PlatformEvent(Action action) {
		this(new ArrayList<Action>());
		actions.add(action);
	}

	public static class Action implements Serializable {
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
}
