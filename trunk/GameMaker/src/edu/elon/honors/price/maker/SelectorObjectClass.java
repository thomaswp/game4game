package edu.elon.honors.price.maker;

import java.util.ArrayList;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.SelectorActorClass.OnActorClassChangedListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class SelectorObjectClass  extends Spinner implements IPopulatable {

	protected final static int MAX_IMAGE_SIZE = 100;
	
	private PlatformGame game;
	private OnObjectClassChangedListener onObjectClassChangedListener;
	private int id;
	
	public SelectorObjectClass(Context context) {
		super(context);
	}
	
	public SelectorObjectClass(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public int getSelectedObjectId() {
		return id;
	}
	
	public void setSelectedObjectId(int id) {
		this.id = id;
		setSelection(id);
	}
	
	public ActorClass getSelectedActor() {
		if (game == null) return null;
		return game.actors[getSelectedObjectId()];
	}
	
	public void setOnActorClassChangedListenter(OnObjectClassChangedListener onObjectClassChangedListener) {
		this.onObjectClassChangedListener = onObjectClassChangedListener;
	}
	
	public void populate(PlatformGame game) {
		Context context = getContext();
		
		//figure out how to maintain this, but
		//change as necessary if the game changes...
		
		this.game = game;
		
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		for (int i = 0; i < game.objects.length; i++) {
			labels.add(game.objects[i].name);
			String name = game.objects[i].imageName;
			Bitmap bitmap = Data.loadObject(name); 
			float zoom = game.objects[i].zoom;
			int width = (int)(bitmap.getWidth() * zoom);
			if (width > MAX_IMAGE_SIZE) {
				zoom *= (float)MAX_IMAGE_SIZE / width;
			}
			int height = (int)(bitmap.getHeight() * zoom);
			if (height > MAX_IMAGE_SIZE) {
				zoom *= (float)MAX_IMAGE_SIZE / width;
			}
			width = (int)(bitmap.getWidth() * zoom);
			height = (int)(bitmap.getHeight() * zoom);
			bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			images.add(bitmap);
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
				if (onObjectClassChangedListener != null) {
					onObjectClassChangedListener.onObjectClassChanged(position);
				}
				SelectorObjectClass.this.id = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
		
		setSelectedObjectId(id);
	}
	
	public static abstract class OnObjectClassChangedListener {
		public abstract void onObjectClassChanged(int newId);
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		return false;
	}
}
