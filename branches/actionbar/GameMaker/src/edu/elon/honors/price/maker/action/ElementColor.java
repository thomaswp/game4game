package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.Screen;
import edu.elon.honors.price.maker.SelectorColor;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ElementColor extends Element {

	SelectorColor selectorColor;
	
	public ElementColor(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void readParameters(Iterator params) {
		selectorColor.setColor(params.getInt());
	}
	
	@Override
	public void addParameters(Parameters params) {
		params.addParam(selectorColor.getColor());
	}
	
	
	@Override
	public void genView() {
		selectorColor = new SelectorColor(context);
		LinearLayout layout = new LinearLayout(context);
		LayoutParams lps = new LayoutParams(Screen.dipToPx(200, context), 
				LayoutParams.WRAP_CONTENT);
		layout.addView(selectorColor, lps);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		int color = selectorColor.getColor();
		String colorString = TextUtils.getColorString(color);
		return "[" + TextUtils.getColoredText("color", colorString) + "]";
	}
}
