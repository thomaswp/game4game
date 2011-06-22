package edu.elon.honors.price.maker;

import com.twp.platform.Platformer;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.maker.PlatformMakerLogic.ActorHolder;
import edu.elon.honors.price.maker.PlatformMakerLogic.RectHolder;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PlatformMaker extends Game {

	private static final int REQUEST_CODE_TEXTURE = 0;
	private static final int REQUEST_CODE_ACTOR = 1;

	private Rect selectionRect = new Rect();
	private int actorId = -1;
	private boolean isSelecting;
	private String gameFinal;
	
	public String getGameName() {
		return gameFinal.substring(GameMaker.PREFIX.length());
	}

	public void onCreate(Bundle savedInstanceState) {
		this.gameFinal = getIntent().getExtras().getString("map");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Logic getNewLogic() {

		final PlatformMaker gm = this;
		RectHolder rectHolder = new RectHolder() {

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

				startActivityForResult(intent, REQUEST_CODE_TEXTURE);
			}

			@Override
			public Rect getRect() {
				return selectionRect;
			}
		};

		ActorHolder actorHolder = new ActorHolder() {

			@Override
			public void newActor(PlatformGame game) {
				if (isSelecting) {
					return;
				}
				isSelecting = true;

				Intent intent = new Intent(gm, PlatformActorSelector.class);
				int[] ids = new int[game.actors.length];
				for (int i = 1; i < ids.length; i++) {
					ids[i] = game.actors[i].imageId;
				}
				String[] names = new String[ids.length];
				for (int i = 1; i < ids.length; i++) {
					names[i] = game.actors[i].name;
					if (names[i] == null) names[i] = "";
				}
				intent.putExtra("ids", ids);
				intent.putExtra("id", actorId);
				intent.putExtra("names", names);

				startActivityForResult(intent, REQUEST_CODE_ACTOR);
			}

			@Override
			public int getActorId() {
				return actorId;
			}
		};


		PlatformMakerLogic pm = new PlatformMakerLogic(getGameName(), rectHolder, actorHolder);
		return pm;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Database");
		menu.add("Save");
		menu.add("Load");
		menu.add("Test");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().equals("Database")) {
			Intent intent = new Intent(this, PlatformDatabase.class);
			intent.putExtra("game", getGameName());
			startActivity(intent);
		} else if (item.getTitle().equals("Save")) {
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
			intent.putExtra("map", gameFinal);
			startActivity(intent);
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_TEXTURE) {
				int left = data.getExtras().getInt("left");
				int top = data.getExtras().getInt("top");
				int right = data.getExtras().getInt("right");
				int bottom = data.getExtras().getInt("bottom");
				selectionRect.set(left, top, right, bottom);
			} else if (requestCode == REQUEST_CODE_ACTOR) {
				actorId = data.getExtras().getInt("id");
			}
		}
		isSelecting = false;
	}



}