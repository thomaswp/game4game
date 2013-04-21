package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MapEditorObjectSelectorView extends ScrollingImageSelectorView {

	private PlatformGame game;

	public MapEditorObjectSelectorView(Context context, int id, PlatformGame game) {
		super(context, id);
		this.game = game;
	}

	@Override
	protected Bitmap[] getBitmaps() {
		Bitmap[] objects = new Bitmap[game.objects.length];
		for (int i = 0; i < objects.length; i++) {
			objects[i] = Data.loadObject(game.objects[i].imageName);
			objects[i] = Bitmap.createScaledBitmap(objects[i], 
					(int)(objects[i].getWidth() * game.objects[i].zoom), 
					(int)(objects[i].getHeight() * game.objects[i].zoom), true);
		}
		return objects;
	}

	@Override
	protected String getDescription(int id) {
		return game.objects[id].name;
	}
}
