package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.data.Behavior.Parameter;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;


@AutoAssign
public class DatabaseEditBehavior extends DatabaseActivity {

	private int REQUEST_EDIT_EVENT = 0;

	private Behavior behavior;
	private EditText editTextName;
	private TextView textViewType;
	private RadioGroup radioGroupEvents, radioGroupSwitches, 
	radioGroupVariables, radioGroupParameters;
	private Button buttonNewEvent, buttonEditEvent, buttonDeleteEvent,
	buttonNewSwitch, buttonEditSwitch, buttonDeleteSwitch,
	buttonNewVariable, buttonEditVariable, buttonDeleteVariable,
	buttonNewParameter, buttonEditParameter, buttonDeleteParameter;
	private ScrollView scrollView;

	private Editor[] editors;
	
	public static void startForResult(DatabaseActivity activity, int requestCode,
			Behavior behavior) {
		Intent intent = activity.getNewGameIntent(DatabaseEditBehavior.class);
		intent.putExtra("behavior", behavior);
		activity.startActivityForResult(intent, requestCode);
	}
	
	public static Behavior getBehaviorResult(Intent data) {
		return (Behavior) data.getSerializableExtra("behavior");
	}

	private Behavior readBehavior() {
		if (getIntent().getExtras().containsKey("behavior")) {
			return (Behavior)getIntent().getExtras().getSerializable("behavior");
		} else {
			BehaviorType type = 
				(BehaviorType)getIntent().getExtras().getSerializable("type");
			return new Behavior(type);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.database_edit_behavior);
		setDefaultButtonActions();

		if (savedInstanceState != null) {
			behavior = (Behavior)savedInstanceState.getSerializable("behavior");
		} else {
			behavior = readBehavior();
		}

		autoAssign();		
		populateFileds();

		editors = new Editor[] {
				new EventEditor(),
				new SwitchEditor(),
				new VariableEditor(),
				new ParameterEditor()
		};

		if (savedInstanceState != null) {
			int[] indices = savedInstanceState.getIntArray("indices");
			for (int i = 0; i < indices.length; i++) {
				if (i < editors.length) {
					if (indices[i] >= 0) {
						RadioButton button = (RadioButton)editors[i].
						getRadioGroup().getChildAt(indices[i]);
						button.setChecked(true);
					}
				}
			}
			final int y = savedInstanceState.getInt("scroll");
			scrollView.post(new Runnable() {
				@Override
				public void run() {
					scrollView.scrollTo(0, y);
				}
			});
		}

		scrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				editTextName.clearFocus();
				return false;
			}
		});

		addButtonEvents();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("behavior", behavior);
		int[] indices = new int[editors.length];
		for (int i = 0; i < editors.length; i++) {
			indices[i] = editors[i].getIndex();
		}
		outState.putIntArray("indices", indices);
		outState.putInt("scroll", scrollView.getScrollY());
	}

	@Override
	public void onFinishing() {
		behavior.name = editTextName.getText().toString();
	}

	@Override
	public boolean hasChanged() {
		return !GameData.areEqual(behavior, readBehavior()) ||
		super.hasChanged();
	}

	@Override
	public void putExtras(Intent intent) {
		super.putExtras(intent);
		intent.putExtra("behavior", behavior);
	}

	private void addButtonEvents() {
		Button[][] buttons = new Button[][] {
				new Button[] {
						buttonNewEvent,
						buttonNewSwitch,
						buttonNewVariable,
						buttonNewParameter
				},

				new Button[] {
						buttonEditEvent,
						buttonEditSwitch,
						buttonEditVariable,
						buttonEditParameter
				},

				new Button[] {
						buttonDeleteEvent,
						buttonDeleteSwitch,
						buttonDeleteVariable,
						buttonDeleteParameter
				},
		};

		for (int i = 0; i < editors.length; i++) {
			final Editor editor = editors[i];
			buttons[0][i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editor.onNew();
				}
			});
			buttons[1][i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editor.onEdit();
				}
			});
			buttons[2][i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editor.onDelete();
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, 
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode >= 0 && requestCode < editors.length) {
				editors[requestCode].onActivityResult(data);
			}
		}
	}

	private void populateFileds() {
		editTextName.setText(behavior.name);

		for (Event event : behavior.events) {
			RadioButton button = new RadioButton(this);
			button.setText(describeEvent(event));
			radioGroupEvents.addView(button);
		}

		textViewType.setText(behavior.type.getName());

		for (int i = 0; i < behavior.switchNames.size(); i++) {
			RadioButton button = new RadioButton(this);
			button.setText(describeSwitch(i));
			radioGroupSwitches.addView(button);
		}

		for (int i = 0; i < behavior.variableNames.size(); i++) {
			RadioButton button = new RadioButton(this);
			button.setText(describeVariable(i));
			radioGroupVariables.addView(button);
		}

		for (Parameter parameter : behavior.parameters) {
			RadioButton button = new RadioButton(this);
			button.setText(describeParameter(
					parameter));
			radioGroupParameters.addView(button);
		}
	}

	private String describeEvent(Event event) {
		return event.name;
	}

	private Spanned describeVariable(int index) {
		String name = behavior.variableNames.get(index);
		Integer value = behavior.variables.get(index);
		return valueString(name, value.toString(), 
				TextUtils.COLOR_VALUE);
	}

	private Spanned describeSwitch(int index) {
		String name = behavior.switchNames.get(index);
		String value = behavior.switches.get(index) ?
				"On" : "Off";
		return valueString(name, value, 
				TextUtils.COLOR_VALUE);
	}

	private Spanned describeParameter(Parameter parameter) {
		return valueString(parameter.name, 
				parameter.type.toString(), TextUtils.COLOR_MODE);
	}

	private Spanned valueString(String name, String value, String color) {
		String cValue = TextUtils.getColoredText(
				value, color);
		String cName = TextUtils.getColoredText(
				name, TextUtils.COLOR_VARIABLE);
		return Html.fromHtml(String.format("%s (%s)", cName, cValue));
	}

	private abstract class Editor {
		protected RadioGroup radioGroup;

		public RadioGroup getRadioGroup() {
			return radioGroup;
		}

		public Editor(RadioGroup radioGroup) {
			this.radioGroup = radioGroup;
		}

		public void onActivityResult(Intent data) {

		}

		protected abstract CharSequence addItem();
		protected abstract void editItem(int index);
		protected abstract boolean deleteItem(int index);

		protected int getIndex() {
			return radioGroup.indexOfChild(findViewById(
					radioGroup.getCheckedRadioButtonId()));
		}

		protected boolean indexInRange(int index) {
			return index >= 0 && index < radioGroup.getChildCount();
		}

		public void onNew() {
			RadioButton button = 
				new RadioButton(DatabaseEditBehavior.this);
			button.setText(addItem());
			radioGroup.addView(button);
		}


		public void onEdit() {
			int index = getIndex();
			if (indexInRange(index)) {
				editItem(index);
			}
		}

		public void onDelete() {
			int index = getIndex();
			if (indexInRange(index)) {
				if (deleteItem(index)) {
					radioGroup.removeViewAt(index);
				}
			}
		}
	}

	private class EventEditor extends Editor {

		public EventEditor() {
			super(radioGroupEvents);
		}

		@Override
		protected CharSequence addItem() {
			Event event = new Event();
			behavior.events.add(event);
			return describeEvent(event);
		}

		@Override
		protected boolean deleteItem(int index) {
			behavior.events.remove(index);
			return true;
		}

		@Override
		public void editItem(int index) {
			Intent intent = new Intent(DatabaseEditBehavior.this,
					DatabaseEditEvent.class);
			intent.putExtra("game", game);
			intent.putExtra("event", behavior.events.get(index));
			intent.putExtra("behavior", behavior);
			startActivityForResult(intent, REQUEST_EDIT_EVENT);
		}

		@Override
		public void onActivityResult(Intent data) {
			Event event = (Event)data.getExtras()
			.getSerializable("event");

			int index = getIndex();
			Debug.write(index);
			behavior.events.remove(index);
			behavior.events.add(index, event);

			((RadioButton)radioGroup.getChildAt(index)).setText(
					describeEvent(event));
		}
	}

	public class SwitchEditor extends Editor {

		public SwitchEditor() {
			super(radioGroupSwitches);
		}

		@Override
		protected CharSequence addItem() {
			behavior.addSwitch();
			return describeSwitch(behavior.switches.size() - 1);
		}

		@Override
		protected void editItem(final int index) {
			View view = getLayoutInflater().inflate(
					R.layout.database_edit_behavior_switch, null);
			final EditText editTextName = 
				(EditText)view.findViewById(R.id.editTextName);
			final RadioButton radioOn = 
				(RadioButton)view.findViewById(R.id.radioOn);

			editTextName.setText(behavior.switchNames.get(index));
			RadioGroup radioGroupValue = 
				(RadioGroup)view.findViewById(R.id.radioGroupValue);
			int radio = behavior.switches.get(index) ? 0 : 1;
			((RadioButton)radioGroupValue.getChildAt(radio)).setChecked(true);

			new AlertDialog.Builder(DatabaseEditBehavior.this)
			.setTitle("Edit Swtich")
			.setView(view)
			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					behavior.switches.set(index, radioOn.isChecked());
					behavior.switchNames.set(index, 
							editTextName.getText().toString());
					((RadioButton)radioGroup.getChildAt(index)).setText(
							describeSwitch(index));
				}
			})
			.setNegativeButton("Cancel", null)
			.show();
		}

		@Override
		protected boolean deleteItem(int index) {
			boolean success = 
				behavior.removeSwitch(index);
			if (!success) {
				showAlert("Cannot Delete",
					"This switch is currently in use " +
					"by one or more Events in this behavior " +
					"and cannot be deleted.");
			}
			return success;
		}

	}

	public class VariableEditor extends Editor {

		public VariableEditor() {
			super(radioGroupVariables);
		}

		@Override
		protected CharSequence addItem() {
			behavior.addVariable();
			return describeVariable(behavior.variables.size() - 1);
		}

		@Override
		protected void editItem(final int index) {
			View view = getLayoutInflater().inflate(
					R.layout.database_edit_behavior_variable, null);
			final EditText editTextName = 
				(EditText)view.findViewById(R.id.editTextName);
			final EditText editTextValue = 
				(EditText)view.findViewById(R.id.editTextValue);

			editTextName.setText(behavior.variableNames.get(index));
			editTextValue.setText(behavior.variables.get(index).toString());

			new AlertDialog.Builder(DatabaseEditBehavior.this)
			.setTitle("Edit Variable")
			.setView(view)
			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int value = 0;
					try {
						value = Integer.parseInt(
								editTextValue.getText().toString());
					} catch (NumberFormatException e) { }
					behavior.variables.set(index, value);
					behavior.variableNames.set(index, 
							editTextName.getText().toString());
					((RadioButton)radioGroup.getChildAt(index)).setText(
							describeVariable(index));
				}
			})
			.setNegativeButton("Cancel", null)
			.show();
		}

		@Override
		protected boolean deleteItem(int index) {
			boolean success = behavior.removeVariable(index);
			if (!success) {
				showAlert("Cannot Delete",
					"This variable is currently in use " +
					"by one or more Events in this behavior " +
					"and cannot be deleted.");
			}
			return success;
		}

	}

	public class ParameterEditor extends Editor {

		public ParameterEditor() {
			super(radioGroupParameters);
		}

		@Override
		protected CharSequence addItem() {
			Parameter param = new Parameter();
			behavior.parameters.add(param);
			return describeParameter(param);
		}

		@Override
		protected void editItem(final int index) {
			View view = getLayoutInflater().inflate(
					R.layout.database_edit_behavior_param, null);
			final EditText editTextName = 
				(EditText)view.findViewById(R.id.editTextName);
			final RadioGroup radioGroupType = 
				(RadioGroup)view.findViewById(R.id.radioGroupType);
			final Parameter param = behavior.parameters.get(index);

			editTextName.setText(param.name);
			int radio = param.type == ParameterType.Switch ? 0 : 1;
			((RadioButton)radioGroupType.getChildAt(radio)).setChecked(true);

			new AlertDialog.Builder(DatabaseEditBehavior.this)
			.setTitle("Edit Parameter")
			.setView(view)
			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					param.name = editTextName.getText().toString();
					if (((RadioButton)radioGroupType.getChildAt(0)).isChecked()) {
						param.type = ParameterType.Switch;
					} else {
						param.type = ParameterType.Variable;
					}

					((RadioButton)radioGroup.getChildAt(index)).setText(
							describeParameter(param));
				}
			})
			.setNegativeButton("Cancel", null)
			.show();
		}

		@Override
		protected boolean deleteItem(int index) {
			boolean success = behavior.removeParameter(index);
			if (!success) {
				showAlert("Cannot Delete",
					"This parameter is currently in use " +
					"by one or more Events in this behavior " +
					"and cannot be deleted.");
			}
			return success;
		}

	}
}
