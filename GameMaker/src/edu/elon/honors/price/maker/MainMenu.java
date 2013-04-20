package edu.elon.honors.price.maker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.eujeux.data.GameInfo;
import com.twp.platform.Platformer;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.GameCache;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.GameCache.GameDetails;
import edu.elon.honors.price.data.GameCache.GameType;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.share.WebLogin;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.LiveFolders;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost.TabContentFactory;

@AutoAssign
public class MainMenu extends Activity implements IViewContainer {
	
	private final static String APP_NAME = "GameMaker";
	
	private final static String[] HELP_TEXT = new String[] {
		"Select a game to Edit or Test!",
		"Play a game from the community, or Branch it to make it your own.",
		"Select a tutorial to learn more about " + APP_NAME + "."
	};
	
	private final static SimpleDateFormat dateFormat = 
			new SimpleDateFormat("MM/dd", Locale.US);
	
	private ListView listViewGames;
	private TextView textViewHelp;
	private TextView textViewHint;
	private Button buttonHint, buttonAdd;
	private LinearLayout linearLayoutHint, linearLayoutButtons;
	
	private LinkedList<LinkedList<Button>> contextButtons = 
			new LinkedList<LinkedList<Button>>();
	
	@AutoAssignIgnore
	private TabHost tabHost;
	
	private GameCache gameCache;
	
	private int[] selectedIndices = new int[3];
	private GameDetails selectedGame;
	
	
	private int getTab() {
		return tabHost.getCurrentTab();
	}
	
	private GameType getType() {
		return GameType.values()[getTab()];
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main_menu);
		AutoAssignUtils.autoAssign(this);
		
		//createDirs();
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		TabSpec editTab = tabHost.newTabSpec("edit");
		editTab.setContent(R.id.tabEdit);
		editTab.setIndicator(createTabView(tabHost.getContext(), "Edit"));
		
		TabSpec playTab = tabHost.newTabSpec("play");
		playTab.setContent(R.id.tabEdit);
		playTab.setIndicator(createTabView(tabHost.getContext(), "Play"));
		
		TabSpec tutorialsTab = tabHost.newTabSpec("tutorials");
		tutorialsTab.setContent(R.id.tabEdit);
		tutorialsTab.setIndicator(createTabView(tabHost.getContext(), "Tutorials"));
		
		createButtons();
		
		tabHost.addTab(editTab);
		tabHost.addTab(playTab);
		tabHost.addTab(tutorialsTab);

