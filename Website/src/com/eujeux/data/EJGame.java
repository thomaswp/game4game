package com.eujeux.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.eujeux.LoginUtils;
import com.eujeux.QueryUtils;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.apphosting.api.UserServicePb.UserService;

@PersistenceCapable
public class EJGame extends EJData {

	
	@Persistent
	private String name;
	
	@Persistent
	private int majorVersion = 1;

	@Persistent
	private int minorVersion = 0;
	
	@Persistent
	private Date uploadDate = new Date();
	
	@Persistent
	private long lastEdited;
	
	@Persistent
	private Long creatorId;
	
	@Persistent
	private Integer downloads = 0;
	
	@Persistent
	private String description = "Game description here...";
	
	@Persistent
	private BlobKey blobKey;
	
	public EJGame() { }
	public EJGame(String name, Long creatorKey, BlobKey blobKey) {
		this.name = name;
		this.creatorId = creatorKey;
		this.blobKey = blobKey;
	}
	
	@Override
	public String toString() {
		return String.format("%s v%d.%d", name, majorVersion, minorVersion);
	}
	
	public GameInfo getInfo() {
		GameInfo game = getInfo(null);
		game.creatorName = QueryUtils.queryUnique(
				EJUser.class, "id == %s", creatorId)
				.getUserName();
		return game;
	}

	public GameInfo getInfo(UserInfo info) {
		GameInfo game = new GameInfo();
		game.id = id;
		game.name = name;
		game.blobKeyString = blobKey.getKeyString();
		game.downloads = downloads == null ? 0 : downloads;
		game.uploadDate = uploadDate;
		game.majorVersion = majorVersion;
		game.minorVersion = minorVersion;
		game.creatorId = creatorId;
		game.creatorName = info.userName;
		game.description = description;
		game.lastEdited = lastEdited;
		return game;
	}
	
	public void update(GameInfo info) {
		setName(info.name);
		setDescription(info.description);
	}
	
	@Override
	public boolean hasPermission() {
		EJUser user = LoginUtils.getUser();
		return user != null && user.id == creatorId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}
	public int getMinorVersion() {
		return minorVersion;
	}
	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	public long getLastEdited() {
		return lastEdited;
	}
	public void setLastEdited(long lastEdited) {
		this.lastEdited = lastEdited;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorKey) {
		this.creatorId = creatorKey;
	}
	public BlobKey getBlobKey() {
		return blobKey;
	}
	public void setBlobKey(BlobKey blobKey) {
		this.blobKey = blobKey;
	}
	public Integer getDownloads() {
		return downloads;
	}
	public void setDownloads(Integer downloads) {
		this.downloads = downloads;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}