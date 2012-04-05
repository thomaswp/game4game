package edu.elon.honors.price.maker.action;

import java.util.LinkedList;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class ElementCompound extends Element {

	private LinkedList<Element> elements;
	
	public ElementCompound(Attributes atts, Context context) {
		super(atts, context);
		this.elements = getElements();
	}
	
	protected abstract LinkedList<Element> getElements();

	@Override
	protected void genView() {
		children.addAll(elements);
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		main = host = layout;
		super.genView();
	}
	
	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < children.size(); i++) {
			if (i != 0) {
				sb.append(" ");
			}
			sb.append(children.get(i).getDescription(game));
		}
		
		return sb.toString();
	}

}
