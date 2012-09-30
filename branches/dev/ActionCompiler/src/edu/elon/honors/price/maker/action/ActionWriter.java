package edu.elon.honors.price.maker.action;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.xml.sax.Attributes;

public class ActionWriter extends ActionFragmentWriter {
	
	private final static String[] IMPORTS = new String[] {
			"edu.elon.honors.price.maker.action.*",
			"edu.elon.honors.price.data.types.*",
			"edu.elon.honors.price.data.Event.Parameters.Iterator",
			"edu.elon.honors.price.data.Event.Parameters"
	};
	
	public String fileName;
	protected String readableName;
	protected int id;
	protected String category;
	
	@Override
	protected String getSuperclass() {
		return "Action";
	}
	
	public ActionWriter(StringWriter writer, String qName, Attributes atts) {
		super(writer, qName, atts);
		name = "Action" + name;
		fileName = camel(name, true);
		readableName = atts.getValue("name");
		id = Integer.parseInt(atts.getValue("id"));
		category = atts.getValue("category");
	}

	@Override
	public void writeHeader() {
		//writeLn("package edu.elon.honors.price.maker.action;");
		writeLn();
		
		for (String s : IMPORTS) {
			writeLn("import %s;", s);
		}
		writeLn();
		
		super.writeHeader();
		
		writeConstant("String", "NAME", quote(readableName));
		writeConstant("int", "ID", "" + id);
		writeConstant("String", "CATEGORY", category == null ? "null" : quote(category));
		
		writeLn();
		
	}
	
	@Override
	public void writeFooter() {
		writeJavadoc();
		super.writeFooter();
	}
}
