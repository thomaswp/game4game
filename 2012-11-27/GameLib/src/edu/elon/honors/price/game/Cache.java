package edu.elon.honors.price.game;

import java.util.HashMap;

import android.graphics.Bitmap;

public final class Cache {
	private static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	public static boolean isBitmapRegistered(int id) {
		return isBitmapRegistered("" + id);
	}
	
	public static boolean isBitmapRegistered(String id) {
		return cache.containsKey(id);
	}

	public static Bitmap getRegisteredBitmap(int id) {
		return getRegisteredBitmap("" + id);
	}
	
	public static Bitmap getRegisteredBitmap(String id) {
		if (cache.containsKey(id))
			return cache.get(id);
		return null;
	}

	public static void clearCache() {
		cache.clear();
	}

	public static void RegisterBitmap(int id, Bitmap bitmap) {
		RegisterBitmap("" + id, bitmap);
	}
	
	public static void RegisterBitmap(String id, Bitmap bitmap) {
		cache.put(id, bitmap);
	}

}
