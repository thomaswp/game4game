package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorPoint;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.SelectorVariable;
import edu.elon.honors.price.maker.TextUtils;

public class ElementPoint extends Element {

	private SelectorPoint selectorPoint;
	private RadioButton radioExact, radioVariable;
	private SelectorVariable selectorVarX, selectorVarY;

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VALUE;
	}

	public ElementPoint(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		Parameters ps = new Parameters();
		int mode = radioExact.isChecked() ? 0 : 1;
		ps.addParam(mode);
		if (mode == 0) {
			ps.addParam(selectorPoint.getX());
			ps.addParam(selectorPoint.getY());
		} else {
			ps.addParam(selectorVarX.getVariableId());
			ps.addParam(selectorVarY.getVariableId());
		}
		params.addParam(ps);
	}

	@Override
	protected int readParameters(Parameters params, int index) {
		Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			radioExact.setChecked(true);
			selectorPoint.setPoint(ps.getInt(1), 
					ps.getInt(2));
		} else {
			radioVariable.setChecked(true);
			selectorVarX.setVariableId(ps.getInt(1));
			selectorVarY.setVariableId(ps.getInt(2));
		}
		return index + 1;
	}

	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setFocusableInTouchMode(true);
		layout.setOrientation(LinearLayout.HORIZONTAL);

		RadioGroup radioGroup = new RadioGroup(context);
		radioExact = new RadioButton(context);
		radioExact.setText("the exact point");
		radioVariable = new RadioButton(context);
		radioVariable.setText("the variable point");
		radioGroup.addView(radioExact);
		radioGroup.addView(radioVariable);
		radioExact.setChecked(true);
		layout.addView(radioGroup);

		selectorPoint = new SelectorPoint(context);
		LayoutParams lps = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
		lps.leftMargin = 10;
		lps.gravity = Gravity.CENTER;
		layout.addView(selectorPoint, lps);


		final LinearLayout varLayout = new LinearLayout(context);
		lps = new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		lps.leftMargin = 10;
		lps.gravity = Gravity.CENTER;
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
		varLayout.setVisibility(LinearLayout.GONE);
		layout.addView(varLayout, lps);

		radioExact.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					varLayout.setVisibility(LinearLayout.GONE);
					selectorPoint.setVisibility(LinearLayout.VISIBLE);
				}
			}
		});

		radioVariable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					varLayout.setVisibility(LinearLayout.VISIBLE);
					selectorPoint.setVisibility(LinearLayout.GONE);
				}
			}
		});

		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		if (radioExact.isChecked()) {
			sb.append("(");
			TextUtils.addColoredText(sb, selectorPoint.getX(), 
					DatabaseEditEvent.COLOR_VALUE);
			sb.append(", ");
			TextUtils.addColoredText(sb, selectorPoint.getY(),
					DatabaseEditEvent.COLOR_VALUE);
			sb.append(")");
		} else {
			sb.append("(");
			String name = game.variableNames[selectorVarX.getVariableId()];
			TextUtils.addColoredText(sb, name, 
					DatabaseEditEvent.COLOR_VARIABLE);
			sb.append(", ");
			name = game.variableNames[selectorVarY.getVariableId()];
			TextUtils.addColoredText(sb, name,
					DatabaseEditEvent.COLOR_VARIABLE);
			sb.append(")");
		}
		return sb.toString();
	}
}

