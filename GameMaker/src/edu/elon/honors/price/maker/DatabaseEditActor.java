package edu.elon.honors.price.maker;

import java.util.ArrayList;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.maker.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class DatabaseEditActor extends DatabaseActivity {

	public final static int SPEEDS = 10;

	final static float SPEED_SCALE = SPEEDS / ActorClass.MAX_SPEED;
	final static float JUMP_SCALE = SPEEDS / ActorClass.MAX_JUMP;

	private int actorId;
	private ActorClass actor;
	private EditText actorName;
	private Spinner eventSpinner, directionSpinner, behaviorSpinner;
	private SelectorActorImage imageSpinner;
	private SeekBar speed, jump;
	private boolean forceSelect;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actorId = getIntent().getExtras().getInt("id");
		setContentView(R.layout.database_edit_actor);

		actorName = (EditText)findViewById(R.id.editTextActorName);
		imageSpinner = (SelectorActorImage)findViewById(R.id.spinnerActorImage);
		speed = (SeekBar)findViewById(R.id.seekBarSpeed);
		jump = (SeekBar)findViewById(R.id.seekBarJump);
		eventSpinner = (Spinner)findViewById(R.id.spinnerEvent);
		directionSpinner = (Spinner)findViewById(R.id.spinnerDirection);
		behaviorSpinner = (Spinner)findViewById(R.id.spinnerBehavior);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		actor = game.actors[actorId];
		
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

		speed.setMax(SPEEDS);
		jump.setMax(SPEEDS);
		speed.setProgress((int)(actor.speed * SPEED_SCALE + 0.5f));
		jump.setProgress((int)(actor.jumpVelocity * JUMP_SCALE + 0.5f));

		eventSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_text, new String[] {
				"Touches a wall",
				"Touches an edge",
				"Touches the hero",
				"Touches another actor"
		}));

		directionSpinner.setAdapter(new ArrayAdapter<String>(
				this, R.layout.spinner_text, ActorClass.DIRECTIONS));

		behaviorSpinner.setAdapter(new ArrayAdapter<String>(
				this, R.layout.spinner_text, ActorClass.BEHAVIORS));
		
		eventSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int selection, long id) {
				eventSpinner.requestFocus();
				directionSpinner.setVisibility(id > 1 ? View.VISIBLE : View.INVISIBLE);
				int select = 0;
				int dirIndex = directionSpinner.getSelectedItemPosition();
				switch (selection) {
					case 0: select = actor.wallBehavior; break;
					case 1: select = actor.edgeBehavior; break;
					case 2: select = actor.heroContactBehaviors[dirIndex]; break;
					case 3: select = actor.actorContactBehaviors[dirIndex]; break;
				}
				forceSelect = true;
				behaviorSpinner.setSelection(select);
				forceSelect = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				directionSpinner.setVisibility(View.INVISIBLE);
			}
		});		
		
		directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int selection, long id) {
				
				int select = 0;
				int dirIndex = selection;
				int event = eventSpinner.getSelectedItemPosition();
				switch (event) {
					case 2: select = actor.heroContactBehaviors[dirIndex]; break;
					case 3: select = actor.actorContactBehaviors[dirIndex]; break;
					default: return;
				}
				forceSelect = true;
				behaviorSpinner.setSelection(select);
				forceSelect = false;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				directionSpinner.setVisibility(View.INVISIBLE);
			}
		});
		
		behaviorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int selection, long id) {
				
				if (forceSelect) return;
				
				int event = eventSpinner.getSelectedItemPosition();
				int dirIndex = directionSpinner.getSelectedItemPosition();
				switch (event) {
					case 0: actor.wallBehavior = selection; break;
					case 1: actor.edgeBehavior = selection; break;
					case 2: actor.heroContactBehaviors[dirIndex] = selection; break;
					case 3: actor.actorContactBehaviors[dirIndex] = selection; break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				directionSpinner.setVisibility(View.INVISIBLE);
			}
		});
		

		directionSpinner.setSelection(0);
		eventSpinner.setSelection(0);

		setDefaultButtonActions();
	}
	
	@Override
	public void onFinishing() {
		actor.name = actorName.getText().toString();
		actor.imageName = imageSpinner.getSelectedImageName();
		actor.speed = speed.getProgress() / SPEED_SCALE;
		actor.jumpVelocity = jump.getProgress() / JUMP_SCALE;
		game.actors[actorId] = actor;
	}
}
