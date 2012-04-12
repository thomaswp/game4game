package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SelectorPoint extends LinearLayout implements IPopulatable {

	protected PlatformGame game;
	protected int x, y;
	protected EditText[] coords;
	protected Button select;
	
	public int getPointX() {
		return x;
	}
	
	public int getPointY() {
		return y;
	}
	
	public void setPoint(int x, int y) {
		this.x = x;
		this.y = y;
		
		validatePoints();
		
		coords[0].setText("" + this.x);
		coords[1].setText("" + this.y);
		
	}
	
	private void validatePoints() {
		if (game != null) {
			Map map = game.getSelectedMap();
			int right = game.getMapWidth(map) - 1;
			int bottom = game.getMapHeight(map) - 1;
			
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			
			if (x > right) x = right;
			if (y > bottom) y = bottom;
		}
	}
	
	public SelectorPoint(Context context) {
		super(context);
		setup();
	}

	public SelectorPoint(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}
	
	public void populate(final PlatformGame game) {
		this.game = game;
		setPoint(x, y);
		
		select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), SelectorMapPoint.class);
				intent.putExtra("game", game);
				intent.putExtra("x", x);
				intent.putExtra("y", y);
				((Activity)getContext()).startActivityForResult(intent, getId());
			}
		});
	}
	
	protected void setup() {
		this.setOrientation(HORIZONTAL);
		
		x = 0;
		y = 0;
		
		coords = new EditText[2];
		
		float textSize = 24;
		
		TextView tv = new TextView(getContext());
		tv.setText("(");
		tv.setTextSize(textSize);
		addView(tv);
		
		String middles[] = new String[] {",", ")"};
		
		select = new Button(getContext());
		select.setText("Select");
		
		for (int i = 0; i < coords.length; i++) {
			coords[i] = new EditText(getContext());
			coords[i].setInputType(InputType.TYPE_CLASS_NUMBER);
			coords[i].setMinimumWidth(40);//Width(50);
			final int fi = i;
			coords[i].setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					int value = Integer.parseInt(v.getText().toString());
					switch (fi) {
					case 0: 
						x = value;
						break;
					case 1: 
						y = value;
						break;
					}
					setPoint(x, y);
					
					return false;
				}
			});
			addView(coords[i]);
			tv = new TextView(getContext());
			tv.setText(middles[i]);
			tv.setTextSize(textSize);
			addView(tv);
		}
		
		addView(select);
	}
	

	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			x = data.getIntExtra("x", 0);
			y = data.getIntExtra("y", 0);
			setPoint(x, y);
			return true;
		}
		return false;
	}
}
