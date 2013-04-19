package edu.elon.honors.price.maker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.twp.platform.Platformer;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.GameCache;
import edu.elon.honors.price.data.GameCache.GameDetails;
import edu.elon.honors.price.data.GameCache.GameType;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.share.WebLogin;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainMenu extends Activity {

	private GameDetails selectedMap;
	private GameCache gameCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

//		Intent intent = new Intent(this, MyGamesActivity.class);
//		startActivity(intent);
		
		setContentView(R.layout.main_menu);
		
		createDirs();

		loadButtons();
		Debug.write("Files: %s", Arrays.toString(fileList()));

//		gameCache = GameCache.getGameCache(this);
//		for (GameDetails gameDetails : gameCache.getGames(GameType.Edit)) {
//			PlatformGame game = gameDetails.loadGame(this);
//			for (Map map : game.maps) {
//				map.events = null;
//			}
//			game.actorBehaviors = null;
//			game.objectBehaviors = null;
//			
//			
//			long time = System.currentTimeMillis();
//			Data.saveDataGlobal("test", this, game);
//			time = System.currentTimeMillis() - time;
//			Debug.write("%s: %d", gameDetails.getName(), time);
//		}
//		deleteFile("test");

		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		gameCache = GameCache.getGameCache(this);
		Debug.write(gameCache.getGames(GameType.Edit));
		loadMaps();
	}

	private void createDirs() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				ArrayList<String> dirs = new ArrayList<String>();
				dirs.add(Data.ACTORS_DIR);
				dirs.add(Data.TILESETS_DIR);
				dirs.add("export/");

				for (int i = 0; i < dirs.size(); i++) {
					File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), Data.SD_FOLDER + dirs.get(i));
					if (!file.exists()) {
						file.mkdirs();
					}
				}
			} catch (Exception ex) { 
				Debug.write("Could not create resource directory on SD Card");
				ex.printStackTrace(); 
			}
		} else {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
				Debug.write("SD Card is Read Only");
			} else {
				Debug.write("No SD Card detected");
			}
		}
	}
	
	private void loadMaps() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);
		layout.removeAllViews();
		RadioGroup group = new RadioGroup(this);
		layout.addView(group);

		selectedMap = null;
		
		for (final GameDetails details : gameCache.getGames(GameType.Edit)) {
			final String name = details.getName();
			RadioButton b = new RadioButton(this);
			b.setText(name);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selectedMap == details)
						edit();
					else
						selectedMap = details;
				}
			});
			b.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					gameCache.makeCopy(details, "Copy of " + name, 
							GameType.Edit, MainMenu.this);
					loadMaps();
					return true;
				}
			});
			group.addView(b);
			if (selectedMap == null) {
				selectedMap = details;
				b.setChecked(true);
			}
		}
	}

	private void loadButtons() {
		Button newGame = (Button)findViewById(R.id.buttonNewGame);
		Button edit = (Button)findViewById(R.id.buttonEdit);
		Button play = (Button)findViewById(R.id.buttonPlay);
		Button delete = (Button)findViewById(R.id.buttonDelete);
		Button copy = (Button)findViewById(R.id.buttonCopy);
		Button test = (Button)findViewById(R.id.buttonTest);
		Button myGames = (Button)findViewById(R.id.buttonGames);
		
		myGames.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, WebLogin.class));
			}
		});

		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edit();
			}
		});

		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play();
			}
		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete();
			}
		});

		newGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newGame();
			}
		});

//		copy.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				new AlertDialog.Builder(MainMenu.this)
//				.setIcon(android.R.drawable.ic_dialog_alert)
//				.setTitle("Import/Export")
//				.setPositiveButton("Import", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						importGames();
//					}
//
//				})
//				.setNeutralButton("Export", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						exportGames();
//					}
//
//				})
//				.show();	
//			}
//		});
		
		test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedMap != null) {
					Intent intent = new Intent(MainMenu.this, TestActivity.class);
					PlatformGame game = Data.loadData(selectedMap.getFilename(), MainMenu.this);
					intent.putExtra("gameName", selectedMap);
					intent.putExtra("game", game);
					startActivity(intent);
				}
			}
		});
	}

//	private void importGames() {
//		File dir = new File(Environment.getExternalStorageDirectory(), 
//				Data.SD_FOLDER + "export/");
//		for (String file : dir.list()) {
//			if (file.indexOf(PREFIX) == 0) {
//				try {
//					FileInputStream fis = new FileInputStream(new File(dir, file));
//					ObjectInputStream ois = new ObjectInputStream(fis);
//					PlatformGame game = (PlatformGame)ois.readObject();
//					ois.close();
//					
//					FileOutputStream fos = openFileOutput(file, MODE_PRIVATE);
//					ObjectOutputStream oos = new ObjectOutputStream(fos);
//					oos.writeObject(game);
//					oos.close();
//					loadMaps();
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//	}
//
//	private void exportGames() {
//		String[] files = fileList();
//		for (String file : files) {
//			if (file.indexOf(PREFIX) == 0) {
//				try {
//					FileInputStream fis = openFileInput(file);
//					ObjectInputStream ois = new ObjectInputStream(fis);
//					PlatformGame game = (PlatformGame)ois.readObject();
//					File file2 = new File(Environment.getExternalStorageDirectory(), 
//							Data.SD_FOLDER + "export/" + file);
//					FileOutputStream fos = new FileOutputStream(file2);
//					ObjectOutputStream oos = new ObjectOutputStream(fos);
//					oos.writeObject(game);
//					oos.close();
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//	}
	
	private void newGame() {
		PlatformGame game = new PlatformGame();
		int n = 1;
		String name;
		do {
			name = "Map" + n++;
			boolean novel = true;
			for (GameDetails details : gameCache.getGames(GameType.Edit)) {
				if (details.getFilename().equals(name)) {
					novel = false;
				}
			}
			if (novel) break;
		} while (true);
		gameCache.addGame(name, GameType.Edit, game, this);
		loadMaps();
	}

	private void delete() {
		if (selectedMap != null) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Delete?")
			.setMessage("Confirm Delete")
			.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					gameCache.deleteGame(selectedMap, GameType.Edit, MainMenu.this);
					loadMaps();
				}

			})
			.setNegativeButton("Cancel", null)
			.show();
		}
	}

	private void edit() {
		if (selectedMap != null) {
			Intent intent = new Intent(this, MapEditor.class);
			PlatformGame game = Data.loadData(selectedMap.getFilename(), this);
			intent.putExtra("gameDetails", selectedMap);
			intent.putExtra("game", game);
			startActivity(intent);
		}
	}

	private void play() {
		if (selectedMap != null) {
			Intent intent = new Intent(this, Platformer.class);
			intent.putExtra("map", selectedMap);
			startActivity(intent);
		}
	}
}
