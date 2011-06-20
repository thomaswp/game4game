package edu.elon.honors.price.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * A class for saving and loading persistent data as well as
 * game resources.
 * 
 * @author Thomas Price
 *
 */
public final class Data {
	
	public final static Uri CONTENT_URI = Uri.parse("content://edu.elon.honors.price.maker/");
	
	private static Resources resources;
	private static HashMap<Integer, Bitmap> cache = new HashMap<Integer, Bitmap>();
	
	/**
	 * Sets the resources to load from. Must be set
	 * before loadBitmap can be called.
	 * @param resources The resources
	 */
	public static void setResources(Resources resources) {
		Data.resources = resources;
	}
	
	public static boolean isBitmapRegistered(int id) {
		return cache.containsKey(id);
	}
	
	public static Bitmap getRegisteredBitmap(int id) {
		if (cache.containsKey(id))
			return cache.get(id);
		return null;
	}
	
	public static void clearCache() {
		cache.clear();
	}
	
	public static void RegisterBitmap(Bitmap bitmap, int id) {
		cache.put(id, bitmap);
	}
	
	/**
	 * Loads a Bitmap from the game's resources.
	 * @param id The id of the resource to be loaded
	 * @return
	 */
	public static Bitmap loadBitmap(int id) {
		try {
			if (cache.containsKey(id)) {
				//Game.debug("Cache");
				return cache.get(id);
			}
			else {
				//Game.debug("Load New");
				Bitmap bmp = BitmapFactory.decodeResource(resources, id);
				cache.put(id, bmp);
				return bmp;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static String loadString(int id) {
		return resources.getString(id);
	}
	
	/**
	 * Saves a serializable class, using the given activity with the given name.
	 * 
	 * @param name The name of the saved file. Used to load the data later.
	 * @param parent The Activity to use for the saving.
	 * @param data The serializable class to save.
	 */
	public static boolean saveObject(String name, Activity parent, Serializable data) {
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
	
	/**
	 * Loads a serializable class, using the given activity with the given name.
	 * 
	 * @param name The name of the file to be loaded.
	 * @param parent The Activity to use for the loading.
	 */
	public static Object loadObject(String name, Activity parent) {
		String[] files = parent.fileList();
		boolean found = false;
		for (int i = 0; i < files.length; i++)
			found |= files[i].equals(name);
		if (!found)
			return null;
		
		try {
			FileInputStream fis = parent.openFileInput(name);
			ObjectInputStream in = new ObjectInputStream(fis);
			Object data = in.readObject();
			in.close();
			return data;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static Object loadObjectPublic(String name, Activity parent) {
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
}
