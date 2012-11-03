package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

@AutoAssign
public class DatabaseEditObjectClass extends DatabaseActivity {
	
	private int id;
	
	private EditText editTextObjectName;
	private SelectorObjectImage selectorObjectImage;
	private SelectorBehaviorInstances selectorBehaviorInstances;
	private Button buttonScale;
	private SeekBar seekBarDensity;
	private SelectorCollidesWith selectorCollidesWith;
	
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
		autoAssign();
		
		editTextObjectName.setText(objectClass.name);
		selectorObjectImage.setSelectedImageName(objectClass.imageName);
		selectorBehaviorInstances.setBehaviors(objectClass.behaviors, 
				BehaviorType.Object);
		selectorBehaviorInstances.populate(game);
		
		buttonScale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectorActivityScale.startForResult(
						DatabaseEditObjectClass.this, false, id);
			}
		});
		
		setButtonScaleText();
		
		seekBarDensity.setProgress((int)(objectClass.density * seekBarDensity.getMax()));
		seekBarDensity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				getObject().density = (float)progress / seekBar.getMax();
			}
		});
		
		selectorCollidesWith.setMapClass(getObject());
	}
	
	protected void setButtonScaleText() {
		buttonScale.setText(String.format("%.02f", getObject().zoom));
	}
	
	@Override
	protected void onFinishing() {
		ObjectClass objectClass = getObject();
		objectClass.name = editTextObjectName.getText().toString();
		objectClass.imageName = selectorObjectImage.getSelectedImageName();
		objectClass.behaviors = selectorBehaviorInstances.getBehaviors();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			selectorBehaviorInstances.populate(game);
			selectorBehaviorInstances.onActivityResult(requestCode, data);
			setButtonScaleText();
			selectorCollidesWith.setMapClass(getObject());
		}
	}
}