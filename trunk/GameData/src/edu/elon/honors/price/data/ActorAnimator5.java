package edu.elon.honors.price.data;

public class ActorAnimator5 extends ActorAnimator {
	protected ActionParams createActionParams(Action action) {
		switch (action) {
		case Climbing: return new ActionParams(0, 4, 4);
		case WalkingLeft: return new ActionParams(1, 0, 8);
		case WalkingRight: return new ActionParams(2, 0, 8);
		case ActionLeft: return new ActionParams(3, 0, 4);
		case ActionRight: return new ActionParams(3, 4, 4);
		case JumpingLeft: return new ActionParams(4, 0, 4);
		case JumpingRight: return new ActionParams(4, 4, 4);
		}
		return null;
	}

	@Override
	public int getTotalRows() {
		return 5;
	}

	@Override
	public int getTotalCols() {
		return 8;
	}

	@Override
	protected int getJumpHold() {
		return 2;
	}
}
