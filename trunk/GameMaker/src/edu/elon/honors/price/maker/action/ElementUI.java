package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;

public class ElementUI extends ElementMulti {

	public ElementUI(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		return new Option[] {
			new OptionElement("the button", 
					new ElementButton(attributes, context)),
			new OptionElement("the joystick", 
					new ElementJoystick(attributes, context))
		};
	}

}
