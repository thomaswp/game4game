package edu.elon.honors.price.maker;

import java.util.Arrays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
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
				if (item.zoom != 1) {
					bmp = Bitmap.createScaledBitmap(bmp, 
							(int)(bmp.getWidth() * item.zoom),
							(int)(bmp.getHeight() * item.zoom),
							true);
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

	@Override
	public void addEditorButtons() {
		TutorialUtils.addHighlightable(buttonEdit, 
				EditorButton.DatabaseObjectsEdit, parent);
	}
}
