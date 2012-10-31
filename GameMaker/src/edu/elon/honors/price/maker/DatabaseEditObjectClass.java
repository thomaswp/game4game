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
	
	private EditText editTextObjectName;
	private SelectorObjectImage selectorObjectImage;
	private SelectorBehaviorInstances selectorBehaviors;
	private Button buttonScale;
	
	public ObjectClass getObject() {
		return game.objects[id];
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		id = getIntent().getExtras().getInt("id");
		ObjectClass objectClass = getObject();
		
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
		
		buttonScale = (Button)findViewById(R.id.buttonEditScale); 
		buttonScale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectorActivityScale.startForResult(
						DatabaseEditObjectClass.this, false, id);
			}
		});
		
		setButtonScaleText();
	}
	
	protected void setButtonScaleText() {
		buttonScale.setText(String.format("%.02f", getObject().zoom));
	}
	
	@Override
	protected void onFinishing() {
		ObjectClass objectClass = getObject();
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
			setButtonScaleText();
		}
	}
}