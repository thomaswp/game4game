package edu.elon.honors.price.maker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import com.twp.platform.Platformer;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

		setContentView(R.layout.main);

		createDirs();
		
		loadMaps();
		loadButtons();

		super.onCreate(savedInstanceState);
	}

	private void createDirs() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				ArrayList<String> dirs = new ArrayList<String>();
				dirs.add(Data.ACTORS_DIR);
				dirs.add(Data.TILESETS_DIR);
				dirs.add("export/");
				
				for (int i = 0; i < dirs.size(); i++) {
					File file = new File(Environment.getExternalStorageDirectory(), Data.SD_FOLDER + dirs.get(i));
					if (!file.exists()) {
						file.mkdirs();
					}
				}
			} catch (Exception ex) { 
				Game.debug("Could not create resource directory on SD Card");
				ex.printStackTrace(); 
			}
		} else {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
				Game.debug("SD Card is Read Only");
			} else {
				Game.debug("No SD Card detected");
			}
		}
	}

	private void loadMaps() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);
		layout.removeAllViews();
		RadioGroup group = new RadioGroup(this);
		layout.addView(group);

		selectedMap = null;

		String[] files = fileList();
		for (String file : files) {
			if (file.indexOf(PREFIX) == 0) {
				final String name = file.substring(PREFIX.length());
				final String fileName = file;
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
    				FileOutputStream fos = openFileOutput(file, MODE_WORLD_WRITEABLE);
    				ObjectOutputStream oos = new ObjectOutputStream(fos);
    				oos.writeObject(game);
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
    				
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private void newGame() {
		String[] files = fileList();
		int n = 0;
		String name;
		boolean exists = false;
		do {
			name = PREFIX + "Map_" + ++n;
			exists = false;
			for (int i = 0; i < files.length; i++) 
				exists |= files[i].equals(name); 
		} while (exists);

		Data.saveGame(name, this, new PlatformGame());

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
					String name = selectedMap.substring(PREFIX.length());
					deleteFile(name + MapEditorLogic.DATA);
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
			intent.putExtra("map", selectedMap);
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
