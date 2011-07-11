package edu.elon.honors.price.maker;

import com.twp.platform.Platformer;

import edu.elon.honors.price.audio.Audio;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.maker.PlatformMakerLogic.ActorHolder;
import edu.elon.honors.price.maker.PlatformMakerLogic.RectHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	private int actorId = -2;
	private boolean isSelecting;
	private String gameName;
	private PlatformMakerLogic logic;

	public void onCreate(Bundle savedInstanceState) {
		this.gameName = getIntent().getExtras().getString("map");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Logic getNewLogic() {

		final PlatformMaker gm = this;
		RectHolder rectHolder = new RectHolder() {

			@Override
			public void newRect(String bitmapName, int tileWidth, int tileHeight) {
				if (isSelecting) {
					return;
				}
				isSelecting = true;

				Intent intent = new Intent(gm, PlatformTextureSelector.class);
				intent.putExtra("id", bitmapName);
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

				intent.putExtra("id", actorId);
				intent.putExtra("gameName", gameName);

				startActivityForResult(intent, REQUEST_CODE_ACTOR);
			}

			@Override
			public int getActorId() {
				return actorId;
			}
		};


		PlatformMakerLogic pm = new PlatformMakerLogic(gameName.substring(GameMaker.PREFIX.length()), rectHolder, actorHolder);
		return pm;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (logic != null) {
			view.setLogic(logic);
		} else {
			logic = (PlatformMakerLogic)getLogic();
		}
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
			intent.putExtra("game", logic.getGame());
			startActivityForResult(intent, PlatformActivity.REQUEST_RETURN_GAME);
		} else if (item.getTitle().equals("Save")) {
			save();
		} else if (item.getTitle().equals("Load")) {
			load();
		} else if (item.getTitle().equals("Test")) {
			new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Save First?")
	        .setMessage("Do you want to save before testing?")
	        .setPositiveButton("Save and Test", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	save();
					test();
	            }

	        })
	        .setNeutralButton("Test", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	test();
	            }

	        })
	        .setNegativeButton("Cancel", null)
	        .show();
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void test() {
		Intent intent = new Intent(PlatformMaker.this, Platformer.class);
		intent.putExtra("map", gameName);
		startActivity(intent);
		Game.debug(System.currentTimeMillis());
	}
	
	private void save() {
		try {
			((PlatformMakerLogic)view.getLogic()).saveFinal();
			Toast.makeText(this, "Save Successful!", Toast.LENGTH_SHORT).show(); 
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show(); 
		}
	}
	
	private void load() {
		try {
			((PlatformMakerLogic)view.getLogic()).loadFinal();
			Toast.makeText(this, "Load Successful!", Toast.LENGTH_SHORT).show(); 
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Load Failed!", Toast.LENGTH_SHORT).show(); 
		}
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
			} else if (requestCode == PlatformActivity.REQUEST_RETURN_GAME) {
				logic.setGame((PlatformGame)data.getSerializableExtra("game"));
			}
		}
		isSelecting = false;
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Save?")
        .setMessage("Do you want to save before quitting?")
        .setPositiveButton("Save and Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	save();
                PlatformMaker.this.finish(); 
            }

        })
        .setNeutralButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PlatformMaker.this.finish(); 
            }

        })
        .setNegativeButton("Cancel", null)
        .show();	
	}
}