package edu.elon.honors.price.maker;

public class TextUtils {
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
}
