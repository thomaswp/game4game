package edu.elon.honors.price.maker;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.data.Behavior.ParameterType;
import edu.elon.honors.price.data.BehaviorInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Behavior.Parameter;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.data.types.Variable;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.action.Element;
import edu.elon.honors.price.maker.action.ElementBoolean;
import edu.elon.honors.price.maker.action.ElementNumber;
import edu.elon.honors.price.maker.action.ElementSwitch;
import edu.elon.honors.price.maker.action.ElementVariable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SelectorBehaviorInstances extends LinearLayout 
implements IPopulatable{

	private RadioGroup radioGroupBehaviors;
	private Button buttonNew, buttonEdit, buttonDelete;
	
	private List<BehaviorInstance> behaviors;
	private BehaviorType type;
	private PlatformGame game;
	
	public List<BehaviorInstance> getBehaviors() {
		return behaviors;
	}
	
	public void setBehaviors(List<BehaviorInstance> behaviors,
			 BehaviorType type) {
		this.behaviors = behaviors;
		this.type = type;
		createRadios();
	}
	
	public SelectorBehaviorInstances(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SelectorBehaviorInstances(Context context) {
		super(context);
		init();
	}
	
	private void init() {		
		LayoutInflater.from(getContext()).inflate(
				R.layout.selector_behavior_instances, this);
		
		radioGroupBehaviors = 
			(RadioGroup)findViewById(R.id.radioGroupBehaviors);
		buttonNew = (Button)findViewById(R.id.buttonNewBehavior);
		buttonEdit = (Button)findViewById(R.id.buttonEditBehavior);
		buttonDelete = (Button)findViewById(R.id.buttonDeleteBehavior);
		
		buttonNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				List<Behavior> behaviors = game.getBehaviors(type);
				String[] choices = new String[behaviors.size()];
				for (int i = 0; i < behaviors.size(); i++) {
					choices[i] = behaviors.get(i).name;
				}
				new AlertDialog.Builder(getContext())
				.setTitle("Add Behavior")
				.setItems(choices, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BehaviorInstance instance = 
							new BehaviorInstance(which, type);
						SelectorBehaviorInstances.this
						.behaviors.add(instance);
						addButton(instance);
					}
				})
				.setNegativeButton("Cancel", null)
				.show();
			}
		});
		
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final int index = getSelectedIndex();
				if (index < 0) return;
				BehaviorInstance instance = behaviors.get(index);
				Intent intent = new Intent(getContext(), 
						DatabaseEditBehaviorInstance.class);
				intent.putExtra("game", game);
				intent.putExtra("instance", instance);
				intent.putExtra("index", index);
				((Activity)getContext()).startActivityForResult(intent, 
						getId());
			}
		});
		
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
	}
	
	private int getSelectedIndex() {
		return radioGroupBehaviors.indexOfChild(
				radioGroupBehaviors.findViewById(
					radioGroupBehaviors.getCheckedRadioButtonId()));
	}
	
	private void createRadios() {
		if (game == null || behaviors == null) return;
		int index = getSelectedIndex();
		radioGroupBehaviors.removeAllViews();
		for (BehaviorInstance behavior : behaviors) {
			addButton(behavior);
		}
		if (index >= 0) {
			((RadioButton)radioGroupBehaviors.getChildAt(index))
			.setChecked(true);
		}
	}
	
	private void addButton(BehaviorInstance behavior) {
		RadioButton button = new RadioButton(getContext());
		Spanned name = getBehaviorName(behavior);
		button.setText(name);
		radioGroupBehaviors.addView(button);
	}
	
	private Spanned getBehaviorName(BehaviorInstance behavior) {
		StringBuilder sb = new StringBuilder();
		Behavior base = behavior.getBehavior(game);
		TextUtils.addColoredText(sb, base.name, TextUtils.COLOR_ACTION);
		sb.append(" (");
		
		List<Object> params = behavior.parameters;
		
		if (params.size() != base.parameters.size()) {
			params.clear();
			for (int i = 0; i < base.parameters.size(); i++) {
				params.add(null);
			}
		}
		
		for (int i = 0; i < base.parameters.size(); i++) {
			if (i > 0) sb.append(", ");
			Parameter baseParam = base.parameters.get(i);
			TextUtils.addColoredText(sb, baseParam.name, 
					TextUtils.COLOR_MODE);
			sb.append(" = ");
			Object param = params.get(i);
			String name = TextUtils.HTMLEscape("<None>");
			name = TextUtils.getColoredText(name, Color.LTGRAY);
			if (param != null && param instanceof Parameters) {
				Element element = null;
				if (baseParam.type == ParameterType.Switch) {
					element = new ElementBoolean(null, getContext());
					//dumb work around for "on"/"off" not being inited
					((ElementBoolean )element).genView();
				} else if (baseParam.type == ParameterType.Variable) {
					element = new ElementNumber(null, getContext());
				}
				
				if (element != null) {
					DatabaseActivity.populateViews(element.getView(), game);
					element.setParameters((Parameters) param);
					name = element.getDescription(game);
				}
			}
			sb.append(name);		
		}
		
		sb.append(")");
		
		return Html.fromHtml(sb.toString());
	}

	@Override
	public void populate(PlatformGame game) {
		this.game = game;
		createRadios();
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			int index = data.getIntExtra("index", 0);
			BehaviorInstance instance = (BehaviorInstance)
				data.getSerializableExtra("instance");
			behaviors.set(index, instance);
			((RadioButton)radioGroupBehaviors.getChildAt(index))
			.setText(getBehaviorName(instance));
		}
		return false;
	}

}
