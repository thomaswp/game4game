package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.types.ActorClassPointer;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorActorClass;
import edu.elon.honors.price.maker.TextUtils;

public class ElementActorClass extends Element {

	private SelectorActorClass selectorActorClass;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	public ElementActorClass(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorActorClass.getSelectedClass());
	}
	
	@Override
	protected void readParameters(Iterator params) {
		selectorActorClass.setSelectedClass((ActorClassPointer)params.getObject());
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorActorClass = new SelectorActorClass(context);
		selectorActorClass.setEventContext(eventContext);
		ViewGroup.LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(selectorActorClass, params);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		Behavior behavior = eventContext == null ? null : eventContext.getBehavior();
		String name = selectorActorClass.getSelectedClass().getName(game, behavior);
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}

}
