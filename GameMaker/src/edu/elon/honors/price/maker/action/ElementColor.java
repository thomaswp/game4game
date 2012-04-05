package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.SelectorColor;

import android.content.Context;
import android.graphics.Color;

public class ElementColor extends Element {

	SelectorColor selectorColor;
	
	public ElementColor(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	public int readParameters(Parameters params, int index) {
		selectorColor.setColor(params.getInt(index));
		return index + 1;
	}
	
	@Override
	public void addParameters(Parameters params) {
		params.addParam(selectorColor.getColor());
	}
	
	
	@Override
	public void genView() {
		selectorColor = new SelectorColor(context);
		main = selectorColor;
	}

	@Override
	public String getDescription(PlatformGame game) {
		int color = selectorColor.getColor();
		return String.format("#%2X%2X%2X", Color.red(color), Color.green(color),
				Color.blue(color));
	}
}
