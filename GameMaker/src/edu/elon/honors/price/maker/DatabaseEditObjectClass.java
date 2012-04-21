package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ObjectClass;
import android.os.Bundle;
import android.widget.EditText;

public class DatabaseEditObjectClass extends DatabaseActivity {
	
	private int id;
	private ObjectClass objectClass;
	
	private EditText editTextObjectName;
	private SelectorObjectImage selectorObjectImage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		id = getIntent().getExtras().getInt("id");
		objectClass = game.objects[id];
		
		setContentView(R.layout.database_edit_object);
		setDefaultButtonActions();
		
		editTextObjectName = (EditText)findViewById(R.id.editTextObjectName);
		editTextObjectName.setText(objectClass.name);
		
		selectorObjectImage = (SelectorObjectImage)findViewById(R.id.selectorObjectImage);
		selectorObjectImage.setSelectedImageName(objectClass.imageName);
	}
	
	@Override
	protected void onFinishing() {
		objectClass.name = editTextObjectName.getText().toString();
		objectClass.imageName = selectorObjectImage.getSelectedImageName();
	}
}