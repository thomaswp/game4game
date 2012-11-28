package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.UILayout;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SelectorUIControl extends Spinner implements IPopulatable {

	private PlatformGame game;
	private int id;
	private int controlType;
	private OnControlChangedListener onControlChangedListener;
	
	public static int CONTROL_BUTTON = 0;
	public static int CONTROL_JOY = 1;
	
	public int getSelectedControlId() {
		return getSelectedItemPosition();
	}
	
	public void setSelectedControlId(int id) {
		this.id = id;
		if (id >= 0 && id < getCount())
			setSelection(id);
	}

	public void setOnControlChangedListenter(OnControlChangedListener onControlChangedListener) {
		this.onControlChangedListener = onControlChangedListener;
	}
	
	public void setControlType(int controlType) {
		if (this.controlType != controlType) {
			this.controlType = controlType;
			id = 0;
			if (game != null) {
				populate(game);
			} else {
				setSelectedControlId(id);
			}
		}
		
	}
	
	public SelectorUIControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		controlType = attrs.getAttributeIntValue("android", "controlType", 0);
	}
	
	public SelectorUIControl(Context context, int controlType) {
		super(context);
		this.controlType = controlType;
	}

	@Override
	public void populate(PlatformGame game) {
		this.game = game;
		setSelectedControlId(id);
		
		ArrayList<String> items = new ArrayList<String>();
		if (controlType == CONTROL_BUTTON) {
			for (UILayout.Button control : game.uiLayout.buttons) {
				items.add(control.name);
			}
		} else {
			for (UILayout.JoyStick control : game.uiLayout.joysticks) {
				items.add(control.name);
			}
			
		}
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getContext(), R.layout.spinner_text, items);
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		
		setAdapter(adapter);
		
		setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (onControlChangedListener != null) {
					onControlChangedListener.onObjectClassChanged(position);
				}
				SelectorUIControl.this.id = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		return false;
	}
	
	public static abstract class OnControlChangedListener {
		public abstract void onObjectClassChanged(int newId);
	}
}
