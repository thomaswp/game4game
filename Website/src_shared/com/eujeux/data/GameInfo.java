package com.eujeux.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public long id;
	public String name;
	public String blobKeyString;
	public int downloads;
	public Date uploadDate;
	public long creatorId;
	public String creatorName;
	public int majorVersion, minorVersion;
	public String description;
	public long lastEdited;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat();	

	public String getVersionString() {
		return String.format(Locale.US, "%d.%d", majorVersion, minorVersion);
	}
	
	public String getUploadDateString() {
		return dateFormat.format(uploadDate);
	}
}
