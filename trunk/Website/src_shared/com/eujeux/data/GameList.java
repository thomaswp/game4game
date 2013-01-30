package com.eujeux.data;

import java.util.List;
import java.util.LinkedList;

public class GameList extends LinkedList<GameInfo> {
	private static final long serialVersionUID = 1L;
	
	public String cursorString;
	
	public GameList(List<GameInfo> list, String cursorString) {
		super(list);
		this.cursorString = cursorString;
	}
}
