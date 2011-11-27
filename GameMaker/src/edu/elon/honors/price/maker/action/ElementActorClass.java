package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorActorClass;
import edu.elon.honors.price.maker.SelectorPoint;
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
		params.addParam(selectorActorClass.getSelectedActorId());
	}
	
	@Override
	protected int readParameters(Parameters params, int index) {
		selectorActorClass.setSelectedActorId(params.getInt(index));
		return index + 1;
	}
	
	@Override
	public void genView() {
		selectorActorClass = new SelectorActorClass(context);
		main = selectorActorClass;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		int id = selectorActorClass.getSelectedActorId();
		String name = game.actors[id].name;
		TextUtils.addColoredText(sb, name, color);
		return sb.toString();
	}

}
