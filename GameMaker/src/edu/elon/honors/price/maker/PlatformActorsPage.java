package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ImageView.ScaleType;

public class PlatformActorsPage extends PlatformDatabasePage{
	private int selectedIndex = -1;
	private ImageView imageView;
	private RadioGroup radioGroup; 
	private EditText editSize;
	
	public PlatformActorsPage(PlatformDatabase parent, int viewId) {
		super(parent, viewId);
	}
	
	@Override
	public void onCreate() {
		imageView = (ImageView)findViewById(R.id.imageViewSelectedActor);
		imageView.setScaleType(ScaleType.CENTER);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroupActors);
		editSize = (EditText)findViewById(R.id.editTextResize);

		createButtonEvents();
	}
	
	@Override
	public void onResume() {
		createRadioButtons();
	}
	

	@Override
	public void onPause() {	}
	
	private void createButtonEvents() {

		Button editButton = (Button)findViewById(R.id.buttonEditActor);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedIndex >= 0) {
					Intent intent = new Intent(parent, PlatformEditActor.class);
					intent.putExtra("id", selectedIndex);
					intent.putExtra("game", parent.gameName);
					parent.startActivity(intent);
				}
			}
		});

		Button reset = (Button)parent.findViewById(R.id.buttonResetActor);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getGame().actors[selectedIndex] = new PlatformActor();
				RadioButton button = (RadioButton)radioGroup.getChildAt(selectedIndex - 1);
				button.setText(getGame().actors[selectedIndex].name);
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
					if (newSize != getGame().actors.length) {
						PlatformActor[] newActors = new PlatformActor[newSize];
						for (int i = 0; i < newActors.length; i++) {
							if (i < getGame().actors.length) {
								newActors[i] = getGame().actors[i];
							} else {
								newActors[i] = new PlatformActor();
							}
						}
						getGame().actors = newActors;
						createRadioButtons();
					}
				} else {
					editSize.setVisibility(View.VISIBLE);
					editSize.setText("" + (getGame().actors.length - 1));
					me.setText("Set new size");
				}
			}
		});
	}

	private void createRadioButtons() {
		radioGroup.removeAllViews();
		imageView.setImageBitmap(null);
		selectedIndex = -1;
		for (int i = 1; i < getGame().actors.length; i++) {
			RadioButton button = new RadioButton(parent);
			button.setText(getGame().actors[i].name);
			button.setTag(i);

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
		String imageName = getGame().actors[selectedIndex].imageName;
		Bitmap bmp = Data.loadActor(imageName, parent);
		bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
		imageView.setImageBitmap(bmp);
	}
}
