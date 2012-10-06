package edu.elon.honors.price.maker;

import java.util.LinkedList;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.data.PlatformGame;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class PageBehaviors extends Page {

	private Spinner spinnerType;
	private RadioGroup radioGroup;
	private Button buttonNew, buttonEdit;

	private LinkedList<Behavior> behaviors;

	public PageBehaviors(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		return R.layout.page_behaviors;
	}

	@Override
	public String getName() {
		return "Behaviors";
	}

	private int getIndex() {
		return radioGroup.indexOfChild(findViewById(
				radioGroup.getCheckedRadioButtonId()));
	}

	private BehaviorType getBehaviorType() {
		return BehaviorType.values()[spinnerType.getSelectedItemPosition()];
	}

	@Override
	public void onCreate() {
		spinnerType = (Spinner)findViewById(R.id.spinnerType);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroupBehaviors);
		buttonNew = (Button)findViewById(R.id.buttonNew);
		buttonEdit = (Button)findViewById(R.id.buttonEdit);

		LinkedList<String> types = new LinkedList<String>();
		for (BehaviorType t : BehaviorType.values()) {
			types.add(t.getName());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent, 
				R.layout.spinner_text, types);
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		spinnerType.setAdapter(adapter);

		spinnerType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int index, long id) {
				populateBehaviors();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
		spinnerType.setSelection(0);

		buttonNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newBehavior();
			}
		});

		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editBehavior();
			}
		});
	}

	private void populateBehaviors() {
		BehaviorType type = getBehaviorType();
		PlatformGame game = getGame();

		behaviors = game.getBehaviors(type);

		radioGroup.removeAllViews();
		for (Behavior behavior : behaviors) {
			RadioButton button = new RadioButton(parent);
			button.setText(behavior.name);
			radioGroup.addView(button);
		}
	}

	private void newBehavior() {
		Behavior behavior = new Behavior(getBehaviorType());
		behaviors.add(behavior);
		RadioButton button = new RadioButton(parent);
		button.setText(behavior.name);
		radioGroup.addView(button);
	}

	private void editBehavior() {
		int index = getIndex();
		if (index >= 0) {
			Intent intent = new Intent(parent, DatabaseEditBehavior.class);
			intent.putExtra("game", getGame());
			intent.putExtra("behavior", behaviors.get(index));
			parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
		}
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		behaviors = getGame().getBehaviors(getBehaviorType());
		int index = getIndex();
		if (index >= 0) {
			Behavior behavior = (Behavior)data.getExtras().
			getSerializable("behavior");
			behaviors.set(index, behavior);
			((RadioButton)radioGroup.getChildAt(index)).
			setText(behavior.name);
		}
	}

}
