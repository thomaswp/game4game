package edu.elon.honors.price.maker.action;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class Element {
	public static final int ID_INC = 100;

	protected ArrayList<Element> children = new ArrayList<Element>();

	public Element(Attributes atts) { }

	public void addChild(Element child) {
		children.add(child);
	}

	public ParamViewHolder genView(Context context) {
		final LinearLayout layout = new LinearLayout(context);
		final ParamViewHolder[] holders = new ParamViewHolder[children.size()];
		layout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < children.size(); i++) {
			ParamViewHolder child = children.get(i).genView(context);
			layout.addView(child.getView());
			holders[i] = child;
		}

		return new BasicParamViewHolder(layout, holders);
	}

	public static Element genElement(String qName, Attributes atts) {
		if (qName.equals("action")) {
			return new ElementAction(atts);
		} else if (qName.equals("text")) {
			return new ElementText(atts);
		} else if (qName.equals("radio")) {
			return new ElementRadio(atts);
		} else if (qName.equals("choice")) {
			return new ElementChoice(atts);
		} else if (qName.equals("group")) {
			return new ElementGroup(atts);
		} else if (qName.equals("switch")) {
			return new ElementSwitch(atts);
		}

		throw new RuntimeException("Unrecognized attribute: " + qName);
	}

	protected static class BasicParamViewHolder implements ParamViewHolder {
		private View view;
		private ParamViewHolder[] holders;

		public BasicParamViewHolder(View view, ParamViewHolder[] holders) {
			this.view = view;
			this.holders = holders;
		}

		public BasicParamViewHolder(View view) {
			this.view = view;
		}

		public View getView() {
			return view;
		}

		public void addParameters(Parameters params) {
			if (holders != null) {
				for (int i = 0; i < holders.length; i++) {
					holders[i].addParameters(params);
				}
			}
		}
	}

	public static interface ParamViewHolder {
		public abstract View getView();
		public abstract void addParameters(Parameters params);
	}
}
