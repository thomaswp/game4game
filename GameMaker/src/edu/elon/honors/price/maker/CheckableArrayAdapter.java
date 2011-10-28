package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.TextView;

public class CheckableArrayAdapter extends ArrayAdapter<String> {
	public CheckableArrayAdapter(Context context, int textViewResourceId, String[] strings) {
		super(context, textViewResourceId, strings);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		View row=inflater.inflate(R.layout.checkable_array_adapter_row, parent, false);
		setRow(row, getItem(position), getContext());
		return row;
	}		

	public static int getSelectedId(ListView listView) {
		for (int i = 0; i < listView.getChildCount(); i++) {
			if (listView.getChildAt(i) instanceof Checkable) {
				if (((Checkable)listView.getChildAt(i)).isChecked()) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public static void setSelectedId(ListView listView, int id) {
		for (int i = 0; i < listView.getChildCount(); i++) {
			if (listView.getChildAt(i) instanceof Checkable && i == id) {
				((Checkable)listView.getChildAt(i)).setChecked(true);
			}
		}
	}

	private static void setRow(View row, String string, Context context) {
		TextView label=(TextView)row.findViewById(R.id.textView);
		label.setText(string);
		label.setTextSize(20);
	}
}
