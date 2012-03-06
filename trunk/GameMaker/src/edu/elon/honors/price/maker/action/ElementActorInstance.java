package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorActorClass;
import edu.elon.honors.price.maker.SelectorActorInstance;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ElementActorInstance extends Element {

	private SelectorActorInstance selectorActorInstance;
	private RadioButton radioInstance, radioTriggering;

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}

	public ElementActorInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		int mode = radioInstance.isChecked() ? 0 : 1;
		Parameters ps = new Parameters();
		ps.addParam(mode);
		if (mode == 0) {
			ps.addParam(selectorActorInstance.getSelectedInstance());
		}
		params.addParam(ps);
	}

	@Override
	protected int readParameters(final Parameters params, final int index) {
		final Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			radioInstance.setChecked(true);
			selectorActorInstance.post(new Runnable() {
				@Override
				public void run() {
					selectorActorInstance.setSelectedInstance(ps.getInt(1));
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
		radioInstance.setText("the specific actor");
		radioTriggering = new RadioButton(context);
		radioTriggering.setText("the triggering actor");

		group.addView(radioInstance);
		group.addView(radioTriggering);
		layout.addView(group);
		radioInstance.setChecked(true);
		selectorActorInstance = new SelectorActorInstance(context);
		selectorActorInstance.setGravity(Gravity.CENTER);
		LayoutParams lps = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
		lps.leftMargin = 10;
		lps.gravity = Gravity.CENTER;
		layout.addView(selectorActorInstance, lps);
		main = layout;
		
		radioInstance.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorActorInstance.setVisibility(View.VISIBLE);
				}
			}
		});
		radioTriggering.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorActorInstance.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		if (radioInstance.isChecked()) {
			int index = selectorActorInstance.getSelectedInstance();
			ActorInstance instance = game.getSelectedMap().actors.get(index);
			String actorString;
			if (instance.classIndex > 0)
				actorString = String.format("%s %03d", 
						game.actors[instance.classIndex].name, instance.id);
			else
				actorString = game.hero.name;
			TextUtils.addColoredText(sb, actorString, color);
		} else {
			TextUtils.addColoredText(sb, "the triggering actor", 
					DatabaseEditEvent.COLOR_MODE);
		}
		return sb.toString();
	}

}
