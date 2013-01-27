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
import com.google.gdata.data.contacts.Website;

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
		this.email = email.toLowerCase();
		this.userName = email.toLowerCase();
		if (userName.length() > WebSettings.MAX_USERNAME_LENGTH) {
			userName = userName.substring(0, WebSettings.MAX_USERNAME_LENGTH);
		}
		this.lastLogin = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return String.format("User '%s': %s", userName, email);
	}

	public void generateDefaultName() {
		if (id != null) {
			userName = "user" + id.toString();
		}
		if (userName.length() > WebSettings.MAX_USERNAME_LENGTH) {
			userName = userName.substring(0, WebSettings.MAX_USERNAME_LENGTH);
		}
	}
	
	public UserInfo getInfo() {
		UserInfo info = new UserInfo();
		info.userName = userName;
		info.id = id;
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

	public boolean hasEditPermission(MyUserInfo user) {
		return user.email.equals(email);
	}
	
	public boolean update(MyUserInfo info) {
		if (!hasEditPermission(info)) return false;
		if (!MyUserInfo.validUsername(info.userName)) return false;
		
		if (!info.userName.equals(userName)) {
			EJUser user = QueryUtils.queryUnique(
					EJUser.class, "userName == %s", info.userName);
			if (user != null) return false;
		}
		
		setUserName(info.userName);
		return true;
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
