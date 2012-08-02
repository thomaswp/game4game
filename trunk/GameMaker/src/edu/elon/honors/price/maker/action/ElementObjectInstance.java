package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.action.EventContext.Scope;
import edu.elon.honors.price.maker.action.EventContext.TriggerType;

public class ElementObjectInstance extends ElementMulti {

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	@Override
	protected String getGroupName() {
		return "Object";
	}

	public ElementObjectInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		Option[] options = new Option[] {
			new OptionElement("the exact object", 
					new ElementExactObjectInstance(attributes, context)),
			new OptionEmpty(context, "the triggering object", 
					"the triggering object"),
			new OptionEmpty(context, "the created object",
					"the created object")
		};
		
		options[0].visible = 
			eventContext.getScope() == Scope.MapEvent;
		options[1].enabled = 
			eventContext.hasTrigger(TriggerType.ObjectTrigger);
		options[2].visible = 
			eventContext.getScope() == Scope.ObjectBehavior;
		
		return options;
	}

}
