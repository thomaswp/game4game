package edu.elon.honors.price.maker.action.writer;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.xml.sax.Attributes;

public class ActionWriter extends ActionFragmentWriter {
	
	protected final static String[] IMPORTS = new String[] {
			"edu.elon.honors.price.maker.action.*",
			"edu.elon.honors.price.data.*",
			"edu.elon.honors.price.data.types.*",
			"edu.elon.honors.price.data.Event.Parameters.Iterator",
			"edu.elon.honors.price.data.Event.Parameters",
			"com.twp.platform.*",
			"edu.elon.honors.price.physics.*",
			"edu.elon.honors.price.input.*"
	};
	
	protected final static String PACKAGE = "edu.elon.honors.price.maker.action";
	
	public String fileName;
	protected String readableName;
	protected int id;
	protected String category;
	
	@Override
	protected String getSuperclass() {
		return "ActionInstance";
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
		writeLn("package %s;", PACKAGE);
		writeLn();
		
		for (String s : IMPORTS) {
			writeLn("import %s;", s);
		}
		writeLn();

		javadoc.add(String.format("%03d <b><i>%s</i></b> (%s)<br />", id, readableName, category));
		super.writeHeader();
		
		writeConstant("String", "NAME", quote(readableName));
		writeConstant("int", "ID", "" + id);
		writeConstant("String", "CATEGORY", category == null ? "null" : quote(category));
		
		writeLn();
		
		
	}
	
//	@Override
//	public void writeFooter() {
//		writeJavadoc();
//		super.writeFooter();
//	}
}
