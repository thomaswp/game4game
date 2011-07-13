package edu.elon.honors.price.maker;

import java.util.ArrayList;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Hero;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.R;
import edu.elon.honors.price.maker.DatabaseEditActor.ImageAdapter;

public class PageHero extends Page {
	
	final float speedScale = DatabaseEditActor.SPEEDS / ActorClass.MAX_SPEED;
	final float jumpScale = DatabaseEditActor.SPEEDS / ActorClass.MAX_JUMP;
	private Hero actor;
	private Spinner imageSpinner, eventSpinner, directionSpinner, behaviorSpinner;
	private SeekBar speed, jump;
	private boolean forceSelect;
	
	@Override
	public int getViewId() {
		return R.layout.platformheroeditor;
	}


	@Override
	public String getName() {
		return "Hero";
	}
	
	public PageHero(Database parent) {
		super(parent);
	}

	
	@Override
	public void onCreate() {
		imageSpinner = (Spinner)findViewById(R.id.spinnerActorImage);
		speed = (SeekBar)findViewById(R.id.seekBarSpeed);
		jump = (SeekBar)findViewById(R.id.seekBarJump);
		eventSpinner = (Spinner)findViewById(R.id.spinnerEvent);
		directionSpinner = (Spinner)findViewById(R.id.spinnerDirection);
		behaviorSpinner = (Spinner)findViewById(R.id.spinnerBehavior);
	}

	@Override
	public void onPause() {
		actor.imageName = (String)imageSpinner.getSelectedItem();
		actor.speed = speed.getProgress() / speedScale;
		actor.jumpVelocity = jump.getProgress() / jumpScale;
	}
	
	@Override
	public void onResume() {
		actor = getGame().hero;
		
		final ArrayList<String> imageNames = Data.getResources(Data.ACTORS_DIR, parent);
		ImageAdapter spinnerAdapter = new ImageAdapter(parent,
				android.R.layout.simple_spinner_dropdown_item,
				imageNames);
		imageSpinner.setAdapter(spinnerAdapter);
		for (int i = 0; i < imageNames.size(); i++) {
			if (imageNames.get(i).equals(actor.imageName))
				imageSpinner.setSelection(i);
		}

		speed.setMax(DatabaseEditActor.SPEEDS);
		jump.setMax(DatabaseEditActor.SPEEDS);
		speed.setProgress((int)(actor.speed * speedScale + 0.5f));
		jump.setProgress((int)(actor.jumpVelocity * jumpScale + 0.5f));

		eventSpinner.setAdapter(new ArrayAdapter<String>(parent, R.layout.spinner_text, new String[] {
				"Touches a wall",
				"Touches an edge",
				"Touches another actor"
		}));
		
		directionSpinner.setAdapter(new ArrayAdapter<String>(parent, R.layout.spinner_text, new String[] {
				"Above this actor",
				"Below this actor",
				"Left of this actor",
				"Right of this actor",
		}));
		
		behaviorSpinner.setAdapter(new ArrayAdapter<String>(parent, R.layout.spinner_text, new String[] {
				"Nothing",
				"Stop",
				"Turn around",
				"Jump",
				"Jump and turn",
				"Toggle Start/Stop",
				"Become stunned",
				"Die"
		}));
		
		eventSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int selection, long id) {
				directionSpinner.setVisibility(id > 1 ? View.VISIBLE : View.INVISIBLE);
				int select = 0;
				int dirIndex = directionSpinner.getSelectedItemPosition();
				switch (selection) {
					case 0: select = actor.wallBehavior; break;
					case 1: select = actor.edgeBehavior; break;
					case 2: select = actor.actorContactBehaviors[dirIndex]; break;
				}
				forceSelect = true;
				behaviorSpinner.setSelection(select);
				Game.debug(select);
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
					case 2: select = actor.actorContactBehaviors[dirIndex]; break;
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
					case 2: actor.actorContactBehaviors[dirIndex] = selection; break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				directionSpinner.setVisibility(View.INVISIBLE);
			}
		});

		eventSpinner.setSelection(0);
		directionSpinner.setSelection(0);
	}
}
