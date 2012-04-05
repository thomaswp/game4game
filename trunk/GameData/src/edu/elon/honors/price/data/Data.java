package edu.elon.honors.price.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import edu.elon.honors.price.game.Cache;
import edu.elon.honors.price.game.Game;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
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
	public final static String ACTIONS_DIR = "actions";
	public final static String ACTORS_DIR = GRAPHICS + "/actors/";
	public final static String TILESETS_DIR = GRAPHICS + "/tilesets/";
	public final static String OBJECTS_DIR = GRAPHICS + "/objects/";
	public final static String BACKGROUNDS_DIR = GRAPHICS + "/backgrounds/";
	public final static String FOREGROUNDS_DIR = GRAPHICS + "/foregrounds/";
	public final static String MIDGROUNDS_DIR = GRAPHICS + "/midgrounds/";
	
	private static Context defaultParent;
	
	public static Context getDefaultParent() {
		return defaultParent == null ? Game.getCurrentGame() : defaultParent;
	}
	
	public static void setDefaultParent(Context parent) {
		defaultParent = parent;
	}
	
	private static Bitmap loadBitmap(String name, Context parent) {
		try {
			if (Cache.isBitmapRegistered(name)) {
				//Game.debug("Cache: " + id);
				return Cache.getRegisteredBitmap(name);
			}
			else {
				//Game.debug("Load New: " + id);
				try {
					ContentResolver cr = parent.getContentResolver();
					AssetFileDescriptor afd = cr.openAssetFileDescriptor(Uri.withAppendedPath(Data.CONTENT_URI, name), "r");
					InputStream is = afd.createInputStream();
					//BitmapFactory.Options options = new Options();
					Bitmap bmp = BitmapFactory.decodeStream(is);
					Cache.RegisterBitmap(name, bmp);
					return bmp;
				} catch (Exception ex) {
					Game.debug("Could not find '" + name + "' in local resrounces. Attempting external storage.");
					ex.printStackTrace();
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						File file = new File(Environment.getExternalStorageDirectory(), Data.SD_FOLDER + name);
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
	
	/**
	 * Returns a list of the file names of all resources in the given directory. 
	 * @param dir The directory to be searched. Use one of the directory contants
	 * found in this class. This will only return Assets if called from the GameMaker
	 * namespace.
	 * @param parent The context in which the request should be made. This should
	 * be an Activity in the GameMaker app.
	 * @return
	 */
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

	public static InputStream loadAction(int id, Context context) throws IOException {
		ArrayList<String> resources = getResources(ACTIONS_DIR, context);
		String idString = String.format("%03d", id);
		for (int i = 0; i < resources.size(); i++) {
			if (resources.get(i).startsWith(idString)) {
				return context.getAssets().open(ACTIONS_DIR + "/" + resources.get(i));
			}
		}
		Game.debug("No actions found for id " + idString);
		return null;
	}
	
	public static Bitmap loadObject(String name) {
		return loadBitmap(OBJECTS_DIR + name, getDefaultParent());
	}
	
	/**
	 * Loads an actor from the ACTORS_DIR directory. Uses the current running
	 * Game as a context. Use only from an appropriate Logic class.
	 * @param name File name (without path but with extension), such as
	 * 'actor.png'.
	 * @return The bitmap.
	 */
	public static Bitmap loadActor(String name) {
		return loadActor(name, getDefaultParent());
	}
	
	/**
	 * Loads an actor from the ACTORS_DIR directory.
	 * @param name File name (without path but with extension), such as
	 * 'actor.png'.
	 * @param context The context in which to load the resource.
	 * @return The bitmap.
	 */
	public static Bitmap loadActor(String name, Context context) {
		return loadBitmap(ACTORS_DIR + name, context);
	}

	/**
	 * Loads a tileset from the TILESETS_DIR directory. Uses the current running
	 * Game as a context. Use only from an appropriate Logic class.
	 * @param name File name (without path but with extension), such as
	 * 'tileset.png'.
	 * @return The bitmap.
	 */
	public static Bitmap loadTileset(String name) {
		return loadTileset(name, getDefaultParent());
	}
	
	/**
	 * Loads a tileset from the TILESETS_DIR directory.
	 * @param name File name (without path but with extension), such as
	 * 'tileset.png'.
	 * @param context The context in which to load the resource.
	 * @return The bitmap.
	 */
	public static Bitmap loadTileset(String name, Context parent) {
		return loadBitmap(TILESETS_DIR + name, parent);
	}

	public static Bitmap loadBackground(String name) {
		return loadBitmap(BACKGROUNDS_DIR + name, getDefaultParent());
	}
	
	public static Bitmap loadForeground(String name) {
		return loadBitmap(FOREGROUNDS_DIR + name, getDefaultParent());
	}
	
	public static Bitmap loadMidground(String name) {
		return loadBitmap(MIDGROUNDS_DIR + name, getDefaultParent());
	}
	
	/**
	 * Used to load game data stored in the GameMaker namespace. Can
	 * be used outside of this namespace.
	 * @param name The file name of the game to be loaded. No directory
	 * should be provided
	 * @param parent The context in which to load the game. This does not
	 * have to be in the GameMaker namespace.
	 * @return The PlatformGame object.
	 */
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

	/**
	 * Used to save game data stored in the GameMaker namespace. Can
	 * be used outside of this namespace.
	 * @param name The file name of the game to be saved. No directory
	 * should be provided
	 * @param parent The context in which to save the game. This does not
	 * have to be in the GameMaker namespace.
	 * @param data The PlatformGame object.
	 * @return true if the save was successful.
	 */
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
