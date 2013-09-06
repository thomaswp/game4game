package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.PlatformGame;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MapEditorActorSelectorView extends ScrollingImageSelectorView {

	private PlatformGame game;

	public MapEditorActorSelectorView(Context context, int id, PlatformGame game) {
		super(context, id);
		this.game = game;
	}

	@Override
	protected Bitmap[] getBitmaps() {
		Bitmap[] bitmaps = new Bitmap[game.actors.length + 1];
		bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.no);
		for (int i = 0; i < game.actors.length; i++) {
			ActorClass actor = game.actors[i];
			bitmaps[i+1] = Data.loadActorIcon(actor.imageName);
			bitmaps[i+1] = Bitmap.createScaledBitmap(bitmaps[i+1], 
					(int)(bitmaps[i+1].getWidth() * 2 * actor.zoom), 
					(int)(bitmaps[i+1].getHeight() * 2 * actor.zoom), false);
		}
		return bitmaps;
	}

	@Override
	protected String getDescription(int id) {
		if (id == 0) {
			return "Remove Actor";
		} else {
			return game.actors[id - 1].name;
		}
	}
}