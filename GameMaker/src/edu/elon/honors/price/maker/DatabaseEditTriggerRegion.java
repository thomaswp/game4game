package edu.elon.honors.price.maker;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.RadioButton;
import android.widget.Spinner;
import edu.elon.honors.price.data.Event.RegionTrigger;
import edu.elon.honors.price.maker.action.EventContext;
import edu.elon.honors.price.maker.action.EventContext.Scope;

public class DatabaseEditTriggerRegion extends DatabaseActivity {
	private RegionTrigger trigger;
	
	//private RadioButton radioAnyActor, radioHero, radioObject;
	private RadioGroup radioGroupType;
	private Spinner spinnerMode;
	private SelectorRegion selectorRegion;
	
	public RegionTrigger getOriginalTrigger() {
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			return (RegionTrigger)extras.getSerializable("trigger");
		}
		return new RegionTrigger();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_region);
		setDefaultButtonActions();
		
		trigger = getOriginalTrigger();
		
		radioGroupType = (RadioGroup)findViewById(R.id.radioGroupType);
		spinnerMode = (Spinner)findViewById(R.id.spinnerMode);
		selectorRegion = (SelectorRegion)findViewById(R.id.selectorRegion1);
		
		//Must set one view inside scroll to focusableInTouchMode
		ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
		scroll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				selectorRegion.clearFocus();
				return false;
			}
		});
		
		EventContext eventContext = 
			getExtra("eventContext", EventContext.class);
		if (eventContext != null && eventContext.getScope() !=
			Scope.MapEvent) {
			selectorRegion.setHasMap(false);
		}
		
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, 
				R.layout.spinner_text, RegionTrigger.MODES);
		modeAdapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		spinnerMode.setAdapter(modeAdapter);
		
		selectorRegion.populate(game);
		
		((RadioButton)radioGroupType.getChildAt(trigger.who)).setChecked(true);
		spinnerMode.setSelection(trigger.mode);
		selectorRegion.setRect(new Rect(trigger.left, trigger.top, 
				trigger.right, trigger.bottom));
				
		radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				trigger.who = radioGroupType.indexOfChild(
						group.findViewById(checkedId));
			}
		});
		
		spinnerMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> paret, View view,
					int selection, long id) {
				trigger.mode = selection;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		selectorRegion.populate(game);

		if (resultCode == RESULT_OK) {
			if (selectorRegion.onActivityResult(requestCode, data)) {
				Rect rect = selectorRegion.rect;
				trigger.left = rect.left;
				trigger.top = rect.top;
				trigger.right = rect.right;
				trigger.bottom = rect.bottom;
			}
		}
	}

	@Override
	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}

	@Override
	protected boolean hasChanged() {
		RegionTrigger originalTrigger = getOriginalTrigger();
		return super.hasChanged() ||
		!trigger.equals(originalTrigger);
	}
}

