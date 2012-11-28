package edu.elon.honors.price.maker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

public class SelectorColor extends Button {

	private int color;
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
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
		setBackgroundColor(Color.BLACK);
		setTextColor(Color.WHITE);
		setText("Black");
	}
}
