package edu.elon.honors.price.maker;

import com.twp.platform.Platformer;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.maker.MapEditorLogic.ActorHolder;
import edu.elon.honors.price.maker.MapEditorLogic.RectHolder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapEditor_Old extends Game {

	private static final int REQUEST_CODE_TEXTURE = 0;
	private static final int REQUEST_CODE_ACTOR = 1;

	private Rect selectionRect = new Rect();
	private int actorId = -2;
	private boolean isSelecting;
	private String gameName;
	private MapEditorLogic logic;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.gameName = getIntent().getExtras().getString("map");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Logic getNewLogic() {

		RectHolder rectHolder = new RectHolder() {

			@Override
			public void newRect(String bitmapName, int tileWidth, int tileHeight) {
				if (isSelecting) {
					return;
				}
				isSelecting = true;

				Intent intent = new Intent(MapEditor_Old.this, MapTextureSelector.class);
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

				Intent intent = new Intent(MapEditor_Old.this, MapActorSelector.class);

				intent.putExtra("id", actorId);
				intent.putExtra("gameName", gameName);

				startActivityForResult(intent, REQUEST_CODE_ACTOR);
			}

			@Override
			public int getActorId() {
				return actorId;
			}
		};


		MapEditorLogic pm = new MapEditorLogic(gameName.substring(MainMenu.PREFIX.length()), rectHolder, actorHolder);
		return pm;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (logic != null) {
			view.setLogic(logic);
		} else {
			logic = (MapEditorLogic)getLogic();
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
			Intent intent = new Intent(this, Database.class);
			intent.putExtra("game", logic.getGame());
			startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
		} else if (item.getTitle().equals("Save")) {
			save();
		} else if (item.getTitle().equals("Load")) {
			if (logic.isChanged()) {
				new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Discard Changes?")
				.setMessage("This game has been changed. Discard changes and load from last save?")
				.setPositiveButton("Load", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						load();
					}

				})
				.setNegativeButton("Cancel", null)
				.show();
			} else {
				load();
			}
		} else if (item.getTitle().equals("Test")) {
			if (logic.isChanged()) {
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
			} else {
				test();
			}
			
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void test() {
		Intent intent = new Intent(MapEditor_Old.this, Platformer.class);
		intent.putExtra("map", gameName);
		startActivity(intent);
		Debug.write(System.currentTimeMillis());
	}

	private void save() {
		try {
			((MapEditorLogic)view.getLogic()).saveFinal();
			Toast.makeText(this, "Save Successful!", Toast.LENGTH_SHORT).show(); 
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show(); 
		}
	}

	private void load() {
		try {
			((MapEditorLogic)view.getLogic()).loadFinal();
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
			} else if (requestCode == DatabaseActivity.REQUEST_RETURN_GAME) {
				logic.setGame((PlatformGame)data.getSerializableExtra("game"));
			}
		}
		isSelecting = false;
	}

	@Override
	public void onBackPressed() {
		if (logic.isChanged()) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Save?")
			.setMessage("Do you want to save before quitting?")
			.setPositiveButton("Save and Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					save();
					MapEditor_Old.this.finish(); 
				}

			})
			.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MapEditor_Old.this.finish(); 
				}

			})
			.setNegativeButton("Cancel", null)
			.show();	
		} else {
			finish();
		}
	}
}