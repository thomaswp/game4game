package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorUIControl;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;

public class ElementButton extends Element {

	private SelectorUIControl selectorButton;
	
	@Override
	public String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	public ElementButton(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorButton.getSelectedControlId());
	}
	
	@Override
	protected void readParameters(Iterator params) {
		final int id = params.getInt();
		selectorButton.post(new Runnable() {
			@Override
			public void run() {
				selectorButton.setSelectedControlId(id);
			}
		});
	}

	@Override
	public void genView() {
		selectorButton = new SelectorUIControl(context, 
				SelectorUIControl.CONTROL_BUTTON);
		main = selectorButton;
	}
	
	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		String name = game.uiLayout.buttons.get(
				selectorButton.getSelectedControlId()).name; 
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
		
	}

}
