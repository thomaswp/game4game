package edu.elon.honors.price.data;

import java.util.HashMap;


public abstract class ActorAnimator {
		
	private enum State {
		Walking,
		Jumping,
		Landing,
		Action,
		Climbing
	}
	
	public enum Action {
		Climbing,
		WalkingLeft,
		WalkingRight,
		ActionLeft,
		ActionRight,
		JumpingLeft,
		JumpingRight,
	}
	
	private State state;
	private int frame;
	private int frameTime;
	private Action action;
	private int lastDir;
	private HashMap<Action, ActionParams> actionParams = 
			new HashMap<ActorAnimator.Action, ActorAnimator.ActionParams>();
	
	public int getFrame() {
		return frame;
	}
	
	public int getAction() {
		return action.ordinal();
	}
	
	public boolean isFlipped() {
		return getActionParams(action).flipped;
	}
	
	public static ActorAnimator create(String imagePath) { 
		if (imagePath.contains(Data.ACTOR_5)) {
			return new ActorAnimator5();
		} else if (imagePath.contains(Data.ACTOR_7)) {
			return new ActorAnimator7();
		} else if (imagePath.contains(Data.ACTOR_2)){
			return new ActorAnimator2();
		}
		return null;
	}
	
	public boolean isWalking() {
		return action == Action.WalkingLeft || 
				action == Action.WalkingRight; 
	}
	
	public boolean isJumping() {
		return action == Action.JumpingLeft ||
				action == Action.JumpingRight;
	}
	
	public boolean isDoingAction() {
		return action == Action.ActionLeft ||
				action == Action.ActionRight;
	}
	
	public int getFramesInAction() {
		return getActionParams(action).frames;
	}

	public static class ActionParams {
		public int row, column, frames;
		public boolean flipped;
		public ActionParams(int row, int column, int frames) {
			this.row = row; this.column = column; this.frames = frames;
		}
		public ActionParams(int row, int column, int frames, boolean flipped) {
			this(row, column, frames);
			this.flipped = flipped;
		}
		public ActionParams() {
			this(0, 0, 0);
		}
	}
	
	protected abstract ActionParams createActionParams(Action action);
	protected abstract int getJumpHold();
	public abstract int getTotalRows();
	public abstract int getTotalCols();
	
	protected boolean hasJump() { 
		return getActionParams(Action.JumpingLeft).frames > 0 &&
				getActionParams(Action.JumpingRight).frames > 0;
	}
	
	protected boolean hasClimb() {
		return getActionParams(Action.Climbing).frames > 0;
	}
	
	public ActionParams getActionParams(Action action) {
		return actionParams.get(action);
	}
	
	public int getFrameLength() {
		return isDoingAction() ? 150 : 100;
	}
	
	public ActorAnimator() {
		state = State.Walking;

		for (Action action : Action.values()) {
			actionParams.put(action, createActionParams(action));
		}
	}
	
	private boolean updateFrame(long timeElapsed) {
		frameTime += timeElapsed;
		if (frameTime > getFrameLength()) {
			frameTime -= getFrameLength();
			frame = frame + 1;
			if (frame >= getFramesInAction()) {
				frame = 0;
				return true;
			}
		}
		return false;
	}
	
	private void resetFrame() {
		resetFrame(0);
	}
	
	private void resetFrame(int frame) {
		this.frame = frame;
		this.frameTime = 0;
	}
	
	public void update(long timeElapsed, int dir, boolean grounded, boolean climbing) {
		
		if (dir != 0) {
			lastDir = dir;
		}
		
		if (hasClimb() && climbing) {
			state = State.Climbing;
		}
		
		
		switch(state) {
		case Landing:
			if (dir != 0) {
				state = State.Walking;
				break;
			}
			if (!isJumping()) {
				action = dir > 0 ? Action.JumpingRight :
					Action.JumpingLeft;
			}
			boolean b = true;
			if (frame < getJumpHold()) {
				resetFrame(getJumpHold());
			} else {
				if (updateFrame(timeElapsed)) {
					state = State.Walking;
					resetFrame();
					b = false;
				}
			}
			if (b) break;
		case Walking:
			if (!isWalking()) {
				action = lastDir > 0 ? Action.WalkingRight : 
					Action.WalkingLeft;
			}
			if (dir != 0) {
				Action nAction = lastDir > 0 ? Action.WalkingRight : 
					Action.WalkingLeft;
				if (action != nAction || !grounded) {
					action = nAction;
				} else {
					updateFrame(timeElapsed);
				}
			} else {
				resetFrame();
			}
			break;
		case Jumping:
			if (frame > 0 && grounded) {
				state = State.Landing;
				break;
			}
			if (!isJumping()) {
				action = lastDir > 0 ? Action.JumpingRight :
					Action.JumpingLeft;
			} else {
				if (dir != 0) {
					Action nAction = lastDir > 0 ? Action.JumpingRight :
						Action.JumpingLeft;
					if (action != nAction) {
						action = nAction;
					}	
				}
				if (frame < getJumpHold()) {
					updateFrame(timeElapsed);
				}
			}
			break;
		case Action:
			action = lastDir > 0 ? Action.ActionRight :
				Action.ActionLeft;
			if (updateFrame(timeElapsed)) {
				if (climbing && hasClimb()) {
					state = State.Climbing;
				} else if (!grounded && hasJump()) {
					state = State.Jumping;
				} else {
					state = State.Walking;
				}
				updateFrame(0);
				if (isJumping()) {
					resetFrame(getJumpHold());
				}
			}
			break;
		case Climbing:
			if (climbing) {
				action = Action.Climbing;
				updateFrame(timeElapsed);
			} else {
				state = grounded ? State.Walking : State.Jumping;
				updateFrame(0);
				if (isJumping()) {
					resetFrame(getJumpHold());
				}
				
			}
			break;
		}
	}
	
	public void jump() {
		if (state != State.Jumping) {
			if (hasJump()) {
				state = State.Jumping;
			}
			resetFrame();
		}
	}
	
	public void action() {
		if (state != State.Action) {
			state = State.Action;
			resetFrame();
		}
	}
	
	@Override
	public String toString() {
		return "State: " + state.toString() + 
				"\nAction: " + action.toString() + 
				"\nFrame: " + frame;
	}
}
