package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class ElementActorBehavior extends Element {

	private Spinner spinner;
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_MODE;
	}
	
	public ElementActorBehavior(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(spinner.getSelectedItemPosition());
	}
	
	@Override
	protected void readParameters(Iterator params) {
		spinner.setSelection(params.getInt());
	}
	
	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		spinner = new Spinner(context);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				context, R.layout.spinner_text, ActorClass.BEHAVIORS);
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		spinner.setAdapter(adapter);
		ViewGroup.LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.addView(spinner, params);
		main = layout;
	}

	@Override
	public String getDescription(PlatformGame game) {
		StringBuilder sb = new StringBuilder();
		TextUtils.addColoredText(sb, 
				ActorClass.BEHAVIORS[spinner.getSelectedItemPosition()], 
				color);
		return sb.toString();
	}
}
