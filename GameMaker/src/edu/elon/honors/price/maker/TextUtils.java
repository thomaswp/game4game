package edu.elon.honors.price.maker;

public class TextUtils {

	public static final String COLOR_VARIABLE = "#00CC00";
	public static final String COLOR_MODE = "#FFCC00";
	public static final String COLOR_VALUE = "#5555FF";
	public static final String COLOR_ACTION = "#8800FF";
	
	public static void addColoredText(StringBuilder sb, int text, String color) {
		addColoredText(sb, "" + text, color);
	}

	public static void  addColoredText(StringBuilder sb, String text, String color) {
		if (color == null) {
			sb.append(text);
			return;
		}
		sb.append("<font color='")
		.append(color)
		.append("'>")
		.append(text)
		.append("</font>");
	}

	public static void addColoredText(StringBuilder sb, float text,
			String color) {
		addColoredText(sb, String.format("%.02f", text), color);
		
	}
	
	public static String getColoredText(String text, int color) {
		return getColoredText(text, getColorString(color));
	}
	
	public static String getColoredText(String text, String color) {
		StringBuilder sb = new StringBuilder();
		addColoredText(sb, text, color);
		return sb.toString();
	}
	
	public static String getColorString(int color) {
		return "#" + Integer.toHexString(color).substring(2);
	}
}
