package edu.elon.honors.price.maker.action;

import java.io.StringWriter;

import org.xml.sax.Attributes;

public class ExpandableWriter extends ActionFragmentWriter {

	private String qName;
	
	public ExpandableWriter(StringWriter writer, String qName, Attributes atts, int tab) {
		super(writer, qName, atts);
		this.qName = qName;
		this.tab = tab;
	}

	@Override
	public void endElement(String qName) {
		if (childWriter == null) {
			if (this.qName.equalsIgnoreCase(qName)) {
				ended = true;
				return;
			}
		}
		super.endElement(qName);
	}
}
