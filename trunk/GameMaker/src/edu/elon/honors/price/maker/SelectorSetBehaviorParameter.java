package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.maker.action.EventContext;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

@AutoAssign
public class SelectorSetBehaviorParameter extends LinearLayout 
implements IViewContainer, IPopulatable {

	private PlatformGame game;
	private BehaviorType type;
	private EventContext eventContext;
	
	private Spinner spinnerBehaviors;
	private TextView textViewDescription;
	private Button buttonEdit;
	private ArrayAdapter<Behavior> adapter;
	
	private Behavior behavior;
	private List<Parameters> parameters = new ArrayList<Parameters>();
	
	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
		if (game != null && behavior != null) {
			int index = game.getBehaviors(type).indexOf(behavior);
			if (index >= 0 && index < adapter.getCount()) {
				spinnerBehaviors.setSelection(index);
			}
		}
		resetParameters();
	}
	
	public SelectorSetBehaviorParameter(Activity context, 
			BehaviorType type, EventContext eventContext) {
		super(context);
		this.type = type;
		this.eventContext = eventContext;
		LayoutInflater.from(context).inflate(
				R.layout.selector_set_behavior_parameter, this);
		AutoAssignUtils.autoAssign(this);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edit();
			}
		});
	}
	
	private void edit() {
		if (game != null) {
			int index = game.getBehaviors(type).indexOf(behavior);
			if (index != -1) {
				BehaviorInstance instance = new BehaviorInstance(index, type);
				instance.parameters.addAll(parameters);
				DatabaseEditBehaviorInstance.startForResult((Activity)getContext(), 
						getId(), instance, eventContext);
			}
		}
	}
	
	private void resetParameters() {
		parameters.clear();
		if (behavior != null) {
			for (int i = 0; i < behavior.parameters.size(); i++) {
				parameters.add(null);
			}
		}
	}

	@Override
	public void populate(PlatformGame game) {
		this.game = game;
		final List<Behavior> behaviors = game.getBehaviors(type);
		adapter = new ArrayAdapter<Behavior>(getContext(), 
				R.layout.spinner_text, behaviors);
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		spinnerBehaviors.setAdapter(adapter);
		spinnerBehaviors.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> spinner, View view,
					int index, long id) { 
				Behavior newBehavior = behaviors.get(index); 
				if (behavior != newBehavior) { 
					resetParameters();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		BehaviorInstance instance = 
				DatabaseEditBehaviorInstance.getBehaviorInstance(data);
		if (instance != null) {
			parameters = instance.parameters;
		}
		return true;
	}

}
