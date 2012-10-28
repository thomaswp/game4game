package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DatabaseEditObjectClass extends DatabaseActivity {
	
	private int id;
	private ObjectClass objectClass;
	
	private EditText editTextObjectName;
	private SelectorObjectImage selectorObjectImage;
	private SelectorBehaviorInstances selectorBehaviors;
	
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
		
		selectorBehaviors = (SelectorBehaviorInstances)findViewById(
				R.id.selectorBehaviorInstances);
		selectorBehaviors.setBehaviors(objectClass.behaviors, 
				BehaviorType.Object);
		selectorBehaviors.populate(game);
		
		((Button)findViewById(R.id.buttonEditZoom))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectorActivityScale.startForResult(
						DatabaseEditObjectClass.this, false, id);
			}
		});
	}
	
	@Override
	protected void onFinishing() {
		objectClass.name = editTextObjectName.getText().toString();
		objectClass.imageName = selectorObjectImage.getSelectedImageName();
		objectClass.behaviors = selectorBehaviors.getBehaviors();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			selectorBehaviors.populate(game);
			selectorBehaviors.onActivityResult(requestCode, data);
		}
	}
}