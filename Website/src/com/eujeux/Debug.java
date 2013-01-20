package com.eujeux;


public class Debug {
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
			write(String.format(format, args));
		} catch (Exception e) {
			write(e);
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
		String tag = "Game";
		try
		{
			throw new Exception("Who called me?");
		}
		catch( Exception e )
		{
			int i = 1;
			while (e.getStackTrace()[i].getClassName().equals(Debug.class.getName()) &&
					e.getStackTrace()[i].getMethodName().equals("write")) i++;
			tag += ": " +
			e.getStackTrace()[i].getClassName() + 
			"." +
			e.getStackTrace()[i].getMethodName() + 
			"()" ;
		}
		System.out.println(msg);
	}
}
