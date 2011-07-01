package edu.elon.honors.price.data;

public class PlatformEventIds {
	/**
	 * P0: Specific | Range
	 * P1: Id/(Start Range, End Range)
	 * P2: Set | ?
	 * P3: Value | Switch | Random
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
	 * P1: ValueX | VariableX
	 * P2: Value/Id 
	 * P3: ValueY | VariableY
	 * P4: Value/Id
	 * P5: Left | Right
	 */
	public static final int ID_CREATE_ACTOR = 2;
	
	/**
	 * P0: Text | Switch | Variable
	 * P1: Text/Id/Id
	 */
	public static final int ID_DEBUG_BOX = 3;
}
