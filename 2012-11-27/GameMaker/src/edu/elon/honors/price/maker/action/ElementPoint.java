package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.maker.action.EventContext.Scope;
import edu.elon.honors.price.maker.action.EventContext.TriggerType;

import android.content.Context;

public class ElementPoint extends ElementMulti {

	@Override
	protected String getGroupName() {
		return "Point";
	}
	
	public ElementPoint(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected Option[] getOptions() {
		Option[] options =  new Option[] {
			new OptionElement("the exact point", 
					new ElementExactPoint(attributes, context)),
			new OptionElement("the variable point",
					new ElementVariablePoint(attributes, context)),
			new OptionEmpty(context, "the triggering point", "the triggering point")
		};
		
		options[0].visible = 
			eventContext.getScope() == Scope.MapEvent;
		options[2].enabled =
				eventContext.hasTrigger(TriggerType.UITrigger) ||
				eventContext.hasTrigger(TriggerType.ActorTrigger) ||
				eventContext.hasTrigger(TriggerType.ObjectTrigger);
		
		return options;
	}
}

