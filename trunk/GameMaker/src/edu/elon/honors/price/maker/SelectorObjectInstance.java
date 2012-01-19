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
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectorObjectInstance extends Button implements IPopulatable {

	protected final static int MAX_IMAGE_SIZE = 100;
	
	private PlatformGame game;
	private int id = 0;
	
	public void setSelectedInstance(int id) {
		this.id = id;
		
		ObjectInstance instance = game.getSelectedMap().objects.get(id);
		ObjectClass objectClass = game.objects[instance.classIndex];
		Bitmap bitmap = Data.loadObject(objectClass.imageName);
		float zoom = objectClass.zoom;
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
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		//String text = String.format("%03d: ", id) + objectClass.name;
		String text = objectClass.name;
		
		setText(text);
		setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
	}
	
	public SelectorObjectInstance(Context context) {
		super(context);
	}
	
	public SelectorObjectInstance(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void populate(final PlatformGame game) {
		this.game = game; 
		setSelectedInstance(id);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), SelectorMapObjectInstance.class);
				intent.putExtra("game", game);
				intent.putExtra("id", id);
				((Activity)getContext()).startActivityForResult(intent, getId());
			}
		});
	}

	@Override
	public boolean onActivityResult(int requestCode, Intent data) {
		if (requestCode == getId()) {
			setSelectedInstance(data.getExtras().getInt("id"));
			return true;
		}
		return false;
	}

}
