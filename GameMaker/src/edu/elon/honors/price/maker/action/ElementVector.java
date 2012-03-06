package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorObjectClass;
import edu.elon.honors.price.maker.SelectorObjectInstance;
import edu.elon.honors.price.maker.SelectorVector;
import edu.elon.honors.price.maker.TextUtils;

public class ElementVector extends Element {

	private SelectorVector selectorVector;
	private RadioButton radioInstance, radioTriggering;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VALUE;
	}
	
	public ElementVector(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		int mode = radioInstance.isChecked() ? 0 : 1;
		Parameters ps = new Parameters();
		ps.addParam(mode);
		if (mode == 0) {
			ps.addParam(selectorVector.getVectorX());
			ps.addParam(selectorVector.getVectorY());
		}
		params.addParam(ps);
	}
	
	@Override
	protected int readParameters(Parameters params, int index) {
		final Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			radioInstance.setChecked(true);
			selectorVector.post(new Runnable() {
				@Override
				public void run() {
					selectorVector.setVector(ps.getInt(1), ps.getInt(2));
				}
			});
		} else {
			radioTriggering.setChecked(true);
		}
		return index + 1;
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		RadioGroup group = new RadioGroup(context);
		radioInstance = new RadioButton(context);
		radioInstance.setText("the specific vector");
		radioTriggering = new RadioButton(context);
		radioTriggering.setText("the triggering Joystick's vector");

		group.addView(radioInstance);
		group.addView(radioTriggering);
		layout.addView(group);
		radioInstance.setChecked(true);
		selectorVector = new SelectorVector(context);
		LayoutParams lps = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
		lps.leftMargin = 10;
		lps.gravity = Gravity.CENTER;
		layout.addView(selectorVector, lps);
		main = layout;

		radioInstance.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorVector.setVisibility(View.VISIBLE);
				}
			}
		});
		radioTriggering.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorVector.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		if (radioInstance.isChecked()) {
			sb.append("[");
			TextUtils.addColoredText(sb, selectorVector.getVectorX(), color);
			sb.append(", ");
			TextUtils.addColoredText(sb, selectorVector.getVectorX(), color);
			sb.append("]");
		} else {
			TextUtils.addColoredText(sb, "the triggering Joystick's vector", 
					DatabaseEditEvent.COLOR_MODE);
		}
		return sb.toString();
	}

}

