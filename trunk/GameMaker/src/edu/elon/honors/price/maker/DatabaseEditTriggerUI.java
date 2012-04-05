package edu.elon.honors.price.maker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import edu.elon.honors.price.data.Event.ActorOrObjectTrigger;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.UITrigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.SelectorActorClass.OnActorClassChangedListener;
import edu.elon.honors.price.maker.SelectorUIControl.OnControlChangedListener;

public class DatabaseEditTriggerUI extends DatabaseActivity {

	private UITrigger trigger;
	private RadioButton radioButton, radioJoystick, radioTouch;
	private SelectorUIControl selectorUIButton, selectorUIJoystick;

	public UITrigger getOriginalTrigger() {
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			return (UITrigger)extras.getSerializable("trigger");
		}
		return new UITrigger();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_ui);
		setDefaultButtonActions();

		trigger = getOriginalTrigger();

		radioButton = (RadioButton)findViewById(R.id.radioButton);
		radioJoystick = (RadioButton)findViewById(R.id.radioJoystick);
		radioTouch = (RadioButton)findViewById(R.id.radioTouchScreen);
		selectorUIButton = (SelectorUIControl)findViewById(R.id.selectorUIControlButton);
		selectorUIJoystick = (SelectorUIControl)findViewById(R.id.selectorUIControlJoystick);

		selectorUIButton.setControlType(SelectorUIControl.CONTROL_BUTTON);
		selectorUIJoystick.setControlType(SelectorUIControl.CONTROL_JOY);
		
		final LinearLayout layoutButton = (LinearLayout)findViewById(R.id.linearLayoutButton);
		final LinearLayout layoutJoy = (LinearLayout)findViewById(R.id.linearLayoutJoystick);
		final LinearLayout layoutScreen = (LinearLayout)findViewById(R.id.linearLayoutScreen);
		
		final RadioGroup radioGroupButton = (RadioGroup)findViewById(R.id.radioGroupButton);
		final RadioGroup radioGroupJoy = (RadioGroup)findViewById(R.id.radioGroupJoy);
		final RadioGroup radioGroupScreen = (RadioGroup)findViewById(R.id.radioGroupScreen);
		
		((RadioGroup)findViewById(R.id.radioGroupType)).clearCheck();
		
		populate();

		radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					layoutButton.setVisibility(View.VISIBLE);
					layoutJoy.setVisibility(View.GONE);
					layoutScreen.setVisibility(View.GONE);
					trigger.controlType = UITrigger.CONTROL_BUTTON;
					if (trigger.condition > 1) trigger.condition = 0;
					((RadioButton)radioGroupButton.getChildAt(trigger.condition)).setChecked(true);
				}
			}
		});
		radioJoystick.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					layoutButton.setVisibility(View.GONE);
					layoutJoy.setVisibility(View.VISIBLE);
					layoutScreen.setVisibility(View.GONE);
					trigger.controlType = UITrigger.CONTROL_JOY;
					((RadioButton)radioGroupJoy.getChildAt(trigger.condition)).setChecked(true);
				}
			}
		});
		radioTouch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					layoutButton.setVisibility(View.GONE);
					layoutJoy.setVisibility(View.GONE);
					layoutScreen.setVisibility(View.VISIBLE);
					trigger.controlType = UITrigger.CONTROL_TOUCH;
					((RadioButton)radioGroupScreen.getChildAt(trigger.condition)).setChecked(true);
				}
			}
		});
		
		switch (trigger.controlType) {
		case UITrigger.CONTROL_BUTTON: 
			radioButton.setChecked(true); 
			selectorUIButton.setSelectedControlId(trigger.index);
			break;
		case UITrigger.CONTROL_JOY: 
			radioJoystick.setChecked(true);
			selectorUIJoystick.setSelectedControlId(trigger.index);
			break;
		case UITrigger.CONTROL_TOUCH: 
			radioTouch.setChecked(true); 
			break;
		}
		
		selectorUIButton.setOnControlChangedListenter(new OnControlChangedListener() {
			@Override
			public void onObjectClassChanged(int newId) {
				trigger.index = newId;
			}
		});
		
		selectorUIJoystick.setOnControlChangedListenter(new OnControlChangedListener() {
			@Override
			public void onObjectClassChanged(int newId) {
				trigger.index = newId;
			}
		});
		
		RadioGroup.OnCheckedChangeListener occl = new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				View button = findViewById(checkedId);
				trigger.condition = group.indexOfChild(button);
			}
		};
		radioGroupButton.setOnCheckedChangeListener(occl);
		radioGroupJoy.setOnCheckedChangeListener(occl);
		radioGroupScreen.setOnCheckedChangeListener(occl);
	}

	private void populate() {
		selectorUIButton.populate(game);
		selectorUIJoystick.populate(game);
	}

	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}

	protected boolean hasChanged() {
		UITrigger originalTrigger = getOriginalTrigger();
		return super.hasChanged() ||
		!trigger.equals(originalTrigger);
	}
}


