package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorObjectClass;
import edu.elon.honors.price.maker.TextUtils;

public class ElementObjectClass extends Element {

	private SelectorObjectClass selectorObjectClass;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	public ElementObjectClass(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorObjectClass.getSelectedObjectId());
	}
	
	@Override
	protected void readParameters(Iterator params) {
		selectorObjectClass.setSelectedObjectId(params.getInt());
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorObjectClass = new SelectorObjectClass(context);
		ViewGroup.LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(selectorObjectClass, params);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		int id = selectorObjectClass.getSelectedObjectId();
		String name = game.objects[id].name;
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}

}

