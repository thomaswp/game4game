package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.game.Game;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class DatabaseEditTriggerSwitch extends DatabaseActivity {

	private SwitchTrigger trigger, originalTrigger;
	
	private SelectorSwitch selectorSwitch;
	private RadioButton radioOn, radioOff;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.database_edit_trigger_switch);
		setDefaultButtonActions();
		
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			originalTrigger = (SwitchTrigger)extras.getSerializable("trigger");
			trigger = (SwitchTrigger)extras.getSerializable("trigger");
		} else {
			originalTrigger = new SwitchTrigger(0, true);
			trigger = new SwitchTrigger(0, true);
		}
		
		selectorSwitch = (SelectorSwitch)findViewById(R.id.selectorSwitch1);
		radioOn = (RadioButton)findViewById(R.id.radioOn);
		radioOn.setChecked(trigger.value);
		radioOff = (RadioButton)findViewById(R.id.radioOff);
		radioOff.setChecked(!trigger.value);
		
		selectorSwitch.populate(game);
		selectorSwitch.setSwitchId(trigger.switchId);
		
		radioOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				trigger.value = isChecked;
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		selectorSwitch.populate(game);
		
		if (resultCode == RESULT_OK) {
			selectorSwitch.onActivityResult(requestCode, data);
			trigger.switchId = selectorSwitch.getSwitchId();
		}
	}
	
	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}
	
	protected boolean hasChanged() {
		return super.hasChanged() || 
			!originalTrigger.equals(trigger);
	}
}
