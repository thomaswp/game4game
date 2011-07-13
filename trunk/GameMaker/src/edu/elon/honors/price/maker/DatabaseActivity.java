package edu.elon.honors.price.maker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;

public class DatabaseActivity extends Activity {
	
	protected PlatformGame game;
	
	public static final int REQUEST_RETURN_GAME = 10;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onBackPressed() {
		onFinishing();
		if (hasChanged()) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Keep Changes?")
			.setMessage("Do you want to keep the changes you made to this page?")
			.setPositiveButton("Keep Changes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finishOk();
				}

			})
			.setNeutralButton("Discard Changes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}

			})
			.setNegativeButton("Stay Here", null)
			.show();	
		} else {
			finish();
		}
	}
	
	protected boolean hasChanged() {
		PlatformGame oldGame = (PlatformGame)getIntent().getExtras().getSerializable("game");
		return !PlatformGame.areEqual(oldGame, game);
	}

	protected void onFinishing() {
		
	}
	
	protected final void finishOk() {
		onFinishing();
		Intent intent = new Intent();
		intent.putExtra("game", game);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RETURN_GAME) {
			if (resultCode == RESULT_OK) {
				game = (PlatformGame)data.getExtras().getSerializable("game");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
