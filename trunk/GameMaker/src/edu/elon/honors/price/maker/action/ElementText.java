package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ElementText extends Element {
	private String text;
	
	public ElementText(Attributes atts) {
		super(atts);
		text = atts.getValue("value");
	}

	@Override
	public ParamViewHolder genView(Context context) {
		TextView tv = new TextView(context);
		tv.setTextSize(20);
		tv.setText(text);
		return new BasicParamViewHolder(tv);
	}
}
