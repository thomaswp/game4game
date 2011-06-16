package edu.elon.honors.price.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class ResourceProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://edu.elon.honors.price.data");
	
	private MatrixCursor cursor;

	@Override
	public ParcelFileDescriptor openFile(Uri u, String mode) throws FileNotFoundException {
		return null;
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
		cursor = new MatrixCursor(new String[] { "name", "data" });
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
