package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public abstract class PageList<T> extends Page {

	protected final String LAST_SELECTED = "lastSelected";
	
	protected ListView listView;
	protected final static int REQUEST_EDIT_ITEM = 101;
	protected int editIndex;
	private LinearLayout linearLayoutButtons;
	protected Button buttonEdit, buttonReset, buttonAdd;
	
	@Override
	public int getLayoutId() {
		return R.layout.page_list;
	}
	
	protected int getSelectedIndex() {
		return listView.getCheckedItemPosition();
	}
	
	@SuppressWarnings("unchecked")
	protected CheckableArrayAdapter<T> getListViewAdapter() {
		return ((CheckableArrayAdapter<T>)listView.getAdapter());
	}
	
	public PageList(Database parent) {
		super(parent);
	}
	
	@Override
	public void onCreate(ViewGroup parentView) {
		super.onCreate(parentView);
		listView = (ListView)findViewById(R.id.listView);
		((TextView)findViewById(R.id.textViewPage)).setText(getName());
		createButtonEvents();
		
		
		linearLayoutButtons = (LinearLayout)findViewById(
				R.id.linearLayoutButtons);
	}
	
	@Override
	public void onResume() {
		listView.setAdapter(getAdapter());
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		int selected = getIntPreference(LAST_SELECTED, 0);
		if (selected >= 0 && selected < getAdapter().getCount()) {
			listView.setItemChecked(selected, true);
		}
	}

	@Override
	protected void onPause() { 
		putPreference(LAST_SELECTED, getSelectedIndex());
	}
	
	protected void addPanelView(View view) {
		linearLayoutButtons.addView(view);
	}
	
	protected abstract void editItem(int index);
	protected abstract void resetItem(int index);
	protected abstract void addItem();
	protected abstract CheckableArrayAdapter<T> getAdapter();
	protected abstract T getItem(int index);
	protected abstract String getItemCategory();
	
	private void createButtonEvents() {

		buttonEdit = (Button)findViewById(R.id.buttonEdit);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = getSelectedIndex();
				if (index < 0) return;
				editIndex = index;
				editItem(index);
			}
		});

		buttonReset = (Button)findViewById(R.id.buttonReset);
		buttonReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int index = getSelectedIndex();
				if (index < 0) return;
				
				new AlertDialog.Builder(parent)
				.setTitle("Reset?")
				.setMessage("Are you sure you want to reset this to a new " + 
				getItemCategory() + "?")
				.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						resetItem(index);
						getListViewAdapter().replaceItem(index, getItem(index));
					}
				})
				.setNegativeButton("Cancel", null)
				.show();
			}
		});

		buttonAdd = (Button)findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int startSize = listView.getAdapter().getCount();
				addItem();
				getListViewAdapter().insert(getItem(startSize), startSize);
			}
		});
		buttonAdd.setText("Add " + getItemCategory());
	}
	

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		super.onActivityResult(requestCode, data);
		if (requestCode == REQUEST_EDIT_ITEM) {
			getListViewAdapter().replaceItem(editIndex, getItem(editIndex));
		}
	}
}
