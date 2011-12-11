package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.Stack;

import edu.elon.honors.price.data.ActionIds;
import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DatabaseEditEvent extends DatabaseActivity {

	private static final String[] TRIGGER_TYPES = new String[] {
		"Switch Trigger",
		"Variable Trigger",
		"Actor Trigger",
		"Region Trigger"
	};

	public static final String COLOR_VARIABLE = "#00CC00";
	public static final String COLOR_MODE = "#FFCC00";
	public static final String COLOR_VALUE = "#5555FF";
	public static final String COLOR_ACTION = "#8800FF";

	private int id;
	private EditText editTextName;
	private LinearLayout linearLayoutTriggers, linearLayoutActions;
	private ReturnResponse returnResponse;

	private Event getEvent() {
		return game.getSelectedMap().events[id];
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

		//Must set one view to focusableInTouchMode
		ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
		scroll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (editTextName.hasFocus()) {
					editTextName.clearFocus();
				}
				return false;
			}
		});

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
				newAction();
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

	private void newAction() {
		Intent intent = new Intent(DatabaseEditEvent.this, 
				DatabaseNewAction.class);
		intent.putExtra("game", game);

		returnResponse = new ReturnResponse() {
			@Override
			void run(Intent data) {
				Action action = (Action)data.getExtras().
				getSerializable("action");
				getEvent().actions.add(action);
				populateViews();
			}
		};

		startActivityForResult(intent, REQUEST_RETURN_GAME);
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
					break;
				case 3:
					intent = new Intent(DatabaseEditEvent.this,
							DatabaseEditTriggerRegion.class);
					break;
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

		linearLayoutActions.removeAllViews();
		Stack<LinearLayout> hosts = new Stack<LinearLayout>();
		hosts.add(linearLayoutActions);

		for (int i = 0; i < event.actions.size(); i++) {
			final Action action = event.actions.get(i);

			while (hosts.size() - 1 > action.indent) {
				View button = createAddActionButton(hosts.size() - 1, i);
				LinearLayout layout = hosts.pop();
				layout.addView(button);
				hosts.peek().addView(layout);
			}

			ActionView av = new ActionView(this, i);
			hosts.peek().addView(av);
			if (hosts.peek().getChildCount() == 1 && hosts.size() > 1) {
				int res = hosts.size() % 2 == 0 ?
						R.drawable.border_green : R.drawable.border_blue;
				hosts.peek().setBackgroundDrawable(
						getResources().getDrawable(res));
			}


			if (action.canHaveChildren()) {
				LinearLayout layout = new LinearLayout(this);
				layout.setOrientation(LinearLayout.VERTICAL);
				LinearLayout.LayoutParams lp = 
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT, 
							LinearLayout.LayoutParams.FILL_PARENT);
				lp.setMargins(20, 0, 0, 0);
				layout.setLayoutParams(lp);
				hosts.push(layout);
			}
		}

		while (hosts.size() > 1) {
			View button = createAddActionButton(hosts.size() - 1, 
					event.actions.size());
			LinearLayout layout = hosts.pop();
			layout.addView(button);
			hosts.peek().addView(layout);
		}
	}

	private View createAddActionButton(final int indent, final int index) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		//		for (int j = 0; j < indent; j++) {
		//			TextView indentTV = new TextView(this);
		//			indentTV.setTextSize(20);
		//			indentTV.setText("\u21B3");
		//			layout.addView(indentTV);
		//		}
		Button button = new Button(this);
		button.setText("Add Action");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newIndentAction(index, indent);
			}
		});
		layout.addView(button);

		return layout;
	}

	private void newIndentAction(final int index, final int indent) {
		returnResponse = new ReturnResponse() {
			@Override
			void run(Intent data) {
				Action a = (Action)data.getExtras()
				.getSerializable("action");
				a.indent = indent;
				getEvent().actions.add(index, a);
				populateViews();
			}
		};
		Intent intent = new Intent(DatabaseEditEvent.this, 
				DatabaseNewAction.class);
		intent.putExtra("game", game);
		startActivityForResult(intent, REQUEST_RETURN_GAME);
	}

	private abstract static class ReturnResponse {
		abstract void run(Intent data);
	}

	private class ActionView extends LinearLayout {
		private int index;

		public Action getAction() {
			return getEvent().actions.get(index);
		}

		public ActionView(final Context context, int actionIndex) {
			super(context);

			index = actionIndex;

			setGravity(Gravity.CENTER_VERTICAL);

			//			for (int i = 0; i < getAction().indent; i++) {
			//				TextView indent = new TextView(context);
			//				indent.setTextSize(20);
			//				indent.setText("\u21B3");
			//				addView(indent);
			//			}

			TextView tv = createTextView();
			addView(tv);

			Button buttonOptions = new Button(context);
			buttonOptions.setText("!");
			buttonOptions.setWidth(50);
			buttonOptions.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(context).setItems(
							new String[] { "Edit", "Insert", "Delete" },
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
										case 0: edit();	break;
										case 1: insert(); break;
										case 2: delete(); break;
									}
								}
							}
					).show();
				}
			});
			
			addView(buttonOptions);

			//			Button buttonEdit = new Button(context);
			//			buttonEdit.setText("Edit");
			//			buttonEdit.setWidth(100);
			//			buttonEdit.setOnClickListener(new OnClickListener() {
			//				@Override
			//				public void onClick(View v) {
			//					edit();
			//				}
			//			});
			//			addView(buttonEdit);
			//
			//			Button buttonInsert = new Button(context);
			//			buttonInsert.setText("Insert");
			//			buttonInsert.setWidth(100);
			//			buttonInsert.setOnClickListener(new OnClickListener() {
			//				@Override
			//				public void onClick(View v) {
			//					insert();
			//				}
			//			});
			//			addView(buttonInsert);
			//
			//			Button buttonDelete = new Button(context);
			//			buttonDelete.setText("Delete");
			//			buttonDelete.setWidth(100);
			//			buttonDelete.setOnClickListener(new OnClickListener() {
			//				@Override
			//				public void onClick(View v) {
			//					delete();
			//				}
			//			});
			//			addView(buttonDelete);

		}

		private void edit() {
			returnResponse = new ReturnResponse() {
				@Override
				void run(Intent data) {
					Action action = (Action)data.getExtras()
					.getSerializable("action");
					action.indent = getAction().indent;
					getEvent().actions.remove(index);
					getEvent().actions.add(index, action);
					populateViews();
				}
			};
			Intent intent = new Intent(DatabaseEditEvent.this, 
					DatabaseEditAction.class);
			intent.putExtra("game", game);
			intent.putExtra("id", getAction().id);
			intent.putExtra("params", getAction().params);
			startActivityForResult(intent, REQUEST_RETURN_GAME);
		}

		private void insert() {
			Intent intent = new Intent(DatabaseEditEvent.this, 
					DatabaseNewAction.class);
			intent.putExtra("game", game);

			returnResponse = new ReturnResponse() {
				@Override
				void run(Intent data) {
					Action action = (Action)data.getExtras().
					getSerializable("action");
					action.indent = getAction().indent;
					getEvent().actions.add(index, action);
					populateViews();
				}
			};

			startActivityForResult(intent, REQUEST_RETURN_GAME);
		}

		private void delete() {
			int toRemove = 1;
			final ArrayList<Action> actions = getEvent().actions;
			for (int i = index + 1; i < actions.size(); i++) {
				if (actions.get(i).indent > actions.get(index).indent) {
					toRemove++;
				} else {
					break;
				}
			}
			final int toRemoveF = toRemove;
			if (toRemove == 1) {
				getEvent().actions.remove(index);
				populateViews();
			} else {
				new AlertDialog.Builder(getContext())
				.setTitle("Delete?")
				.setMessage("This will delete all the actions inside this statement. Are you sure you want to delete?")
				.setPositiveButton("Delete", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < toRemoveF; i++) {
							actions.remove(index);
							populateViews();
						}
					}
				})
				.setNegativeButton("Cancel", null)
				.show();
			}
		}

		private TextView createTextView() {
			LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					10, getResources().getDisplayMetrics());
			TextView tv = new TextView(getContext());
			//tv.setWidth(200);
			tv.setSingleLine(false);
			tv.setTextSize(16);
			tv.setLayoutParams(lp);
			tv.requestLayout();

			tv.setText(Html.fromHtml(getAction().description));
			return tv;
		}
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

			TextView tv = createTextView();
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

		private TextView createTextView() {
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

		private String getTriggerText(SwitchTrigger trigger) {
			StringBuilder sb = new StringBuilder();

			TextUtils.addColoredText(sb, game.switchNames[trigger.switchId], COLOR_VARIABLE);
			sb.append(" turns ");
			TextUtils.addColoredText(sb, trigger.value ? "On" : "Off", COLOR_VALUE);

			return sb.toString();
		}

		private String getTriggerText(VariableTrigger trigger) {
			StringBuilder sb = new StringBuilder();

			TextUtils.addColoredText(sb, game.variableNames[trigger.variableId], COLOR_VARIABLE);
			sb.append(" is ");
			TextUtils.addColoredText(sb, VariableTrigger.OPERATORS[trigger.test], COLOR_MODE);
			sb.append(" ");
			if (trigger.with == VariableTrigger.WITH_VALUE) {
				TextUtils.addColoredText(sb, trigger.valueOrId, COLOR_VALUE);
			} else {
				TextUtils.addColoredText(sb, game.variableNames[trigger.valueOrId], COLOR_VARIABLE);
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

				TextUtils.addColoredText(sb, actorString, COLOR_VARIABLE);
			} else {
				sb.append("A ");
				TextUtils.addColoredText(sb, game.actors[trigger.id].name, COLOR_VARIABLE);
			}
			sb.append(" ");
			TextUtils.addColoredText(sb, ActorTrigger.ACTIONS[trigger.action], COLOR_MODE);

			return sb.toString();
		}

		private String getTriggerText(RegionTrigger trigger) {
			StringBuilder sb = new StringBuilder();

			if (trigger.onlyHero) {
				TextUtils.addColoredText(sb, game.hero.name, COLOR_VARIABLE);
			} else {
				sb.append("An actor");
			}
			sb.append(" ");
			TextUtils.addColoredText(sb, RegionTrigger.MODES[trigger.mode], COLOR_MODE);
			sb.append(" the region (");
			TextUtils.addColoredText(sb, trigger.left, COLOR_VALUE);
			sb.append(", ");
			TextUtils.addColoredText(sb, trigger.top, COLOR_VALUE);
			sb.append(", ");
			TextUtils.addColoredText(sb, trigger.right, COLOR_VALUE);
			sb.append(", ");
			TextUtils.addColoredText(sb, trigger.bottom, COLOR_VALUE);
			sb.append(")");

			return sb.toString();
		}
	}
}