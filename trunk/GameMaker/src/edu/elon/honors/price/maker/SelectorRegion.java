package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
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

public class SelectorRegion extends LinearLayout {

	protected PlatformGame game;
	protected Rect rect;
	protected EditText[] coords;
	protected Button select;
	
	public Rect getRect() {
		return rect;
	}
	
	public void setRect(Rect rect) {
		this.rect = rect;
		
		validateRect();
		
		coords[0].setText("" + rect.left);
		coords[1].setText("" + rect.top);
		coords[2].setText("" + rect.right);
		coords[3].setText("" + rect.bottom);
		
	}
	
	private void validateRect() {
		if (game != null) {
			Map map = game.getSelectedMap();
			int right = game.getMapWidth(map) - 1;
			int bottom = game.getMapHeight(map) - 1;
			
			if (rect.left < 0) rect.left = 0;
			if (rect.top < 0) rect.top = 0;
			if (rect.right < 0) rect.right = 0;
			if (rect.bottom < 0) rect.bottom = 0;
			
			if (rect.right > right) rect.right = right;
			if (rect.bottom > bottom) rect.bottom = bottom;
			if (rect.left > right) rect.left = right;
			if (rect.top > bottom) rect.top = bottom;
		}
	}
	
	public SelectorRegion(Context context) {
		super(context);
		setup();
	}

	public SelectorRegion(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}
	
	public void populate(final PlatformGame game) {
		this.game = game;
		setRect(rect);
		
		select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), SelectorMapRegion.class);
				intent.putExtra("game", game);
				intent.putExtra("left", rect.left);
				intent.putExtra("top", rect.top);
				intent.putExtra("right", rect.right);
				intent.putExtra("bottom", rect.bottom);
				((Activity)getContext()).startActivityForResult(intent, getId());
			}
		});
	}
	
	protected void setup() {
		this.setOrientation(HORIZONTAL);
		
		rect = new Rect();
		
		coords = new EditText[4];
		
		float textSize = 24;
		
		TextView tv = new TextView(getContext());
		tv.setText("\u21F1: (");
		tv.setTextSize(textSize);
		addView(tv);
		
		String middles[] = new String[] {",", ") ", ",", ")"};
		
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
					Rect r = getRect();
					int value = Integer.parseInt(v.getText().toString());
					switch (fi) {
					case 0: 
						r.left = value;
						if (r.left > r.right) r.right = r.left;
						break;
					case 1: 
						r.top = value; 
						if (r.top > r.bottom) r.bottom = r.top;
						break;
					case 2: 
						r.right = value; 
						if (r.right < r.left) r.left = r.right;
						break;
					case 3: 
						r.bottom = value;
						if (r.bottom < r.top) r.top = r.bottom;
						break;
					}
					setRect(r);
					
					return false;
				}
			});
			addView(coords[i]);
			tv = new TextView(getContext());
			tv.setText(middles[i]);
			tv.setTextSize(textSize);
			addView(tv);
			
			if (i == 1) {
				addView(select);
				tv = new TextView(getContext());
				tv.setText(" \u21F2: (");
				tv.setTextSize(textSize);
				addView(tv);
			}
		}
	}
	

	public void onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			int left = data.getIntExtra("left", 0);
			int top = data.getIntExtra("top", 0);
			int right = data.getIntExtra("right", 0);
			int bottom = data.getIntExtra("bottom", 0);
			setRect(new Rect(left, top, right, bottom));
		}
	}
}
