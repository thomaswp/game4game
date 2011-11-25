package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;

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
	
	public ElementAction(Attributes atts) {
		super(atts);
		id = Integer.parseInt(atts.getValue("id"));
		name = atts.getValue("name");
	}
	
	public ParamViewHolder genView(Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		TextView title = new TextView(context);
		title.setTextSize(24);
		title.setText(name);
		layout.addView(title);
		
		ParamViewHolder[] holders = new ParamViewHolder[children.size()];
		for (int i = 0; i < children.size(); i++) {
			holders[i] = children.get(i).genView(context);
			layout.addView(holders[i].getView());
		}
		
		return new BasicParamViewHolder(layout, holders);
	}
}
