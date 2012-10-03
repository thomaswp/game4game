package edu.elon.honors.price.maker.action.writer;

import java.io.StringWriter;
import java.util.List;

import org.xml.sax.Attributes;

public class ActionFactoryWriter extends Writer {

	List<String> classes;
	
	public ActionFactoryWriter(StringWriter writer, List<String> classes) {
		super(writer);
		this.classes = classes;
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
		writeLn("public static ActionInstance getInstance(int id) {");
		tab++;
		for (String c : classes) {
			writeLn("if (id == %s.ID) return new %s();", c, c);
		}
		writeLn("return null;");
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
