package com.eujeux.data;

import java.io.Serializable;

public class RatingInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public long userId, gameId;
	public boolean plusCreative, plusImpressive, plusFun;
	
	@Override
	public String toString() {
		String s = "Rating: ";
		if (plusCreative) s += "creative ";
		if (plusImpressive) s += "impressive";
		if (plusFun) s += "fun ";
		return s;
	}
}
