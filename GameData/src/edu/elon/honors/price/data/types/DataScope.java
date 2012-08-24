package edu.elon.honors.price.data.types;

public enum DataScope {
	Global,
	Local,
	Param;
	
	public int toInt() {
		return ordinal();
	}
	
	public static DataScope fromInt(int i) {
		return values()[i];
	}
}
