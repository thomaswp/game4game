package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;

public class ElementActorInstance extends ElementMulti {

	public ElementActorInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		return new Option[] {
			new OptionElement("the exact actor", 
					new ElementExactActorInstance(attributes, context)),
			new OptionEmpty(context, "the triggering actor", 
					"the triggering actor")
		};
	}

}
