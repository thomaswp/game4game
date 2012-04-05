package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorActorClass;
import edu.elon.honors.price.maker.SelectorActorInstance;
import edu.elon.honors.price.maker.SelectorObjectInstance;
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

public class ElementExactObjectInstance extends Element {

	private SelectorObjectInstance selectorObjectInstance;

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}

	public ElementExactObjectInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorObjectInstance.getSelectedInstance());
	}

	@Override
	protected int readParameters(Parameters params, int index) {
		final int id = params.getInt(index);
		selectorObjectInstance.post(new Runnable() {
			@Override
			public void run() {
				selectorObjectInstance.setSelectedInstance(id);
			}
		});
		return index + 1;
	}

	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorObjectInstance = new SelectorObjectInstance(context);
		LayoutParams lps = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
		layout.addView(selectorObjectInstance, lps);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		int index = selectorObjectInstance.getSelectedInstance();
		ObjectInstance instance = game.getSelectedMap().objects.get(index);
		String name = game.objects[instance.classIndex].name;
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}

}
