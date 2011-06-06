package edu.elon.honors.price.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * A class for saving and loading persistent data as well as
 * game resources.
 * 
 * @author Thomas Price
 *
 */
public final class Data {
	
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
	
	/**
	 * Saves a serializable class, using the given activity with the given name.
	 * 
	 * @param name The name of the saved file. Used to load the data later.
	 * @param parent The Activity to use for the saving.
	 * @param data The serializable class to save.
	 */
	public static void saveObject(String name, Activity parent, Serializable data) {
		try {
			FileOutputStream fos = parent.openFileOutput(name, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(data);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Loads a serializable class, using the given activity with the given name.
	 * 
	 * @param name The name of the file to be loaded.
	 * @param parent The Activity to use for the loading.
	 */
	public static Object loadObject(String name, Activity parent) {
		try {
			FileInputStream fos = parent.openFileInput(name);
			ObjectInputStream in = new ObjectInputStream(fos);
			Object data = in.readObject();
			in.close();
			return data;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	
}