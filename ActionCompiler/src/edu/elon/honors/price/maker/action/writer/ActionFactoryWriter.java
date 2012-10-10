package edu.elon.honors.price.maker.action.writer;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;

public class ActionFactoryWriter extends Writer {

	List<ActionWriter> actions;
	
	public ActionFactoryWriter(StringWriter writer, List<ActionWriter> actions) {
		super(writer);
		this.actions = actions;
	}

	@Override
	public void writeHeader() {
		writeLn("package %s;", ActionWriter.PACKAGE);
		writeLn();
		
//		for (String s : ActionWriter.IMPORTS) {
//			writeLn("import %s;", s);
//		}
		
		writeLn("public class ActionFactory {");
		tab++;
		
		writeLn("public final static int[] ACTION_IDS = new int[] {");
		tab++;
		for (ActionWriter action : actions) {
			writeLn("%d,", action.id);
		}
		tab--;
		writeLn("};");
		
		LinkedList<String> names = new LinkedList<String>();
		LinkedList<String> categories = new LinkedList<String>();
		for (ActionWriter action : actions) {
			int id = action.id;
			while (names.size() <= id) {
				names.add(null);
				categories.add(null);
			} 
			if (names.get(id) != null) throw new RuntimeException(
					"No two Actions may have the same ID!");
			names.set(id, action.readableName);
			categories.set(id, action.category);
		}
		
		writeLn("public final static String[] ACTION_NAMES = new String[] {");
		tab++;
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			writeLn("%s,", quote(name == null ? "" : name));
		}
		tab--;
		writeLn("};");
		
		writeLn("public final static String[] ACTION_CATEGORIES = new String[] {");
		tab++;
		for (int i = 0; i < categories.size(); i++) {
			String category = categories.get(i);
			writeLn("%s,", quote(category == null ? "Misc" : category));
		}
		tab--;
		writeLn("};");
		
		writeLn("public static ActionInstance getInstance(int id) {");
		tab++;
		for (ActionWriter action : actions) {
			writeLn("if (id == %s.ID) return new %s();", 
					action.name, action.name);
		}
		writeLn("return null;");
		tab--;
		writeLn("}");
		
		writeLn("public static java.util.LinkedList<Class<?>> getInterpreters() {");
		tab++;
		writeLn("java.util.LinkedList<Class<?>> classes = " +
				"new java.util.LinkedList<Class<?>>();");
		for (ActionWriter action : actions) {
			writeLn("try {");
			tab++;
			writeLn("classes.add(Class.forName(\"%s.%s\"));", ActionWriter.PACKAGE, 
					action.name.replace("Action", "Interpreter"));
			tab--;
			writeLn("} catch (Exception e) { }");
		}
		writeLn("return classes;");
		tab--;
		writeLn("}");
		
		tab--;
		writeLn("}");
	}

	@Override
	public void writeElement(String qName, Attributes atts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endElement(String qName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeFooter() {
		// TODO Auto-generated method stub
		
	}

}
