package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class PlatformDatabase extends PlatformDatabaseActivity {

	private int selectedIndex = -1;
	private ImageView imageView;
	private RadioGroup radioGroup; 
	private EditText editSize;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.platformactorselector);

		imageView = (ImageView)findViewById(R.id.imageViewSelectedActor);
		imageView.setScaleType(ScaleType.CENTER);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroupActors);
		editSize = (EditText)findViewById(R.id.editTextResize);

		createButtonEvents();
	}

	@Override
	public void onResume() {
		super.onResume();

		createRadioButtons();
	}

	private void createButtonEvents() {
		final PlatformDatabase me = this;

		Button editButton = (Button)findViewById(R.id.buttonEditActor);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedIndex >= 0) {
					Intent intent = new Intent(me, PlatformEditActor.class);
					intent.putExtra("id", selectedIndex);
					intent.putExtra("game", gameName);
					startActivity(intent);
				}
			}
		});

		Button reset = (Button)findViewById(R.id.buttonResetActor);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				game.actors[selectedIndex] = new PlatformActor();
				RadioButton button = (RadioButton)radioGroup.getChildAt(selectedIndex - 1);
				button.setText(game.actors[selectedIndex].name);
				loadSelectedBitmap();
			}
		});

		Button resize = (Button)findViewById(R.id.buttonResizeActors);
		resize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button me = (Button)v;
				if (editSize.getVisibility() == View.VISIBLE) {
					editSize.setVisibility(View.INVISIBLE);
					me.setText(R.string.resize);
					int newSize = Integer.parseInt(editSize.getText().toString()) + 1;
					if (newSize != game.actors.length) {
						PlatformActor[] newActors = new PlatformActor[newSize];
						for (int i = 0; i < newActors.length; i++) {
							if (i < game.actors.length) {
								newActors[i] = game.actors[i];
							} else {
								newActors[i] = new PlatformActor();
							}
						}
						game.actors = newActors;
						createRadioButtons();
					}
				} else {
					editSize.setVisibility(View.VISIBLE);
					editSize.setText("" + (game.actors.length - 1));
					me.setText("Set new size");
				}
			}
		});
	}

	private void createRadioButtons() {
		radioGroup.removeAllViews();
		imageView.setImageBitmap(null);
		selectedIndex = -1;
		for (int i = 1; i < game.actors.length; i++) {
			RadioButton button = new RadioButton(this);
			button.setText(game.actors[i].name);
			button.setTag(i);

			final PlatformDatabase me = this;
			final int index = i;
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					selectedIndex = index;
					loadSelectedBitmap();
				}
			});

			radioGroup.addView(button);
		}
	}

	private void loadSelectedBitmap() {
		String imageName = game.actors[selectedIndex].imageName;
		Bitmap bmp = Data.loadActor(imageName, this);
		bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
		imageView.setImageBitmap(bmp);
	}
}
