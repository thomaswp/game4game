package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;

public class ElementPoint extends ElementMulti {

	public ElementPoint(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected Option[] getOptions() {
		return new Option[] {
			new OptionElement("the exact point", 
					new ElementExactPoint(attributes, context)),
			new OptionElement("the variable point",
					new ElementVariablePoint(attributes, context))
		};
	}
}

