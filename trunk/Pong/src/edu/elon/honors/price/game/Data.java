package edu.elon.honors.price.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.app.Activity;
import android.content.Context;

/**
 * A class for saving and loading persistent data.
 * 
 * @author Thomas Price
 *
 */
public final class Data {
	/**
	 * Saves a serializable class, using the given activity with the given name.
	 * 
	 * @param name The name of the saved file. Used to load the data later.
	 * @param parent The Activity to use for the saving.
	 * @param data The serializable class to save.
	 */
	public static void save(String name, Activity parent, Serializable data) {
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
	public static Object load(String name, Activity parent) {
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
