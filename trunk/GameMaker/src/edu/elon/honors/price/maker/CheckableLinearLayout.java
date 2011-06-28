package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Game;
import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

/*
 * This class is useful for using inside of ListView that needs to have checkable items.
 * Adapted from http://tokudu.com/2010/android-checkable-linear-layout/
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
	private CheckedTextView _checkbox;
    	
    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
	}
    
    @Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
    	// find checked text view
		int childCount = getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View v = getChildAt(i);
			if (v instanceof CheckedTextView) {
				_checkbox = (CheckedTextView)v;
			}
		}    	
    }
    
    @Override 
    public boolean isChecked() { 
        return _checkbox != null ? _checkbox.isChecked() : false;
    }
    
    @Override 
    public void setChecked(boolean checked) {
    	//int c = Color.argb(255, 255, 200, 0);
    	//setBackgroundColor(checked ? c : Color.TRANSPARENT);
    	if (_checkbox != null) {
    		_checkbox.setChecked(checked);
    	}
    }
    
    @Override 
    public void toggle() {
    	Game.debug("!!");
    	if (_checkbox != null) {
    		_checkbox.toggle();
    	}
    } 
} 