package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

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

	public ElementRadio(Attributes atts) {
		super(atts);
	}

	@Override
	public ParamViewHolder genView(Context context) {
		final ParamViewHolder[] holders = new ParamViewHolder[children.size()];
		
		final LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		final RadioGroup group = new RadioGroup(context);
		group.setOrientation(RadioGroup.HORIZONTAL);
		layout.addView(group);
		for (int i = 0; i < children.size(); i++) {
			ElementChoice choice = (ElementChoice)children.get(i);
			final RadioButton button = new RadioButton(context);
			button.setText(choice.getText());
			if (i == 0) {
				button.post(new Runnable() {
					@Override
					public void run() {
						button.setChecked(true);
					}
				});
			}
			group.addView(button);

			holders[i] = choice.genView(context);
			final View view = holders[i].getView();
			view.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
			button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					view.setVisibility(isChecked ? View.VISIBLE : View.GONE);
				}
			});
			layout.addView(view);
		}

		return new ParamViewHolder() {

			@Override
			public View getView() {
				return layout;
			}
			
			public void addParameters(Parameters params) {
				int index = 0;
				for (int i = 1; i < group.getChildCount(); i++) {
					if (((RadioButton)group.getChildAt(i)).isChecked()) {
						index = i;
					}
				}
				params.addParam(index);
				holders[index].addParameters(params);
			}
		};
	}
}
