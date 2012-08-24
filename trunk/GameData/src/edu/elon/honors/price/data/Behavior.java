package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.data.types.DataScope;
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
	
	public boolean removeSwitch(int index) {
		ArrayList<Switch> switches = new ArrayList<Switch>();
		for (Event event : events) {
			for (Action action : event.actions) {
				switches.addAll(action.params.getAllByClass(Switch.class));
			}
			for (Trigger trigger : event.triggers) {
				if (trigger instanceof SwitchTrigger) {
					switches.add(
							((SwitchTrigger) trigger).triggerSwitch);
				}
			}
		}
		for (Switch s : switches) {
			if (s.scope == DataScope.Local && s.id == index) {
				return false;
			}
		}
		for (Switch s : switches) {
			if (s.scope == DataScope.Local && s.id > index) {
				s.id--;
			}
		}
		
		this.switches.remove(index);
		switchNames.remove(index);
		return true;
	}
	
	public boolean removeVariable(int index) {
		ArrayList<Variable> variables = new ArrayList<Variable>();
		for (Event event : events) {
			for (Action action : event.actions) {
				variables.addAll(
						action.params.getAllByClass(Variable.class));
			}
			for (Trigger trigger : event.triggers) {
				if (trigger instanceof VariableTrigger) {
					VariableTrigger vt = (VariableTrigger)trigger;
					variables.add(vt.variable);
					if (vt.withVariable != null) {
						variables.add(vt.withVariable);
					}
				}
			}
		}
		for (Variable v : variables) {
			if (v.scope == DataScope.Local && v.id == index) {
				return false;
			}
		}
		for (Variable v : variables) {
			if (v.scope == DataScope.Local && v.id > index) {
				v.id--;
			}
		}
		
		this.variables.remove(index);
		variableNames.remove(index);
		return true;
	}
	
	//TODO: remove param
		
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
