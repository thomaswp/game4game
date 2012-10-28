package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ObjectClass;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

@AutoAssign
public class SelectorActivityScale extends DatabaseActivity {
	private LinearLayout linearLayoutPreview;
	private SeekBar seekBarScale;
	private TextView textViewInfo;
	private ImageView imageView;
	private CharacterUpdater characterUpdater;
	
	private Bitmap image;
	
	private static final int MAX_SIZE = 500;
	
	public static void startForResult(DatabaseActivity activity, boolean isActor, int index) {
		Intent intent = activity.getNewGameIntent(SelectorActivityScale.class);
		intent.putExtra("isActor", isActor);
		intent.putExtra("index", index);
		activity.startActivityForResult(intent, REQUEST_RETURN_GAME);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_activity_scale);
		autoAssign();
		setDefaultButtonActions();
		
		
		float scale;
		
		boolean isActor = getIntent().getBooleanExtra("isActor", true);
		int index = getIntent().getIntExtra("index", 0);
		if (isActor) {
			final ActorClass actor = game.actors[index];
			image = Data.loadActor(actor.imageName);
			scale = actor.zoom;
			characterUpdater = new CharacterUpdater() {
				@Override
				public void update(float newScale) {
					actor.zoom = newScale;
				}
			};
		} else {
			final ObjectClass object = game.objects[index];
			image = Data.loadObject(object.imageName);
			scale = object.zoom;
			characterUpdater = new CharacterUpdater() {
				@Override
				public void update(float newScale) {
					object.zoom = newScale;
				}
			};
		}
		
		linearLayoutPreview.addView(
				new SelectorMapPreview(this, game, savedInstanceState));
		
		imageView.setImageBitmap(image);
		
		if (image.getWidth() > image.getHeight()) {
			seekBarScale.setMax(MAX_SIZE);
		} else {
			seekBarScale.setMax(MAX_SIZE * image.getWidth() / image.getHeight());
		}
		seekBarScale.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				imageView.getLayoutParams().width = progress;
				imageView.getLayoutParams().height = progress * 
						image.getHeight() / image.getWidth();
				imageView.invalidate();
				
				makeInfoText();
				float newScale = (float)imageView.getWidth() / image.getWidth();
				characterUpdater.update(newScale);
			}
		});
		seekBarScale.setProgress((int)(image.getWidth() * scale));
	}
	
	private void makeInfoText() {
		textViewInfo.setText(String.format(
				"Scale: %.2f, Width: %d, Height %d",
				(float)imageView.getWidth() / image.getWidth(),
				imageView.getWidth(), imageView.getHeight()));
	}
	
	private interface CharacterUpdater {
		public void update(float newScale);
	}
}
