package edu.elon.honors.price.maker.action;

import java.util.LinkedList;

import org.xml.sax.Attributes;

import edu.elon.honors.price.maker.action.EventContext.Scope;

import android.content.Context;

public class ElementBody extends ElementMulti {

	public ElementBody(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected String getGroupName() {
		return "Actor/Object";
	}

	@Override
	protected Option[] getOptions() {
		Option[] options = new Option[] {
				new OptionElement("The specific actor", 
						new ElementExactActorInstance(attributes, context)),
				new OptionElement("Any actor of the class", 
						new ElementActorClass(attributes, context)),
				new OptionElement("The specific object", 
						new ElementExactObjectInstance(attributes, context)),
				new OptionElement("Any object of the class", 
						new ElementObjectClass(attributes, context)),
				new OptionEmpty(context, "this actor", "this actor"),
				new OptionEmpty(context, "this object", "this object")
		};
		
		options[4].visible = eventContext.getScope() == Scope.ActorBehavior;
		options[5].visible = eventContext.getScope() == Scope.ObjectBehavior;
		
		return options;
	}


}
