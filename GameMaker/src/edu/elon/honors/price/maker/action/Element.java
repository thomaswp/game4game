package edu.elon.honors.price.maker.action;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class Element {
	public static final int ID_INC = 100;

	protected ArrayList<Element> children = new ArrayList<Element>();
	protected Context context;
	protected View main;
	protected ViewGroup host;
	protected String color;

	protected String getDefaultColor() {
		return null;
	}
	
	public Element(Attributes atts, Context context) {
		this.context = context;
		readAttributes(atts);
		genView();
	}
	
	public void addChild(Element child) {
		children.add(child);
		if (host != null) {
			host.addView(child.getView());
		}
	}

	public View getView() {
		return main;
	}
	
	public Parameters getParameters() {
		Parameters params = new Parameters();
		addParameters(params);
		return params;
	}
	
	public void setParameters(Parameters params) {
		readParameters(params, 0);
	}
	
	protected void addParameters(Parameters params) {
		for (int i = 0; i < children.size(); i++) {
			children.get(i).addParameters(params);
		}
	}
	
	protected int readParameters(Parameters params, int index) {
		for (int i = 0; i < children.size(); i++) {
			index = children.get(i).readParameters(params, index);
		}
		return index;
	}

	protected void readAttributes(Attributes atts) { 
		String color = atts.getValue("color");
		if (color != null) {
			if (color.equals("value")) {
				this.color = DatabaseEditEvent.COLOR_VALUE;
			} else if (color.equals("variable")) {
				this.color = DatabaseEditEvent.COLOR_VARIABLE;
			} else if (color.equals("mode")) {
				this.color = DatabaseEditEvent.COLOR_MODE;
			} else if (color.equals("action")) {
				this.color = DatabaseEditEvent.COLOR_ACTION;
			}
		}
		if (this.color == null) {
			this.color = getDefaultColor();
		}
	}
	
	protected void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		main = layout;
		host = layout;
	}
	
	public abstract String getDescription(PlatformGame game);

	public static Element genElement(String qName, Attributes atts, Context context) {
		if (qName.equals("action")) {
			return new ElementAction(atts, context);
		} else if (qName.equals("text")) {
			return new ElementText(atts, context);
		} else if (qName.equals("radio")) {
			return new ElementRadio(atts, context);
		} else if (qName.equals("choice")) {
			return new ElementChoice(atts, context);
		} else if (qName.equals("group")) {
			return new ElementGroup(atts, context);
		} else if (qName.equals("switch")) {
			return new ElementSwitch(atts, context);
		} else if (qName.equals("variable")) {
			return new ElementVariable(atts, context);
		} else if (qName.equals("description")) {
			return new ElementDescription(atts, context);
		} else if (qName.equals("number")) {
			return new ElementNumber(atts, context);
		} else if (qName.equals("actorInstance")) {
			return new ElementActorInstance(atts, context);
		}

		throw new RuntimeException("Unrecognized attribute: " + qName);
	}
}
