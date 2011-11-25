package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ElementSwitch extends Element {

	private SelectorSwitch selectorSwitch;
	
	public ElementSwitch(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorSwitch.getSwitchId());
	}
	
	@Override
	protected int readParameters(Parameters params, int index) {
		selectorSwitch.setSwitchId(params.getInt(index));
		return index + 1;
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorSwitch = new SelectorSwitch(context);
		layout.addView(selectorSwitch);
		selectorSwitch.setWidth(200);
		main = layout;
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		TextUtils.addColoredText(sb, selectorSwitch.getSwitchId(), 
				DatabaseEditEvent.COLOR_VARIABLE);
		return sb.toString();
	}
}
