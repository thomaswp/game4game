package edu.elon.honors.price.maker;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

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

		if (savedInstanceState != null && savedInstanceState.containsKey("game")) {
			game = (PlatformGame)savedInstanceState.getSerializable("game");
		}
		if (game == null) {
			game = (PlatformGame)getIntent().getExtras().
				getSerializable("game");
		}


		super.onCreate(savedInstanceState);
	}
	
	protected void autoAssign() {
		Field[] fields = this.getClass().getDeclaredFields();
		boolean typeAnnotation = 
			this.getClass().getAnnotation(AutoAssign.class) != null;
		for (Field field : fields) {
			if (!View.class.isAssignableFrom(field.getType())) {
				continue;
			}
			AutoAssign aa = field.getAnnotation(AutoAssign.class);
			if (aa != null || typeAnnotation) {
				String name;
				if (aa != null && aa.name().length() > 0) {
					name = aa.name();
				} else {
					name = field.getName();
				}
				
				try {
					Field id = R.id.class.getField(name);
					int lid = (Integer)id.get(null);
					View view = findViewById(lid);
					if (view != null && 
							field.getType().isAssignableFrom(view.getClass())) {
						field.setAccessible(true);
						field.set(this, view);
					}
					//Game.debug("%s successfully assigned!", field.getName());
				} catch (Exception e) {
					Game.debug("Problem with AutoAssign for field %s in class %s",
							field.getName(), this.getClass().getName());
					Game.debug(e);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		onFinishing();
		super.onBackPressed();
	}

	@Override
	protected boolean hasChanged() {
		//long time = System.currentTimeMillis();
		PlatformGame oldGame = (PlatformGame)getIntent().getExtras().getSerializable("game");
		boolean r = !PlatformGame.areEqual(oldGame, game);
		//time = System.currentTimeMillis() - time;
		//Game.debug("Game compared in " + time + "ms");
		return r;
	}

	protected final void finishOk(Intent intent) {
		onFinishing();
		intent.putExtra("game", game);
		putExtras(intent);
		super.finishOk(intent);
	}	

	/**
	 * Called before the game is Serialized.
	 * This is an activity's opportunity to
	 * save any form data to the game.
	 */
	protected void onFinishing() { }

	protected void putExtras(Intent intent) { }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) { 
		super.onSaveInstanceState(outState);
		onFinishing();
		outState.putSerializable("game", game);
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
		}
		//}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
