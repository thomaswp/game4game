package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.maker.action.ActionFactory;

public class DatabaseNewAction extends DatabaseActivity {
	private final static ArrayList<Category> categories =
		Category.createCategories();

	private ListView listViewCategories;
	private LinearLayout linearLayoutActions;
	private Action action;

	@Override
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

		listViewCategories.setAdapter(new CheckableArrayAdapterString(
				this, categoryNames));

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
				if (!data.getExtras().containsKey("finishAll")) {
					this.finishOk();
				}
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
		for (int i = 0; i < category.actions.size(); i++) {
			linearLayoutActions.addView(
					createActionView(category.actions.get(i)));
		}
	}

	private View createActionView(final int id) {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER_VERTICAL);

		TextView tv = new TextView(this);
		tv.setTextSize(20);
		tv.setWidth(dipToPx(175));
		tv.setText(ActionFactory.ACTION_NAMES[id]);
		layout.addView(tv);

		Button button = new Button(this);
		button.setText("Select");
		button.setWidth(dipToPx(100));
		layout.addView(button);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DatabaseNewAction.this, 
						DatabaseEditAction.class);
				intent.putExtra("game", game);
				intent.putExtra("id", id);
				intent.putExtra("eventContext", 
						getIntent().getSerializableExtra("eventContext"));
				startActivityForResult(intent, REQUEST_RETURN_GAME);
			}
		});

		return layout;
	}

	public static class Category implements Comparable<Category> {
		public final String name;
		public List<Integer> actions;

		public Category(String name) {
			this.name = name;
			actions = new LinkedList<Integer>();
		}
		
		public Category(String name, List<Integer> actions) {
			this.name = name;
			this.actions = actions;
		}

		private static ArrayList<Category> createCategories() {
			ArrayList<Category> categories = new ArrayList<Category>();

			int[] ids = ActionFactory.ACTION_IDS;
			
			for (int i : ids) {
				String name = ActionFactory.ACTION_CATEGORIES[i];
				Category category = null;
				for (Category cat : categories) {
					if (cat.name.equals(name)) {
						category = cat;
						break;
					}
				}
				if (category == null && !name.equalsIgnoreCase("hidden")) {
					 category = new Category(name);
					 categories.add(category);
				}
				if (category != null) {
					category.actions.add(i);
				}
			}
			
			Collections.sort(categories);

			ArrayList<Integer> all = new ArrayList<Integer>();
			for (Category cat : categories) {
				all.addAll(cat.actions);
			}
			Collections.sort(all, new Comparator<Integer>() {
				@Override
				public int compare(Integer lhs, Integer rhs) {
					return ActionFactory.ACTION_NAMES[lhs].compareTo(
							ActionFactory.ACTION_NAMES[rhs]);
				}
			});

			categories.add(new Category("All", all));

			return categories;
		}

		@Override
		public int compareTo(Category another) {
			return name.compareTo(another.name);
		}
	}
}
