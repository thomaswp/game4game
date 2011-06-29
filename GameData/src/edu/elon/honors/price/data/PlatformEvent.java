package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;

public class PlatformEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Action> actions = new ArrayList<PlatformEvent.Action>();

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
	}
}
