package com.eujeux.data;

import java.util.List;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.eujeux.Debug;
import com.eujeux.LoginUtils;
import com.eujeux.QueryUtils;

@PersistenceCapable
public class EJUser extends EJData {
	
	@Persistent
	private String email;
	
	@Persistent
	private String userName;
	
	@Persistent
	private String lastToken;
	
	@Persistent
	private Long lastLogin;
	
	public EJUser(String email) {
		this.email = email;
		this.userName = email;
		this.lastLogin = System.currentTimeMillis();
	}
	
	public UserInfo getInfo() {
		UserInfo info = new UserInfo();
		info.userName = userName;
		return info;
	}
	
	public MyUserInfo getMyInfo() {
		MyUserInfo info = new MyUserInfo();
		info.userName = userName;
		info.email = email;
		List<EJGame> games = QueryUtils.query(
				EJGame.class, "creatorId == %s", id);
		for (EJGame game : games) {
			GameInfo gi = game.getInfo(info);
			info.games.add(gi);
			
		}
		return info;
	}

	@Override
	public boolean hasPermission() {
		EJUser user = LoginUtils.getUser();
		return user != null && user.id == id;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLastToken() {
		return lastToken;
	}

	public void setLastToken(String lastToken) {
		this.lastToken = lastToken;
	}

	public Long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Long lastLogin) {
		this.lastLogin = lastLogin;
	}
}
