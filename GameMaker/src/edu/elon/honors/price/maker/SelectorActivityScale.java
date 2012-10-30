package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	private Button buttonReset;
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
			image = Data.loadActorIcon(actor.imageName);
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
				new SelectorMapPreviewScale(this, game, savedInstanceState));
		
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

				float newScale = (float)progress / image.getWidth();
				makeInfoText(newScale);
				characterUpdater.update(newScale);
			}
		});
		seekBarScale.setProgress((int)(image.getWidth() * scale));
		
		buttonReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				seekBarScale.setProgress(image.getWidth());
			}
		});
	}
	
	private void makeInfoText(float scale) {
		textViewInfo.setText(String.format(
				"Scale: %.2f, Width: %d, Height %d",
				scale, (int)(image.getWidth() * scale), 
				(int)(image.getHeight() * scale)));
	}
	
	private interface CharacterUpdater {
		public void update(float newScale);
	}
	
	private class SelectorMapPreviewScale extends SelectorMapPreview {
		public SelectorMapPreviewScale(Context context, PlatformGame game,
				Bundle savedInstanceState) {
			super(context, game, savedInstanceState);
		}
		
		@Override
		protected void drawObject(Canvas c, ObjectInstance instance, float x, float y, 
				Bitmap bitmap, Paint paint) {
			ObjectClass objectClass = game.objects[instance.classIndex];
			bitmap = Data.loadObject(objectClass.imageName);
			paint.setAlpha(255);
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setFilterBitmap(true);
			c.save();
			c.scale(objectClass.zoom, objectClass.zoom, x, y);
			c.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y - bitmap.getWidth() / 2, paint);
			c.restore();
			paint.reset();
		}
		
	}
}
