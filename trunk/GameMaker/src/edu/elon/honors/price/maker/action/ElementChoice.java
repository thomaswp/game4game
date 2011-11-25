package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;

public class ElementChoice extends Element {

	private String text;
	
	public String getText() {
		return text;
	}
	
	public ElementChoice(Attributes atts) {
		super(atts);
		text = atts.getValue("text");
	}
}
