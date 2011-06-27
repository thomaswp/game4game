package edu.elon.honors.price.maker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;

public class PlatformDatabaseActivity extends Activity {
	
	public static final String TEMP = "_TEMP";
	
	protected PlatformGame game;
	protected String gameName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		gameName = getIntent().getExtras().getString("game");
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		game = (PlatformGame)Data.loadGame(getTempFile(), this);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		Data.saveGame(getTempFile(), this, game);
		super.onPause();
	}
	
	protected String getTempFile() {
		return getGameFile() + TEMP;
	}
	
	protected String getGameFile() {
		return gameName + PlatformMakerLogic.MAP;
	}
	
}
