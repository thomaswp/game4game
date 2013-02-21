package edu.elon.honors.price.maker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import edu.elon.honors.price.data.Behavior;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.types.EventPointer;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.action.EventContext;

public class SelectorEvent extends Spinner implements IPopulatable {

	private EventPointer event = new EventPointer();
	private PlatformGame game;
	private EventContext eventContext;
	private List<Event> events = new LinkedList<Event>();
	private ArrayAdapter<Event> adapter;
	
	public EventPointer getEvent() {
		return event;
	}
	
	public void setEvent(EventPointer event) {
		this.event = event;
		int index = event.getEventIndex();
		if (index >= 0 && index < events.size()) {
			setSelection(event.getEventIndex());
		}
	}
	
	public SelectorEvent(Context context, EventContext eventContext) {
		super(context);
		this.eventContext = eventContext;
		adapter = new ArrayAdapter<Event>(context, R.layout.spinner_text, events);
		setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int index, long id) {
				event.setEvent(game, SelectorEvent.this.eventContext.getBehavior(), 
						index);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		adapter.setDropDownViewResource(R.layout.spinner_text_dropdown);
		setAdapter(adapter);
	}
	
	@Override
	public void populate(PlatformGame game) {
		this.game = game;
		adapter.clear();
		if (eventContext.hasBehavior()) {
			for (Event e : eventContext.getBehavior().events) {
				adapter.add(e);
			}
		} else {
			for (Event e : game.getSelectedMap().events) {
				adapter.add(e);
			}
		}
		
		setEvent(event);
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		return false;
	}

}
