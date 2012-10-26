package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorClass;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public abstract class PageList<T> extends Page {

	protected ListView listView;
	
	@Override
	public int getViewId() {
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
	public void onCreate() {
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
	
	private void createButtonEvents() {

		Button editButton = (Button)findViewById(R.id.buttonEdit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = getSelectedIndex();
				if (index < 0) return;
				editItem(index);
				getListViewAdapter().replaceItem(index, getItem(index));
			}
		});

		Button reset = (Button)parent.findViewById(R.id.buttonReset);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int index = getSelectedIndex();
				if (index < 0) return;
				resetItem(index);
				getListViewAdapter().replaceItem(index, getItem(index));
			}
		});

		Button resize = (Button)findViewById(R.id.buttonAdd);
		resize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int startSize = listView.getAdapter().getCount();
				addItem();
				if (listView.getAdapter().getCount() == startSize) {
					getListViewAdapter().insertItem(getItem(startSize), startSize);
				}
			}
		});
	}

	private int getSelectedIndex() {
		return listView.getCheckedItemPosition();
	}
}
