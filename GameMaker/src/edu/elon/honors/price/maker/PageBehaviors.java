package edu.elon.honors.price.maker;

import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.game.Debug;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class PageBehaviors extends PageList<Behavior> {

	private final static String PREF_SPINNER_INDEX = "spinnerIndex";
	
	private Spinner spinnerType;
	
	private BehaviorType getBehaviorType() {
		return BehaviorType.values()[spinnerType.getSelectedItemPosition()];
	}
	
	private List<Behavior> getBehaviors() {
		return getGame().getBehaviors(getBehaviorType());
	}
	
	public PageBehaviors(Database parent) {
		super(parent);
	}
	
	@Override
	public void onCreate(ViewGroup parentView) {
		LinkedList<String> types = new LinkedList<String>();
		for (BehaviorType t : BehaviorType.values()) {
			types.add(t.getName());
		}
		
		spinnerType = new Spinner(parent);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent, 
				R.layout.spinner_text, types);
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		spinnerType.setAdapter(adapter);

		spinnerType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int index, long id) {
				getAdapter().clear();
				for (Behavior b : getBehaviors()) {
					getAdapter().add(b);
				}
				getAdapter().notifyDataSetChanged();
				listView.setAdapter(getAdapter());
				listView.invalidateViews();
				Debug.write("Reset to: %d", getAdapter().getCount());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		int index = getIntPreference(PREF_SPINNER_INDEX, 0);
		if (index < 0 || index >= types.size()) index = 0; 
		spinnerType.setSelection(index);
		
		
		super.onCreate(parentView);
		addPanelView(spinnerType);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		putPreference(PREF_SPINNER_INDEX, 
				spinnerType.getSelectedItemPosition());
	}

	@Override
	protected void editItem(int index) {
		DatabaseEditBehavior.startForResult(parent,
				REQUEST_EDIT_ITEM, getBehaviors().get(index));
	}

	@Override
	protected void resetItem(int index) {
		getBehaviors().set(index, new Behavior(getBehaviorType()));
	}

	@Override
	protected void addItem() {
		getBehaviors().add(new Behavior(getBehaviorType()));
	}

	@Override
	protected CheckableArrayAdapter<Behavior> getAdapter() {
		return new CheckableArrayAdapter<Behavior>(parent, 
				R.layout.array_adapter_row_string, getBehaviors()) {
			@Override
			protected void setRow(int position, Behavior item, View view) {
				TextView tv = (TextView) view.findViewById(R.id.textViewTitle);
				String name = item.name;
//				switch (item.type) {
//				case Actor: 
//					name += " (A)"; 
//					break;
//				case Object: 
//					name += " (O)"; 
//					break;
//				case Map: 
//					name += " (M)"; 
//					break;
//				}
				tv.setText(name);
			}
		};
	}

	@Override
	protected Behavior getItem(int index) {
		return getGame().getBehaviors(getBehaviorType()).get(index);
	}

	@Override
	protected String getItemCategory() {
		return "Behavior";
	}

	@Override
	public String getName() {
		return "Behaviors";
	}

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		Behavior behavior = DatabaseEditBehavior.getBehaviorResult(data);
		int index = getSelectedIndex();
		if (behavior != null && index >= 0) {
			getBehaviors().set(index, behavior);
		}
		super.onActivityResult(requestCode, data);
	}
}
