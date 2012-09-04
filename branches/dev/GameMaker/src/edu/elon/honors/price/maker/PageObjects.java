package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.maker.DatabaseEditObjectClass;

public class PageObjects extends Page {

	private ListView listViewObjects;
	private int selectedId;

	public PageObjects(Database parent) {
		super(parent);
	}

	@Override
	public int getViewId() {
		return R.layout.page_objects;
	}

	@Override
	public String getName() {
		return "Objects";
	}

	@Override
	public void onCreate() {
		listViewObjects = (ListView)findViewById(R.id.listViewObjects);

		Button buttonEdit = (Button)findViewById(R.id.buttonEdit);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedId >= 0) {
					Intent intent = new Intent(parent, DatabaseEditObjectClass.class);
					intent.putExtra("game", getGame());
					intent.putExtra("id", selectedId);
					parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
				}
			}
		});

		Button buttonReset = (Button)findViewById(R.id.buttonReset);
		buttonReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ObjectClass o = new ObjectClass();
				getGame().objects[selectedId] = o;
				CheckableArrayAdapter adapter = 
					(CheckableArrayAdapter)listViewObjects.getAdapter();
				adapter.replaceItem(selectedId, o.name);
			}
		});

		Button buttonAdd = (Button)findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				CheckableArrayAdapter adapter = 
					(CheckableArrayAdapter)listViewObjects.getAdapter();
				
				ObjectClass[] objects = getGame().objects;
				int oldLength = objects.length;
				objects = Arrays.copyOf(objects, oldLength + 1);
				for (int i = oldLength; i < objects.length; i++) {
					objects[i] = new ObjectClass();
					adapter.addItem(objects[i].name);
				}
				getGame().objects = objects;
				((CheckableArrayAdapter)listViewObjects.getAdapter())
					.notifyDataSetChanged();
			}
		});
		
		listViewObjects.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int selected,
					long id) {
				selectedId = selected;
			}
		});
	}

	@Override
	public void onResume() {
		populateListView();
	}

	private void populateListView() {
		ObjectClass[] objects = getGame().objects;
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < objects.length; i++) {
			ObjectClass object = objects[i];
			names.add(object.name);
		}
		listViewObjects.setAdapter(new CheckableArrayAdapter(parent, R.layout.checkable_array_adapter_row, names));
		listViewObjects.setSelection(0);
	}

}
