package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.maker.action.EventContext.Scope;
import edu.elon.honors.price.maker.action.EventContext.TriggerType;

import android.content.Context;

public class ElementUI extends ElementMulti {

	@Override
	protected String getGroupName() {
		return "UI Control";
	}
	
	public ElementUI(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		Option[] options = new Option[] {
			new OptionElement("the button", 
					new ElementButton(attributes, context)),
			new OptionElement("the joystick", 
					new ElementJoystick(attributes, context)),
			new ElementMulti.OptionEmpty(context,
					"the triggering UI control",
					"the triggering UI control")
		};
		
		options[0].visible = options[1].visible =
			eventContext.getScope() == Scope.MapEvent;
		options[2].enabled = 
			eventContext.hasTrigger(TriggerType.UITrigger);
		
		return options;
	}
}
