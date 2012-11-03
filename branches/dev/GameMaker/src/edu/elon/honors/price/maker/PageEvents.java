package edu.elon.honors.price.maker;

import java.util.Arrays;

import edu.elon.honors.price.data.Event;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class PageEvents extends PageList<Event> {

	protected Event getEvent(int index) {
		return getEvents()[index];
	}
	
	protected Event[] getEvents() {
		return getGame().getSelectedMap().events;
	}
	
	public PageEvents(Database parent) {
		super(parent);
	}

	@Override
	public String getName() {
		return "Events";
	}

	@Override
	protected void editItem(int index) {
		Intent intent = new Intent(parent, DatabaseEditEvent.class);
		intent.putExtra("game", getGame());
		intent.putExtra("event", getEvent(index));
		parent.startActivityForResult(intent, REQUEST_EDIT_ITEM);
	}

	@Override
	protected void resetItem(int index) {
		getEvents()[index] = new Event();
	}

	@Override
	protected void addItem() {
		getGame().getSelectedMap().events =
				Arrays.copyOf(getEvents(), getEvents().length + 1);
		getEvents()[getEvents().length - 1] = new Event();
	}

	@Override
	protected CheckableArrayAdapter<Event> getAdapter() {
		return new CheckableArrayAdapter<Event>(parent, 
				R.layout.array_adapter_row_string, getEvents()) {
			@Override
			protected void setRow(int position, Event item, View view) {
				((TextView)view.findViewById(R.id.textViewTitle))
				.setText(item.name);
			}
		};
	}

	@Override
	protected Event getItem(int index) {
		return getEvent(index);
	}

	@Override
	protected String getItemCategory() {
		return "Event";
	}
	
	@Override
	public void onActivityResult(int requestCode, Intent data) {
		getEvents()[editIndex] = (Event)data.getSerializableExtra("event");
		super.onActivityResult(requestCode, data);
	}

//	@Override
//	public void onCreate(ViewGroup parentView) {
//		super.onCreate(parentView);
//		listViewEvents = (ListView)findViewById(R.id.listViewEvents);
//
//		Button buttonEdit = (Button)findViewById(R.id.buttonEdit);
//		buttonEdit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (selectedId >= 0) {
//					Intent intent = new Intent(parent, DatabaseEditEvent.class);
//					intent.putExtra("game", getGame());
//					intent.putExtra("event", getGame().getSelectedMap().events[selectedId]);
//					parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
//				}
//			}
//		});
//
//		Button buttonReset = (Button)findViewById(R.id.buttonReset);
//		buttonReset.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Event e = new Event();
//				getGame().getSelectedMap().events[selectedId] = e;
//				CheckableArrayAdapterString adapter = 
//					(CheckableArrayAdapterString)listViewEvents.getAdapter();
//				adapter.replaceItem(selectedId, e.name);
//			}
//		});
//
//		Button buttonAdd = (Button)findViewById(R.id.buttonAdd);
//		buttonAdd.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) { 
//				CheckableArrayAdapterString adapter = 
//					(CheckableArrayAdapterString)listViewEvents.getAdapter();
//				
//				Event[] events = getGame().getSelectedMap().events;
//				int oldLength = events.length;
//				events = Arrays.copyOf(events, oldLength + 1);
//				for (int i = oldLength; i < events.length; i++) {
//					events[i] = new Event();
//					adapter.add(events[i].name);
//				}
//				getGame().getSelectedMap().events = events;
//				((CheckableArrayAdapterString)listViewEvents.getAdapter())
//					.notifyDataSetChanged();
//			}
//		});
//		
//		listViewEvents.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int selected,
//					long id) {
//				selectedId = selected;
//			}
//		});
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, Intent data) {
//		super.onActivityResult(requestCode, data);
//		if (selectedId >= 0) {
//			getGame().getSelectedMap().events[selectedId] = 
//				(Event)data.getExtras().getSerializable("event");
//		}
//		//populateListView();
//	}
//
//	private void populateListView() {
//		Event[] events = getGame().getSelectedMap().events;
//		ArrayList<String> names = new ArrayList<String>();
//		for (int i = 0; i < events.length; i++) {
//			Event event = events[i];
//			names.add(event.name);
//		}
//		listViewEvents.setAdapter(new CheckableArrayAdapterString(parent, names));
//		//doesn't work...
//		listViewEvents.setSelection(selectedId);
//	}
//
//	
}
