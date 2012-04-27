package edu.elon.honors.price.maker;

import android.content.Context;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.maker.SelectorMapBase.SelectorMapView;

public class SelectorMapPreview extends SelectorMapView {

	public SelectorMapPreview(Context context, PlatformGame game) {
		super(context, game);
	}

	@Override
	protected boolean showRightButton() {
		return false;
	}

	@Override
	protected int getBackgroundTransparency() {
		return 255;
	}
	
	public void refreshTileset() {
		tiles = createTiles(game.tilesets[game.getSelectedMap().tilesetId], 
				getContext());
	}
}
