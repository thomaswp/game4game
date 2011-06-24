package edu.elon.honors.price.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import edu.elon.honors.price.game.Cache;
import edu.elon.honors.price.game.Game;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

/**
 * A class for saving and loading persistent data as well as
 * game resources.
 * 
 * @author Thomas Price
 *
 */
public final class Data {

	public final static Uri CONTENT_URI = Uri.parse("content://edu.elon.honors.price.maker/");
	public final static String SD_FOLDER = "Game Maker/";
	public final static String GRAPHICS = "graphics";
	public final static String ACTORS_DIR = GRAPHICS + "/actors/";
	public final static String TILESETS_DIR = GRAPHICS + "/tilesets/";
	
	private static Bitmap loadBitmap(String id, Context parent) {
		try {
			if (Cache.isBitmapRegistered(id)) {
				//Game.debug("Cache: " + id);
				return Cache.getRegisteredBitmap(id);
			}
			else {
				//Game.debug("Load New: " + id);
				try {
					ContentResolver cr = parent.getContentResolver();
					AssetFileDescriptor afd = cr.openAssetFileDescriptor(Uri.withAppendedPath(Data.CONTENT_URI, id), "r");
					InputStream is = afd.createInputStream();
					Bitmap bmp = BitmapFactory.decodeStream(is);
					Cache.RegisterBitmap(id, bmp);
					return bmp;
				} catch (Exception ex) {
					Game.debug("Could not find '" + id + "' in local resrounces. Attempting external storage.");
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						File file = new File(Environment.getExternalStorageDirectory(), Data.SD_FOLDER + id);
						return BitmapFactory.decodeFile(file.getAbsolutePath());
					} else {
						Game.debug("Failed: No SD Card detected");
						return null;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static ArrayList<String> getResources(String dir, Context parent) {
		ArrayList<String> files = new ArrayList<String>();
		
		if (dir.endsWith("/"))
			dir = dir.substring(0, dir.length() - 1);
		
		try {
			String[] assets = parent.getAssets().list(dir);
			for (int i = 0; i < assets.length; i++) {
				files.add(assets[i]);
			}
		} catch (Exception ex) {
			Game.debug("No local resources for '" + dir + "'");
		}
		
		try {
			File file = new File(Environment.getExternalStorageDirectory(), Data.SD_FOLDER + dir);
			String[] externals = file.list();
			for (int i = 0; i < externals.length; i++) {
				files.add(externals[i]);
			}
		} catch (Exception ex) {
			Game.debug("No external resources for '" + dir + "'");
		}
		
		return files;
	}

	public static Bitmap loadActor(String name) {
		return loadActor(name, Game.getCurrentGame());
	}
	
	public static Bitmap loadActor(String name, Context context) {
		return loadBitmap(ACTORS_DIR + name, context);
	}

	public static Bitmap loadTileset(String name) {
		return loadTileset(name, Game.getCurrentGame());
	}
	
	public static Bitmap loadTileset(String name, Context parent) {
		return loadBitmap(TILESETS_DIR + name, parent);
	}

	public static Object loadGame(String name, Activity parent) {
		try {
			InputStream is = parent.getContentResolver().openInputStream(Uri.withAppendedPath(Data.CONTENT_URI, name));
			ObjectInputStream in = new ObjectInputStream(is);
			Object data = in.readObject();
			in.close();
			return data;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean saveGame(String name, Activity parent, Serializable data) {
		try {
			FileOutputStream fos = parent.openFileOutput(name, Context.MODE_WORLD_WRITEABLE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(data);
			out.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
