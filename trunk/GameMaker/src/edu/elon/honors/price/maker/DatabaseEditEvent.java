package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Event.ActorTrigger;
import edu.elon.honors.price.data.Event.RegionTrigger;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.game.Game;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DatabaseEditEvent extends DatabaseActivity {
	
	private static final String[] TRIGGER_TYPES = new String[] {
		"Switch Trigger",
		"Variable Trigger",
		"Actor Trigger",
		"Region Trigger"
	};
	
	private static final String COLOR_VARIABLE = "#00CC00";
	private static final String COLOR_MODE = "#FFCC00";
	private static final String COLOR_VALUE = "#5555FF";
	
	private int id;
	private EditText editTextName;
	private LinearLayout linearLayoutTriggers, linearLayoutActions;
	private ReturnResponse returnResponse;
	
	private Event getEvent() {
		return game.getSelectedMap().events.get(id);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		id = getIntent().getExtras().getInt("id");
		setContentView(R.layout.database_edit_event);
		
		setDefaultButtonActions();
		
		editTextName = (EditText)findViewById(R.id.editTextName);
		linearLayoutTriggers = (LinearLayout)findViewById(R.id.linearLayoutTriggers);
		linearLayoutActions = (LinearLayout)findViewById(R.id.linearLayoutActions);
		Button buttonNewTrigger = (Button)findViewById(R.id.buttonNewTrigger);
		Button buttonNewAction = (Button)findViewById(R.id.buttonNewAction);
		
		editTextName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				getEvent().name = v.getText().toString();
				return false;
			}
		});
		
		buttonNewTrigger.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newTrigger();
			}
		});
		
		buttonNewAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		populateViews();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK && returnResponse != null) {
			returnResponse.run(data);
			returnResponse = null;
		}
	}
	
	private void newTrigger() {
		new AlertDialog.Builder(DatabaseEditEvent.this)
		.setTitle("New Trigger")
		//.setMessage("Select a type of Trigger:")
		.setItems(TRIGGER_TYPES, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				switch (which) {
				case 0:
					intent = new Intent(DatabaseEditEvent.this, 
							DatabaseEditTriggerSwitch.class);
					break;
				case 1:
					intent = new Intent(DatabaseEditEvent.this,
							DatabaseEditTriggerVariable.class);
					break;
				case 2:
					intent = new Intent(DatabaseEditEvent.this,
							DatabaseEditTriggerActor.class);
				case 3:
					intent = new Intent(DatabaseEditEvent.this,
							DatabaseEditTriggerRegion.class);
				}
				
				if (intent != null) {
					intent.putExtra("game", game);
					
					returnResponse = new ReturnResponse() {
						@Override
						void run(Intent data) {
							Trigger trigger = (Trigger)data.getExtras().
									getSerializable("trigger");
							getEvent().triggers.add(trigger);
							populateViews();
						}
					};
					startActivityForResult(intent, REQUEST_RETURN_GAME);
				}
			}
		})
		//.setNegativeButton("Cancel", null)
		.show();
	}
	
	private void populateViews() {
		Event event = getEvent(); 
		
		editTextName.setText(event.name);
		
		linearLayoutTriggers.removeAllViews();
		for (int i = 0; i < event.triggers.size(); i++) {
			TriggerView tv = new TriggerView(this, i);
			linearLayoutTriggers.addView(tv);
		}
	}
	
	private abstract static class ReturnResponse {
		abstract void run(Intent data);
	}
	
	private class TriggerView extends LinearLayout {

		private int index;
		
		private Trigger getTrigger() {
			return getEvent().triggers.get(index);
		}
		
		public TriggerView(Context context, int triggerIndex) {
			super(context);
			
			this.index = triggerIndex;
			
			setGravity(Gravity.CENTER_VERTICAL);
			
			TextView tv = creatTextView();
			addView(tv);
			
			Button buttonEdit = new Button(context);
			buttonEdit.setText("Edit");
			buttonEdit.setWidth(100);
			buttonEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					returnResponse = new ReturnResponse() {
						@Override
						void run(Intent data) {
							Trigger trigger = (Trigger)data.getExtras().getSerializable("trigger");
							ArrayList<Trigger> triggers = getEvent().triggers;
							triggers.remove(index);
							triggers.add(index, trigger);
							populateViews();
						}
					};
					Intent intent = new Intent(getContext(), getEditorClass());
					intent.putExtra("game", game);
					intent.putExtra("trigger", getTrigger());
					startActivityForResult(intent, REQUEST_RETURN_GAME);
				}
			});
			addView(buttonEdit);
			
			Button buttonDelete = new Button(context);
			buttonDelete.setText("Delete");
			buttonDelete.setWidth(100);
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getEvent().triggers.remove(index);
					populateViews();
				}
			});
			addView(buttonDelete);
		}
		
		private Class<?> getEditorClass() {
			Trigger trigger = getTrigger();
			
			if (trigger instanceof SwitchTrigger) {
				return DatabaseEditTriggerSwitch.class;
			} else if (trigger instanceof VariableTrigger) {
				return DatabaseEditTriggerVariable.class;
			} else if (trigger instanceof ActorTrigger) {
				return DatabaseEditTriggerActor.class;
			} else if (trigger instanceof RegionTrigger) {
				return DatabaseEditTriggerRegion.class;
			} 
			
			return null;
		}
		
		private TextView creatTextView() {
			Trigger trigger = getTrigger();
			
			
			LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					10, getResources().getDisplayMetrics());
			TextView tv = new TextView(getContext());
			tv.setWidth(200);
			tv.setSingleLine(false);
			tv.setTextSize(16);
			tv.setLayoutParams(lp);
			tv.requestLayout();
			
			
			
			String text = null;
			if (trigger instanceof SwitchTrigger) {
				text = getTriggerText((SwitchTrigger)trigger);
			} else if (trigger instanceof VariableTrigger) {
				text = getTriggerText((VariableTrigger)trigger);
			} else if (trigger instanceof ActorTrigger) {
				text = getTriggerText((ActorTrigger)trigger);
			} else if (trigger instanceof RegionTrigger) {
				text = getTriggerText((RegionTrigger)trigger);
			}
			
			tv.setText(Html.fromHtml(text));
			return tv;
		}
		
		private void addColoredText(StringBuilder sb, int text, String color) {
			addColoredText(sb, "" + text, color);
		}
		
		private void addColoredText(StringBuilder sb, String text, String color) {
			sb.append("<font color='")
			.append(color)
			.append("'>")
			.append(text)
			.append("</font>");
		}
		
		private String getTriggerText(SwitchTrigger trigger) {
			StringBuilder sb = new StringBuilder();
			
			addColoredText(sb, game.switchNames[trigger.switchId], COLOR_VARIABLE);
			sb.append(" turns ");
			addColoredText(sb, trigger.value ? "On" : "Off", COLOR_VALUE);
			
			return sb.toString();
		}
		
		private String getTriggerText(VariableTrigger trigger) {
			StringBuilder sb = new StringBuilder();
			
			addColoredText(sb, game.variableNames[trigger.variableId], COLOR_VARIABLE);
			sb.append(" is ");
			addColoredText(sb, VariableTrigger.OPERATORS[trigger.test], COLOR_MODE);
			sb.append(" ");
			if (trigger.with == VariableTrigger.WITH_VALUE) {
				addColoredText(sb, trigger.valueOrId, COLOR_VALUE);
			} else {
				addColoredText(sb, game.variableNames[trigger.valueOrId], COLOR_VARIABLE);
			}
			
			return sb.toString();
		}
		
		private String getTriggerText(ActorTrigger trigger) {
			StringBuilder sb = new StringBuilder();
			
			if (trigger.forInstance) {
				ActorInstance instance = game.getSelectedMap().actors.get(trigger.id);
				
				String actorString;
				if (instance.classIndex > 0)
					actorString = String.format("%s %03d", 
							game.actors[instance.classIndex].name, instance.id);
				else
					actorString = game.hero.name;
				
				addColoredText(sb, actorString, COLOR_VARIABLE);
			} else {
				sb.append("A ");
				addColoredText(sb, game.actors[trigger.id].name, COLOR_VARIABLE);
			}
			sb.append(" ");
			addColoredText(sb, ActorTrigger.ACTIONS[trigger.action], COLOR_MODE);
			
			return sb.toString();
		}
		
		private String getTriggerText(RegionTrigger trigger) {
			StringBuilder sb = new StringBuilder();
			
			if (trigger.onlyHero) {
				addColoredText(sb, game.hero.name, COLOR_VARIABLE);
			} else {
				sb.append("An actor");
			}
			sb.append(" ");
			addColoredText(sb, RegionTrigger.MODES[trigger.mode], COLOR_MODE);
			sb.append(" the region (")
			.append(trigger.left).append(", ")
			.append(trigger.top).append(", ")
			.append(trigger.right).append(", ")
			.append(trigger.bottom).append(")");
			
			return sb.toString();
		}
	}
}