package edu.elon.honors.price.maker;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class CheckableArrayAdapterString extends CheckableArrayAdapter<String> {

	private static final int listItemResourceId = R.layout.array_adapter_row_string;
	
	public CheckableArrayAdapterString(Context context, String[] items) {
		super(context, listItemResourceId, items);
	}

	public CheckableArrayAdapterString(Context context, List<String> items) {
		super(context, listItemResourceId, items);
	}

	@Override
	protected void setRow(int position, String item, View view) {
		TextView title = (TextView)view.findViewById(R.id.textViewTitle);
		title.setText(item);
	}
}
