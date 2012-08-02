package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.LinkedList;

public class Behavior implements Serializable {
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
