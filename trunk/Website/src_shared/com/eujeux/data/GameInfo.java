package com.eujeux.data;

import java.io.Serializable;
import java.util.Date;

public class GameInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public long id;
	public String name;
	public String blobKeyString;
	public int downloads;
	public Date uploadDate;
	public UserInfo creator;
	public int majorVersion, minorVersion;
	

	public String getVersionString() {
		return String.format("%d.%d", majorVersion, minorVersion);
	}
}
