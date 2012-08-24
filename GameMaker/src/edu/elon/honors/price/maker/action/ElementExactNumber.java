package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ElementExactNumber extends Element {

	private EditText editText;
	
	public ElementExactNumber(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VALUE;
	}
	
	@Override
	protected void addParameters(Parameters params) {
		String text = editText.getText().toString();
		if (text.length() == 0) text = "0";
		int value = Integer.parseInt(text);
		params.addParam(value);
	}
	
	@Override
	protected void readParameters(Iterator params) {
		editText.setText("" + params.getInt());
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setFocusableInTouchMode(true);
		editText = new EditText(context);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER | 
				InputType.TYPE_NUMBER_FLAG_SIGNED);
		int maxLength = 9;
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		editText.setFilters(fArray);
		editText.setText("0");
		
		layout.addView(editText);
		editText.setWidth(200);

		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		TextUtils.addColoredText(sb, editText.getText().toString(), color);
		return sb.toString();
	}
}
