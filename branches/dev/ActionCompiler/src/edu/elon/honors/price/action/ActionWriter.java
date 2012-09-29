package edu.elon.honors.price.action;

import java.io.PrintWriter;

import org.xml.sax.Attributes;

public class ActionWriter extends ActionFragmentWriter {
	
	private final static String[] IMPORTS = new String[] {
			"edu.elon.honors.price.action.*",
			"edu.elon.honors.price.data.*"
	};
	
	public String fileName;
	protected int id;
	
	public ActionWriter(String qName, Attributes atts) {
		super(qName, atts);
		fileName = camel(name, true);
		id = Integer.parseInt(atts.getValue("id"));
	}

	@Override
	public void writeHeader() {
		for (String s : IMPORTS) {
			writeLn("import %s;", s);
		}
		writeLn();
		
		writeLn("public class %s extends Action {", camel(name, true));
		tab++;
		writeConstant("String NAME = %s;", quote(name));
		writeConstant("int ID = %d;", id);
		
		writeLn();
		
		tab--;
		writeLn("}");
	}
	
	@Override
	public void writeElement(String qName, Attributes atts) {
		
	}
}
