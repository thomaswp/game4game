package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.SelectorSwitch;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

public class ElementSwitch extends Element {

	public ElementSwitch(Attributes atts) {
		super(atts);
	}
	
	@Override
	public ParamViewHolder genView(Context context) {
		final LinearLayout layout = new LinearLayout(context);
		final SelectorSwitch ss = new SelectorSwitch(context);
		layout.addView(ss);
		ss.setWidth(200);
		return new ParamViewHolder() {
			
			@Override
			public View getView() {
				return layout;
			}
			
			@Override
			public void addParameters(Parameters params) {
				params.addParam(ss.getSwitchId());
			}
		};
	}
}
