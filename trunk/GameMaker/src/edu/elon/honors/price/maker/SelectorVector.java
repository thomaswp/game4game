package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class SelectorVector extends Button implements IPopulatable{

	private float x, y;
	private OnVectorChangedListener onVectorChangedListener;
	
	public float getVectorX() {
		return x;
	}
	
	public float getVectorY() {
		return y;
	}
	
	public void setVector(float x, float y) {
		this.x = x;
		this.y = y;
		setText(String.format("Select Vector: [%.02f, %.02f]", x, y));
		if (onVectorChangedListener != null) {
			onVectorChangedListener.onVectorChanged(x, y);
		}
	}
	
	public void setOnVectorChangedListener(OnVectorChangedListener 
			onVectorChangedListener) {
		this.onVectorChangedListener = onVectorChangedListener;
	}
	
	public SelectorVector(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public SelectorVector(Context context) {
		super(context);
		setup();
	}
	
	private void setup() {
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), 
						SelectorActivityVector.class);
				intent.putExtra("x", x);
				intent.putExtra("y", y);
				
				((Activity)getContext()).startActivityForResult(
						intent, getId());
			}
		});
		
		setVector(0, -1);
	}

	@Override
	public void populate(PlatformGame game) {
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			if (data.hasExtra("x")) {
				x = data.getExtras().getFloat("x");
			}
			if (data.hasExtra("y")) {
				y = data.getExtras().getFloat("y");
			}
			setVector(x, y);
			return true;
		}
		return false;
	}

	public interface OnVectorChangedListener {
		public void onVectorChanged(float x, float y);
	}
}
