package edu.elon.honors.price.maker.action;

import org.xml.sax.Attributes;

import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.maker.DatabaseEditEvent;
import edu.elon.honors.price.maker.Screen;
import edu.elon.honors.price.maker.TextUtils;

import android.content.Context;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ElementSeekBar extends Element {

	private final static int SEGMENTS = 100;
	
	private float min, max;
	private SeekBar seekBar;
	private TextView textView;

	public ElementSeekBar(Attributes atts, Context context) {
		super(atts, context);
	}
	
	@Override
	protected String getDefaultColor() {
		return DatabaseEditEvent.COLOR_VALUE;
	}
	
	@Override
	protected void readAttributes(Attributes atts) {
		super.readAttributes(atts);
		min = Float.parseFloat(atts.getValue("min"));
		max = Float.parseFloat(atts.getValue("max"));
	}
	
	@Override
	protected void addParameters(Parameters params) {
		params.addParam(getNumber());
	}

	@Override
	protected void readParameters(Iterator params) {
		setNumber(params.getFloat());
	}

	@Override
	public void genView() {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setPadding(5, 5, 5, 5);
		
		seekBar = new SeekBar(context);
		seekBar.setMax(SEGMENTS);
		
		LayoutParams lps = new LayoutParams(Screen.dipToPx(200, context), 
				LayoutParams.WRAP_CONTENT);
		lps.rightMargin = Screen.dipToPx(10, context);
		layout.addView(seekBar, lps);
		
		textView = new TextView(context);
		layout.addView(textView);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setText(getNumber());
			}
		});
		
		setNumber(min);
		
		super.genView();
		layout.addView(main);
		main = layout;
	}
	
	private float getNumber() {
		float range = max - min;
		return (float)seekBar.getProgress() / seekBar.getMax() * range + min;
	}
	
	private void setNumber(float x) {
		setText(x);
		float range = max - min;
		int n = (int)((x - min) / range * seekBar.getMax());
		seekBar.setProgress(n);
	}
	
	private void setText(float x) {
		textView.setText(String.format("%.02f", x));
	}

	@Override
	public String getDescription(PlatformGame game) {
		return TextUtils.getColoredText(textView.getText().toString(), color);
	}
}
