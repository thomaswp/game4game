package com.twp.platform;

public class Globals {
	private static final int SIZE = 1000;
	
	private static Globals instance = new Globals();
	private int[] variables = new int[SIZE];
	private boolean[] switches = new boolean[SIZE];
	
	public static Globals getInstance() {
		return instance;
	}
	
	public static void setInstance(Globals instance) {
		Globals.instance = instance; 
	}
	
	public static int[] getVariables() {
		return instance.variables;
	}
	
	public static boolean[] getSwitches() {
		return instance.switches;
	}
	
}
