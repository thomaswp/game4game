/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericharlow.DragNDrop;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.maker.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class DragNDropAdapter<T> extends BaseAdapter implements RemoveListener, DropListener<T> {

    private int layoutId;
    private LayoutInflater mInflater;
    private List<T> mContent;

    public DragNDropAdapter(Context context, List<T> content, int layoutId) {
    	mInflater = LayoutInflater.from(context);
    	mContent = content;
    	this.layoutId = layoutId;
    }
    
    /**
     * The number of items in the list
     * @see android.widget.ListAdapter#getCount()
     */
    @Override
	public int getCount() {
        return mContent.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficient to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    @Override
	public T getItem(int position) {
        return mContent.get(position);
    }

    /**
     * Use the array index as a unique id.
     * @see android.widget.ListAdapter#getItemId(int)
     */
    @Override
	public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, null);
        }
        
        setView(convertView, position, getItem(position));

        return convertView;
    }
    
    protected abstract void setView(View view, int position, T item);

	@Override
	public void onRemove(int which) {
		if (which < 0 || which > mContent.size()) return;		
		mContent.remove(which);
	}

	@Override
	public T onDropFrom(int from) {
		T temp = mContent.get(from);
		mContent.remove(from);
		return temp;
	}

	@Override
	public void onDropTo(int to, T item) {
		mContent.add(to, item);
	}
}