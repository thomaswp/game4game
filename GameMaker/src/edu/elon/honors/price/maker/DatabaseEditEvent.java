package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.UITrigger;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.Event.ActorOrObjectTrigger;
import edu.elon.honors.price.data.Event.RegionTrigger;
import edu.elon.honors.price.data.Event.SwitchTrigger;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.data.Event.VariableTrigger;
import edu.elon.honors.price.game.Game;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DatabaseEditEvent extends DatabaseActivity {

	private static final String[] TRIGGER_TYPES = new String[] {
		"Switch Trigger",
		"Variable Trigger",
		"Actor/Object Trigger",
		"Region Trigger",
		"UI Trigger"
	};

	public static final String COLOR_VARIABLE = "#00CC00";
	public static final String COLOR_MODE = "#FFCC00";
	public static final String COLOR_VALUE = "#5555FF";
	public static final String COLOR_ACTION = "#8800FF";

	private int id;
	private EditText editTextName;
	private LinearLayout linearLayoutTriggers, linearLayoutActions;
	private ReturnResponse returnResponse;
	private LinearLayout linearLayoutMain;
	private RelativeLayout selectionLayout;
	private ScrollView scrollView;
	private LinkedList<ActionView> actionViews = new LinkedList<ActionView>();

	private boolean selecting;
	private LinkedList<Action> copy = new LinkedList<Event.Action>();
	private LinearLayout selection;
	private Rect selectionRect = new Rect();

	private Event getEvent() {
		return game.getSelectedMap().events[id];
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		id = getIntent().getExtras().getInt("id");
		setContentView(R.layout.database_edit_event);

		setDefaultButtonActions();

		editTextName = (EditText)findViewById(R.id.editTextName);
		linearLayoutTriggers = (LinearLayout)findViewById(R.id.linearLayoutTriggers);
		linearLayoutActions = (LinearLayout)findViewById(R.id.linearLayoutActions);
		selectionLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
		Button buttonNewTrigger = (Button)findViewById(R.id.buttonNewTrigger);
		Button buttonNewAction = (Button)findViewById(R.id.buttonNewAction);

		linearLayoutMain = (LinearLayout)findViewById(R.id.linearLayoutMain);
		selection = new LinearLayout(this);
		selection.setVisibility(View.INVISIBLE);
		selection.setBackgroundResource(R.drawable.border_white);
		selectionLayout.addView(selection);

		//Must set one view to focusableInTouchMode
		scrollView = (ScrollView)findViewById(R.id.scrollView1);
		scrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (editTextName.hasFocus()) {
					editTextName.clearFocus();
				}

				if (selecting) {
					updateSelection(event);
					return true;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Select");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().equals("Select")) {
			startSelection();
		}

		return true; 
	}

	private void startSelection() {
		selecting = true;
	}


	private boolean selectionNeedUpdate;
	private void updateSelection(MotionEvent event) {
		selectionNeedUpdate = true;
		updateSelection(event.getX(), event.getY(), event.getAction());
	}


	private void updateSelection(final float x, final float y, 
			final int action) {
		if (!selectionNeedUpdate) {
			return;
		}
		selectionNeedUpdate = false;

		int rX = (int)x;
		int rY = (int)y + scrollView.getScrollY();

		if (action == MotionEvent.ACTION_DOWN) {
			selection.setVisibility(LinearLayout.VISIBLE);
			selectionRect.set(rX, rY, rX, rY);
		}

		selectionRect.right = rX;
		selectionRect.bottom = rY;

		RelativeLayout.LayoutParams lps = 
			(RelativeLayout.LayoutParams)selection.getLayoutParams();
		lps.leftMargin = Math.min(selectionRect.left, selectionRect.right);
		lps.topMargin = Math.min(selectionRect.top, selectionRect.bottom);
		lps.width = Math.max(1, Math.abs(selectionRect.width()));
		lps.height = Math.max(1, Math.abs(selectionRect.height()));
		selection.setLayoutParams(lps);
		selection.invalidate();

		if (checkScroll(y)) {
			if (action == MotionEvent.ACTION_MOVE) {
				selectionNeedUpdate = true;
				scrollView.post(new Runnable() {
					@Override
					public void run() {
						updateSelection(x, y, action);
					}
				});
			}
		}

		updateViewSelection();

		if (action == MotionEvent.ACTION_UP) {
			endSelection();
		}
	}

	private boolean checkScroll(float lastTouchY) {
		int border = 30;
		boolean recurse = false;
		if (lastTouchY < border) {
			scrollView.scrollBy(0, -1);
			recurse = true;
		}
		if (lastTouchY > scrollView.getHeight() - border) {
			if (scrollView.getHeight() + scrollView.getScrollY() < 
					linearLayoutMain.getHeight()) {
				scrollView.scrollBy(0, 1);
				recurse = true;
			}
		}

		return recurse;
	}

	private int[] loc = new int[2];
	private void updateViewSelection() {
		int indent = -1;
		for (ActionView view : actionViews) {

			view.getLocationOnScreen(loc);
			int viewTop = loc[1], viewBot = viewTop + view.getHeight();
			selection.getLocationOnScreen(loc);
			int locTop = loc[1], locBot = locTop + selection.getHeight();

			boolean topIn = viewTop > locTop && viewTop < locBot;
			boolean bottomIn = viewBot > locTop && viewTop < locBot;

			Game.debug("%d, %d", viewTop, locTop);

			if (topIn || bottomIn) {
				if (indent == -1) {
					indent = view.getAction().indent;
				}
				if (view.getAction().indent < indent) {
					view.setHighlight(false);
				} else {
					view.setHighlight(true);
				}
			} else {
				view.setHighlight(false);
			}
		}
	}

	private void endSelection() {
		selecting = false;
		selection.setVisibility(LinearLayout.GONE);

		new AlertDialog.Builder(this)
		.setTitle("Action?")
		.setItems(new String[] {
				"Cut",
				"Copy",
				"Delete"
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0: cutSelection(); break;
				case 1: copySelection(); break;
				case 2: deleteSelection(); break;
				}

				for (ActionView view : actionViews) {
					view.setHighlight(false);
				}
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				for (ActionView view : actionViews) {
					view.setHighlight(false);
				}
			}
		})
		.show();
	}

	private void cutSelection() {
		copySelection();
		deleteSelection();
	}

	private void copySelection() {
		LinkedList<Action> actions = new LinkedList<Event.Action>();
		int indent = -1;
		for (ActionView view : actionViews) {
			if (view.getHighlight()) {
				Action a = view.getAction().copy();
				if (indent == -1) {
					indent = a.indent;
				}
				a.indent -= indent;
				if (a.indent >= 0) {
					actions.add(view.getAction());
				}
			}
		}
		Game.debug(actions);
	}

	private void deleteSelection() {
		for (ActionView view : actionViews) {
			if (view.getHighlight()) {
				getEvent().actions.remove(view.getAction());
			}
		}
		populateViews();
	}

	@Override
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
				case 4:
					intent = new Intent(DatabaseEditEvent.this,
							DatabaseEditTriggerUI.class);
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
		actionViews.clear();
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
			actionViews.add(av);
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
							android.view.ViewGroup.LayoutParams.FILL_PARENT, 
							android.view.ViewGroup.LayoutParams.FILL_PARENT);
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
		private Drawable lastBackground;
		private boolean highlight;

		public Action getAction() {
			return getEvent().actions.get(index);
		}

		public boolean getHighlight() {
			return highlight;
		}

		public void setHighlight(boolean highlight) {
			if (highlight == this.highlight) {
				return;
			}

			if (highlight) {
				lastBackground = getBackground();
				setBackgroundResource(R.drawable.border_selected);
			} else {
				setBackgroundDrawable(lastBackground);
			}

			this.highlight = highlight;
		}

		public ActionView(final Context context, int actionIndex) {
			super(context);

			index = actionIndex;

			setGravity(Gravity.CENTER_VERTICAL);

			LinearLayout.LayoutParams params = new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			TextView tv = createTextView();
			tv.setLayoutParams(params);
			addView(tv);

			Button buttonOptions = new Button(context);
			buttonOptions.setText("!");
			buttonOptions.setWidth(50);
			buttonOptions.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(context).setItems(
							new String[] { "Edit", "Insert", "Delete",
									"Cut", "Copy", "Paste"},
									new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
									case 0: edit();	break;
									case 1: insert(); break;
									case 2: delete(); break;
									case 3: cut(); break;
									case 4: copy(); break;
									case 5: paste(); break;
									}
								}
							}
					).show();
				}
			});

			addView(buttonOptions);

		}

		private void cut() {
			copy();
			delete();
		}

		private void copy() {
			LinkedList<Action> actions = new LinkedList<Event.Action>();
			int indent = getAction().indent;
			for (int i = index; i < getEvent().actions.size(); i++) {
				Action action = getEvent().actions.get(i);
				if (i > index && action.indent <= indent) {
					break;
				}
				Action copy = action.copy();
				copy.indent -= indent;
				actions.add(copy);
			}
			game.copyData = actions;
		}

		private void paste() {
			if (game.copyData != null && 
					game.copyData instanceof List<?>) {
				List<?> list = ((List<?>)game.copyData);
				int indent = getAction().indent;
				for (int i = 0; i < list.size(); i++) {
					if (!(list.get(i) instanceof Action)) {
						return;
					}
					Action action = ((Action)list.get(i)).copy();
					action.indent += indent;
					getEvent().actions.add(i + 1, action);
				}
				populateViews();
			}
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
			LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
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
			} else if (trigger instanceof ActorOrObjectTrigger) {
				return DatabaseEditTriggerActor.class;
			} else if (trigger instanceof RegionTrigger) {
				return DatabaseEditTriggerRegion.class;
			} else if (trigger instanceof UITrigger) {
				return DatabaseEditTriggerUI.class;
			}

			return null;
		}

		private TextView createTextView() {
			Trigger trigger = getTrigger();


			LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
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
			} else if (trigger instanceof ActorOrObjectTrigger) {
				text = getTriggerText((ActorOrObjectTrigger)trigger);
			} else if (trigger instanceof RegionTrigger) {
				text = getTriggerText((RegionTrigger)trigger);
			} else if (trigger instanceof UITrigger) {
				text = getTriggerText((UITrigger)trigger);
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

		private String getTriggerText(ActorOrObjectTrigger trigger) {
			StringBuilder sb = new StringBuilder();

			if (trigger.mode == ActorOrObjectTrigger.MODE_ACTOR_INSTANCE) {
				ActorInstance instance = game.getSelectedMap().actors.get(trigger.id);

				String actorString;
				if (instance.classIndex > 0)
					actorString = String.format("%s %03d", 
							game.actors[instance.classIndex].name, instance.id);
				else
					actorString = game.hero.name;

				TextUtils.addColoredText(sb, actorString, COLOR_VARIABLE);
			} else if (trigger.mode == ActorOrObjectTrigger.MODE_ACTOR_CLASS) {
				sb.append("A ");
				TextUtils.addColoredText(sb, game.actors[trigger.id].name, COLOR_VARIABLE);
			} else if (trigger.mode == ActorOrObjectTrigger.MODE_OBJECT_INSTANCE) {
				ObjectInstance instance = game.getSelectedMap().objects.get(trigger.id);
				TextUtils.addColoredText(sb, game.objects[instance.classIndex].name, COLOR_VARIABLE);
			} else {
				sb.append("A ");
				TextUtils.addColoredText(sb, game.objects[trigger.id].name, COLOR_VARIABLE);
			}
			sb.append(" ");
			TextUtils.addColoredText(sb, ActorOrObjectTrigger.ACTIONS[trigger.action], COLOR_MODE);

			return sb.toString();
		}

		private String getTriggerText(RegionTrigger trigger) {
			StringBuilder sb = new StringBuilder();

			if (trigger.who == RegionTrigger.WHO_HERO) {
				TextUtils.addColoredText(sb, game.hero.name, COLOR_VARIABLE);
			} else if (trigger.who == RegionTrigger.WHO_ACTOR) {
				sb.append("An actor");
			} else {
				sb.append("An object");
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

		private String getTriggerText(UITrigger trigger) {
			StringBuilder sb = new StringBuilder();

			switch (trigger.controlType) {
			case UITrigger.CONTROL_BUTTON: 
				TextUtils.addColoredText(sb, "The button ", COLOR_MODE);
				TextUtils.addColoredText(sb, 
						game.uiLayout.buttons.get(trigger.index).name, 
						COLOR_VARIABLE);
				break;
			case UITrigger.CONTROL_JOY: 
				TextUtils.addColoredText(sb, "The joystick ", COLOR_MODE);
				TextUtils.addColoredText(sb, 
						game.uiLayout.joysticks.get(trigger.index).name, 
						COLOR_VARIABLE);
				break;
			case UITrigger.CONTROL_TOUCH: 
				TextUtils.addColoredText(sb, "The screen", COLOR_MODE);
				break;
			}
			sb.append(" is ");
			switch (trigger.condition) {
			case UITrigger.CONDITION_PRESS:
				TextUtils.addColoredText(sb, "pressed", COLOR_MODE); break;
			case UITrigger.CONDITION_RELEASE:
				TextUtils.addColoredText(sb, "released", COLOR_MODE); break;
			case UITrigger.CONDITION_MOVE:
				TextUtils.addColoredText(sb, "moved", COLOR_MODE); break;
			}

			return sb.toString();
		}
	}
}
