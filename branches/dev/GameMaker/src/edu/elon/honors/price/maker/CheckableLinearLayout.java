package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Game;
import android.content.Context;
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
	private OnCheckedChangedListener onCheckedChangedListener;
    	
	public void setOnCheckedChangedListener(OnCheckedChangedListener onCheckedChangedListener) {
		this.onCheckedChangedListener = onCheckedChangedListener;
	}
	
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
    	boolean wasChecked = isChecked();
    	if (_checkbox != null) {
    		_checkbox.setChecked(checked);
    	}
    	if (wasChecked != checked) {
    		if (onCheckedChangedListener != null) {
    			onCheckedChangedListener.onCheckChanged(checked);
        	}
    	}
    }
    
    @Override 
    public void toggle() {
    	if (_checkbox != null) {
    		_checkbox.toggle();
    	}
    } 
    
    public interface OnCheckedChangedListener {
    	public void onCheckChanged(boolean checked);
    }
} 