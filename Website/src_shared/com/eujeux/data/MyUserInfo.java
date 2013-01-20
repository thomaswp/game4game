package com.eujeux.data;

import java.util.LinkedList;

public class MyUserInfo extends UserInfo {
	private static final long serialVersionUID = 1L;

	public String email;
	public LinkedList<GameInfo> games = new LinkedList<GameInfo>();
}
