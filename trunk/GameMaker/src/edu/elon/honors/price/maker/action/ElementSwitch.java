package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.game.Game;
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
		params.addParam(selectorSwitch.getSwitch());
		Switch s = selectorSwitch.getSwitch();
		Game.debug("Write: %d, %s", s.id, s.scope.toString());
	}
	
	@Override
	protected void readParameters(Iterator params) {
		Switch s = params.getSwitch();
		selectorSwitch.setSwitch(s);
		Game.debug("Read: %d, %s", s.id, s.scope.toString());
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorSwitch = new SelectorSwitch(context);
		selectorSwitch.setEventContext(eventContext);
		layout.addView(selectorSwitch);
		selectorSwitch.setWidth(200);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		String name = selectorSwitch.getText().toString();
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}
}
