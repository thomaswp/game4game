package edu.elon.honors.price.maker;

import java.util.LinkedList;
import edu.elon.honors.price.data.Behavior.Parameter;
import edu.elon.honors.price.data.Behavior.ParameterType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class SelectorActivitySwitch extends SelectorActivityIndex {

	protected RadioGroup radioGroupDefaultValue;
	protected RadioButton radioOn, radioOff;
	
	@Override
	protected String getType() {
		return "Switch";
	}
	
	@Override
	protected void setName(String name) {
		game.switchNames[id] = name;
	}
		
	@Override
	protected void setId(int id) {
		super.setId(id);
		if (game.switchValues[id])
			radioGroupDefaultValue.check(radioOn.getId());
		else
			radioGroupDefaultValue.check(radioOff.getId());
		editTextItemName.setText(game.switchNames[id]);
		textViewId.setText(String.format("%03d", id));
	}
	
	@Override
	protected String makeRadioButtonText(int id) {
		return String.format("%03d: %s", id, game.switchNames[id]);
	}

	@Override
	protected void setupSelectors() {
		RelativeLayout host = (RelativeLayout)findViewById(R.id.relativeLayoutDefault);
		radioGroupDefaultValue = new RadioGroup(this);
		radioGroupDefaultValue.setOrientation(LinearLayout.HORIZONTAL);
		host.addView(radioGroupDefaultValue);
		
		radioOn = new RadioButton(this);
		radioOn.setText("On");
		radioGroupDefaultValue.addView(radioOn);
		radioOff = new RadioButton(this);
		radioOff.setText("Off");
		radioGroupDefaultValue.addView(radioOff);
		
		radioGroupDefaultValue.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				game.switchValues[id] = (checkedId == radioOn.getId());
			}
		});
	}

	@Override
	protected int getItemsSize() {
		return game.switchNames.length;
	}

	@Override
	protected void resizeItems(int newSize) {
		game.resizeSwitches(newSize);
	}

	@Override
	protected String getLocalName(int id) {
		return eventContext.getBehavior().switchNames.get(id);
	}

	@Override
	protected String getLocalDefault(int id) {
		return eventContext.getBehavior().switches.get(id) ?
				"On" : "Off";
	}

	@Override
	protected int getLocalSize() {
		return eventContext.getBehavior().switchNames.size();
	}

	@Override
	protected String getParamName(int id) {
		LinkedList<Parameter> params = 
			eventContext.getBehavior().parameters;
		return params.get(id).name;
	}

	@Override
	protected int getParamSize() {
		return eventContext.getBehavior().parameters.size();
	}
	
	@Override 
	protected boolean getParamVisible(int id) {
		LinkedList<Parameter> params = 
			eventContext.getBehavior().parameters;
		return params.get(id).type == ParameterType.Switch;
	}
}
