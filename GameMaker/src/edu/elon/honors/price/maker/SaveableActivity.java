package edu.elon.honors.price.maker;

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
	private boolean loaded;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//Game.debug("RESTORING INSTANCE!!!");
		}
		
		//This just makes sure everything has
		//had a chance to load before back can be pressed
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						loaded = true;
					}
				});
			}
		});
	}

	/**
	 * Called before {@link #finishOk()} in case
	 * any error or warning messages need to be
	 * displayed. Return true if the saving should
	 * continue, false if it should be canceled.
	 */
	protected boolean onSaving() {
		return true;
	}

	/**
	 * Checks {@link #hasChanged()} for any changes
	 * and then, if there are any, shows an alert dialog
	 * asking if the user wants to save these changes
	 * (or cancel the back action). If there are no changes
	 * just calls {@link #finish()}.
	 */
	@Override
	public void onBackPressed() {
		if (!loaded) return;
		if (hasChanged()) {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Keep Changes?")
			.setMessage("Do you want to keep the changes you made to this page?")
			.setPositiveButton("Keep Changes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (onSaving()) {
						finishOk();
					}
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

	/**
	 * Calls {@link #finishOk(Intent)} with a
	 * new Intent as an argument. This method
	 * is final an not meant to be overridden.
	 */
	protected final void finishOk() {
		Intent intent = new Intent();
		finishOk(intent);
	}

	/**
	 * Calls {@link #setResult(int, Intent)}, with
	 * RESULT_OK and the passed data, then finishes
	 * the activity.
	 * @param data
	 */
	protected void finishOk(Intent data) {
		setResult(RESULT_OK, data);
		finish();
	}

	/**
	 * Finishes this activits, and sends a
	 * message back to any other {@link #SaveableActivity()}
	 * classed open below this one to close as well.
	 */
	protected void finishOkAll() {
		Intent intent = new Intent();
		intent.putExtra("finishAll", true);
		finishOk(intent);
	}

	/**
	 * This method is called to check if the
	 * saveable data in this class has changed.
	 * Should be overridden.
	 * @return true if the data has changed
	 */
	protected boolean hasChanged() {
		return false;
	}

	/**
	 * Sets the onClick action of the button with id "buttonOk"
	 * to call {@link #finishOk()} and of the button with id
	 * "buttonCancel" to {@link #finish()}. Also sets the 
	 * okLongClick behavior of the ok button to 
	 * {@link #finishOkAll()}.  
	 */
	protected void setDefaultButtonActions() {
		Button buttonOk = (Button)findViewById(R.id.buttonOk);
		Button buttonCancel = (Button)findViewById(R.id.buttonCancel);

		if (buttonOk != null) {
			buttonOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!loaded) return;
					if (onSaving()) {
						finishOk();
					}
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

	/**
	 * Handles activity results by calling {@link #finishOkAll()} if
	 * the closing activity has requested it.
	 */
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
