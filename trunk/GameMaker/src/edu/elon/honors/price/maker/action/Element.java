package edu.elon.honors.price.maker.action;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditAction;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.IEventContextual;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class Element {
	public static final int ID_INC = 100;

	protected ArrayList<Element> children = new ArrayList<Element>();
	protected Context context;
	protected View main;
	protected ViewGroup host;
	protected String color;
	protected Attributes attributes;
	protected EventContext eventContext;

	protected String getDefaultColor() {
		return null;
	}
	
	public String getWarning() {
		for (Element e : children) {
			String warning = e.getWarning();
			if (warning != null) return warning;
		}
		return null;
	}
	
	public Element(Attributes atts, Context context) {
		this.context = context;
		this.attributes = atts;
		if (context instanceof IEventContextual) {
			eventContext = 
				((IEventContextual) context).getEventContext();
		}
		if (atts != null) {
			readAttributes(atts);
		} else {
			this.color = getDefaultColor();
		}
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
		readParameters(params.iterator());
	}
	
	protected void addParameters(Parameters params) {
		for (int i = 0; i < children.size(); i++) {
			children.get(i).addParameters(params);
		}
	}
	
	protected void readParameters(Iterator params) {
		for (int i = 0; i < children.size(); i++) {
			children.get(i).readParameters(params);
		}
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
		} else if (qName.equals("exactNumber")) {
			return new ElementExactNumber(atts, context);
		} else if (qName.equals("actorInstance")) {
			return new ElementActorInstance(atts, context);
		} else if (qName.equals("actorClass")) {
			return new ElementActorClass(atts, context);
		} else if (qName.equals("exactPoint")) {
			return new ElementPoint(atts, context);
		} else if (qName.equals("line")) {
			return new ElementLine(atts, context);
		} else if (qName.equals("string")) {
			return new ElementString(atts, context);
		} else if (qName.equals("region")) {
			return new ElementRegion(atts, context);
		} else if (qName.equals("objectInstance")) {
			return new ElementObjectInstance(atts, context);
		} else if (qName.equals("objectClass")) {
			return new ElementObjectClass(atts, context);
		} else if (qName.equals("actorBehavior")) {
			return new ElementActorBehavior(atts, context);
		} else if (qName.equals("point")) {
			return new ElementPoint(atts, context);
		} else if (qName.equals("vector")) {
			return new ElementVector(atts, context);
		} else if (qName.equals("number")) {
			return new ElementNumber(atts, context);
		} else if (qName.equals("exactActorInstance")) {
			return new ElementExactActorInstance(atts, context);
		} else if (qName.equals("variablePoint")) {
			return new ElementVariablePoint(atts, context);
		} else if (qName.equals("joystick")) {
			return new ElementJoystick(atts, context);
		} else if (qName.equals("button")) {
			return new ElementButton(atts, context);
		} else if (qName.equals("ui")) {
			return new ElementUI(atts, context);
		} else if (qName.equals("boolean")) {
			return new ElementBoolean(atts, context);
		} else if (qName.equals("color")) {
			return new ElementColor(atts, context);
		} else if (qName.equals("body")) {
			return new ElementBody(atts, context);
		}

		throw new RuntimeException("Unrecognized attribute: " + qName);
	}
}
