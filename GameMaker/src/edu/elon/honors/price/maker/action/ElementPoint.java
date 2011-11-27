package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorPoint;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.TextUtils;

public class ElementPoint extends Element {
	
	private SelectorPoint selectorPoint;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VALUE;
	}
	
	public ElementPoint(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorPoint.getX());
		params.addParam(selectorPoint.getY());
	}
	
	@Override
	protected int readParameters(Parameters params, int index) {
		selectorPoint.setPoint(params.getInt(index), 
				params.getInt(index + 1));
		return index + 2;
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setFocusableInTouchMode(true);
		selectorPoint = new SelectorPoint(context);
		layout.addView(selectorPoint);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		TextUtils.addColoredText(sb, selectorPoint.getX(), color);
		sb.append(", ");
		TextUtils.addColoredText(sb, selectorPoint.getY(), color);
		sb.append(")");
		return sb.toString();
	}
}

