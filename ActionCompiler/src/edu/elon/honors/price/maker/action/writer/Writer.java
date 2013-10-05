package edu.elon.honors.price.maker.action.writer;

import java.io.StringWriter;
import java.util.LinkedList;

import org.xml.sax.Attributes;

public abstract class Writer {
	
	public static final boolean DEBUG = false;
	
	protected StringWriter writer;
	protected LinkedList<String> defferedLines = 
			new LinkedList<String>();
	private boolean writingDeffered;
	private boolean writingJavadoc;
	
	protected int tab;
	
	public Writer(StringWriter writer) {
		this.writer = writer;
	}
	
	public abstract void writeHeader();
	public abstract void writeElement(String qName, Attributes atts);
	public abstract void endElement(String qName);
	public abstract void writeFooter();
	
	
	public static String camel(String name) {
		return camel(name, false);
	}
	
	public static String camel(String name, boolean caps) {
		name = name.replace("\'", "").replace("\"", "")
				.replace("-", " ").replace("_", " ")
				.replace(".", " ");
		LinkedList<String> replace = new LinkedList<String>();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c >= 'a' && c <= 'z') continue;
			if (c >= 'A' && c <= 'Z') continue;
			if (c >= '0' && c <= '9') continue;
			if (c == ' ') continue;
			replace.add("" + c);
		}
		for (String r : replace) {
			name = name.replace(r, "");
		}
		String[] parts = name.split("\\s");
		String s = "";
		for (String part : parts) {
			if (part.length() > 0) {
				if (caps) {
					part = capitalize(part);
				} else {
					part = deCapitalize(part);
				}
				s += part;
				caps = true;
			}
		}
		return s;
	}
	
	public static String capitalize(String name) {
		if (name.length() > 0) {
			char start = name.charAt(0);
			name = Character.toUpperCase(start) + name.substring(1);
		}
		return name;
	}
	
	public static String deCapitalize(String name) {
		if (name.length() > 0) {
			char start = name.charAt(0);
			name = Character.toLowerCase(start) + name.substring(1);
		}
		return name;
	}
	
	protected String quote(String text) {
		return String.format("\"%s\"", text);
	}
	
	protected void tab() {
		for (int i = 0; i < tab; i++) {
			writer.append("\t");
		}
	}
	
	protected void writeConstant(String type, String name, String value) {
		writeLn("public static final %s %s = %s;", type, name, value);
	}
	
	protected void writeConstant(String var) {
		writeLn("public static final " + var);
	}
	
	protected void writeVariable(String type, String name) {
		writeLn("public %s %s;", type, name);
	}
	
	protected void writeAssignment(String lhs, String rhs) {
		writeLn("%s = %s;", lhs, rhs);
	}
	
	protected void writeLn() {
		writeLn("");
	}
	
	@SuppressWarnings("unused")
	protected void writeLn(String format, Object... args) {
		String text = format;
		if (args.length > 0) {
			text = String.format(format, args);
		}
		if (writingJavadoc && !text.equals("/**")) {
			text = " * " + text;
		}
		
		if (writingDeffered) {
			defferedLines.add(text);
		} else {
			tab();
			writer.append(text);
			
			if (!writingJavadoc && DEBUG) {
				int detab = tab * 4 + text.length();
				int tabs = 17 - detab / 4;
				String tag = "";
				for (int i = 0; i < tabs; i++) tag += "\t";
				tag += "// ";
				try {
					throw new RuntimeException();
				} catch (Exception e) {
					
					int i = 1;
					while (e.getStackTrace()[i].getClassName().equals(Writer.class.getName())) i++;
					String className = e.getStackTrace()[i].getClassName();
					String[] split = className.split("\\."); 
					className = split[split.length - 1];
					tag +=
					className + 
					"." +
					e.getStackTrace()[i].getMethodName() + 
					"()" ;
				}
				writer.append(tag);
			}
			
			writer.append("\n");
		}
	}
	
	protected void writeComment(String format, Object... args) {
		writeLn("//" + format, args);
	}
	
	protected void writeJavadoc(String format, Object... args) {
		writeLn("/** " + format + " */", args);
	}
	
	protected void startJavadocBlock() {
		writingJavadoc = true;
		writeLn("/**");
		
	}
	
	protected void endJavadockBlock() {
		writingJavadoc = false;
		writeLn(" */");
	}
	
	protected void writeDeffered() {
		for (String line : defferedLines) {
			writeLn(line);
		}
	}
	
	protected void writeDeferred(String format, Object... args) {
		writingDeffered = true;
		writeLn(format, args);
		writingDeffered = false;
	}
	
	protected abstract class DeferredWriter {
		public DeferredWriter() {
			writingDeffered = true;
			write();
			writingDeffered = false;
		}
		
		public abstract void write();
	}
}
