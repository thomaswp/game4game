package edu.elon.honors.price.maker.action;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.Attributes;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xpath.internal.compiler.Keywords;

public class ActionFragmentWriter extends Writer {

	protected String name;
	
	protected LinkedList<String> variableNames = 
			new LinkedList<String>();
	protected LinkedList<String> toInstantiate = 
			new LinkedList<String>();
	
	protected LinkedList<String> javadoc =
			new LinkedList<String>(); 
	
	protected String radioName;
	protected String currentChoice;
	
	protected int choiceNum;
	protected boolean choiceWriterHeaded;

	protected ExpandableWriter childWriter;
	
	protected boolean ended;
	public boolean isEnded() {
		return ended;
	}
	
	/**
	 * Maps an element name to the data type that is placed
	 * into parameters at that element's index. Defaults
	 * to Parameters if not in this list.
	 */
	protected final static HashMap<String, String> ELEMENT_TYPES =
			new HashMap<String, String>();
	static {
		ELEMENT_TYPES.put("switch", "Switch");
		ELEMENT_TYPES.put("variable", "Variable");
		ELEMENT_TYPES.put("actorClass", "int");
		ELEMENT_TYPES.put("objectClass", "int");
		ELEMENT_TYPES.put("button", "int");
		ELEMENT_TYPES.put("joystick", "int");
	}
	
	protected final static String[] IGNORE_ELEMENTS = new String[] {
		"text", "description"
	};
	
	protected final static LinkedList<String> KEYWORDS = 
			new LinkedList<String>();
	static {
		KEYWORDS.add("switch");
		KEYWORDS.add("if");
		KEYWORDS.add("case");
		KEYWORDS.add("for");
		KEYWORDS.add("new");
		KEYWORDS.add("true");
		KEYWORDS.add("boolean");
		//TODO: make a complete implementation
	}
		
	public ActionFragmentWriter(StringWriter writer, String qName, Attributes atts) {
		super(writer);
		name = capitalize(nameVariable(qName, atts, "", false));
	}
	
	protected String nameVariable(String qName, Attributes atts) {
		return nameVariable(qName, atts, "", true);
	}
	
	protected String nameVariable(String qName, Attributes atts, 
			String prefix) {
		return nameVariable(qName, atts, prefix, true);
	}
	
	protected String nameVariable(String qName, Attributes atts, 
			String prefix, boolean save) {
		String name = atts.getValue("name");
		if (name == null) name = atts.getValue("text");
		if (name == null) name = qName;
		
		name = camel(prefix + " " + name, false);
		
		//TODO: checking 'save' is a dirty implementation for if...
		if (save && KEYWORDS.contains(name)) {
			char c = name.charAt(0);
			if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
				name = "an" + capitalize(name);
			} else {
				name = "a" + capitalize(name);
			}
		}
		
		int counter = 1;
		String nName = name;
		while (variableNames.contains(nName)) {
			counter++;
			nName = name + counter;
		}
		if (save) {
			variableNames.add(nName);
		}
		
