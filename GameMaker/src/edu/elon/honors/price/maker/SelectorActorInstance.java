package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class SelectorActorInstance extends Button {

	private PlatformGame game;
	private int id = 1;
	
	public int getSelectedInstance() {
		return id;
	}
	
	public void setSelectedInstance(int id) {
		this.id = id;
		
		BitmapDrawable drawable;
		String text;
		if (id < 1) {
			drawable = null;
			text = "None";
		} else {
			ActorClass actor;
			if (id == 1) {
				actor = game.hero;
			} else {
				int classIndex = game.getSelectedMap().actors.get(id).classIndex; 
				actor = game.actors[classIndex];
			}
			Bitmap bmp = Data.loadActor(actor.imageName);
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
			
			drawable = new BitmapDrawable(bmp);
			text = String.format("%03d: ", id) + actor.name;
		}
		
		setText(text);
		setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
	}
	
	public SelectorActorInstance(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelectorActorInstance(Context context) {
		super(context);
	}

	public void populate(final PlatformGame game) {
		this.game = game; 
		setSelectedInstance(id);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), SelectorMapActorInstance.class);
				intent.putExtra("game", game);
				intent.putExtra("id", id);
				//Game.debug(getId());
				((Activity)getContext()).startActivityForResult(intent, getId());
			}
		});
	}

	public void onActivityResult(int requestCode, Intent data) {
		//Game.debug(getId());
		if (requestCode == getId()) {
			setSelectedInstance(data.getExtras().getInt("id"));
		}
	}
}
