package edu.elon.honors.price.maker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.TextView;

public class CheckableArrayAdapterString extends ArrayAdapter<String> {
	
	ArrayList<String> items;
	int checkedIndex = -1;
	
	public CheckableArrayAdapterString(Context context, int textViewResourceId, ArrayList<String> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
			convertView = inflater.inflate(R.layout.checkable_array_adapter_row, parent, false);
		}
		setRow(convertView, getItem(position));
		if (checkedIndex == position) {
			//TODO: Fix your life
		}
		
		
		return convertView;
	}		

	public void addItem(String item) {
		items.add(item);
		notifyDataSetChanged();
	}
	
	public String removeItem(int index) {
		String s = items.remove(index);
		notifyDataSetChanged();
		return s;
	}
	
	public void insertItem(String item, int index) {
		items.add(index, item);
		notifyDataSetChanged();
	}
	
	public void replaceItem(int index, String newItem) {
		items.remove(index);
		items.add(index, newItem);
		notifyDataSetChanged();
	}
	
//	public static int getSelectedId(ListView listView) {
//		for (int i = 0; i < listView.getChildCount(); i++) {
//			if (listView.getChildAt(i) instanceof Checkable) {
//				if (((Checkable)listView.getChildAt(i)).isChecked()) {
//					return i + listView.getFirstVisiblePosition();
//				}
//			}
//		}
//		return -1;
//	}
//	
//	public static void setSelectedId(ListView listView, int id) {
//		for (int i = 0; i < listView.getChildCount(); i++) {
//			if (listView.getChildAt(i) instanceof Checkable && i == id) {
//				((Checkable)listView.getChildAt(i)).setChecked(true);
//			}
//		}
//	}

	private static void setRow(View row, String string) {
//		TextView label=(TextView)row.findViewById(R.id.textView);
//		label.setText(string);
//		label.setTextSize(20);
	}
}
