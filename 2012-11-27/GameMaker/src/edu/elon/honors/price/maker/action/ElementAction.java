package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
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
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_ACTION;
	}
	
	public ElementAction(Attributes atts, Context context) {
		super(atts, context);
		color = DatabaseEditEvent.COLOR_ACTION;
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		super.readAttributes(atts);
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
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		sb.append("<i>");
		TextUtils.addColoredText(sb, name, color);
		sb.append("</i>:");
		for (int i = 0; i < children.size(); i++) {
			sb.append(" ");
			sb.append(children.get(i).getDescription(game));
		}
		
		return sb.toString();
	}
}
