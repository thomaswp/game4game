package edu.elon.honors.price.maker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.twp.platform.Platformer;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

public class GameMaker extends Activity {
	
	public static final String PREFIX = "final-";
	
	private String selectedMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main);
		
		ContentResolver cr = getContentResolver();
		try {
			AssetFileDescriptor afd = cr.openAssetFileDescriptor(Uri.withAppendedPath(Data.CONTENT_URI, "graphics/actors/ghost.png"), "r");
			Game.debug(afd.getDeclaredLength());
			InputStream is = afd.createInputStream();
			Bitmap bmp = BitmapFactory.decodeStream(is);
			Game.debug(bmp.getWidth());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		loadMaps();
		loadButtons();
		
		super.onCreate(savedInstanceState);
	}
	
	private void loadMaps() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout1);
		layout.removeAllViews();
		RadioGroup group = new RadioGroup(this);
		layout.addView(group);
		
		selectedMap = null;
				
		String[] files = fileList();
		for (String file : files) {
			if (file.indexOf(PREFIX) == 0) {
				final String name = file.substring(PREFIX.length());
				final String fileName = file;
				RadioButton b = new RadioButton(this);
				b.setText(name);
				b.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (selectedMap == fileName)
							edit();
						else
							selectedMap = fileName;
					}
				});
				group.addView(b);
				if (selectedMap == null) {
					selectedMap = fileName;
					b.setChecked(true);
				}
			}
		}
	}
	
	private void loadButtons() {
		Button newGame = (Button)findViewById(R.id.buttonNewGame);
		Button edit = (Button)findViewById(R.id.buttonEdit);
		Button play = (Button)findViewById(R.id.buttonPlay);
		Button delete = (Button)findViewById(R.id.buttonDelete);
		Button copy = (Button)findViewById(R.id.buttonCopy);
		
		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edit();
			}
		});
		
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play();
			}
		});
		
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete();
			}
		});
		
		newGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				newGame();
			}
		});
	}
	
	private void newGame() {
		String[] files = fileList();
		int n = 0;
		String name;
		boolean exists = false;
		do {
			name = PREFIX + "Map_" + ++n;
			exists = false;
			for (int i = 0; i < files.length; i++) 
				exists |= files[i].equals(name); 
		} while (exists);
		
		Data.saveObject(name, this, new PlatformGame());
		
		loadMaps();
	}
	
	private void delete() {
		if (selectedMap != null) {
			new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Delete?")
	        .setMessage("Confirm Delete")
	        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                  deleteFile(selectedMap);
	                  String name = selectedMap.substring(PREFIX.length());
	                  deleteFile(name + PlatformMakerLogic.DATA);
	                  deleteFile(name + PlatformMakerLogic.MAP);
	                  loadMaps();
	            }

	        })
	        .setNegativeButton("Cancel", null)
	        .show();
		}
	}
	
	private void edit() {
		if (selectedMap != null) {
			Intent intent = new Intent(this, PlatformMaker.class);
			intent.putExtra("map", selectedMap);
			startActivity(intent);
		}
	}
	
	private void play() {
		if (selectedMap != null) {
			Intent intent = new Intent(this, Platformer.class);
			intent.putExtra("map", selectedMap);
			startActivity(intent);
		}
	}
}
