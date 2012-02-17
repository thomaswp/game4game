package edu.elon.honors.price.maker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import edu.elon.honors.price.data.Event.ActorTrigger;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.UITrigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.SelectorActorClass.OnActorClassChangedListener;

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
		
		populate();
		
		radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorUIButton.setVisibility(View.VISIBLE);
					selectorUIJoystick.setVisibility(View.GONE);
				}
			}
		});
		radioJoystick.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorUIButton.setVisibility(View.GONE);
					selectorUIJoystick.setVisibility(View.VISIBLE);
				}
			}
		});
		radioTouch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					selectorUIButton.setVisibility(View.GONE);
					selectorUIJoystick.setVisibility(View.GONE);
				}
			}
		});
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

