package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.SelectorSwitch;
import edu.elon.honors.price.maker.TextUtils;

public class ElementString extends Element {
	
	private int length;
	private EditText editText;
	
	public ElementString(Attributes atts, Context context) {
		super(atts, context);
	}

	@Override
	protected void readAttributes(Attributes atts) {
		super.readAttributes(atts);
		length = Integer.parseInt(atts.getValue("length"));
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(editText.getText().toString());
	}
	
	@Override
	protected int readParameters(Parameters params, int index) {
		editText.setText(params.getString(index));
		return index + 1;
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		editText = new EditText(context);
		layout.addView(editText);
		editText.setWidth(200);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		return editText.getText().toString();
	}
}
