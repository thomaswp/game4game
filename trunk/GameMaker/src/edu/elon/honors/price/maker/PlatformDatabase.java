package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class PlatformDatabase extends Activity {
	private String gameName;
	private PlatformGame game;
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		gameName = getIntent().getExtras().getString("game");
		game = (PlatformGame)Data.loadObject(gameName + PlatformMakerLogic.MAP, this);

		//setContentView(R.layout.platformdatabase);
		
		super.onCreate(savedInstanceState);
	}
}
