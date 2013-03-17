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
}