		listViewGames.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int index,
					long id) {
				selectedIndices[getTab()] = index;
				selectedGame = gameCache.getGames(getType()).get(index);
				linearLayoutButtons.setVisibility(View.VISIBLE);
			}
		});
		
		gameCache = GameCache.getGameCache(this);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				textViewHelp.setText(HELP_TEXT[tabHost.getCurrentTab()]);
				loadGames();
			}
		});
		tabHost.setCurrentTab(0);
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		gameCache = GameCache.getGameCache(this);
		loadGames();
	}
	
	@SuppressWarnings("unused")
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
	
	private void createButtons() {
		OnClickListener goEdit = new OnClickListener() {
			@Override
			public void onClick(View v) {
				edit();
			}
		};
		OnClickListener goPlay = new OnClickListener() {
			@Override
			public void onClick(View v) {
				play();
			}
		};
		OnClickListener goCopy = new OnClickListener() {
			@Override
			public void onClick(View v) {
				copy();
			}
		};
		OnClickListener goDelete = new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete();
			}
		};
		
		LinkedList<Button> editButtons = new LinkedList<Button>();
		editButtons.add(makeButton("Edit", goEdit));
		editButtons.add(makeButton("Test", goPlay));
		editButtons.add(makeButton("Copy", goCopy));
		editButtons.add(makeButton("Delete", goDelete));
		contextButtons.add(editButtons);
		
		LinkedList<Button> playButtons = new LinkedList<Button>();
		playButtons.add(makeButton("Play", goPlay));
		playButtons.add(makeButton("Branch", goCopy));
		playButtons.add(makeButton("Delete", goDelete));
		contextButtons.add(playButtons);
		
		LinkedList<Button> tutorialButtons = new LinkedList<Button>();
		tutorialButtons.add(makeButton("Go!", goEdit));
		tutorialButtons.add(makeButton("Reset", goCopy));
		tutorialButtons.add(makeButton("Branch", goCopy));
		contextButtons.add(tutorialButtons);	
	}
	
	private Button makeButton(String text, OnClickListener onClickListener) {
		Button button = new Button(this);
		button.setText(text);
		button.setOnClickListener(onClickListener);
		return button;
	}
	
	private interface NameDialogListener { void doAction(String name); }
	
	private final static int DIALOG_NEW_GAME = 1;
	private final static int DIALOG_COPY_GAME = 2;
	private final static int DIALOG_BRANCH_GAME = 3;
	@Override
	protected Dialog onCreateDialog(int id, final Bundle args) {
		String title = null, message = null;
		final NameDialogListener listener;
		
		if (id == DIALOG_NEW_GAME) {
			title = "New Game";
			message = "Give you new game a name:";
			listener = new NameDialogListener() {
				@Override
				public void doAction(String name) {
					createNewGame(name);
				}
			};
		} else if (id == DIALOG_COPY_GAME) {
			title = "Copy Game";
			message = "Give a name to the new copy:";
			listener = new NameDialogListener() {
				@Override
				public void doAction(String name) {
					createGameCopy(name);
				}
			};
		} else if (id == DIALOG_BRANCH_GAME) {
			title = "Branch Game";
			message = "This will create a copy of this game for you to edit.\n"
					+ "Give the new game a name:";
			listener = new NameDialogListener() {
				@Override
				public void doAction(String name) {
					createGameCopy(name);
				}
			};
		} else {
			listener = null;
		}
		
		if (listener != null) {
			final View dialogView = getLayoutInflater()
					.inflate(R.layout.main_menu_dialog_name, null);
			return new AlertDialog.Builder(this)
			.setTitle(title)
			.setMessage(message)
			.setView(dialogView)
			.setPositiveButton("Ok", new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText editTextName = (EditText)
							dialogView.findViewById(R.id.editTextName);
					listener.doAction(editTextName.getText().toString());
				}
			})
			.setNeutralButton("Cancel", null)
			.create();
		}
		
		return null;
	}
	
	@Override 
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == DIALOG_NEW_GAME || id == DIALOG_BRANCH_GAME ||
				id == DIALOG_COPY_GAME) {
			EditText editTextName = (EditText)
					dialog.findViewById(R.id.editTextName);
			String name = "New Game";
			if (id == DIALOG_BRANCH_GAME || id == DIALOG_COPY_GAME) {
				if (selectedGame != null) {
					name = "Copy of " + selectedGame.getName();
				}
			}
			editTextName.setText(name);
		}
	}

	private void newGame() {
		showDialog(DIALOG_NEW_GAME);
	}
	
	private void createNewGame(String name) {
		PlatformGame game = new PlatformGame();
		GameDetails details = gameCache.addGame(name, GameType.Edit, game, this);
		int index = gameCache.getGames(GameType.Edit).indexOf(details);
		selectedIndices[getTab()] = index;
		loadGames();
		
	}
	
	private void edit() {
		if (selectedGame != null) {
			Intent intent = new Intent(this, MapEditor.class);
			PlatformGame game = Data.loadData(selectedGame.getFilename(), this);
			intent.putExtra("gameDetails", selectedGame);
			intent.putExtra("game", game);
			startActivity(intent);
		}
	}
	
	private void play() {
		if (selectedGame != null) {
			Intent intent = new Intent(this, Platformer.class);
			intent.putExtra("map", selectedGame.getFilename());
			startActivity(intent);
		}
	}
	
	private void copy() {
		if (selectedGame != null) {
			GameType type = getType();
			if (type == GameType.Edit) {
				showDialog(DIALOG_COPY_GAME);
			} else {
				showDialog(DIALOG_BRANCH_GAME);
			}
		}
	}
	
	private void createGameCopy(String name) {
		if (selectedGame != null) {
			GameDetails details = gameCache.makeCopy(
					selectedGame, name, GameType.Edit, this);
			int index = gameCache.getGames(GameType.Edit).indexOf(details);
			selectedIndices[0] = index;
			if (getTab() != 0) {
				tabHost.setCurrentTab(0);
			} else {
				loadGames();
			}
		}
	}
	
	private void delete() {
		if (selectedGame != null) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Delete?")
			.setMessage("Confirm Delete")
			.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					gameCache.deleteGame(selectedGame, getType(), MainMenu.this);
					loadGames();
				}

			})
			.setNegativeButton("Cancel", null)
			.show();
		}
	}
	
	private void goOnline() {
		startActivity(new Intent(MainMenu.this, WebLogin.class));
	}
	

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	
	private void loadGames() {
		selectedGame = null;
		
		int tab = getTab();
		GameType type = getType();
		GamesAdapter adapter = new GamesAdapter(this, gameCache.getGames(type));
		listViewGames.setAdapter(adapter);
		
		int selection = selectedIndices[tab];
		if (selection < adapter.getCount() && selection >= 0) {
			listViewGames.setItemChecked(selection, true);
			listViewGames.setSelection(selection);
			selectedGame = adapter.getItem(selection);
			linearLayoutButtons.setVisibility(View.VISIBLE);
		} else {
			linearLayoutButtons.setVisibility(View.INVISIBLE);
		}
		
		if (listViewGames.getAdapter().getCount() == 0) {
			linearLayoutHint.setVisibility(View.VISIBLE);
			updateHint();
		} else {
			linearLayoutHint.setVisibility(View.GONE);
		}
		
		linearLayoutButtons.removeAllViews();
		for (Button button : contextButtons.get(tab)) {
			Debug.write(button.getText().toString());
			linearLayoutButtons.addView(button);
		}
		
		if (type == GameType.Edit) {
			buttonAdd.setVisibility(View.VISIBLE);
			buttonAdd.setText("New Game");
			buttonAdd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					newGame();
				}
			});
		} else if (type == GameType.Play) {
			buttonAdd.setVisibility(View.VISIBLE);
			buttonAdd.setText("Go Online");
			buttonAdd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goOnline();
				}
			});
		} else {
			buttonAdd.setVisibility(View.GONE);
		}
	}
	
	private void updateHint() {
		GameType type = getType();
		if (type == GameType.Edit) {
			textViewHint.setText(getString(R.string.mainMenuEditHint));
			buttonHint.setText("New Game");
			buttonHint.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					newGame();
				}
			});
		} else if (type == GameType.Play) {
			textViewHint.setText(getString(R.string.mainMenuPlayHint));
			buttonHint.setText("Go Online");
			buttonHint.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goOnline();
				}
			});
		} else {
			linearLayoutHint.setVisibility(View.GONE);
		}
	}
	
	private class GamesAdapter extends CheckableArrayAdapter<GameDetails> {
		public GamesAdapter(Context context, List<GameDetails> games) {
			super(context, R.layout.array_adapter_row_game, games);
		}

		@Override
		protected void setRow(int position, GameDetails item, View view) {
			GameDetails details = getItem(position);
			
			TextView textViewTitle = (TextView)view.findViewById(R.id.textViewTitle);
			textViewTitle.setText(details.getName());

			TextView textViewDesciption = (TextView)view.findViewById(
					R.id.textViewDescription);
			TextView textViewDetails = (TextView)view.findViewById(
					R.id.textViewDetails);
			
			textViewDesciption.setVisibility(details.hasWebsiteInfo() ?
					View.VISIBLE : View.GONE);
			
			if (details.hasWebsiteInfo()) {
				GameInfo info = details.getWebsiteInfo();
				
				textViewDesciption.setText(info.description);
				
				String detailString = String.format(Locale.US,
						"%s v%d.%d, created by %s.",
						info.name,
						info.majorVersion, info.minorVersion,
						TextUtils.getColoredText(info.creatorName, "#9999BB"));
				textViewDetails.setText(Html.fromHtml(detailString));
			} else {
				String detailString = String.format("Created on %s.", 
						dateFormat.format(details.getDateCreated()));
				textViewDetails.setText(detailString);
			}
			
		}
	}
}
