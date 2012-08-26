package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;

public class ElementBoolean extends ElementMulti {

	private String onString = "On", offString = "Off";
	
	@Override
	protected String getGroupName() {
		return onString + "/" + offString;
	}
	
	public ElementBoolean(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		String on = atts.getValue("on");
		if (on != null && on.length() > 0) {
			onString = on;
		} else {
			onString = "On";
		}
		String off = atts.getValue("off");
		if (off != null && off.length() > 0) {
			offString = off;
		} else {
			onString = "Off";
		}
	}

	@Override
	protected Option[] getOptions() {
		return new Option[] {
			new OptionEmpty(context, onString, onString, 
					TextUtils.COLOR_VALUE),
			new OptionEmpty(context, offString, offString, 
					TextUtils.COLOR_VALUE),
			new OptionElement("a Switch's value",
					new ElementSwitch(attributes, context))
		};
	}

}
