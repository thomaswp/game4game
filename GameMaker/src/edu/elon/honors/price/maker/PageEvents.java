package edu.elon.honors.price.maker;

import java.util.Arrays;

import edu.elon.honors.price.data.Event;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
	public void onCreate(ViewGroup parentView) {
		super.onCreate(parentView);

		Button buttonEnable = new Button(parent);
		buttonEnable.setText("Enable/Disable");
		buttonEnable.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Event event = getEvent(getSelectedIndex());
				event.disabled = !event.disabled;
				listView.invalidateViews();
			}
		});
		addPanelView(buttonEnable);
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
				TextView tv = ((TextView)view.findViewById(R.id.textViewTitle));
				if (item.disabled) {
					CharSequence text = Html.fromHtml(
							TextUtils.getColoredText(item.name, TextUtils.COLOR_DISABLED));
					Spannable span = Spannable.Factory.getInstance().newSpannable(text);
					TextUtils.strikeText(span);
					tv.setText(span);
				} else {
					tv.setText(item.name);
				}
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

	@Override
	public void addEditorButtons() {
		// TODO Auto-generated method stub
		
	}	
}
