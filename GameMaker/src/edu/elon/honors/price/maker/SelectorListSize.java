package edu.elon.honors.price.maker;

import java.security.InvalidParameterException;

import android.content.Context;
import android.content.DialogInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class SelectorListSize extends RelativeLayout {

	private int maxSize = Integer.MAX_VALUE;
	private int size;
	
	private Button buttonResize;
	private EditText editText;
	private SizeChangedListener sizeChangedListener;
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(int maxSize) {
		if (maxSize < 1)
			throw new InvalidParameterException("Max size cannot be less than 1");
		
		this.maxSize = maxSize;
		
		int maxChars = (int)Math.log10(maxSize) + 1;
		
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(maxChars);
		editText.setFilters(filterArray);
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		editText.setVisibility(GONE);
		editText.setText("" + size);
		buttonResize.setText(R.string.resize);
		if (size != this.size && sizeChangedListener != null) {
			sizeChangedListener.onSizeChanged(size);
		}
		this.size = size;
	}
	
	public void setOnSizeChangedListener(SizeChangedListener sizeChangedListener) {
		this.sizeChangedListener = sizeChangedListener;
	}
	
	public SelectorListSize(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflator = ((Activity)getContext()).getLayoutInflater();
		inflator.inflate(R.layout.selector_list_size, this);
		
		buttonResize = (Button)findViewById(R.id.buttonResize);
		editText = (EditText)findViewById(R.id.editText);
		
		buttonResize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editText.getVisibility() == GONE) {
					editText.setVisibility(VISIBLE);
					buttonResize.setText("Resize");
				} else {
					final int newSize = Integer.parseInt("0" + editText.getText().toString());
					if (newSize > maxSize) {
						Builder builder = new Builder(getContext());
						builder.setTitle("Too large")
						.setMessage("The size cannot exceed " + maxSize + ".")
						.setPositiveButton("Ok", null)
						.show();
					} else if (newSize <  size) {
						Builder builder = new Builder(getContext());
						builder.setTitle("Delete entries?")
						.setMessage("This size is smaller than the current size " +
								"and will result in deleted entries. Proceed?")
						.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								setSize(newSize);
							}
						})
						.setNegativeButton("No", null)
						.show();
					} else {
						setSize(newSize);
					}
				}
			}
		});
	}
	
	public static abstract class SizeChangedListener {
		public abstract void onSizeChanged(int newSize);
	}

}
