package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorActorClass;
import edu.elon.honors.price.maker.SelectorActorInstance;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.widget.LinearLayout;

public class ElementActorInstance extends Element {

	private SelectorActorInstance selectorActorInstance;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	public ElementActorInstance(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorActorInstance.getSelectedInstance());
	}
	
	@Override
	protected int readParameters(final Parameters params, final int index) {
		selectorActorInstance.post(new Runnable() {
			@Override
			public void run() {
				selectorActorInstance.setSelectedInstance(params.getInt(index));
			}
		});
		return index + 1;
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorActorInstance = new SelectorActorInstance(context);
		layout.addView(selectorActorInstance);
		selectorActorInstance.setWidth(200);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		int index = selectorActorInstance.getSelectedInstance();
		ActorInstance instance = game.getSelectedMap().actors.get(index);
		String actorString;
		if (instance.classIndex > 0)
			actorString = String.format("%s %03d", 
					game.actors[instance.classIndex].name, instance.id);
		else
			actorString = game.hero.name;
		TextUtils.addColoredText(sb, actorString, color);
		return sb.toString();
	}

}
