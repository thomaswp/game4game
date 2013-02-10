package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Behavior.BehaviorType;
import edu.elon.honors.price.maker.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;

public class DatabaseEditActorClass extends DatabaseActivity {

	public final static int INCREMENTS = 20;

	final static float SPEED_SCALE = INCREMENTS / ActorClass.MAX_SPEED;
	final static float JUMP_SCALE = INCREMENTS / ActorClass.MAX_JUMP;

	private int actorId;
	private EditText actorName;
	private SelectorActorImage imageSpinner;
	private SeekBar speed, jump;
	private Button buttonScale;
	private SelectorBehaviorInstances selectorBehaviors;
	private SelectorCollidesWith selectorCollidesWith;
	@AutoAssign
	private CheckBox checkBoxDoubleJump;
	
	private ActorClass getActor() {
		return game.actors[actorId];
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actorId = getIntent().getExtras().getInt("id");
		setContentView(R.layout.database_edit_actor);

		actorName = (EditText)findViewById(R.id.editTextActorName);
		imageSpinner = (SelectorActorImage)findViewById(R.id.spinnerActorImage);
		speed = (SeekBar)findViewById(R.id.seekBarSpeed);
		jump = (SeekBar)findViewById(R.id.seekBarJump);
		selectorBehaviors = (SelectorBehaviorInstances)findViewById(
				R.id.selectorBehaviorInstances);
		buttonScale = (Button)findViewById(R.id.buttonScale);
		selectorCollidesWith = (SelectorCollidesWith)findViewById(R.id.selectorCollidesWith);
		
		autoAssign();
		
		selectorBehaviors.populate(game);
		
		ActorClass actor = getActor();
		
		actorName.setText(actor.name);
		
		ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
		scroll.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (actorName.hasFocus()) {
					actorName.clearFocus();
				}
				return false;
			}
		});

		imageSpinner.setSelectedImageName(actor.imageName);

		speed.setMax(INCREMENTS);
		jump.setMax(INCREMENTS);
		speed.setProgress((int)(actor.speed * SPEED_SCALE + 0.5f));
		jump.setProgress((int)(actor.jumpVelocity * JUMP_SCALE + 0.5f));
		
		selectorBehaviors.setBehaviors(actor.behaviors,
				BehaviorType.Actor);
		
		setButtonScaleText();
		buttonScale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SelectorActivityScale.startForResult(
						DatabaseEditActorClass.this, true, actorId);
			}
		});

		selectorCollidesWith.setMapClass(actor);
		
		checkBoxDoubleJump.setChecked(actor.doubleJump);
		checkBoxDoubleJump.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				getActor().doubleJump = isChecked;
			}
		});
		
		setDefaultButtonActions();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			selectorBehaviors.populate(game);
			setButtonScaleText();
			selectorBehaviors.onActivityResult(requestCode, data);
			selectorCollidesWith.setMapClass(getActor());
		}
	}
	
	private void setButtonScaleText() {
		buttonScale.setText(String.format("%.02f", getActor().zoom));
	}
	
	@Override
	public void onFinishing() {
		ActorClass actor = getActor();
		
		actor.name = actorName.getText().toString();
		actor.imageName = imageSpinner.getSelectedImageName();
		actor.speed = speed.getProgress() / SPEED_SCALE;
		actor.jumpVelocity = jump.getProgress() / JUMP_SCALE;
		game.actors[actorId] = actor;
		actor.behaviors = selectorBehaviors.getBehaviors();
	}
}
