package edu.elon.honors.price.graphics;

import android.graphics.Bitmap;

public class AnimatedSprite extends Sprite {
	private int frame;
	private Bitmap[] frames;
	private int animFrameLength, animStartFrame, animNumFrames, animTime = -1;
	
	public int getFrame() {
		return frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
		setBitmap(frames[frame]);
	}

	public Bitmap[] getFrames() {
		return frames;
	}
	
	public AnimatedSprite(Viewport viewport, Bitmap[] frames, float x, float y) {
		super(viewport, frames[0]);
		setX(x);
		setY(y);
		this.frames = frames;
	}
	
	public void Animate(int frameLength, int startFrame, int numFrames) {
		animTime = 0;
		animFrameLength = frameLength;
		animStartFrame = startFrame;
		animNumFrames = numFrames;
	}
	
	public void StopAnimation() {
		animTime = -1;
	}
	
	@Override
	public void update(long timeElapsed) {
		if (animTime >= 0) {
			animTime += timeElapsed;
			setFrame(animStartFrame + ((animTime / animFrameLength) % animNumFrames));
		}
		super.update(timeElapsed);
	}
}
