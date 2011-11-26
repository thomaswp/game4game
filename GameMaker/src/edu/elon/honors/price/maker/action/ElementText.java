package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ElementText extends Element {
	private String text;
	
	public ElementText(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		super.readAttributes(atts);
		text = atts.getValue("text");
	}

	@Override
	public void genView() {
		TextView tv = new TextView(context);
		tv.setTextSize(20);
		tv.setText(text);
		main = tv;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		TextUtils.addColoredText(sb, text, color);
		return sb.toString();
	}
}
