package edu.elon.honors.price.data;

import edu.elon.honors.price.data.ActorAnimator.ActionParams;

public class ActorAnimator2 extends ActorAnimator {

	@Override
	protected ActionParams createActionParams(Action action) {
		switch (action) {
		case Climbing: return new ActionParams();
		case WalkingLeft: return new ActionParams(0, 1, 4);
		case WalkingRight: return new ActionParams(0, 1, 4, true);
		case ActionLeft: return new ActionParams(1, 1, 4);
		case ActionRight: return new ActionParams(1, 1, 4, true);
		case JumpingLeft: return new ActionParams();
		case JumpingRight: return new ActionParams();
		}
		return null;
	}

	@Override
	protected int getJumpHold() {
		return 0;
	}

	@Override
	public int getTotalRows() {
		return 2;
	}

	@Override
	public int getTotalCols() {
		// TODO Auto-generated method stub
		return 5;
	}

}
