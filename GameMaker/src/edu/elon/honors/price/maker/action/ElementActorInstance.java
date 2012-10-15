package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.maker.action.EventContext.Scope;
import edu.elon.honors.price.maker.action.EventContext.TriggerType;

import android.content.Context;

public class ElementActorInstance extends ElementMulti {

	@Override
	protected String getGroupName() {
		return "Actor";
	}
	
	public ElementActorInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected Option[] getOptions() {
		Option[] options = new Option[] {
			new OptionElement("the exact actor", 
					new ElementExactActorInstance(attributes, context)),
			new OptionEmpty(context, "the triggering actor", 
					"the triggering actor"),
			new OptionEmpty(context, "the last created actor", 
					"the last created actor"),
			new OptionEmpty(context, "this actor", "this actor")
		};
		
		options[0].visible = 
			eventContext.getScope() == Scope.MapEvent;
		options[1].enabled = 
			eventContext.hasTrigger(TriggerType.ActorTrigger) ||
			eventContext.hasTrigger(TriggerType.RegionTrigger);
		options[3].visible = 
			eventContext.getScope() == Scope.ActorBehavior;
		
		return options;
	}

}
