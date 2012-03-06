package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorObjectInstance;
import edu.elon.honors.price.maker.TextUtils;

public class ElementObjectInstance  extends Element {

	private SelectorObjectInstance selectorObjectInstance;
	private RadioButton radioInstance, radioTriggering;

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}

	public ElementObjectInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		int mode = radioInstance.isChecked() ? 0 : 1;
		Parameters ps = new Parameters();
		ps.addParam(mode);
		if (mode == 0) {
			ps.addParam(selectorObjectInstance.getSelectedInstance());
		}
		params.addParam(ps);
	}

	@Override
	protected int readParameters(final Parameters params, final int index) {
		final Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			radioInstance.setChecked(true);
			selectorObjectInstance.post(new Runnable() {
				@Override
				public void run() {
					selectorObjectInstance.setSelectedInstance(ps.getInt(1));
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
		radioInstance.setText("the specific object");
		radioTriggering = new RadioButton(context);
		radioTriggering.setText("the triggering object");

		group.addView(radioInstance);
		group.addView(radioTriggering);
		layout.addView(group);
		radioInstance.setChecked(true);
		selectorObjectInstance = new SelectorObjectInstance(context);
		selectorObjectInstance.setGravity(Gravity.CENTER);
		LayoutParams lps = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
		lps.leftMargin = 10;
		lps.gravity = Gravity.CENTER;
		layout.addView(selectorObjectInstance, lps);
		main = layout;

		radioInstance.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorObjectInstance.setVisibility(View.VISIBLE);
				}
			}
		});
		radioTriggering.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorObjectInstance.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		if (radioInstance.isChecked()) {
			int index = selectorObjectInstance.getSelectedInstance();
			if (index < 0) {
				TextUtils.addColoredText(sb, "None", DatabaseEditEvent.COLOR_MODE);
			} else {
				ObjectInstance instance = game.getSelectedMap().objects.get(index);
				String objectString;
				objectString = String.format("%s %03d", 
						game.objects[instance.classIndex].name, instance.id);
				TextUtils.addColoredText(sb, objectString, color);
			}
		} else {
			TextUtils.addColoredText(sb, "the triggering object", 
					DatabaseEditEvent.COLOR_MODE);
		}
		return sb.toString();
	}

}
