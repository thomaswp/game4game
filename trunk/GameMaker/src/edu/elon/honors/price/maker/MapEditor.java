package edu.elon.honors.price.maker;

import com.twp.platform.Platformer;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapEditor extends MapActivityBase {

	protected String gameName;
	protected ReturnResponse returnResponse;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Data.setDefaultParent(this);
		gameName = getIntent().getExtras().getString("gameName");
		super.onCreate(savedInstanceState);
	}

	@Override
	protected MapView getMapView(PlatformGame game) {
		return new MapEditorView(this, game);
	}

	protected boolean hasChanged() {
		PlatformGame oldGame = (PlatformGame)getIntent().getExtras().getSerializable("game");
		return !PlatformGame.areEqual(oldGame, game);
	}

	protected void finishOk() {
		finish();
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
			intent.putExtra("game", game);
			startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
		} else if (item.getTitle().equals("Save")) {
			save();
		} else if (item.getTitle().equals("Load")) {
			if (hasChanged()) {
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
			if (hasChanged()) {
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
		Intent intent = new Intent(this, Platformer.class);
		intent.putExtra("map", gameName);
		startActivity(intent);
		Game.debug(System.currentTimeMillis());
	}

	private void save() {
		try {
			((MapEditorView)view).saveMapData();
			Data.saveGame(gameName, this, game);
			PlatformGame gameCopy = (PlatformGame) Data.loadGame(gameName, this);
			getIntent().putExtra("game", gameCopy);
			Toast.makeText(this, "Save Successful!", Toast.LENGTH_SHORT).show(); 
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show(); 
		}
	}
	
	private void refresh() {
		((MapEditorView)view).refresh();
	}

	private void load() {
		try {
			game = (PlatformGame) Data.loadGame(gameName, this);
			((MapEditorView)view).setGame(game, true);
			PlatformGame gameCopy = (PlatformGame) Data.loadGame(gameName, this);
			getIntent().putExtra("game", gameCopy);
			refresh();
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
			if (requestCode == DatabaseActivity.REQUEST_RETURN_GAME) {
				game = (PlatformGame)data.getExtras().getSerializable("game");
				((MapEditorView)view).setGame(game, false);
			} else {
				if (returnResponse != null) {
					returnResponse.onReturn(data);
					returnResponse = null;
				}
			}
			refresh();
		}
	}

	@Override
	public void onBackPressed() {
		if (hasChanged()) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Save?")
			.setMessage("Do you want to save before quitting?")
			.setPositiveButton("Save and Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					save();
					MapEditor.this.finish(); 
				}

			})
			.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MapEditor.this.finish(); 
				}

			})
			.setNegativeButton("Cancel", null)
			.show();	
		} else {
			finish();
		}
	}
	
	public static abstract class ReturnResponse {
		public abstract void onReturn(Intent data);
	}
}
