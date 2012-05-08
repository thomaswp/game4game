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
		"If...",
		"Create Object",
		"Move Object",
		"Set Velocity",
		"Show Debug Message",
		"Point Operation",
		"Change Gravity",
		"UI Action",
		"Destroy Object",
		"Draw to Screen",
		"Loop..."
	};
	
	/**
	 * P0: Specific | Range
	 * P1: Id/(Start Range, End Range)
	 * P2: Set | Toggle
	 * P3: On | Off | Switch | Random/-
	 * P4: -/-/Id/-
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
	 * P0: Actor Class
	 * P1: Point
	 * P2: Left | Right
	 */
	public static final int ID_CREATE_ACTOR = 2;
	
	/**
	 * P0: Var Point X
	 * P1: Var Point Y
	 * P2: Set | Add | Subtract
	 * P3: Point | Vector | Actor | Object
	 * P4: Point/Vector/Actor/Object
	 * P5: -/Magnitude/-/-
	 */
	public static final int ID_POINT_OPERATION = 12;
	
	
	/**
	 * P0: Actor Instance
	 * P1: Point
	 * P2: Left | Right | Retain
	 */
	public static final int ID_MOVE_ACTOR = 4;
	/**
	 * P0: Actor Instance
	 * P1: Behavior
	 */
	public static final int ID_ACTOR_BEHAVIOR = 5;
	
	/**
	 * P0: On | Off
	 */
	public static final int ID_HERO_SET_LADDER = 6;
	
	/**
	 * P0: Object Class
	 * P1: Point
	 */
	public static final int ID_CREATE_OBJECT = 8;
	
	/**
	 * P0: Object Instance
	 * P1: Point
	 */
	public static final int ID_MOVE_OBJECT = 9;
	
	/**
	 * P0: Object Instance
	 */
	public static final int ID_DESTROY_OBJECT = 15;
	
	/**
	 * P0: Text | Switch | Variable
	 * P1: Text/Id/Id
	 */
	public static final int ID_DEBUG_BOX = 3;
	
	/**
	 * P0: Text | Switch | Variable
	 * P1: Text/Id/Id
	 */
	public static final int ID_DEBUG_MESSAGE = 11;
	
	/**
	 * 
	 */
	public static final int ID_IF = 7;
	
	/**
	 * 
	 */
	public static final int ID_LOOP = 17;
	
	/**
	 * P0: Actor or Object
	 * P1: Actor/Object
	 * P2: Vector
	 */
	public static final int ID_SET_VELOCITY = 10;
	
	/**
	 * P0: Vector
	 * P1: Magnitude
	 */
	public static final int ID_CHANGE_GRAVITY = 13;
	
	/**
	 * P0: UI Control
	 * P1: Visibility | Default Behavior
	 * P2: Boolean
	 */
	public static final int ID_UI_ACTION = 14;
	
	/**
	 * P0: Clear | Draw
	 * 
	 */
	public static final int ID_DRAW_TO_SCREEN = 16;
}

