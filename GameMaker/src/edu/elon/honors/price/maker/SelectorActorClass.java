package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class SelectorActorClass extends Spinner {
	
	private PlatformGame game;
	private OnActorClassChangedListener onActorClassChangedListener;

	public int getSelectedActorId() {
		return getSelectedItemPosition() + 1;
	}
	
	public void setSelectedActorId(int id) {
		setSelection(id - 1);
	}
	
	public ActorClass getSelectedActor() {
		if (game == null) return null;
		return game.actors[getSelectedActorId()];
	}
	
	public void setOnActorClassChangedListenter(OnActorClassChangedListener onActorClassChangedListener) {
		this.onActorClassChangedListener = onActorClassChangedListener;
	}
	
	public SelectorActorClass(Context context, PlatformGame game) {
		super(context);	
	}
	
	public SelectorActorClass(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void populate(PlatformGame game) {
		Context context = getContext();
		
		//figure out how to maintain this, but
		//change as necessary if the game changes...
		
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
		
		setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (onActorClassChangedListener != null) {
					onActorClassChangedListener.onActorClassChanged(position + 1);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
	}
	
	public static abstract class OnActorClassChangedListener {
		public abstract void onActorClassChanged(int newId);
	}
}
