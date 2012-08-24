package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.maker.action.EventContext;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class DatabaseEditTriggerSwitch extends DatabaseActivity {

	private SwitchTrigger trigger;
	
	private SelectorSwitch selectorSwitch;
	private RadioButton radioOn, radioOff;
	
	public SwitchTrigger getOriginalTrigger() {
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			return (SwitchTrigger)extras.getSerializable("trigger");
		}
		return new SwitchTrigger();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.database_edit_trigger_switch);
		setDefaultButtonActions();
		
		EventContext eventContext = 
			getExtra("eventContext", EventContext.class);
		
		trigger = getOriginalTrigger();
		
		selectorSwitch = (SelectorSwitch)findViewById(R.id.selectorSwitch1);
		selectorSwitch.setEventContext(eventContext);
		radioOn = (RadioButton)findViewById(R.id.radioOn);
		radioOn.setChecked(trigger.value);
		radioOff = (RadioButton)findViewById(R.id.radioOff);
		radioOff.setChecked(!trigger.value);
		
		selectorSwitch.populate(game);
		selectorSwitch.setSwitch(trigger.triggerSwitch);
		
		radioOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				trigger.value = isChecked;
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		selectorSwitch.populate(game);
		
		if (resultCode == RESULT_OK) {
			selectorSwitch.onActivityResult(requestCode, data);
			trigger.triggerSwitch = selectorSwitch.getSwitch();
		}
	}
	
	@Override
	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}
	
	@Override
	protected boolean hasChanged() {
		SwitchTrigger originalTrigger = getOriginalTrigger();
		return super.hasChanged() || 
			!originalTrigger.equals(trigger);
	}
}
