package edu.elon.honors.price.maker;

import java.util.Arrays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.maker.DatabaseEditObjectClass;

public class PageObjects extends PageList<ObjectClass> {
	
	protected ObjectClass getObject(int index) {
		return getObjects()[index];
	}
	
	protected ObjectClass[] getObjects() {
		return getGame().objects;
	}

	public PageObjects(Database parent) {
		super(parent);
	}

	@Override
	public String getName() {
		return "Objects";
	}

	@Override
	protected void editItem(int index) {
		Intent intent = new Intent(parent, DatabaseEditObjectClass.class);
		intent.putExtra("game", getGame());
		intent.putExtra("id", index);
		parent.startActivityForResult(intent, REQUEST_EDIT_ITEM);
	}

	@Override
	protected void resetItem(int index) {
		getObjects()[index] = new ObjectClass();
	}

	@Override
	protected void addItem() {
		int length = getObjects().length;
		getGame().objects = Arrays.copyOf(
				getObjects(), length + 1);
		getObjects()[length] = new ObjectClass();
	}

	@Override
	protected CheckableArrayAdapter<ObjectClass> getAdapter() {
		return new CheckableArrayAdapter<ObjectClass>(parent, R.layout.array_adapter_row_image, getObjects()) {
			
			@Override
			protected void setRow(int position, ObjectClass item, View row) {
				TextView label=(TextView)row.findViewById(R.id.textViewTitle);
				label.setText(item.name);
				ImageView icon=(ImageView)row.findViewById(R.id.imageViewIcon);
				Bitmap bmp = Data.loadObject(item.imageName);
				int maxHeight = 100;
				if (bmp.getHeight() > maxHeight) {
					int width = bmp.getWidth() * maxHeight / bmp.getHeight();
					bmp = Bitmap.createScaledBitmap(bmp, width, maxHeight, true);
				}
				icon.setImageBitmap(bmp);
			}
		};
	}

	@Override
	protected ObjectClass getItem(int index) {
		return getObject(index);
	}

	@Override
	protected String getItemCategory() {
		return "Object";
	}


//	@Override
//	public void onCreate(ViewGroup parentView) {
//		super.onCreate(parentView);
//		listViewObjects = (ListView)findViewById(R.id.listViewObjects);
//
//		Button buttonEdit = (Button)findViewById(R.id.buttonEdit);
//		buttonEdit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (selectedId >= 0) {
//					Intent intent = new Intent(parent, DatabaseEditObjectClass.class);
//					intent.putExtra("game", getGame());
//					intent.putExtra("id", selectedId);
//					parent.startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
//				}
//			}
//		});
//
//		Button buttonReset = (Button)findViewById(R.id.buttonReset);
//		buttonReset.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ObjectClass o = new ObjectClass();
//				getGame().objects[selectedId] = o;
//				CheckableArrayAdapterString adapter = 
//					(CheckableArrayAdapterString)listViewObjects.getAdapter();
//				adapter.replaceItem(selectedId, o.name);
//			}
//		});
//
//		Button buttonAdd = (Button)findViewById(R.id.buttonAdd);
//		buttonAdd.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) { 
//				CheckableArrayAdapterString adapter = 
//					(CheckableArrayAdapterString)listViewObjects.getAdapter();
//				
//				ObjectClass[] objects = getGame().objects;
//				int oldLength = objects.length;
//				objects = Arrays.copyOf(objects, oldLength + 1);
//				for (int i = oldLength; i < objects.length; i++) {
//					objects[i] = new ObjectClass();
//					adapter.add(objects[i].name);
//				}
//				getGame().objects = objects;
//				((CheckableArrayAdapterString)listViewObjects.getAdapter())
//					.notifyDataSetChanged();
//			}
//		});
//		
//		listViewObjects.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int selected,
//					long id) {
//				selectedId = selected;
//			}
//		});
//	}
//
//	@Override
//	public void onResume() {
//		populateListView();
//	}
//
//	private void populateListView() {
//		ObjectClass[] objects = getGame().objects;
//		ArrayList<String> names = new ArrayList<String>();
//		for (int i = 0; i < objects.length; i++) {
//			ObjectClass object = objects[i];
//			names.add(object.name);
//		}
//		listViewObjects.setAdapter(new CheckableArrayAdapterString(parent, names));
//		listViewObjects.setSelection(0);
//	}

}
