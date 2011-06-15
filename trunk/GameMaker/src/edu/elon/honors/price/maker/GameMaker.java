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

	private static final int REQUEST_CODE = 3;

	private Rect selectionRect = new Rect();
	private boolean isSelecting;

	@Override
	protected Logic getNewLogic() {
		final GameMaker gm = this;
		PlatformMaker pm = new PlatformMaker(new RectHolder() {

			@Override
			public void newRect(int bitmapId, int tileWidth, int tileHeight) {
				if (isSelecting) {
					Game.debug("Stoppped");
					return;
				}
				isSelecting = true;
				
				Intent intent = new Intent(gm, TextureSelector.class);
				intent.putExtra("id", bitmapId);
				intent.putExtra("tileWidth", tileWidth);
				intent.putExtra("tileHeight", tileHeight);
				Rect rect = this.getRect();
				intent.putExtra("left", rect.left);
				intent.putExtra("top", rect.top);
				intent.putExtra("right", rect.right);
				intent.putExtra("bottom", rect.bottom);
				
				startActivityForResult(intent, REQUEST_CODE);
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
			try {
				((PlatformMaker)view.getLogic()).loadFinal(this);
				Toast.makeText(this, "Load Successful!", Toast.LENGTH_SHORT).show(); 
			} catch (Exception ex) {
				ex.printStackTrace();
				Toast.makeText(this, "Load Failed!", Toast.LENGTH_SHORT).show(); 
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			int left = data.getExtras().getInt("left");
			int top = data.getExtras().getInt("top");
			int right = data.getExtras().getInt("right");
			int bottom = data.getExtras().getInt("bottom");
			selectionRect.set(left, top, right, bottom);
			isSelecting = false;
		}
	}



}