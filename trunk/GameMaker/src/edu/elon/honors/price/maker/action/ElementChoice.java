package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;

import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.TextUtils;

public class ElementChoice extends Element {

	private String text;
	
	public String getText() {
		return text;
	}
	
	public ElementChoice(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		text = atts.getValue("text");
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		TextUtils.addColoredText(sb, text, DatabaseEditEvent.COLOR_MODE);
		for (int i = 0; i < children.size(); i++) {
			sb.append(" ");
			sb.append(children.get(i).getDescription());
		}
		
		return sb.toString();
	}
}
