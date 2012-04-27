package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class SelectorVector extends Button implements IPopulatable{

	private float x, y;
	
	public float getVectorX() {
		return x;
	}
	
	public float getVectorY() {
		return y;
	}
	
	public void setVector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public SelectorVector(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Game.debug("!!");
				y = 1;
				x = 0;
			}
		});
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
