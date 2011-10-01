package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.Spinner;

public class SelectorActorImage extends Spinner {

	PlatformGame game;

	public String getSelectedImageName() {
		return (String)getSelectedItem();
	}
	
	public void setSelectedImageName(String name) {
		for (int i = 0; i < getCount(); i++) {
			if (name.equals(getItemAtPosition(i))) {
				setSelection(i);
			}
		}
	}
	
	public SelectorActorImage(Context context) {
		super(context);
		setup();
	} 
	
	public SelectorActorImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}
	
	private void setup() {
		Context context = getContext();
		
		ArrayList<String> imageNames = Data.getResources(Data.ACTORS_DIR, context);
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		for (int i = 0; i < imageNames.size(); i++) {
			Bitmap bmp = Data.loadActor(imageNames.get(i));
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
			images.add(bmp);
		}
		
		ImageAdapter adapter = new ImageAdapter(
				context, 
				android.R.layout.simple_spinner_dropdown_item, 
				imageNames,
				images);
		
		setAdapter(adapter);
	}
}
