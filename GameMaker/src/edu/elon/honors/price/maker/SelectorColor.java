package edu.elon.honors.price.maker;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class SelectorColor extends Button {

	private int color;
	private AmbilWarnaDialog colorPicker;
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
		setText(Html.fromHtml(TextUtils.getColoredText("Select Color", color)));
		colorPicker.setColor(color);
	}
	
	public SelectorColor(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public SelectorColor(Context context) {
		super(context);
		setup();
	}

	private void setup() {
		OnAmbilWarnaListener listener = new OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {
				setColor(color);
			}
			
			@Override
			public void onCancel(AmbilWarnaDialog dialog) { }
		};
		colorPicker = new AmbilWarnaDialog(getContext(), color, listener);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorPicker.show();
			}
		});
		setColor(color);
	}
}
