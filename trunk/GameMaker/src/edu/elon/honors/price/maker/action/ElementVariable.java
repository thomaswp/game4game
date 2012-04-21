package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorVariable;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.widget.LinearLayout;

public class ElementVariable extends Element {

	private SelectorVariable selectorVariable;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	public ElementVariable(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorVariable.getVariableId());
	}
	
	@Override
	protected int readParameters(Parameters params, int index) {
		selectorVariable.setVariableId(params.getInt(index));
		return index + 1;
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorVariable = new SelectorVariable(context);
		layout.addView(selectorVariable);
		selectorVariable.setWidth(200);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		String name = game.variableNames[selectorVariable.getVariableId()];
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}

}
