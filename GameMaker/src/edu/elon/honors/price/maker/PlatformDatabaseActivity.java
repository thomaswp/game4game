package edu.elon.honors.price.maker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;

public class PlatformDatabaseActivity extends Activity {
	
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
		game = (PlatformGame)Data.loadGame(gameName + PlatformMakerLogic.MAP, this);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		Data.saveGame(gameName + PlatformMakerLogic.MAP, this, game);
		super.onPause();
	}
}
