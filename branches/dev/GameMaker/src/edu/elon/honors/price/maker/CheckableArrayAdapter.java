package edu.elon.honors.price.maker;

import java.util.List;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.CheckableLinearLayout.OnCheckedChangedListener;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;

public abstract class CheckableArrayAdapter<T> extends ArrayAdapter<T> {
	
	List<T> items;
	int childResource;
	int checkedIndex = -1;
	LayoutInflater inflater;
	
	public int getCheckedIndex() {
		return checkedIndex;
	}
	
	public void setCheckedIndex(int checkedIndex) {
		this.checkedIndex = checkedIndex;
		notifyDataSetChanged();
	}
	
	protected abstract void setRow(int position, T item, View view);
	
	public CheckableArrayAdapter(Context context, int listItemResourceId, List<T> items) {
		super(context, 0, items);
		this.items = items;
		
		childResource = listItemResourceId;
		inflater = ((Activity)getContext()).getLayoutInflater();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.checkable_array_adapter_row, parent, false);
		}
		CheckableLinearLayout checkable = (CheckableLinearLayout)convertView;
		
		View child = null;
		if (checkable.getChildCount() == 2) {
			child = checkable.getChildAt(1);
		} else {
			child = inflater.inflate(childResource, null);
			LayoutParams lps = new LayoutParams(
					LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			lps.gravity = Gravity.CENTER_VERTICAL;
			checkable.addView(child, lps);
		}
		setRow(position, items.get(position), child);
		
		checkable.setChecked(checkedIndex == position);
		checkable.setOnCheckedChangedListener(new OnCheckedChangedListener() {
			@Override
			public void onCheckChanged(boolean checked) {
				checkedIndex = position;
			}
		});
		
		return convertView;
	}		

	public void addItem(T item) {
		items.add(item);
		notifyDataSetChanged();
	}
	
	public T removeItem(int index) {
		T s = items.remove(index);
		notifyDataSetChanged();
		return s;
	}
	
	public void insertItem(T item, int index) {
		items.add(index, item);
		notifyDataSetChanged();
	}
	
	public void replaceItem(int index, T newItem) {
		items.remove(index);
		items.add(index, newItem);
		notifyDataSetChanged();
	}
}
