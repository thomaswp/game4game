package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import edu.elon.honors.price.maker.DatabaseEditEvent;

public class ElementObjectInstance extends ElementMulti {

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}

	public ElementObjectInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		return new Option[] {
			new OptionElement("the exact object", 
					new ElementExactObjectInstance(attributes, context)),
			new OptionEmpty(context, "the triggering object", 
					"the triggering object"),
			new OptionEmpty(context, "the created object",
					"the created object")
		};
	}

}
