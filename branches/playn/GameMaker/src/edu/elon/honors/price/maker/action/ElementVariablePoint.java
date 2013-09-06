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
import android.widget.TextView;

public class ElementVariablePoint extends Element {

	private SelectorVariable selectorVarX, selectorVarY;
	String scope;

	public ElementVariablePoint(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		super.readAttributes(atts);
		scope = atts.getValue("scope");
	}

	@Override
	protected void addParameters(Parameters params) {
		Parameters ps = new Parameters();
		ps.addParam(selectorVarX.getVariable());
		ps.addParam(selectorVarY.getVariable());
		params.addParam(ps);
	}

	@Override
	protected void readParameters(Iterator params) {
		Parameters ps = params.getParameters();
		selectorVarX.setVariable(ps.getVariable(0));
		selectorVarY.setVariable(ps.getVariable(1));
	}

	@Override
	public void genView() {
		LinearLayout varLayout = new LinearLayout(context);
		varLayout.setOrientation(LinearLayout.HORIZONTAL);
		selectorVarX = new SelectorVariable(context);
		selectorVarX.setEventContext(eventContext);
		selectorVarX.setAllowedScopes(scope);
		selectorVarY = new SelectorVariable(context);
		selectorVarY.setEventContext(eventContext);
		selectorVarY.setAllowedScopes(scope);
		TextView tvLp = new TextView(context), tvRp = new TextView(context),
		tvCom = new TextView(context);
		tvLp.setText("("); tvRp.setText(")"); tvCom.setTag(", ");
		varLayout.addView(tvLp);
		varLayout.addView(selectorVarX);
		varLayout.addView(tvCom);
		varLayout.addView(selectorVarY);
		varLayout.addView(tvRp);
		main = varLayout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		String name = selectorVarX.getText().toString();
		TextUtils.addColoredText(sb, name, 
				DatabaseEditEvent.COLOR_VARIABLE);
		sb.append(", ");
		name = selectorVarY.getText().toString();
		TextUtils.addColoredText(sb, name,
				DatabaseEditEvent.COLOR_VARIABLE);
		sb.append(")");
		return sb.toString();
	}

}
