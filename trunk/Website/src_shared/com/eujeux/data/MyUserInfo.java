package com.eujeux.data;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class MyUserInfo extends UserInfo {
	private static final long serialVersionUID = 1L;
	
	public String email;
	public LinkedList<GameInfo> games = new LinkedList<GameInfo>();
	
	private static Pattern usernamePattern;
	
	
	public static boolean validUsername(String username) {
		if (usernamePattern == null) {
			usernamePattern = Pattern.compile("^[a-z0-9_\\-@\\.]{3,20}$");
		}
		return usernamePattern.matcher(username).matches();
	}
}
