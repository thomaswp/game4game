package edu.elon.honors.price.maker;

import com.twp.platform.Platformer;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.GameCache.GameDetails;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorAction;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.data.GameCache;
import edu.elon.honors.price.data.GameData;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.graphics.Graphics;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapEditor extends MapActivityBase {

	protected GameDetails gameDetails;
	protected ReturnResponse returnResponse;
	private boolean testing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Data.setDefaultParent(this);
		gameDetails = (GameDetails)getIntent().getExtras().getSerializable(
				"gameDetails");
		//gameDetails = gameDetails.getSynced(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		Graphics.reset();
		if (testing) {
			TutorialUtils.fireCondition(EditorAction.MapEditorFinishTest, this);
			testing = false;
		}
	}
	
	@Override
	protected MapView getMapView(PlatformGame game, Bundle savedInstanceState) {
		return new MapEditorView(this, game, savedInstanceState);
	}

	@Override
	protected boolean hasChanged() {
		PlatformGame oldGame = (PlatformGame)getIntent().getExtras().getSerializable("game");
		return !GameData.areEqual(oldGame, game);
	}
	
	@Override
	protected void finishOk(Intent data) {
		save();
		finish();
	}
	
	@Override
	protected void finishOkAll() { }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Database");
		menu.add("Save");
		menu.add("Load");
		menu.add("Test");
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		TutorialUtils.fireCondition(EditorButton.MapEditorMenu, this);
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().equals("Database")) {
			openDatabase();
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
	
	protected void openDatabase() {
		Intent intent = new Intent(this, Database.class);
		intent.putExtra("game", game);
		startActivityForResult(intent, DatabaseActivity.REQUEST_RETURN_GAME);
	}
	
	protected void test() {
		testing = true;
		Intent intent = new Intent(this, Platformer.class);
		intent.putExtra("map", gameDetails.getFilename());
		startActivity(intent);
	}

	protected void save() {
		try {
			((MapEditorView)view).saveMapData();
			gameDetails.saveGame(game, this);
			PlatformGame gameCopy = gameDetails.loadGame(this);
			getIntent().putExtra("game", gameCopy);
			Toast.makeText(this, "Save Successful!", Toast.LENGTH_SHORT).show(); 
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show(); 
		}
	}
	
	private void refresh() {
		((MapEditorView)view).refreshLayers();
	}

	protected void load() {
		try {
			game = gameDetails.loadGame(this);
			((MapEditorView)view).setGame(game, true);
			PlatformGame gameCopy = gameDetails.loadGame(this);
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
	
	public static abstract class ReturnResponse {
		public abstract void onReturn(Intent data);
	}

	@Override
	protected String getPreferenceId() {
		return game.ID;
	}
}
