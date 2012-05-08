package edu.elon.honors.price.maker;

import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;

public class DatabaseActivity extends SaveableActivity {

	public static final int REQUEST_RETURN_GAME = 10;
	
	protected PlatformGame game;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		game = (PlatformGame)getIntent().getExtras().
		getSerializable("game");


		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		onFinishing();
		super.onBackPressed();
	}

	protected boolean hasChanged() {
		//long time = System.currentTimeMillis();
		PlatformGame oldGame = (PlatformGame)getIntent().getExtras().getSerializable("game");
		boolean r = !PlatformGame.areEqual(oldGame, game);
		//time = System.currentTimeMillis() - time;
		//Game.debug("Game compared in " + time + "ms");
		return r;
	}

	protected void finishOk(Intent intent) {
		onFinishing();
		intent.putExtra("game", game);
		putExtras(intent);
		super.finishOk(intent);
	}	

	protected void onFinishing() { }

	protected void putExtras(Intent intent) { }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//removed so that Selectors can take advantage of the system 
		//if (requestCode == REQUEST_RETURN_GAME) { 
		if (resultCode == RESULT_OK) {
			if (data.hasExtra("game")) {
				Serializable obj = data.getExtras().getSerializable("game");;
				if (obj instanceof PlatformGame) {
					game = (PlatformGame) obj;
				}
			}
		}
		//}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
