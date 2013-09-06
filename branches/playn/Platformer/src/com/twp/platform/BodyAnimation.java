package com.twp.platform;

import com.badlogic.gdx.math.Vector2;

public abstract class BodyAnimation {
	public final static float SCALE = PlatformBody.SCALE;
	
	protected int duration;
	protected long totalTimeElapsed;
	private Vector2 frameOffset = new Vector2();
	private float dX, dY;
	
	public boolean isFinished() {
		return totalTimeElapsed > duration;
	}
	
	public BodyAnimation(int durationMS) {
		this.duration = durationMS;
	}
	
	public Vector2 getResetVector() {
		frameOffset.set(-dX, -dY);
		return frameOffset;
	}
	
	public Vector2 getFrameOffset(long timeElapsed) {
		float startTime = totalTimeElapsed;
		totalTimeElapsed += timeElapsed;
		float endTime = totalTimeElapsed;
		if (endTime > duration) {
			frameOffset.set(-dX, -dY);
			dX = 0;
			dY = 0;
		} else {
			setFrameOffset(startTime, endTime, frameOffset);
			dX += frameOffset.x;
			dY += frameOffset.y;
		}
		return frameOffset;
	}
	
	protected abstract void setFrameOffset(float startTime, float endTime,
			Vector2 frameOffset);
}
