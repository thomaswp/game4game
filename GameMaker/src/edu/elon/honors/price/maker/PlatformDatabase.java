package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class PlatformDatabase extends Activity {
	private String gameName;
	private PlatformGame game;
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		gameName = getIntent().getExtras().getString("game");
		game = (PlatformGame)Data.loadGame(gameName + PlatformMakerLogic.MAP, this);

		setContentView(R.layout.platformactorselector);
		
		createRadioButtons();
		
		super.onCreate(savedInstanceState);
	}
	
	private void createRadioButtons() {
		RadioGroup group = (RadioGroup)findViewById(R.id.radioGroupActors);
		for (int i = 1; i < game.actors.length; i++) {
			RadioButton button = new RadioButton(this);
			button.setText(game.actors[i].name);
			button.setTag(i);
			
			group.addView(button);
		}
	}
}
