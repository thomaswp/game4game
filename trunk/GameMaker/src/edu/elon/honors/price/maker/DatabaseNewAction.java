package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import edu.elon.honors.price.data.ActionIds;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.game.Game;

public class DatabaseNewAction extends DatabaseActivity {
	private final static ArrayList<Category> categories =
		Category.createCategories();

	private ListView listViewCategories;
	private LinearLayout linearLayoutActions;
	private Action action;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.database_new_action);
		setDefaultButtonActions();

		listViewCategories = (ListView)findViewById(R.id.listViewCategory);
		linearLayoutActions = (LinearLayout)findViewById(R.id.linearLayoutActions);

		ArrayList<String> categoryNames = new ArrayList<String>();
		for (Category cat : categories) {
			categoryNames.add(cat.name);
		}

		listViewCategories.setAdapter(new CheckableArrayAdapter(this,
				android.R.layout.simple_spinner_item, categoryNames));

		listViewCategories.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int selected, long id) {
				selectCategory(selected);
			}
		});

		listViewCategories.setSelection(0);
		selectCategory(0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data.getExtras().containsKey("action")) {
				action = (Action)data.getExtras().getSerializable("action");
				this.finishOk();
			}
		}
	}

	@Override
	protected void putExtras(Intent data) {
		if (action != null) {
			data.putExtra("action", action);
		}
	}

	private void selectCategory(int selected) {
		linearLayoutActions.removeAllViews();
		Category category = categories.get(selected);
		for (int i = 0; i < category.actions.length; i++) {
			linearLayoutActions.addView(
					createActionView(category.actions[i]));
		}
	}

	private View createActionView(final int id) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER_VERTICAL);

		TextView tv = new TextView(this);
		tv.setTextSize(20);
		tv.setWidth(150);
		tv.setText(ActionIds.ACTION_NAMES[id]);
		layout.addView(tv);

		Button button = new Button(this);
		button.setText("Select");
		button.setWidth(100);
		layout.addView(button);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DatabaseNewAction.this, 
						DatabaseEditAction.class);
				intent.putExtra("game", game);
				intent.putExtra("id", id);
				startActivityForResult(intent, REQUEST_RETURN_GAME);
			}
		});

		return layout;
	}

	public static class Category extends ActionIds {
		public final String name;
		public final int[] actions;

		public Category(String name, int[] events) {
			this.name = name;
			this.actions = events;
		}

		private static ArrayList<Category> createCategories() {
			ArrayList<Category> categories = new ArrayList<Category>();

			categories.add(new Category("Switch/Variable Actions", new int[] {
					ID_SET_SWITCH,
					ID_SET_VARIABLE
			}));

			categories.add(new Category("Actor Actions", new int[] {
					ID_CREATE_ACTOR,
					ID_MOVE_ACTOR//,
					//ID_ACTOR_BEHAVIOR
			}));

//			categories.add(new Category("Hero Actions", new int[] {
//					ID_HERO_SET_LADDER
//			}));
			
			categories.add(new Category("Object Actions", new int[] {
					ID_CREATE_OBJECT
			}));
			
//			categories.add(new Category("Control", new int[] {
//					ID_IF
//			}));

			categories.add(new Category("Debug Actions", new int[] {
					ID_DEBUG_BOX
			}));

			ArrayList<Integer> all = new ArrayList<Integer>();
			for (Category cat : categories) {
				int[] events = cat.actions;
				for (int i = 0; i < events.length; i++)
					all.add(events[i]);
			}
			Collections.sort(all, new Comparator<Integer>() {
				@Override
				public int compare(Integer lhs, Integer rhs) {
					return ActionIds.ACTION_NAMES[lhs].compareTo(
							ActionIds.ACTION_NAMES[rhs]);
				}
			});

			int[] allA = new int[all.size()];
			for (int i = 0; i < allA.length; i++) allA[i] = all.get(i);
			categories.add(new Category("All", allA));

			return categories;
		}
	}
}
