package edu.elon.honors.price.maker;

import com.twp.platform.Platformer;

import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.maker.PlatformMakerLogic.RectHolder;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PlatformMaker extends Game {

	private static final int REQUEST_CODE = 3;

	private Rect selectionRect = new Rect();
	private boolean isSelecting;
	private String mapFinal;

	public void onCreate(Bundle savedInstanceState) {
		this.mapFinal = getIntent().getExtras().getString("map");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Logic getNewLogic() {

		final PlatformMaker gm = this;
		PlatformMakerLogic pm = new PlatformMakerLogic(mapFinal.substring(GameMaker.PREFIX.length()),
				new RectHolder() {

			@Override
			public void newRect(int bitmapId, int tileWidth, int tileHeight) {
				if (isSelecting) {
					return;
				}
				isSelecting = true;

				Intent intent = new Intent(gm, PlatformTextureSelector.class);
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
		menu.add("Test");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().equals("Save")) {
			try {
				((PlatformMakerLogic)view.getLogic()).saveFinal(this);
				Toast.makeText(this, "Save Successful!", Toast.LENGTH_SHORT).show(); 
			} catch (Exception ex) {
				ex.printStackTrace();
				Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show(); 
			}
		} else if (item.getTitle().equals("Load")) {
			try {
				((PlatformMakerLogic)view.getLogic()).loadFinal(this);
				Toast.makeText(this, "Load Successful!", Toast.LENGTH_SHORT).show(); 
			} catch (Exception ex) {
				ex.printStackTrace();
				Toast.makeText(this, "Load Failed!", Toast.LENGTH_SHORT).show(); 
			}
		} else if (item.getTitle().equals("Test")) {
			Intent intent = new Intent(this, Platformer.class);
			intent.putExtra("map", mapFinal);
			startActivity(intent);
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