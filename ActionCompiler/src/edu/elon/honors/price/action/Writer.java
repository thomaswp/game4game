package edu.elon.honors.price.action;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.xml.sax.Attributes;

public abstract class Writer {
	StringWriter writer;
	
	protected int tab;
	
	public Writer() {
	}
	
	public void writeHeader(StringWriter writer) {
		this.writer = writer;
		writeHeader();
	}
	
	protected abstract void writeHeader();
	public abstract void writeElement(String qName, Attributes atts);
	public abstract void writeFooter();
	
	
	public static String camel(String name) {
		return camel(name, false);
	}
	
	public static String camel(String name, boolean caps) {
		String[] parts = name.split("\\s");
		String s = "";
		for (String part : parts) {
			if (part.length() > 0) {
				if (part.length() > 1) {
					char start = part.charAt(0);
					part = Character.toUpperCase(start) + part.substring(1);
				}
				s += part;
				caps = true;
			}
		}
		return s;
	}
	
	protected String quote(String text) {
		return String.format("\"%s\"", text);
	}
	
	protected void tab() {
		for (int i = 0; i < tab; i++) {
			writer.append("\t");
		}
	}
	
	protected void writeConstant(String format, Object... args) {
		writeLn("public static final " + String.format(format, args));
	}
	
	protected void writeConstant(String var) {
		writeLn("public static final " + var);
	}
	
	protected void writeLn() {
		writeLn("");
	}
	
	protected void writeLn(String format, Object... args) {
		writeLn(String.format(format, args));
	}
	
	protected void writeLn(String text) {
		tab();
		writer.append(text + "\n");
	}
}
