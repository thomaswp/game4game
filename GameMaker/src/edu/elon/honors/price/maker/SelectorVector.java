package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Button;

public class SelectorVector extends Button implements IPopulatable{

	public int getVectorX() {
		return 0;
	}
	
	public int getVectorY() {
		return 0;
	}
	
	public void setVector(int x, int y) {
		
	}
	
	public SelectorVector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelectorVector(Context context) {
		super(context);
	}

	@Override
	public void populate(PlatformGame game) {
		setText("Select Vector");
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		return false;
	}

}