		return nName;
	}
	
	protected String getSuperclass() {
		return "ActionFragment";
	}
	
	@Override
	public void writeHeader() {
		writeLn("public class %s extends %s {", name, getSuperclass());
		tab++;
		javadoc.add("<ul>");
	}
	
	@Override
	public void writeElement(String qName, Attributes atts) {
		String originamQName = qName;
		qName = qName.toLowerCase();
		
		if (childWriter != null) {
			if (childWriter instanceof ChoiceWriter && !choiceWriterHeaded) {
				String varName = deCapitalize(childWriter.name);
				writeVariable(childWriter.name, varName);
				writeDeferred("if (%s) %s.readParams(iterator);", currentChoice, varName);
				childWriter.writeHeader();
				choiceWriterHeaded = true;
				toInstantiate.add(childWriter.name);
			}
			childWriter.writeElement(originamQName, atts);
			return;
		}
		
		if (qName.equals("radio")) {
			radioName = nameVariable(originamQName, atts);
			writeDeferred("int %s = iterator.getInt();", radioName);
			javadoc.add(String.format("<li><b>&lt;radio&gt;</b> %s</i>:</li><ul>", radioName));
			return;
		}
		
		if (qName.equals("choice")) {
			currentChoice = nameVariable(qName, atts, radioName);
			String choiceName = currentChoice + "Data";
			String className = capitalize(choiceName);
			writeVariable("boolean", currentChoice);
			writeDeferred("%s = %s == %d;", currentChoice, radioName, choiceNum);
			choiceNum++;
			
			javadoc.add(String.format("<li>%s:</li>", currentChoice));
			
			childWriter = new ChoiceWriter(className, writer, qName, atts, tab);
			choiceWriterHeaded = false;
			return;
		}
		
		if (qName.equals("group")) {
			String varName = nameVariable(originamQName, atts);
			String className = capitalize(varName);
			writeVariable(className, varName);
			
			childWriter = new GroupWriter(writer, qName, atts, tab);
			childWriter.writeHeader();
			
			toInstantiate.add(varName);
			writeDeferred("%s.readParams(iterator.getParameters().iterator());", varName);
			
			javadoc.add(String.format("<li><b>&lt;group&gt;</b> %s:</li>", varName));
			
			return;
		}
		
		for (String ignore : IGNORE_ELEMENTS) {
			if (qName.equals(ignore)) return;
		}
		
		String type = "Parameters";
		for (String key : ELEMENT_TYPES.keySet()) {
			if (qName.equalsIgnoreCase(key)) {
				type = ELEMENT_TYPES.get(key);
			}
		}
		String varName = nameVariable(originamQName, atts);
		writeJavadoc("Type: <b>&lt;%s&gt;</b>", originamQName);
		writeVariable(type, varName);
		writeDeferred("%s = iterator.get%s();", varName, capitalize(type));
		
		for (String read : GameStateWriter.READ_TYPES.keySet()) {
			if (read.equalsIgnoreCase(qName)) {
				String rType = GameStateWriter.READ_TYPES.get(read);
				writeLn("public %s read%s(GameState gameState) throws ParameterException {", 
						rType, capitalize(varName));
				tab++;
				writeLn("return gameState.read%s(%s);", 
						capitalize(read), varName);
				tab--;
				writeLn("}");
				break;
			}
		}
		
		javadoc.add(String.format("<li><b>&lt;%s&gt;</b> %s</li>", originamQName, varName));
	}
	
	public void endElement(String qName) {
		if (childWriter instanceof ChoiceWriter && !choiceWriterHeaded) childWriter = null;
		
		if (childWriter != null) {
			childWriter.endElement(qName);
			if (childWriter.isEnded()) {
				childWriter.writeFooter();
				javadoc.addAll(childWriter.javadoc);
				childWriter = null;
				writeLn();
				
			}
			return;
		}
		
		if (qName.equals("radio")) {
			writeDeferred("");
			javadoc.add("</ul>");
			radioName = null;
			choiceNum = 0;
		}
	}

	@Override
	public void writeFooter() {
		writeConstructor();
		writeReadParams();
		javadoc.add("</ul>");
		writeJavadoc();
		
		tab--;
		writeLn("}");
	}
	
	protected void writeJavadoc() {
		startJavadocBlock();
		for (String line : javadoc) writeLn(line);
		endJavadockBlock();
		writeConstant("String", "JAVADOC", quote(""));
	}
	
	protected void writeReadParams() {
		writeLn();
		writeLn("public void readParams(Iterator iterator) {");
		tab++;
		
		for (String line : defferedLines) {
			writeLn(line);
		}
		
		tab--;
		writeLn("}");
	}
	
	protected void writeConstructor() {
		if (toInstantiate.size() == 0) return;
		writeLn();
		
		writeLn("public %s() {", name);
		tab++;
		
		for (String s : toInstantiate) {
			writeAssignment(deCapitalize(s), String.format("new %s()", capitalize(s)));
		}
		
		tab--;
		writeLn("}");
	}
}
