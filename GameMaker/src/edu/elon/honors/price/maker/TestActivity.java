package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TestActivity extends DatabaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);
		
		ListView lv = new ListView(this);
		layout.addView(lv);
		ArrayList<String> items = new ArrayList<String>();
		String[] itemsA =
		"Hello this is a bunch of words and I think you should use them".split(" ");
		for (String item : itemsA) items.add(item);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setAdapter(new MyAdapter(this, R.layout.spinner_text, items));
		
		setContentView(layout);
	}
	
	private static class MyAdapter extends CheckableArrayAdapter<String> {

		public MyAdapter(Context context, int listItemResourceId,
				ArrayList<String> items) {
			super(context, listItemResourceId, items);
		}

		@Override
		protected void setRow(int position, String item, View view) {
			((TextView)view).setText(item);
		}
		
	}
	
}
