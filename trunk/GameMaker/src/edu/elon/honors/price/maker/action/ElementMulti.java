package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;

public abstract class ElementMulti extends Element {

	protected Option[] options;
	protected RadioGroup radioGroup;

	protected int getMode() {
		return radioGroup.indexOfChild(
				radioGroup.findViewById(
						radioGroup.getCheckedRadioButtonId()));
	}

	public ElementMulti(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void addParameters(Parameters params) {
		int mode = getMode();
		Parameters ps = new Parameters();
		ps.addParam(mode);
		options[mode].addParams(ps);
		params.addParam(ps);
	}

	@Override
	protected int readParameters(Parameters params, int index) {
		Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		((RadioButton)radioGroup.getChildAt(mode)).setChecked(true);
		options[mode].readParams(ps, 1);
		return index + 1;
	}

	@Override
	public void genView() {
		options = getOptions();

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		radioGroup = new RadioGroup(context);
		for (Option option : options) {
			RadioButton button = new RadioButton(context);
			button.setText(option.text);
			final Option fOption = option;
			button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						for (Option option : options) {
							if (option == fOption) {
								option.view.setVisibility(View.VISIBLE);
							} else {
								option.view.setVisibility(View.GONE);
							}
						}
					}
				}
			});
			radioGroup.addView(button);
		}
		layout.addView(radioGroup);
		((RadioButton)radioGroup.getChildAt(0)).setChecked(true);

		for (Option option : options) {
			View oLayout = option.view;
			//oLayout.setVisibility(View.GONE);
			LayoutParams lps = new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					option.width);
			lps.leftMargin = 10;
			lps.gravity = Gravity.CENTER;
			layout.addView(oLayout, lps);
		}

		layout.setBackgroundDrawable(
				context.getResources().getDrawable(
						R.drawable.border_white));
		LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//		params.setMargins(10, 0, 0, 0);
		layout.setLayoutParams(params);
//		int width = 0;
//		for (int i = 0; i < layout.getChildCount(); i++) {
//			width = Math.max(width, layout.getChildAt(i).getWidth());
//		}
//		layout.setMinimumWidth(width);

		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		int mode = getMode();
		options[mode].writeDescription(sb, game);
		return sb.toString();
	}

	protected abstract Option[] getOptions();

	protected static abstract class Option {
		public View view;
		public String text;
		public int width;

		public Option(View view, String text) {
			this.view = view;
			this.text = text;
			this.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}

		public abstract void readParams(Parameters params, int index);
		public abstract void addParams(Parameters params);
		public abstract void writeDescription(StringBuilder sb,
				PlatformGame game);
	}

	protected static class OptionElement extends Option {

		private Element element;

		public OptionElement(String text, Element element) {
			super(element.getView(), text);
			this.element = element;
			if (view.getLayoutParams() != null) {
				width = view.getLayoutParams().width;
			}
		}

		@Override
		public void readParams(Parameters params, int index) {
			element.readParameters(params, index);
		}

		@Override
		public void addParams(Parameters params) {
			element.addParameters(params);
		}

		@Override
		public void writeDescription(StringBuilder sb, PlatformGame game) {
			sb.append(element.getDescription(game));
		}

	}
	
	protected static class OptionEmpty extends Option {

		private String description;
		private String color;
		
		public OptionEmpty(Context context, String text, String description) {
			this(context, text, description, DatabaseEditEvent.COLOR_MODE);
		}
		
		public OptionEmpty(Context context, String text, String description, String color) {
			super(new LinearLayout(context), text);
			this.description = description;
			this.color = color;
		}

		@Override
		public void readParams(Parameters params, int index) { }

		@Override
		public void addParams(Parameters params) { }

		@Override
		public void writeDescription(StringBuilder sb, PlatformGame game) {
			if (color == null) {
				sb.append(description);
			} else {
				TextUtils.addColoredText(sb, description, color);
			}
		}
		
	}
}
