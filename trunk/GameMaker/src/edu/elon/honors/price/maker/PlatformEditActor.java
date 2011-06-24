package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.game.Game;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class PlatformEditActor extends PlatformDatabaseActivity {
	
	private static final float SPEED_SCALE = 50;
	
	private int actorId;
	private EditText actorName;
	private Button okButton;
	private Spinner imageSpinner;
	private ImageView actorImage;
	private EditText speed, jump;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		actorId = getIntent().getExtras().getInt("id");
		setContentView(R.layout.platformactoreditor);
		
		actorName = (EditText)findViewById(R.id.editTextActorName);
		okButton = (Button)findViewById(R.id.buttonOk);
		imageSpinner = (Spinner)findViewById(R.id.spinnerActorImage);
		actorImage = (ImageView)findViewById(R.id.imageViewActor);
		speed = (EditText)findViewById(R.id.editTextSpeed);
		jump = (EditText)findViewById(R.id.editTextJump);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		final PlatformEditActor me = this;
		
		actorName.setText(getActor().name);
		
		final ArrayList<String> imageNames = Data.getResources(Data.ACTORS_DIR, this);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item,
				imageNames);
		imageSpinner.setAdapter(spinnerAdapter);
		imageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				Bitmap bmp = Data.loadActor(imageNames.get(position), me);
				bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
				actorImage.setImageBitmap(bmp);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				actorImage.setImageBitmap(null);
			}
		});
		for (int i = 0; i < imageNames.size(); i++) {
			if (imageNames.get(i).equals(getActor().imageName))
				imageSpinner.setSelection(i);
		}
		
		speed.setText("" + (int)(getActor().speed * SPEED_SCALE));
		jump.setText("" + (int)(getActor().jumpVelocity * SPEED_SCALE));
		
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActor().name = actorName.getText().toString();
				getActor().imageName = (String)imageSpinner.getSelectedItem();
				getActor().speed = Integer.parseInt(speed.getText().toString()) / SPEED_SCALE;
				getActor().jumpVelocity = Integer.parseInt(jump.getText().toString()) / SPEED_SCALE;
				finish();
			}
		});
	}
	
	private PlatformActor getActor() {
		return game.actors[actorId];
	}
}
