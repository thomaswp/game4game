package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.types.ObjectClassPointer;
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
		params.addParam(selectorObjectClass.getSelectedClass());
	}
	
	@Override
	protected void readParameters(Iterator params) {
		selectorObjectClass.setSelectedClass((ObjectClassPointer)params.getObject());
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorObjectClass = new SelectorObjectClass(context);
		selectorObjectClass.setEventContext(eventContext);
		ViewGroup.LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(selectorObjectClass, params);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		Behavior behavior = eventContext == null ? null : eventContext.getBehavior();
		String name = selectorObjectClass.getSelectedClass().getName(game, behavior);
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}

}

