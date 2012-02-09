package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorObjectInstance;
import edu.elon.honors.price.maker.TextUtils;

public class ElementObjectInstance  extends Element {

	private SelectorObjectInstance selectorObjectInstance;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}
	
	public ElementObjectInstance(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorObjectInstance.getSelectedInstance());
	}
	
	@Override
	protected int readParameters(final Parameters params, final int index) {
		selectorObjectInstance.post(new Runnable() {
			@Override
			public void run() {
				selectorObjectInstance.setSelectedInstance(params.getInt(index));
			}
		});
		return index + 1;
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorObjectInstance = new SelectorObjectInstance(context);
		layout.addView(selectorObjectInstance);
		selectorObjectInstance.setWidth(200);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		int index = selectorObjectInstance.getSelectedInstance();
		ObjectInstance instance = game.getSelectedMap().objects.get(index);
		String objectString;
		if (instance.classIndex > 0)
			objectString = String.format("%s %03d", 
					game.objects[instance.classIndex].name, instance.id);
		else
			objectString = game.hero.name;
		TextUtils.addColoredText(sb, objectString, color);
		return sb.toString();
	}

}
