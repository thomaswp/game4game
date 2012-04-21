package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.widget.LinearLayout;

public class ElementSwitch extends Element {

	private SelectorSwitch selectorSwitch;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
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
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		String name = game.switchNames[selectorSwitch.getSwitchId()];
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}
}
