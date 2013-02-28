package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Behavior.Parameter;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.types.ActorClassPointer;
import edu.elon.honors.price.data.types.DataScope;
import edu.elon.honors.price.data.types.ObjectClassPointer;
import edu.elon.honors.price.data.types.ScopedData;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;

public class Behavior extends GameData {
	private static final long serialVersionUID = 1L;

	public enum BehaviorType {
		Map("Map Behavior"),
		Actor("Actor Behavior"),
		Object("Object Behavior");
		
		private String name;
		
		BehaviorType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public enum ParameterType {
		Switch("Switch", Switch.class), 
		Variable("Variable", Variable.class),
		ActorClass("Actor", ActorClassPointer.class),
		ObjectClass("Object", ObjectClassPointer.class);
		
		private String name;
		private Class<? extends ScopedData<?>> paramClass;
		
		ParameterType(String name, Class<? extends ScopedData<?>> paramClass) {
			this.name = name;
			this.paramClass = paramClass;
		}
		
		public String getName() {
			return name;
		}
		
		public Class<? extends ScopedData<?>> getParamClass() {
			return paramClass;
		}
	}
	
	public String name = "";
	public BehaviorType type;
	public LinkedList<Event> events = new LinkedList<Event>();
	
	public LinkedList<Integer> variables = new LinkedList<Integer>();
	public LinkedList<String> variableNames = new LinkedList<String>();
	public LinkedList<Boolean> switches = new LinkedList<Boolean>();
	public LinkedList<String> switchNames = new LinkedList<String>();
	
	public LinkedList<Parameter> parameters = new LinkedList<Parameter>();
	
	public LinkedList<Parameter> getParamters(ParameterType type) {
		LinkedList<Parameter> list = new LinkedList<Behavior.Parameter>();
		for (Parameter p : parameters) {
			if (p.type == type) list.add(p);
		}
		return list;
	}
	
	public Behavior(BehaviorType type) {
		this.type = type;
		this.name = "New " + type.getName();
		this.events.add(new Event());
		addSwitch();
		addVariable();
		this.parameters.add(new Parameter());
		
	}
	
	public void addSwitch() {
		addSwitch("New Switch", false);
	}
	
	public void addSwitch(String name, boolean value) {
		switchNames.add(name);
		switches.add(value);
	}
	
	public void addVariable() {
		addVariable("New Variable", 0);
	}
	
	public void addVariable(String name, int value) {
		variableNames.add(name);
		variables.add(value);
	}
	
	
	private <T extends ScopedData<?>> List<T> getScoped(Class<T> c) {
		ArrayList<T> data = new ArrayList<T>();
		for (Event event : events) {
			for (Action action : event.actions) {
				data.addAll(action.params.getAllByClass(c));
			}
			for (Trigger trigger : event.triggers) {
				data.addAll(trigger.params.getAllByClass(c));
			}
		}
		return data;
	}
	
	private boolean inUse(List<? extends ScopedData<?>> data,
			DataScope scope, int index) {
		for (ScopedData<?> d : data) {
			if (d.scope == scope && d.id == index) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adjust for the removal of the Local or Param variable by shifting
	 * any references to variables after it down by one. Does nothing
	 * if the variable is in use.
	 */
	private boolean tryRemoveScoped(List<? extends ScopedData<?>> data,
			DataScope scope, int index) {
		
		if (inUse(data, scope, index)) {
			return false;
		}
		
		removeScoped(data, scope, index);
		
		return true;
	}
	
	private void removeScoped(List<? extends ScopedData<?>> data,
			DataScope scope, int index) {
		for (ScopedData<?> d : data) {
			if (d.scope == scope && d.id > index) {
				d.id--;
			}
		}
	}
	
	public boolean removeSwitch(int index) {
		if (!tryRemoveScoped(getScoped(Switch.class), DataScope.Local, index)) {
			return false;
		}
		
		this.switches.remove(index);
		switchNames.remove(index);
		return true;
	}
	
	public boolean removeVariable(int index) {
		if (!tryRemoveScoped(getScoped(Variable.class), DataScope.Local, index)) {
			return false;
		}
		
		this.variables.remove(index);
		variableNames.remove(index);
		return true;
	}
	
	public boolean removeParameter(int index) {
		Parameter param = parameters.get(index);
		Class<? extends ScopedData<?>> c = param.type.getParamClass();
		if (inUse(getScoped(c), DataScope.Param, index)) return false;
		
		for (ParameterType type : ParameterType.values()) {
			removeScoped(getScoped(type.getParamClass()), DataScope.Param, index);
		}
		
		parameters.remove(index);
		return true;
	}

	public boolean setParameterType(int index, ParameterType type) {
		Parameter param = parameters.get(index);
		if (inUse(getScoped(param.type.paramClass), DataScope.Param, index)) {
			return false;
		}
		param.type = type;
		return true;
	}
		
	public static class Parameter implements Serializable {
		private static final long serialVersionUID = 1L;
		public ParameterType type;
		public String name;
		
		public Parameter() {
			this("New Parameter", ParameterType.Switch);
		}
		
		public Parameter(String name, ParameterType type) {
			this.name = name;
			this.type = type;
		}
	}
}
