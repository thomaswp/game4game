package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.game.Game;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PageEvents extends Page {

	private ListView listViewEvents;

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
				int id = CheckableArrayAdapter.getSelectedId(listViewEvents);
				if (id >= 0) {
					Intent intent = new Intent(parent, DatabaseEditEvent.class);
					intent.putExtra("game", getGame());
					intent.putExtra("id", id);
					parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
				}
			}
		});

		Button buttonNew = (Button)findViewById(R.id.buttonNew);
		buttonNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getGame().getSelectedMap().events.add(new Event("New Event"));
				final int id = CheckableArrayAdapter.getSelectedId(listViewEvents);
				populateListView();

				if (id >= 0) {
					listViewEvents.post(new Runnable() {
						public void run() {
							CheckableArrayAdapter.setSelectedId(listViewEvents, id);
						}
					});

				}
			}
		});

		Button buttonDelete = (Button)findViewById(R.id.buttonDelete);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = CheckableArrayAdapter.getSelectedId(listViewEvents); 
				ArrayList<Event> events = getGame().getSelectedMap().events;
				if (id >= 0 && id < events.size()) {
					events.remove(id);
				}
				populateListView();
			}
		});
	}

	@Override
	public void onResume() {
		populateListView();
	}

	private void populateListView() {
		ArrayList<Event> events = getGame().getSelectedMap().events;
		String[] names = new String[events.size()];
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			names[i] = event.name;
		}
		listViewEvents.setAdapter(new CheckableArrayAdapter(parent, R.layout.checkable_array_adapter_row, names));

	}

}
