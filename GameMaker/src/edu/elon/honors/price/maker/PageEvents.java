package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.Arrays;

import edu.elon.honors.price.data.Event;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class PageEvents extends Page {

	private ListView listViewEvents;
	private int selectedId;

	public PageEvents(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		return R.layout.page_events;
	}

	@Override
	public String getName() {
		return "Events";
	}

	@Override
	public void onCreate() {
		listViewEvents = (ListView)findViewById(R.id.listViewEvents);

		Button buttonEdit = (Button)findViewById(R.id.buttonEdit);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedId >= 0) {
					Intent intent = new Intent(parent, DatabaseEditEvent.class);
					intent.putExtra("game", getGame());
					intent.putExtra("id", selectedId);
					parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
				}
			}
		});

		Button buttonReset = (Button)findViewById(R.id.buttonReset);
		buttonReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Event e = new Event();
				getGame().getSelectedMap().events[selectedId] = e;
				CheckableArrayAdapter adapter = 
					(CheckableArrayAdapter)listViewEvents.getAdapter();
				adapter.replaceItem(selectedId, e.name);
			}
		});

		Button buttonAdd = (Button)findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				CheckableArrayAdapter adapter = 
					(CheckableArrayAdapter)listViewEvents.getAdapter();
				
				Event[] events = getGame().getSelectedMap().events;
				int oldLength = events.length;
				events = Arrays.copyOf(events, oldLength + 1);
				for (int i = oldLength; i < events.length; i++) {
					events[i] = new Event();
					adapter.addItem(events[i].name);
				}
				getGame().getSelectedMap().events = events;
				((CheckableArrayAdapter)listViewEvents.getAdapter())
					.notifyDataSetChanged();
			}
		});
		
		listViewEvents.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int selected,
					long id) {
				selectedId = selected;
			}
		});
	}

	@Override
	public void onResume() {
		populateListView();
	}

	private void populateListView() {
		Event[] events = getGame().getSelectedMap().events;
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < events.length; i++) {
			Event event = events[i];
			names.add(event.name);
		}
		listViewEvents.setAdapter(new CheckableArrayAdapter(parent, R.layout.checkable_array_adapter_row, names));
		listViewEvents.setSelection(0);
	}

}
