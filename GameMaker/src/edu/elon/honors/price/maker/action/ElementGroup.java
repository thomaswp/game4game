package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.widget.LinearLayout;

import edu.elon.honors.price.data.Event.Parameters;

public class ElementGroup extends Element {

	public ElementGroup(Attributes atts) {
		super(atts);
	}
	
	@Override
	public ParamViewHolder genView(Context context) {
		final ParamViewHolder[] holders = new ParamViewHolder[children.size()];
		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < children.size(); i++) {
			holders[i] = children.get(i).genView(context);
			layout.addView(holders[i].getView());
		}
		return new BasicParamViewHolder(layout, holders) {
			@Override
			public void addParameters(Parameters params) {
				Parameters ps = new Parameters();
				super.addParameters(ps);
				params.addParam(ps);
			}
		};
	}
}
