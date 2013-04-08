package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.SelectorObjectInstance;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ElementExactObjectInstance extends Element {

	private SelectorObjectInstance selectorObjectInstance;

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VARIABLE;
	}

	public ElementExactObjectInstance(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		params.addParam(selectorObjectInstance.getSelectedInstanceId());
	}

	@Override
	protected void readParameters(Iterator params) {
		final int id = params.getInt();
		selectorObjectInstance.post(new Runnable() {
			@Override
			public void run() {
				selectorObjectInstance.setSelectedInstance(id);
			}
		});
	}

	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		selectorObjectInstance = new SelectorObjectInstance(context);
		LayoutParams lps = new LayoutParams(200, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(selectorObjectInstance, lps);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		int id = selectorObjectInstance.getSelectedInstanceId();
		ObjectInstance instance = game.getSelectedMap().getObjectInstanceById(id);
		if (instance != null) {
			String name = game.objects[instance.classIndex].name;
			TextUtils.addColoredText(sb, name, color);
		} else {
			sb.append("[None]");
		}
		return sb.toString();
	}

}
