package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ElementRadio extends Element {

	private RadioGroup group;

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
		int index = getSelectedIndex();
		params.addParam(index);
		children.get(index).addParameters(params);
	}

	@Override
	protected int readParameters(Parameters params, int index) {
		int i = params.getInt(index);
		setSelectedIndex(i);
		return children.get(i).readParameters(params, index + 1);
	}

	@Override
	public void genView() {	
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		group = new RadioGroup(context);
		group.setOrientation(RadioGroup.HORIZONTAL);
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
