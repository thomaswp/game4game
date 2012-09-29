package edu.elon.honors.price.action;

import org.xml.sax.Attributes;

public class ActionFragmentWriter extends Writer {

	protected String name;
	
	public ActionFragmentWriter(String qName, Attributes atts) {
		name = atts.getValue("var");
		if (name == null) name = atts.getValue("name");
		if (name == null) name = atts.getValue("text");
		if (name == null) name = qName;
	}
	
	@Override
	protected void writeHeader() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeElement(String qName, Attributes atts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeFooter() {
		// TODO Auto-generated method stub
		
	}

}
