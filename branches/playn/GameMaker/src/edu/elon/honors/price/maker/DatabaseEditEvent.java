package edu.elon.honors.price.maker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Trigger;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.action.ActionElse;
import edu.elon.honors.price.maker.action.ActionFactory;
import edu.elon.honors.price.maker.action.ActionIf;
import edu.elon.honors.price.maker.action.EventContext;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
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
		"UI Trigger",
		"Time Trigger"
	};

	public static final String COLOR_VARIABLE = 
			TextUtils.COLOR_VARIABLE;
	public static final String COLOR_MODE = 
			TextUtils.COLOR_MODE;
	public static final String COLOR_VALUE = 
			TextUtils.COLOR_VALUE;
	public static final String COLOR_ACTION = 
			TextUtils.COLOR_ACTION;

	private final static int BUTTON_BORDER_FADE = 1200;

	private EditText editTextName;
	private LinearLayout linearLayoutTriggers, linearLayoutActions;
	private ReturnResponse returnResponse;
	private LinearLayout linearLayoutMain;
	private RelativeLayout selectionLayout;
	private ScrollView scrollView;
	private LinkedList<ActionView> actionViews = new LinkedList<ActionView>();
	private LinkedList<TriggerView> triggerViews = new LinkedList<TriggerView>();

	private boolean selecting;
	private boolean selectionStarted;
	private LinearLayout selection;
	private Rect selectionRect = new Rect();

	private Event event;
	private Behavior behavior;

	private Event getEvent() {
		//return game.getSelectedMap().events[id];
		return event;
	}

	private Event readEvent(Bundle savedInstanceState) {
		Bundle extras = savedInstanceState == null ?
				getIntent().getExtras(): savedInstanceState;
				return (Event)extras.getSerializable("event");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		event = readEvent(savedInstanceState);
		if (getIntent().getExtras().containsKey("behavior")) {
			behavior = (Behavior)getIntent().getExtras()
					.getSerializable("behavior");
		}

		if (savedInstanceState != null) {
			returnResponse = (ReturnResponse)savedInstanceState
					.getSerializable("returnResponse");
		}

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
					updateSelection(v, event);
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

		final Handler handler = new Handler();
		if (savedInstanceState == null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						@Override
						public void run() {
							for (ClickableView cv : actionViews) {
								cv.flashbutton();
							}
							for (ClickableView cv : triggerViews) {
								cv.flashbutton();
							}
						}
					});
				}
			});
		}
	}
	
	

	@Override
	protected void onFinishing() {
		super.onFinishing();
		getEvent().name = editTextName.getText().toString();
	}

	@Override
	protected void putExtras(Intent intent) {
		intent.putExtra("event", getEvent());
	}

	@Override
	protected boolean hasChanged() {
		Event event = readEvent(null);

		return !GameData.areEqual(event, this.event) ||
				super.hasChanged();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) { 
		super.onSaveInstanceState(outState);
		outState.putSerializable("returnResponse", returnResponse);
		outState.putSerializable("event", event);
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
		selectionStarted = false;
		for (ActionView view : actionViews) {
			view.startSelection();
		}
	}


	private boolean selectionNeedUpdate;
	private void updateSelection(View sender, MotionEvent event) {
		selectionNeedUpdate = true;
		sender.getLocationOnScreen(loc);
		updateSelection(loc[0] + event.getX(), 
				loc[1] + event.getY(), event.getAction());
	}


	private void updateSelection(final float x, final float y, 
			final int action) {
		if (!selectionNeedUpdate) {
			return;
		}
		selectionNeedUpdate = false;

		int rX = (int)x;
		int rY = (int)y + scrollView.getScrollY();

		if (!selectionStarted) {
			selectionRect.set(rX, rY, rX, rY);
			selection.setVisibility(View.VISIBLE);
			selectionStarted = true;
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

		boolean stopHighlight = false;
		for (int i = 0; i < actionViews.size(); i++) {
			ActionView view = actionViews.get(i);
			
			view.getLocationOnScreen(loc);
			int viewTop = loc[1], viewBot = viewTop + view.getHeight();
			selection.getLocationOnScreen(loc);
			int locTop = loc[1], locBot = locTop + selection.getHeight();

			boolean topIn = viewTop > locTop && viewTop < locBot;
			boolean bottomIn = viewBot > locTop && viewTop < locBot;

			//Debug.write("%d, %d", viewTop, locTop);
			
			int parentIndex = findParentIndex(i);

			if (view.getAction().dependsOn == null) {
				if (topIn || bottomIn) {
					if (indent == -1) {
						indent = view.getAction().indent;
					}
					if (view.getAction().indent < indent || stopHighlight) {
						view.setHighlight(false);
						stopHighlight = true;
					} else {
						view.setHighlight(true);
					}
				} else if (parentIndex >= 0 && 
						actionViews.get(parentIndex).getHighlight()) {
					view.setHighlight(true);
				} else {
					view.setHighlight(false);
				}
			} else {
				for (ActionView av : actionViews) {
					if (av.getAction() == view.getAction().dependsOn) {
						view.setHighlight(av.getHighlight());
						if (!av.getHighlight()) {
							stopHighlight = true;
						}
						break;
					}
				}
			}
		}
	}
	
	private int findParentIndex(int actionIndex) {
		Action action = getEvent().actions.get(actionIndex);
		if (action.indent == 0) return -1;
		for (int i = actionIndex - 1; i >= 0; i--) { 
			if (getEvent().actions.get(i).indent < action.indent) {
				return i;
			}
		}
		return -1;
	}

	private void endSelection() {
		selecting = false;
		selection.setVisibility(View.GONE);
		selection.layout(0, 0, 0, 0);

		int count = 0;
		for (ActionView view : actionViews) {
			if (view.highlight) count++;
			view.endSelection();
		}
		if (count == 0) return;

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
					actions.add(a);
				}
			}
		}
		game.copyData = actions;
	}

	private void deleteSelection() {
		LinkedList<Action> toDelete = new LinkedList<Event.Action>();
		for (ActionView view : actionViews) {
			if (view.getHighlight()) {
				toDelete.add(view.getAction());
			}
		}
		for (Action action : toDelete) {
			getEvent().actions.remove(action);
		}
		populateViews();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && returnResponse != null) {
			returnResponse.run(this, data);
			returnResponse = null;
		}
	}

	private void newAction() {
		Intent intent = new Intent(DatabaseEditEvent.this, 
				DatabaseNewAction.class);
		intent.putExtra("game", game);
		intent.putExtra("eventContext", 
				new EventContext(getEvent(), behavior));

		returnResponse = new NewActionReturnResponse();
		startActivityForResult(intent, REQUEST_RETURN_GAME);
	}

	private static class NewActionReturnResponse extends ReturnResponse {
		private static final long serialVersionUID = 1L;

		@Override
		void run(DatabaseEditEvent me, Intent data) {
			Action action = (Action)data.getExtras().
					getSerializable("action");
			me.addAction(action);
		}
	}

	protected void addAction(Action action) {
		addAction(actionViews.size(), action);
	}

	protected void addAction(int index, Action action) {
		ArrayList<Action> actions = getEvent().actions;
		actions.add(index, action);
		if (action.id == ActionIf.ID) {
			Action actionElse = new Action(ActionElse.ID, new Parameters());
			actionElse.description = TextUtils.getItalicizedText( 
					TextUtils.getColoredText("Else", 
							TextUtils.COLOR_ACTION));
			actionElse.dependsOn = action;
			actionElse.indent = action.indent;
			actions.add(index + 1, actionElse);
		}
		populateViews();
		actionViews.get(index).flashbutton();
	}

	protected void removeAction(int index) {
		getEvent().actions.remove(index);
	}

	private void newTrigger() {
		new AlertDialog.Builder(DatabaseEditEvent.this)
		.setTitle("New Trigger")
		//.setMessage("Select a type of Trigger:")
		.setItems(TRIGGER_TYPES, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(DatabaseEditEvent.this, 
						DatabaseEditTrigger.class);

				intent.putExtra("game", game);
				intent.putExtra("eventContext", 
						new EventContext(getEvent(), behavior));
				intent.putExtra("id", which);
				returnResponse = new NewTriggerReturnResponse();
				startActivityForResult(intent, REQUEST_RETURN_GAME);
			}
		})
		.show();
	}

	private static class NewTriggerReturnResponse extends ReturnResponse {
		private static final long serialVersionUID = 1L;

		@Override
		void run(DatabaseEditEvent me, Intent data) {
			Trigger trigger = (Trigger)data.getExtras().
					getSerializable("trigger");
			me.getEvent().triggers.add(trigger);
			me.populateViews();
			me.triggerViews.get(me.triggerViews.size() - 1).flashbutton();
		}
	}

	private void populateViews() {
		Event event = getEvent(); 

		editTextName.setText(event.name);

		linearLayoutTriggers.removeAllViews();
		triggerViews.clear();
		for (int i = 0; i < event.triggers.size(); i++) {
			TriggerView tv = new TriggerView(this, i);
			triggerViews.add(tv);
			linearLayoutTriggers.addView(tv);
		}

		//Remove orphaned elses
		for (int i = 0; i < event.actions.size(); i++) {
			Action action = event.actions.get(i);
			if (action.dependsOn != null && 
					!event.actions.contains(action.dependsOn)) {
				event.actions.remove(i);

				int indent = action.indent;
				for (int j = i; j < event.actions.size(); j++) {
					if (event.actions.get(j).indent > indent) {
						event.actions.remove(j);
						j--;
					}
				}

				i--;
			}
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


			if (ActionFactory.isParent(action.id)) {
				hosts.push(getHostLayout());
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

	private LinearLayout getHostLayout() {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = 
				new LinearLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.FILL_PARENT, 
						android.view.ViewGroup.LayoutParams.FILL_PARENT);
		lp.setMargins(20, 0, 0, 0);
		layout.setLayoutParams(lp);
		return layout;
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

	private void newIndentAction(int index, int indent) {
		returnResponse = new NewIndentReturnResponse(index, indent);
		Intent intent = new Intent(DatabaseEditEvent.this, 
				DatabaseNewAction.class);
		intent.putExtra("game", game);
		intent.putExtra("eventContext", 
				new EventContext(getEvent(), behavior));
		startActivityForResult(intent, REQUEST_RETURN_GAME);
	}

	private static class NewIndentReturnResponse extends ReturnResponse {
		private int indent, index;
		public NewIndentReturnResponse(int index, int indent) {
			this.index = index;
			this.indent = indent;
		}

		private static final long serialVersionUID = 1L;
		@Override
		void run(DatabaseEditEvent me, Intent data) {
			Action a = (Action)data.getExtras()
					.getSerializable("action");
			a.indent = indent;
			me.addAction(index, a);
		}
	}

	private static abstract class ReturnResponse implements Serializable {
		private static final long serialVersionUID = 1L;

		abstract void run(DatabaseEditEvent me, Intent data);
	}

	private abstract class ClickableView extends LinearLayout {

		protected Button button;

		public void startSelection() {
//			button.setEnabled(false);
//			button.setClickable(false);
		}

		public void endSelection() {
//			button.setEnabled(true);
//			button.setClickable(true);
		}

		public ClickableView(Context context) {
			super(context);
		}
		
		protected void setButtonOnTouch() {
			button.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (selecting) {
						updateSelection(v, event);
						return true;
					}
					return false;
				}
			});
		}

		public void flashbutton() {
			getLocationOnScreen(loc);
			if (loc[1] >= 0 && loc[1] < scrollView.getHeight()) {
				int[] padding = new int[] {
						button.getPaddingLeft(),
						button.getPaddingTop(),
						button.getPaddingRight(),
						button.getPaddingTop()
				};
				TransitionDrawable td = new TransitionDrawable(new Drawable[] {
						getResources().getDrawable(R.drawable.border_white_no_pad),
						getResources().getDrawable(R.drawable.border_action)
				});
				td.setCrossFadeEnabled(true);
				button.setBackgroundDrawable(td);
				td.startTransition(BUTTON_BORDER_FADE);
				button.setPadding(padding[0], padding[1], padding[2], padding[3]);
			}
		}
	}

	private class ActionView extends ClickableView {
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
			button = createTextViewButton();
			button.setLayoutParams(params);
			setButtonOnTouch();
			addView(button);

		}

		private void cut() {
			copy();
			delete(false);
		}
		
		private List<Action> copyActionAndChildren() {
			LinkedList<Action> actions = new LinkedList<Event.Action>();
			
			actions.add(getAction());
			
			int indent = getAction().indent;
			for (int i = index + 1; i < getEvent().actions.size(); i++) {
				Action action = getEvent().actions.get(i);
				if (action.indent <= indent) {
					if (action.id != ActionElse.ID) {
						break;
					} else if (action.indent < indent) {
						break;
					}
				}
				Action copy = action.copy();
				copy.indent -= indent;
				actions.add(copy);
			}
			
			Debug.write(actions);
			
			return actions;
		}

		private void copy() {
			game.copyData = copyActionAndChildren();
		}

		private void paste(boolean below, boolean insert) {
			if (game.copyData != null && 
					game.copyData instanceof List<?>) {
				LinkedList<Integer> indices = new LinkedList<Integer>();

				List<?> list = ((List<?>)game.copyData);
				int indent = getAction().indent +
						(insert ? 1 : 0);
				int offset = index;
				if (below) {
					offset++;
					if (!insert) {
						while (offset < getEvent().actions.size() &&
								(getEvent().actions.get(offset).indent > indent ||
										getEvent().actions.get(offset).id == ActionElse.ID)) {	
							offset++;
						}
					}

				}

				Action lastIf = null;
				for (int i = 0; i < list.size(); i++) {
					if (!(list.get(i) instanceof Action)) {
						return;
					}
					Action action = ((Action)list.get(i)).copy();
					if (action.id == ActionIf.ID) lastIf = action;
					if (action.id == ActionElse.ID) {
						action.dependsOn = lastIf;
					}
					action.indent += indent;
					int index = i + offset;
					getEvent().actions.add(index, action);
					indices.add(index);
				}
				populateViews();
				for (int i : indices) {
					actionViews.get(i).flashbutton();
				}
			}
		}

		private void edit() {
			returnResponse = new EditActionReturnResponse(index);
			Intent intent = new Intent(DatabaseEditEvent.this, 
					DatabaseEditAction.class);
			intent.putExtra("game", game);
			intent.putExtra("id", getAction().id);
			intent.putExtra("params", getAction().params);
			intent.putExtra("eventContext", 
					new EventContext(getEvent(), behavior));
			startActivityForResult(intent, REQUEST_RETURN_GAME);
		}

		private void insert() {
			Intent intent = new Intent(DatabaseEditEvent.this, 
					DatabaseNewAction.class);
			intent.putExtra("game", game);
			intent.putExtra("eventContext", 
					new EventContext(getEvent(), behavior));

			returnResponse = new InsertActionReturnResponse(index);

			startActivityForResult(intent, REQUEST_RETURN_GAME);
		}

		private void delete(boolean warn) {
			int toRemove = copyActionAndChildren().size();
			final int toRemoveF = toRemove;
			if (toRemove == 1 || !warn) {
				delete(toRemove);
			} else if (warn) {
				new AlertDialog.Builder(getContext())
				.setTitle("Delete?")
				.setMessage("This will delete all the actions inside this statement. Are you sure you want to delete?")
				.setPositiveButton("Delete", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete(toRemoveF);
					}
				})
				.setNegativeButton("Cancel", null)
				.show();
			}
		}
		
		private void delete(int number) {
			for (int i = 0; i < number; i++) {
				removeAction(index);
			}
			populateViews();
		}

		private Button createTextViewButton() {
			LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					10, getResources().getDisplayMetrics());
			Button button = new Button(getContext());
			button.setSingleLine(false);
			button.setTextSize(16);
			button.setLayoutParams(lp);
			button.setTextColor(Color.LTGRAY);
			button.requestLayout();
			button.setGravity(Gravity.LEFT);

			button.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					String[] baseItems = new String[] {
							"Edit", "Insert Above", "Delete",
							"Copy", "Cut" 
					};
					boolean isElse = getAction().id == ActionElse.ID;
					final int indexOffset = isElse ? 6 : 0;
					LinkedList<String> items = new LinkedList<String>();
					if (!isElse) {
							items.addAll(Arrays.asList(baseItems));
					}
					if (game.copyData != null && game.copyData instanceof List<?>) {
						if (!isElse) {
							items.add("Paste (Above)");
						}
						items.add("Paste (Below)");
						if (ActionFactory.isParent(getAction().id)){
							items.add("Paste (Nested)");
						}
					}
					if (items.size() > 0) {
						String[] itemsArray = new String[items.size()]; 
						new AlertDialog.Builder(getContext()).setItems(
								items.toArray(itemsArray),
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										int offsetWhich = which + indexOffset;
										switch (offsetWhich) {
										case 0: edit();	break;
										case 1: insert(); break;
										case 2: delete(true); break;
										case 3: copy(); break;
										case 4: cut(); break;
										case 5: paste(false, false); break; //Above
										case 6: paste(true, false); break; //Below
										case 7: paste(true, true); break; //Nested
										}
									}
								}
								).show();
					}
					return true;
				}

			});


			if (getAction().id != ActionElse.ID) {
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						edit();
					}
				});

			}

			button.setBackgroundResource(R.drawable.border_action);
			button.setText(Html.fromHtml(getAction().description));
			return button;
		}
	}

	private static class EditActionReturnResponse extends ReturnResponse {
		private static final long serialVersionUID = 1L;

		private int index;

		public EditActionReturnResponse(int index) {
			this.index = index;
		}

		@Override
		void run(DatabaseEditEvent me, Intent data) {
			Action action = (Action)data.getExtras()
					.getSerializable("action");
			action.indent = me.getEvent().actions.get(index).indent;
			Action oldAction = me.getEvent().actions.remove(index);
			me.getEvent().actions.add(index, action);
			for (Action a : me.getEvent().actions) {
				if (a.dependsOn == oldAction) {
					a.dependsOn = action;
				}
			}
			me.populateViews();
			me.actionViews.get(index).flashbutton();
		}
	}

	private static class InsertActionReturnResponse extends ReturnResponse {
		private static final long serialVersionUID = 1L;

		private int index;

		public InsertActionReturnResponse(int index) {
			this.index = index;
		}

		@Override
		void run(DatabaseEditEvent me, Intent data) {
			Action action = (Action)data.getExtras().
					getSerializable("action");
			action.indent = me.getEvent().actions.get(index).indent;
			me.addAction(index, action);
		}
	}

	private static class EditTriggerReturnResponse extends ReturnResponse {
		private static final long serialVersionUID = 1L;

		private int index;

		public EditTriggerReturnResponse(int index) {
			this.index = index;
		}

		@Override
		void run(DatabaseEditEvent me, Intent data) {
			Trigger trigger = (Trigger)data.getExtras().getSerializable("trigger");
			ArrayList<Trigger> triggers = me.getEvent().triggers;
			triggers.remove(index);
			triggers.add(index, trigger);
			me.populateViews();
			me.triggerViews.get(index).flashbutton();
		}
	}

	private class TriggerView extends ClickableView {

		private int index;

		private Trigger getTrigger() {
			return getEvent().triggers.get(index);
		}

		public TriggerView(Context context, int triggerIndex) {
			super(context);

			this.index = triggerIndex;

			setGravity(Gravity.CENTER_VERTICAL);

			button = createTriggerView();
			setButtonOnTouch();
			addView(button);
		}

		private void edit() {
			returnResponse = new EditTriggerReturnResponse(index);
			Intent intent = new Intent(getContext(), DatabaseEditTrigger.class);
			intent.putExtra("game", game);
			intent.putExtra("id", getTrigger().id);
			intent.putExtra("params", getTrigger().params);
			intent.putExtra("eventContext", 
					new EventContext(getEvent(), behavior));
			startActivityForResult(intent, REQUEST_RETURN_GAME);
		}

		private void delete() {
			getEvent().triggers.remove(index);
			populateViews();
		}

		private Button createTriggerView() {
			Trigger trigger = getTrigger();


			LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
					10, getResources().getDisplayMetrics());
			Button button = new Button(getContext());
			//			tv.setWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
			//					200, getResources().getDisplayMetrics()));
			button.setSingleLine(false);
			button.setTextSize(16);
			button.setLayoutParams(lp);
			button.setBackgroundResource(R.drawable.border_action);
			button.setTextColor(Color.LTGRAY);
			button.setGravity(Gravity.LEFT);
			button.requestLayout();

			button.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					new AlertDialog.Builder(getContext()).setItems(
							new String[] { "Edit", "Delete" },
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
									case 0: edit();	break;
									case 1: delete(); break;
									}
								}
							}
							).show();
					return true;
				}
			});
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					edit();
				}
			});

			button.setBackgroundResource(R.drawable.border_action);

			button.setText(Html.fromHtml(trigger.description));

			return button;
		}
	}
}
