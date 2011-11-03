package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.game.Game;
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

	private VariableTrigger trigger, originalTrigger;

	private SelectorVariable selectorVariableSet, selectorVariableTo;
	private RadioButton radioVariable, radioValue;
	private EditText editTextValue;
	private Spinner spinnerOperator;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_variable);
		setDefaultButtonActions();

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			originalTrigger = (VariableTrigger)extras.getSerializable("trigger");
			trigger = (VariableTrigger)extras.getSerializable("trigger");
		} else {
			originalTrigger = new VariableTrigger();
			trigger = new VariableTrigger();
		}

		selectorVariableSet = (SelectorVariable)findViewById(R.id.selectorVariable1);
		selectorVariableTo = (SelectorVariable)findViewById(R.id.selectorVariable2);

		radioValue = (RadioButton)findViewById(R.id.radioValue);
		radioValue.setChecked(trigger.with == VariableTrigger.WITH_VALUE);
		radioVariable = (RadioButton)findViewById(R.id.radioVariable);
		radioVariable.setChecked(trigger.with == VariableTrigger.WITH_VARIABLE);

		editTextValue = (EditText)findViewById(R.id.editTextValue);
		spinnerOperator = (Spinner)findViewById(R.id.spinnerOperator);

		populate();

		selectorVariableSet.setVariableId(trigger.variableId);

		spinnerOperator.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item, VariableTrigger.OPERATORS));
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
				Game.debug("changed");
				selectorVariableTo.setVisibility(isChecked ? View.GONE : View.VISIBLE);
				editTextValue.setVisibility(!isChecked ? View.GONE : View.VISIBLE);
				trigger.with = isChecked ? VariableTrigger.WITH_VALUE :
					VariableTrigger.WITH_VARIABLE;
			}
		});

		if (trigger.with == VariableTrigger.WITH_VALUE) {
			selectorVariableTo.setVariableId(0);
			editTextValue.setText("" + trigger.variableId);
			radioValue.setChecked(true);
			selectorVariableTo.setVisibility(View.GONE);
		} else {
			selectorVariableTo.setVariableId(trigger.variableId);
			editTextValue.setText("0");
			radioVariable.setChecked(true);
			editTextValue.setVisibility(View.GONE);
		}
		editTextValue.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int value = Integer.parseInt(v.getText().toString());
				trigger.valueOrId = value;
				return false;
			}
		});
	}

	private void populate() {
		selectorVariableSet.populate(game);
		selectorVariableTo.populate(game);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		populate();

		if (resultCode == RESULT_OK) {
			if (selectorVariableSet.onActivityResult(requestCode, data)) {
				trigger.variableId = selectorVariableSet.getVariableId();
			}
			if (selectorVariableTo.onActivityResult(requestCode, data)) {
				trigger.valueOrId = selectorVariableTo.getVariableId();
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
