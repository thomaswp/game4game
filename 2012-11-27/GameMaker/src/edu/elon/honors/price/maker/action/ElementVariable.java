package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorVariable;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.widget.LinearLayout;

public class ElementVariable extends Element {

	private SelectorVariable selectorVariable;
	private String scope;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	public ElementVariable(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		super.readAttributes(atts);
		scope = atts.getValue("scope");
	}

	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorVariable.getVariable());
	}
	
	@Override
	protected void readParameters(Iterator params) {
		selectorVariable.setVariable(params.getVariable());
	}
	
	//TODO: Support multiple scopes with flags
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorVariable = new SelectorVariable(context);
		selectorVariable.setEventContext(eventContext);
		selectorVariable.setAllowedScopes(scope);
		layout.addView(selectorVariable);
		selectorVariable.setWidth(200);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		String name = selectorVariable.getText().toString();
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}

}
