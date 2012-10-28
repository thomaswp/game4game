package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Game;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public abstract class PageList<T> extends Page {

	protected ListView listView;
	protected final static int REQUEST_EDIT_ITEM = 101;
	protected int editIndex;
	
	@Override
	public int getLayoutId() {
		return R.layout.page_list;
	}
	
	@SuppressWarnings("unchecked")
	protected CheckableArrayAdapter<T> getListViewAdapter() {
		return ((CheckableArrayAdapter<T>)listView.getAdapter());
	}
	
	public PageList(Database parent) {
		super(parent);
	}
	
	@Override
	public void onResume() { }

	@Override
	protected void onPause() { }
	
	@Override
	public void onCreate(ViewGroup parentView) {
		super.onCreate(parentView);
		listView = (ListView)findViewById(R.id.listView);
		((TextView)findViewById(R.id.textViewPage)).setText(getName());
		createButtonEvents();
		
		listView.setAdapter(getAdapter());
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	protected abstract void editItem(int index);
	protected abstract void resetItem(int index);
	protected abstract void addItem();
	protected abstract CheckableArrayAdapter<T> getAdapter();
	protected abstract T getItem(int index);
	protected abstract String getItemCategory();
	
	private void createButtonEvents() {

		Button editButton = (Button)findViewById(R.id.buttonEdit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = getSelectedIndex();
				if (index < 0) return;
				editIndex = index;
				editItem(index);
			}
		});

		Button reset = (Button)findViewById(R.id.buttonReset);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = getSelectedIndex();
				if (index < 0) return;
				resetItem(index);
				getListViewAdapter().replaceItem(index, getItem(index));
			}
		});

		Button add = (Button)findViewById(R.id.buttonAdd);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int startSize = listView.getAdapter().getCount();
				addItem();
				if (listView.getAdapter().getCount() == startSize) {
					getListViewAdapter().insert(getItem(startSize), startSize);
				}
			}
		});
		add.setText("Add " + getItemCategory());
	}

	private int getSelectedIndex() {
		return listView.getCheckedItemPosition();
	}
	

	@Override
	public void onActivityResult(int requestCode, Intent data) {
		super.onActivityResult(requestCode, data);
		if (requestCode == REQUEST_EDIT_ITEM) {
			getListViewAdapter().replaceItem(editIndex, getItem(editIndex));
		}
	}
}
