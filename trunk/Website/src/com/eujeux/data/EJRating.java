package com.eujeux.data;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class EJRating extends EJData {
	@Persistent
	private Long userId;
	
	@Persistent
	private Long gameId;
	
	@Persistent
	private Boolean plusCreative = false;
	
	@Persistent
	private Boolean plusImpressive = false;
	
	@Persistent
	private Boolean plusFun = false;
	
	public EJRating(Long userId, Long gameId) {
		this.userId = userId;
		this.gameId = gameId;
	}
	
	public RatingInfo getInfo() {
		RatingInfo info = new RatingInfo();
		info.gameId = gameId;
		info.userId = userId;
		info.plusCreative = plusCreative;
		info.plusImpressive = plusImpressive;
		info.plusFun = plusFun;
		return info;
	}

	public void update(RatingInfo info, EJGame game) {
		if (info.plusCreative && !plusCreative) {
			game.setRatingCreative(game.getRatingCreative() + 1);
			plusCreative = info.plusCreative;
		}
		if (info.plusImpressive && !plusImpressive) {
			game.setRatingImpressive(game.getRatingImpressive() + 1);
			plusImpressive = info.plusImpressive;
		}
		if (info.plusFun && !plusFun) {
			game.setRatingFun(game.getRatingFun() + 1);
			plusFun = info.plusFun;
		}
	}
	
	@Override
	public boolean hasPermission() {
		return true;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Boolean getPlusCreative() {
		return plusCreative;
	}

	public void setPlusCreative(Boolean plusCreative) {
		this.plusCreative = plusCreative;
	}

	public Boolean getPlusImpressive() {
		return plusImpressive;
	}

	public void setPlusImpressive(Boolean plusImpressive) {
		this.plusImpressive = plusImpressive;
	}

	public Boolean getPlusFun() {
		return plusFun;
	}

	public void setPlusFun(Boolean plusFun) {
		this.plusFun = plusFun;
	}
}
