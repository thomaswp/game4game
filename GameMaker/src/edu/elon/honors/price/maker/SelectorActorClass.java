package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.Spinner;

public class SelectorActorClass extends Spinner {
	
	PlatformGame game;

	public int getSelectedActorId() {
		return getSelectedItemPosition() + 1;
	}
	
	public ActorClass getSelectedActor() {
		if (game == null) return null;
		return game.actors[getSelectedActorId()];
	}
	
	public SelectorActorClass(Context context, PlatformGame game) {
		super(context);	
	}
	
	public SelectorActorClass(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void populate(PlatformGame game) {
		Context context = getContext();
		
		this.game = game;
		
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		for (int i = 1; i < game.actors.length; i++) {
			labels.add(game.actors[i].name);
			String name = game.actors[i].imageName;
			Bitmap bmp = Data.loadActor(name); 
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
			images.add(bmp);
		}
		
		
		ImageAdapter imageAdapter = new ImageAdapter(
				context, 
				android.R.layout.simple_spinner_dropdown_item, 
				labels,
				images);
		
		setAdapter(imageAdapter);
	}
}
