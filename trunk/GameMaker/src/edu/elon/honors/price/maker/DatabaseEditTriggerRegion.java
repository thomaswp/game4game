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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;
import edu.elon.honors.price.data.Event.RegionTrigger;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.game.Game;

public class DatabaseEditTriggerRegion extends DatabaseActivity {
	private RegionTrigger trigger;
	
	private RadioButton radioAnyActor, radioHero, radioObject;
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
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_region);
		setDefaultButtonActions();
		
		trigger = getOriginalTrigger();
		
		radioAnyActor = (RadioButton)findViewById(R.id.radioAnyActor);
		radioHero = (RadioButton)findViewById(R.id.radioHero);
		radioObject = (RadioButton)findViewById(R.id.radioAnyObject);
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
		
		spinnerMode.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, RegionTrigger.MODES));
		selectorRegion.populate(game);
		
//		radioAnyActor.setChecked(trigger.who == RegionTrigger.WHO_ACTOR);
//		radioHero.setChecked(trigger.who == RegionTrigger.WHO_HERO);
//		radioObject.setChecked(trigger.who == RegionTrigger.WHO_OBJECT);
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

	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}

	protected boolean hasChanged() {
		RegionTrigger originalTrigger = getOriginalTrigger();
		return super.hasChanged() ||
		!trigger.equals(originalTrigger);
	}
}

