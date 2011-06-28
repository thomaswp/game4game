package com.twp.platform;

import java.io.InputStream;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;

public class Platformer extends Game {

	@Override
	protected Logic getNewLogic() {
		String map = null;
		if (getIntent() != null && getIntent().getExtras() != null)
			map = getIntent().getExtras().getString("map");
		else
			map = "final-Map_1";
		
		return new PlatformLogicGDX(map);
	}

}