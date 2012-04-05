package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorVariable;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ElementVariablePoint extends Element {

	private SelectorVariable selectorVarX, selectorVarY;

	public ElementVariablePoint(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorVarX.getVariableId());
		params.addParam(selectorVarY.getVariableId());
	}

	@Override
	protected int readParameters(Parameters params, int index) {
		selectorVarX.setVariableId(params.getInt(index));
		selectorVarY.setVariableId(params.getInt(index + 1));
		return index + 2;
	}

	@Override
	public void genView() {
		LinearLayout varLayout = new LinearLayout(context);
		varLayout.setOrientation(LinearLayout.HORIZONTAL);
		selectorVarX = new SelectorVariable(context);
		selectorVarY = new SelectorVariable(context);
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
		String name = game.variableNames[selectorVarX.getVariableId()];
		TextUtils.addColoredText(sb, name, 
				DatabaseEditEvent.COLOR_VARIABLE);
		sb.append(", ");
		name = game.variableNames[selectorVarY.getVariableId()];
		TextUtils.addColoredText(sb, name,
				DatabaseEditEvent.COLOR_VARIABLE);
		sb.append(")");
		return sb.toString();
	}

}
