package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ElementAction extends Element {

	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ElementAction(Attributes atts, Context context) {
		super(atts, context);
	}
	
	protected void readAttributes(Attributes atts) {
		id = Integer.parseInt(atts.getValue("id"));
		name = atts.getValue("name");
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		TextView title = new TextView(context);
		title.setTextSize(24);
		title.setText(name);
		layout.addView(title);
		
		super.genView();
		layout.addView(main);
		main = layout;
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		TextUtils.addColoredText(sb, name, DatabaseEditEvent.COLOR_ACTION);
		sb.append(":");
		for (int i = 0; i < children.size(); i++) {
			sb.append(" ");
			sb.append(children.get(i).getDescription());
		}
		
		return sb.toString();
	}
}
