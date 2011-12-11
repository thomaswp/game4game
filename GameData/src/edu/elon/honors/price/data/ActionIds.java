package edu.elon.honors.price.data;

public class ActionIds {
	
	public static final String[] ACTION_NAMES = new String[] {
		"Set Switch",
		"Set Variable",
		"Create Actor",
		"Show Debug Box",
		"Move Actor",
		"Trigger Actor Behavior",
		"Set Hero On/Off Ladder",
		"If..."
	};
	
	/**
	 * P0: Specific | Range
	 * P1: Id/(Start Range, End Range)
	 * P2: Set | Toggle
	 * P3: Value | Switch | Random/-
	 * P4: Value/Id/-
	 */
	public static final int ID_SET_SWITCH = 0;
	/**
	 * P0: Specific | Range
	 * P1: Id/(Start Range, End Range)
	 * P2: Set | Add | Subtract | Multiply | Divide | Mod
	 * P3: Value | Variable | Random | Actor
	 * P4: Value/Id/(Lower Bound, Upper Bound)/Id
	 * P5: .../(X | Y)
	 */
	public static final int ID_SET_VARIABLE = 1;
	
	/**
	 * P0: Id
	 * P1: Value | Variable
	 * P2: X Value/Id 
	 * P3: Y Value/Id
	 * P4: Left | Right
	 */
	public static final int ID_CREATE_ACTOR = 2;
	/**
	 * P0: Id
	 * P1: Value | Variable
	 * P2: X Value/Id
	 * P4: Y Value/Id
	 * P5: Left | Right | Retain
	 */
	public static final int ID_MOVE_ACTOR = 4;
	/**
	 * P0: Id
	 * P1: Behavior
	 */
	public static final int ID_ACTOR_BEHAVIOR = 5;
	
	/**
	 * P0: On | Off
	 */
	public static final int ID_HERO_SET_LADDER = 6;
	
	/**
	 * P0: Text | Switch | Variable
	 * P1: Text/Id/Id
	 */
	public static final int ID_DEBUG_BOX = 3;
	
	/**
	 * 
	 */
	public static final int ID_IF = 7;
}
