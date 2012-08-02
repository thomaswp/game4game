package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;

public class ElementNumber extends ElementMulti {

	@Override
	protected String getGroupName() {
		return "Number";
	}
	
	public ElementNumber(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		return new Option[] {
				new OptionElement("the exact value", 
						new ElementExactNumber(attributes, context)),
				new OptionElement("the variable",
						new ElementVariable(attributes, context))
		};
	}

}
