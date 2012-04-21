package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;

public class ElementBoolean extends ElementMulti {

	private String onString, offString;
	
	public ElementBoolean(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		String on = atts.getValue("on");
		if (on != null && on.length() > 0) {
			onString = on;
		} else {
			onString = "On";
		}
		String off = atts.getValue("off");
		if (off != null && off.length() > 0) {
			offString = off;
		} else {
			offString = "Off";
		}
	}

	@Override
	protected Option[] getOptions() {
		return new Option[] {
			new OptionEmpty(context, onString, onString),
			new OptionEmpty(context, offString, offString),
			new OptionElement("a Switch's value",
					new ElementSwitch(attributes, context))
		};
	}

}
