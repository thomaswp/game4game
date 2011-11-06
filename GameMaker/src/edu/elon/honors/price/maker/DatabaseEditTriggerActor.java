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
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.SelectorActorClass.OnActorClassChangedListener;

public class DatabaseEditTriggerActor extends DatabaseActivity {

	private ActorTrigger originalTrigger, trigger;
	
	private RadioButton radioInstance, radioClass;
	private SelectorActorInstance selectorActorInstance;
	private SelectorActorClass selectorActorClass;
	private Spinner spinnerAction;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_actor);
		setDefaultButtonActions();
		
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			originalTrigger = (ActorTrigger)extras.getSerializable("trigger");
			trigger = (ActorTrigger)extras.getSerializable("trigger");
		} else {
			originalTrigger = new ActorTrigger();
			trigger = new ActorTrigger();
		}
		
		radioInstance = (RadioButton)findViewById(R.id.radioInstance);
		radioClass = (RadioButton)findViewById(R.id.radioClass);
		selectorActorInstance = (SelectorActorInstance)findViewById(R.id.selectorActorInstance1);
		selectorActorClass = (SelectorActorClass)findViewById(R.id.selectorActorClass1);
		spinnerAction = (Spinner)findViewById(R.id.spinnerAction);
		
		populate();
		
		selectorActorClass.setSelectedActorId(trigger.forInstance ? 1 : trigger.id);
		selectorActorInstance.setSelectedInstance(trigger.forInstance ? trigger.id : 1);
		radioInstance.setChecked(trigger.forInstance);
		radioClass.setChecked(!trigger.forInstance);
		spinnerAction.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, ActorTrigger.ACTIONS));
		spinnerAction.setSelection(trigger.action);
		
		if (trigger.forInstance) {
			selectorActorClass.setVisibility(View.GONE);
		} else {
			selectorActorInstance.setVisibility(View.GONE);
		}
		
		radioInstance.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				selectorActorInstance.setVisibility(isChecked ? View.VISIBLE : View.GONE);
				selectorActorClass.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
				trigger.forInstance = isChecked;
			}
		});

		selectorActorClass.setOnActorClassChangedListenter(new OnActorClassChangedListener() {
			@Override
			public void onActorClassChanged(int newId) {
				trigger.id = newId;
				
			}
		});
		
		spinnerAction.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				trigger.action = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
	}
	
	private void populate() {
		selectorActorClass.populate(game);
		selectorActorInstance.populate(game);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		populate();

		if (resultCode == RESULT_OK) {
			if (selectorActorInstance.onActivityResult(requestCode, data)) {
				Game.debug("!");
				trigger.id = selectorActorInstance.getSelectedInstance();
			}
		}
	}

	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}

	protected boolean hasChanged() {
		return super.hasChanged() ||
		!trigger.equals(originalTrigger);
	}
}

