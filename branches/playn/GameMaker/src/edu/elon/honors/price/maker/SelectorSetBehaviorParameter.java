package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.action.EventContext;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
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
	
	private int behaviorIndex;
	private List<Parameters> parameters = new ArrayList<Parameters>();
	private String description;
	
	public Behavior getBehavior() {
		int index = behaviorIndex;
		if (adapter != null && index >= 0 && index < adapter.getCount()) {
			return adapter.getItem(index);
		}
		return null;
	}
	
	public void setBehaviorIndex(int index) {
		Debug.write("set: %d", index);
		if (index != behaviorIndex) {
			behaviorIndex = index;
			resetParameters();
			if (adapter != null) {
				spinnerBehaviors.setSelection(index);
			}
		}
	}
	
	public int getBehaviorIndex() {
		Debug.write("get: %d", behaviorIndex);
		return behaviorIndex;
	}
	
	public List<Parameters> getParameters() {
		return parameters;
	}
	
	public void setParameters(List<Parameters> parameters) {
		this.parameters = parameters;
		
		Behavior behavior = getBehavior();
		if (behavior != null) {
			BehaviorInstance instance = new BehaviorInstance(behaviorIndex, type);
			instance.parameters.addAll(parameters);
			description = SelectorBehaviorInstances.getBehaviorNameHtml(getContext(), game, instance); 
		} else {
			description = "[None]";
		}
		textViewDescription.setText(Html.fromHtml(description));
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
		if (getBehavior() != null) {
			BehaviorInstance instance = new BehaviorInstance(behaviorIndex, type);
			instance.parameters.addAll(parameters);
			Debug.write(instance.parameters);
			Debug.write(parameters);
			DatabaseEditBehaviorInstance.startForResult((Activity)getContext(), 
					getId(), game, instance, eventContext);
		}
	}
	
	private void resetParameters() {
		parameters.clear();
		setParameters(parameters);
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
				if (behaviorIndex != index) {
					behaviorIndex = index;
					resetParameters();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});

		if (getBehavior() != null) {
			spinnerBehaviors.setSelection(behaviorIndex);
		}
		
		setParameters(parameters);
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		BehaviorInstance instance = 
				DatabaseEditBehaviorInstance.getBehaviorInstance(data);
		if (instance != null) {
			setParameters(instance.parameters);
		}
		return true;
	}
	
	public String getDescription() {
		Behavior behavior = getBehavior();
		if (behavior == null) {
			return "[Nothing]";
		} else {
			return description;
		}
	}

}
