package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.maker.action.EventContext.Scope;

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
					new ElementVariablePoint(attributes, context))
		};
		
		options[0].visible = 
			eventContext.getScope() == Scope.MapEvent;
		
		return options;
	}
}

