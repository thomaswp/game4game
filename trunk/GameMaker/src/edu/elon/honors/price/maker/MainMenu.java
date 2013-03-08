package edu.elon.honors.price.maker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.twp.platform.Platformer;

import edu.elon.honors.price.data.Data;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainMenu extends Activity {

	public static final String PREFIX = "final-";

	private String selectedMap;

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

		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
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

	public static List<String> getGameFilenames(Context context) {
		LinkedList<String> games = new LinkedList<String>();
		
		String[] files = context.fileList();
		for (String file : files) {
			if (file.indexOf(PREFIX) == 0) {
				games.addFirst(file);
			}
		}
		return games;
	}
	
	private void loadMaps() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);
		layout.removeAllViews();
		RadioGroup group = new RadioGroup(this);
		layout.addView(group);

		selectedMap = null;

		List<String> games = getGameFilenames(this);
		
		for (String file : games) {
			PlatformGame game = (PlatformGame)Data.loadGame(file, this);
			if (game != null) {
				final String fileName = file;
				String name = game.getName(file.substring(PREFIX.length()));
				RadioButton b = new RadioButton(this);
				b.setText(name);
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (selectedMap == fileName)
							edit();
						else
							selectedMap = fileName;
					}
				});
				group.addView(b);
				if (selectedMap == null) {
					selectedMap = fileName;
					b.setChecked(true);
				}
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

		copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MainMenu.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Import/Export")
				.setPositiveButton("Import", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						importGames();
					}

				})
				.setNeutralButton("Export", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exportGames();
					}

				})
				.show();	
			}
		});
		
		test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectedMap != null) {
					Intent intent = new Intent(MainMenu.this, TestActivity.class);
					PlatformGame game = (PlatformGame) Data.loadGame(selectedMap, MainMenu.this);
					intent.putExtra("gameName", selectedMap);
					intent.putExtra("game", game);
					startActivity(intent);
				}
			}
		});
	}

	private void importGames() {
		File dir = new File(Environment.getExternalStorageDirectory(), 
				Data.SD_FOLDER + "export/");
		for (String file : dir.list()) {
			if (file.indexOf(PREFIX) == 0) {
				try {
					FileInputStream fis = new FileInputStream(new File(dir, file));
					ObjectInputStream ois = new ObjectInputStream(fis);
					PlatformGame game = (PlatformGame)ois.readObject();
					ois.close();
					
					FileOutputStream fos = openFileOutput(file, MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(game);
					oos.close();
					loadMaps();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void exportGames() {
		String[] files = fileList();
		for (String file : files) {
			if (file.indexOf(PREFIX) == 0) {
				try {
					FileInputStream fis = openFileInput(file);
					ObjectInputStream ois = new ObjectInputStream(fis);
					PlatformGame game = (PlatformGame)ois.readObject();
					File file2 = new File(Environment.getExternalStorageDirectory(), 
							Data.SD_FOLDER + "export/" + file);
					FileOutputStream fos = new FileOutputStream(file2);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(game);
					oos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static String getNewMapName(Context context, String base) {
		String[] files = context.fileList();
		int n = 0;
		String name;
		boolean exists = false;
		do {
			name = PREFIX + base + ++n;
			exists = false;
			for (int i = 0; i < files.length; i++) 
				exists |= files[i].equals(name); 
		} while (exists);
		return name;
	}
	
	public static String getNewMapName(Context context) {
		return getNewMapName(context, "Map");
	}
	
	private void newGame() {
		String name = getNewMapName(this);

		String id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		id += "_" + System.currentTimeMillis();
		PlatformGame game = new PlatformGame(id, name.substring(PREFIX.length()));
		Data.saveGame(name, this, game);

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
					deleteFile(selectedMap);
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
			PlatformGame game = (PlatformGame) Data.loadGame(selectedMap, this);
			intent.putExtra("gameName", selectedMap);
			intent.putExtra("game", game);
			//			Intent intent = new Intent(this, MapEditor.class);
			//			intent.putExtra("map", selectedMap);
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
