package edu.elon.honors.price.game;

public class DataDebug {
	public static void write(long x) {
		write("" + x);
	}
	
	public static void write(int x) {
		write("" + x);
	}
	
	public static void write(Object o) {
		write(o == null ? "null" : o.toString());
	}

	public static void write(float x) {
		write("" + x);
	}

	public static void write(String format, Object... args) {
		try {
			write(Formatter.format(format, args));
		} catch (Exception e) {
			write(e);
		}
	}
	
	public static void writeFrame(String format, Object... args) {
		long time = System.currentTimeMillis() % 1000;
		if (time < 1000 / 40) {
			write(format, args);
		}
	}
	
	public static void write(Exception e) {
		e.printStackTrace();
	}
	
	/**
	 * A method to write specially formatted debug text.
	 * 
	 * @param text The text to be written.
	 */
	public static void write(String text) {
		String msg = "---{" + text + "}---";
		System.out.println(msg);
	}
}
