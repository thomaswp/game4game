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

public class DatabaseActivity extends Activity {

	public static final int REQUEST_RETURN_GAME = 10;
	
	private Handler finishHandler = new Handler();
	
	protected PlatformGame game;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		game = (PlatformGame)getIntent().getExtras().getSerializable("game");


		super.onCreate(savedInstanceState);
	}

	protected void setDefaultButtonActions() {
		Button buttonOk = (Button)findViewById(R.id.buttonOk);
		Button buttonCancel = (Button)findViewById(R.id.buttonCancel);

		if (buttonOk != null) {
			buttonOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finishOk();
				}
			});
			buttonOk.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					Vibrator vb =(Vibrator)getSystemService(VIBRATOR_SERVICE);
					vb.vibrate(100);
					finishOkAll();
					return true;
				}
			});
		}

		if (buttonCancel != null) {
			buttonCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
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
		long time = System.currentTimeMillis();
		PlatformGame oldGame = (PlatformGame)getIntent().getExtras().getSerializable("game");
		boolean r = !PlatformGame.areEqual(oldGame, game);
		time = System.currentTimeMillis() - time;
		Game.debug("Game compared in " + time + "ms");
		return r;
	}

	protected void onFinishing() {

	}

	protected final void finishOk() {
		onFinishing();
		Intent intent = new Intent();
		intent.putExtra("game", game);
		putExtras(intent);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	protected final void finishOkAll() {
		onFinishing();
		Intent intent = new Intent();
		intent.putExtra("game", game);
		intent.putExtra("finishAll", true);
		putExtras(intent);
		setResult(RESULT_OK, intent);
		finish();
	}

	protected void putExtras(Intent intent) {

	}
	
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
			if (data.hasExtra("finishAll")) {
				boolean finishAll = data.getExtras().getBoolean("finishAll");
				if (finishAll) {
					finishHandler.post(new Runnable() {
						@Override
						public void run() {
							finishOkAll();
						}
					});
				}
			}
		}
		//}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
