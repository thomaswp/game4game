package edu.elon.honors.price.maker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.util.Arrays;

import edu.elon.honors.price.game.Game;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class ResourceProvider extends ContentProvider {
	
	private MatrixCursor cursor;

	@Override
	public ParcelFileDescriptor openFile(Uri u, String mode) throws FileNotFoundException {
		Game.debug(u.toString());
		URI uri = URI.create("file:///data/data/edu.elon.honors.price.maker/files/" + u.getLastPathSegment());
		File file = new File(uri);
		return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return uri;
	}

	@Override
	public boolean onCreate() {
		cursor = new MatrixCursor(new String[] { "name", "path" });
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
