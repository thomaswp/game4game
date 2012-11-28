package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.action.EventContext;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DatabaseEditTriggerVariable extends DatabaseActivity {

	private VariableTrigger trigger;

	private SelectorVariable selectorVariableSet, selectorVariableTo;
	private RadioButton radioVariable, radioValue;
	private EditText editTextValue;
	private Spinner spinnerOperator;

	public VariableTrigger getOriginalTrigger() {
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			return (VariableTrigger)extras.getSerializable("trigger");
		}
		return new VariableTrigger();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_variable);
		setDefaultButtonActions();

		EventContext eventContext = 
			getExtra("eventContext", EventContext.class);
		
		trigger = getOriginalTrigger();

		selectorVariableSet = (SelectorVariable)findViewById(R.id.selectorVariable1);
		selectorVariableSet.setEventContext(eventContext);
		selectorVariableTo = (SelectorVariable)findViewById(R.id.selectorVariable2);
		selectorVariableTo.setEventContext(eventContext);

		radioValue = (RadioButton)findViewById(R.id.radioValue);
		radioValue.setChecked(trigger.with == VariableTrigger.WITH_VALUE);
		radioVariable = (RadioButton)findViewById(R.id.radioVariable);
		radioVariable.setChecked(trigger.with == VariableTrigger.WITH_VARIABLE);

		editTextValue = (EditText)findViewById(R.id.editTextValue);
		spinnerOperator = (Spinner)findViewById(R.id.spinnerOperator);

		populate();

		selectorVariableSet.setVariable(trigger.variable);

		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, 
				R.layout.spinner_text, VariableTrigger.OPERATORS);
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		spinnerOperator.setAdapter(adapter);
		spinnerOperator.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				trigger.test = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		spinnerOperator.setSelection(trigger.test);

		radioValue.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Debug.write("changed");
				selectorVariableTo.setVisibility(isChecked ? View.GONE : View.VISIBLE);
				editTextValue.setVisibility(!isChecked ? View.GONE : View.VISIBLE);
				trigger.with = isChecked ? VariableTrigger.WITH_VALUE :
					VariableTrigger.WITH_VARIABLE;
				if (isChecked) {
					int value = Integer.parseInt(editTextValue.getText().toString());
					trigger.withValue = value;
				} else {
					trigger.withValue = selectorVariableTo.getVariableId();
				}
			}
		});

		if (trigger.with == VariableTrigger.WITH_VALUE) {
			selectorVariableTo.setVariableId(0);
			editTextValue.setText("" + trigger.withValue);
			radioValue.setChecked(true);
			selectorVariableTo.setVisibility(View.GONE);
		} else {
			selectorVariableTo.setVariable(trigger.withVariable);
			editTextValue.setText("0");
			radioVariable.setChecked(true);
			editTextValue.setVisibility(View.GONE);
		}
		editTextValue.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int value = Integer.parseInt(v.getText().toString());
				trigger.withValue = value;
				return false;
			}
		});
	}

	private void populate() {
		selectorVariableSet.populate(game);
		selectorVariableTo.populate(game);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		populate();

		if (resultCode == RESULT_OK) {
			if (selectorVariableSet.onActivityResult(requestCode, data)) {
				trigger.variable = selectorVariableSet.getVariable();
			}
			if (selectorVariableTo.onActivityResult(requestCode, data)) {
				trigger.withVariable = selectorVariableTo.getVariable();
			}
		}
	}

	@Override
	protected void putExtras(Intent intent) {
		intent.putExtra("trigger", trigger);
	}

	@Override
	protected boolean hasChanged() {
		VariableTrigger originalTrigger = getOriginalTrigger();
		return super.hasChanged() ||
			!trigger.equals(originalTrigger);
	}
}
