package edu.elon.honors.price.maker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import edu.elon.honors.price.data.Event.ActorOrObjectTrigger;
import edu.elon.honors.price.maker.action.EventContext;
import edu.elon.honors.price.maker.action.EventContext.Scope;

public class DatabaseEditTriggerActorOrObject extends DatabaseActivity {

	private ActorOrObjectTrigger trigger;
	
	private RadioGroup radioGroupType;
	private SelectorActorInstance selectorActorInstance;
	private SelectorActorClass selectorActorClass;
	private SelectorObjectInstance selectorObjectInstance;
	private SelectorObjectClass selectorObjectClass;
	private Spinner spinnerAction;
	
	public ActorOrObjectTrigger getOriginalTrigger() {
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			return (ActorOrObjectTrigger)extras.getSerializable("trigger");
		}
		
		ActorOrObjectTrigger trigger = new ActorOrObjectTrigger();
		//Just so that a new trigger won't be unequal to itself
		while (radioGroupType != null &&
				radioGroupType.getChildAt(trigger.mode)
				.getVisibility() !=	View.VISIBLE) {
				trigger.mode++;
		}
		return trigger;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_actor_object);
		setDefaultButtonActions();
		
		EventContext eventContext = 
			getExtra("eventContext", EventContext.class);
		boolean hasMap = eventContext == null ||
		eventContext.getScope() == Scope.MapEvent;
		
		trigger = getOriginalTrigger();
		
		radioGroupType = (RadioGroup)findViewById(R.id.radioGroupType);
		selectorActorInstance = (SelectorActorInstance)findViewById(R.id.selectorActorInstance1);
		selectorActorClass = (SelectorActorClass)findViewById(R.id.selectorActorClass1);
		selectorObjectInstance = (SelectorObjectInstance)findViewById(R.id.selectorObjectInstance1);
		selectorObjectClass = (SelectorObjectClass)findViewById(R.id.selectorObjectClass1);
		spinnerAction = (Spinner)findViewById(R.id.spinnerAction);
		
		if (!hasMap) {
			radioGroupType.getChildAt(0).setVisibility(View.GONE);
			radioGroupType.getChildAt(2).setVisibility(View.GONE);
			if (trigger.mode == 0) {
				trigger.mode = 1;
			}
		}
		
		populate();
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, ActorOrObjectTrigger.ACTIONS);
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		spinnerAction.setAdapter(adapter);
		spinnerAction.setSelection(trigger.action);
		
		final View[] selectors = new View[] {
			selectorActorInstance,
			selectorActorClass,
			selectorObjectInstance,
			selectorObjectClass
		};
		radioGroupType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int index = group.indexOfChild(group.findViewById(checkedId));
				for (int i = 0; i < selectors.length; i++) {
					View selector = selectors[i];
					selector.setVisibility(i == index ? View.VISIBLE : View.GONE);
				}
				trigger.mode = index;
			}
		});
		radioGroupType.post(new Runnable() {
			@Override
			public void run() {
				((RadioButton)radioGroupType.
						getChildAt(trigger.mode)).setChecked(false);
				((RadioButton)radioGroupType.
						getChildAt(trigger.mode)).setChecked(true);
			}
		});
		switch (trigger.mode) {
		case ActorOrObjectTrigger.MODE_ACTOR_INSTANCE:
			selectorActorInstance.setSelectedInstance(trigger.id);
			break;
		case ActorOrObjectTrigger.MODE_ACTOR_CLASS:
			selectorActorClass.setSelectedActorId(trigger.id);
			break;
		case ActorOrObjectTrigger.MODE_OBJECT_INSTANCE:
			selectorObjectInstance.setSelectedInstance(trigger.id);
			break;
		case ActorOrObjectTrigger.MODE_OBJECT_CLASS:
			selectorObjectClass.setSelectedObjectId(trigger.id);
			break;
		}
		
//
//		selectorActorClass.setOnActorClassChangedListenter(new OnActorClassChangedListener() {
//			@Override
//			public void onActorClassChanged(int newId) {
//				trigger.id = newId;
//				
//			}
//		});
//		
//		selectorObjectClass.setOnActorClassChangedListenter(new OnObjectClassChangedListener() {
//			@Override
//			public void onObjectClassChanged(int newId) {
//				trigger.id = newId;
//			}
//		});
		
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
		selectorObjectInstance.populate(game);
		selectorObjectClass.populate(game);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		populate();

		if (resultCode == RESULT_OK) {
			if (selectorActorInstance.onActivityResult(requestCode, data)) {
				trigger.id = selectorActorInstance.getSelectedInstanceId();
			}
			if (selectorObjectInstance.onActivityResult(requestCode, data)) {
				trigger.id = selectorObjectInstance.getSelectedInstanceId();
			}
		}
	}

	@Override
	protected void onFinishing() {
		switch (trigger.mode) {
		case ActorOrObjectTrigger.MODE_ACTOR_INSTANCE:
			trigger.id = selectorActorInstance.getSelectedInstanceId();
			break;
		case ActorOrObjectTrigger.MODE_ACTOR_CLASS:
			trigger.id = selectorActorClass.getSelectedActorId();
			break;
		case ActorOrObjectTrigger.MODE_OBJECT_INSTANCE:
			trigger.id = selectorObjectInstance.getSelectedInstanceId();
			break;
		case ActorOrObjectTrigger.MODE_OBJECT_CLASS:
			trigger.id = selectorObjectClass.getSelectedObjectId();
			break;
		}
	}
	
	@Override
	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}

	@Override
	protected boolean hasChanged() {
		ActorOrObjectTrigger originalTrigger = getOriginalTrigger();
		return super.hasChanged() ||
		!trigger.equals(originalTrigger);
	}
}

