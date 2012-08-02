package edu.elon.honors.price.maker;

import java.io.Serializable;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

public class SaveableActivity extends Activity {
	
	private Handler finishHandler = new Handler();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//Game.debug("RESTORING INSTANCE!!!");
		}
	}
	
	@Override
	public void onBackPressed() {
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
	
	protected final void finishOk() {
		Intent intent = new Intent();
		finishOk(intent);
	}
	
	protected void finishOk(Intent data) {
		setResult(RESULT_OK, data);
		finish();
	}
	
	protected void finishOkAll() {
		Intent intent = new Intent();
		intent.putExtra("finishAll", true);
		finishOk(intent);
	}
	
	protected boolean hasChanged() {
		return false;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
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
		super.onActivityResult(requestCode, resultCode, data);
	}
}
