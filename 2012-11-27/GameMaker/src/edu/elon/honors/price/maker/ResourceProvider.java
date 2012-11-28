package edu.elon.honors.price.maker;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import edu.elon.honors.price.data.Data;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class ResourceProvider extends ContentProvider {

	@Override
	public ParcelFileDescriptor openFile(Uri u, String mode) throws FileNotFoundException {
		URI uri = URI.create("file:///data/data/edu.elon.honors.price.maker/files/" + u.getLastPathSegment());
		File file = new File(uri);
		return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);	
	}

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
		if (!uri.getPathSegments().contains(Data.GRAPHICS))
			return super.openAssetFile(uri, mode);
		
		AssetManager am = getContext().getAssets();
		try {
			AssetFileDescriptor afd = am.openFd(uri.getPath().substring(1));
			return afd;
		} catch (Exception ex) {
			throw new FileNotFoundException(ex.getMessage());
		}

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
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
