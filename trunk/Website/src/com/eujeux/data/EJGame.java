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
	private int version = 0;
	
	@Persistent
	private Date uploadDate = new Date();
	
	@Persistent
	private Long creatorId;
	
	@Persistent
	private Integer downloads = 0;
	
	@Persistent
	private BlobKey blobKey;
	
	public EJGame() { }
	public EJGame(String name, Long creatorKey, BlobKey blobKey) {
		this.name = name;
		this.creatorId = creatorKey;
		this.blobKey = blobKey;
	}
	
	public GameInfo getInfo() {
		GameInfo game = getInfo(null);
		game.creator = QueryUtils.queryUnique(
				EJUser.class, "id == %s", creatorId.toString())
				.getInfo();
		return game;
	}

	public GameInfo getInfo(MyUserInfo info) {
		GameInfo game = new GameInfo();
		game.name = name;
		game.blobKeyString = blobKey.getKeyString();
		game.downloads = downloads == null ? 0 : downloads;
		game.uploadDate = uploadDate;
		game.version = version;
		game.creator = info;
		return game;
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
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
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
	
}