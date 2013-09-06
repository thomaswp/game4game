package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ElementRadio extends Element {

	private RadioGroup group;

	@Override
	public String getWarning() {
		return children.get(getSelectedIndex()).getWarning();
	}
	
	public ElementRadio(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	public void addChild(Element child) {
		boolean first = children.size() == 0;
		children.add(child);

		ElementChoice choice = (ElementChoice)child;
		final RadioButton button = new RadioButton(context);
		button.setText(choice.getText());
		if (first) {
			button.post(new Runnable() {
				@Override
				public void run() {
					button.setChecked(true);
				}
			});
		}
		group.addView(button);

		final View view = child.getView();
		view.setVisibility(first ? View.VISIBLE : View.GONE);
		button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				view.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});
		host.addView(view);
	}

	@Override
	protected void addParameters(Parameters params) {
		//write the index of the selected <choice> element
		int index = getSelectedIndex();
		params.addParam(index);
		//have the child Element at the index write its params
		children.get(index).addParameters(params);
	}

	@Override
	protected void readParameters(Iterator params) {
		//read the index of the <choice> element to select
		int index = params.getInt();
		setSelectedIndex(index);
		//have the child Element at that index read its params
		children.get(index).readParameters(params);
	}

	@Override
	public void genView() {	
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		group = new RadioGroup(context);
		group.setOrientation(LinearLayout.HORIZONTAL);
		layout.addView(group);
		main = layout;
		host = layout;
	}

	private int getSelectedIndex() {
		int index = -1;
		for (int i = 0; i < group.getChildCount(); i++) {
			if (((RadioButton)group.getChildAt(i)).isChecked()) {
				index = i;
				break;
			}
		}
		return index;
	}


	private void setSelectedIndex(int index) {
		final RadioButton button = (RadioButton)group.getChildAt(index);
		button.post(new Runnable() {
			@Override
			public void run() {
				button.setChecked(true);
			}
		});
	}

	@Override
	public String getDescription(PlatformGame game) {
		int index = getSelectedIndex();
		return children.get(index).getDescription(game);
	}
}
