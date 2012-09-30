package edu.elon.honors.price.maker.action;

import java.io.StringWriter;

import org.xml.sax.Attributes;

public class ChoiceWriter extends ExpandableWriter {

	
	public ChoiceWriter(String name, StringWriter writer, String qName, Attributes atts, int tab) {
		super(writer, qName, atts, tab);
		this.name = name;
	}
	
}
