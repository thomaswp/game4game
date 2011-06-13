package edu.elon.honors.price.maker;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.maker.PlatformMaker.RectHolder;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GameMaker extends Game {

	private Rect selectionRect = new Rect();
	
	@Override
	protected Logic getNewLogic() {
		final GameMaker gm = this;
		PlatformMaker pm = new PlatformMaker(new RectHolder() {
			
			@Override
			public void newRect(int bitmapId) {
				Intent intent = new Intent(gm, TextureSelector.class);
				intent.putExtra("id", bitmapId);
				startActivityForResult(intent, 0);
			}
			
			@Override
			public Rect getRect() {
				return selectionRect;
			}
		});
		return pm;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Save");
		menu.add("Load");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().equals("Save")) {
			try {
				((PlatformMaker)view.getLogic()).saveFinal(this);
				Toast.makeText(this, "Save Successful!", Toast.LENGTH_SHORT).show(); 
			} catch (Exception ex) {
				ex.printStackTrace();
				Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show(); 
			}
		} else if (item.getTitle().equals("Load")) {
			
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		selectionRect.set(0, 0, 1, 1);
	}

	
	
}