package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorVector;
import edu.elon.honors.price.maker.TextUtils;
import edu.elon.honors.price.maker.action.EventContext.Scope;
import edu.elon.honors.price.maker.action.EventContext.TriggerType;

public class ElementVector extends ElementMulti {
	
	@Override
	protected String getGroupName() {
		return "Vector";
	}
	
	public ElementVector(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected int getVersion() {
		return 1;
	}
	
	@Override
	protected void upgrade(Parameters params) {
		if (params.version < 1) {
			if (params.getInt(0) > 0) {
				params.set(0, params.getInt(0) + 1);
			}
		}
	}

	@Override
	protected Option[] getOptions() {
		final SelectorVector selectorVector = new SelectorVector(context);
		Option optionExact = new Option(selectorVector, "the exact Vector") {
			@Override
			public void writeDescription(StringBuilder sb, PlatformGame game) {
				sb.append("[");
				TextUtils.addColoredText(sb, selectorVector.getVectorX(), 
						DatabaseEditEvent.COLOR_VALUE);
				sb.append(", ");
				TextUtils.addColoredText(sb, selectorVector.getVectorY(), 
						DatabaseEditEvent.COLOR_VALUE);
				sb.append("]");
			}
			
			@Override
			public void readParams(Iterator params) {
				final float x = params.getFloat();
				final float y = params.getFloat();
				selectorVector.post(new Runnable() {
					@Override
					public void run() {
						selectorVector.setVector(x, y);
					}
				});
			}
			
			@Override
			public void addParams(Parameters params) {
				params.addParam(selectorVector.getVectorX());
				params.addParam(selectorVector.getVectorY());
			}
		};
		
		Option[] options = new Option[] {
				optionExact,
				new OptionEmpty(context, "a random vector",
						"a random vector"),
				new OptionEmpty(context, "the triggering vector", 
						"the triggering vector"),
				new OptionElement("a joystick's vector",
						new ElementJoystick(attributes, context))
		};
		
		options[2].enabled = eventContext.hasTrigger(TriggerType.UITrigger);
		options[3].visible = eventContext.getScope() == Scope.MapEvent;
		
		return options;
	}
}

