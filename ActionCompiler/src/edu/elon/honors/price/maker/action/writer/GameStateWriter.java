package edu.elon.honors.price.maker.action.writer;

import java.io.StringWriter;
import java.util.HashMap;

import org.xml.sax.Attributes;

public class GameStateWriter extends Writer {

	/**
	 * Maps an element to to "read" type of the element,
	 * meaning the usable form at runtime, such as 
	 * an ActorBody instead of the index of that body.
	 */
	protected final static HashMap<String, String> READ_TYPES = 
			new HashMap<String, String>();
	static {
		READ_TYPES.put("switch", "boolean");
		READ_TYPES.put("variable", "int");
		READ_TYPES.put("actorInstance", "ActorBody");
		READ_TYPES.put("objectInstance", "ObjectBody");
		READ_TYPES.put("actorClass", "ActorClass");
		READ_TYPES.put("objectClass", "ObjectClass");
		READ_TYPES.put("point", "Point");
		READ_TYPES.put("vector", "Vector");
		READ_TYPES.put("joystick", "JoyStick");
		READ_TYPES.put("button", "Button");
		READ_TYPES.put("number", "int");
		READ_TYPES.put("boolean", "boolean");
		READ_TYPES.put("region", "com.twp.core.game.Rect");
		READ_TYPES.put("ui", "UIControl");
		READ_TYPES.put("event", "Event");
	}
	
	public GameStateWriter(StringWriter writer) {
		super(writer);
	}

	@Override
	public void writeHeader() {
		writeLn("package %s;", ScriptableWriter.PACKAGE);
		writeLn();
		
		for (String s : ScriptableWriter.IMPORTS) {
			writeLn("import %s;", s);
		}
		
		writeLn("@SuppressWarnings(%s)", quote("unused"));
		writeLn("public interface GameState {");
		tab++;
		for (String read : READ_TYPES.keySet()) {
			String type = READ_TYPES.get(read);
			String paramType = "Parameters";
			for (String key : ActionFragmentWriter.ELEMENT_TYPES.keySet()) {
				if (read.equalsIgnoreCase(key)) {
					paramType = ActionFragmentWriter.ELEMENT_TYPES.get(key);
				}
			}
			writeLn("public %s read%s(%s params) throws ParameterException;", type, capitalize(read),
					paramType);
		}
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
