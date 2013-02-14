package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.types.DataScope;
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
		Switch("Switch"), 
		Variable("Variable");
		
		private String name;
		
		ParameterType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
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
	
	private List<Switch> getSwitches() {
		ArrayList<Switch> switches = new ArrayList<Switch>();
		for (Event event : events) {
			for (Action action : event.actions) {
				switches.addAll(action.params.getAllByClass(Switch.class));
			}
			for (Trigger trigger : event.triggers) {
				switches.addAll(trigger.params.getAllByClass(Switch.class));
			}
		}
		return switches;
	}
	
	private List<Variable> getVariables() {
		ArrayList<Variable> variables = new ArrayList<Variable>();
		for (Event event : events) {
			for (Action action : event.actions) {
				variables.addAll(
						action.params.getAllByClass(Variable.class));
			}
			for (Trigger trigger : event.triggers) {
				variables.addAll(
						trigger.params.getAllByClass(Variable.class));
			}
		}
		return variables;
	}
	
	private boolean tryRemoveScoped(List<? extends ScopedData> data,
			DataScope scope, int index) {
		
		for (ScopedData d : data) {
			if (d.scope == scope && d.id == index) {
				return false;
			}
		}
		for (ScopedData d : data) {
			if (d.scope == scope && d.id > index) {
				d.id--;
			}
		}
		return true;
	}
	
	private boolean tryRemoveSwitch(DataScope scope, int index) {
		return tryRemoveScoped(getSwitches(), scope, index);
	}
	
	private boolean tryRemoveVariable(DataScope scope, int index) {
		return tryRemoveScoped(getVariables(), scope, index);
	}
	
	public boolean removeSwitch(int index) {
		if (!tryRemoveSwitch(DataScope.Local, index)) {
			return false;
		}
		
		this.switches.remove(index);
		switchNames.remove(index);
		return true;
	}
	
	public boolean removeVariable(int index) {
		if (!tryRemoveVariable(DataScope.Local, index)) {
			return false;
		}
		
		this.variables.remove(index);
		variableNames.remove(index);
		return true;
	}
	
	public boolean removeParameter(int index) {
		Parameter param = parameters.get(index);
		if (param.type == ParameterType.Switch) {
			if (!tryRemoveSwitch(DataScope.Param, index)) {
				return false;
			}
			tryRemoveVariable(DataScope.Param, index);
		} else if (param.type == ParameterType.Variable) {
			if (!tryRemoveVariable(DataScope.Param, index)) {
				return false;
			}
			tryRemoveSwitch(DataScope.Param, index);
		}
		parameters.remove(index);
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
