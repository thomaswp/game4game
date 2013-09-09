package edu.elon.honors.price.game;

public class Formatter {
	public static String format(String format, Object... args) {
		if (impl != null) {
			return impl.format(format, args);
		}
		return "[No String Formatter]";
	}
	
	public static Impl impl;
	
	public interface Impl {
		String format(String format, Object... args);
	}
}
