package edu.elon.honors.price.maker.action;

import com.twp.platform.BodyAnimation;
import com.twp.platform.BodyAnimationBounce;
import com.twp.platform.PlatformBody;

import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.maker.action.ActionAnimate.WithBounceData;
import edu.elon.honors.price.physics.Vector;

public class InterpreterAnimate extends ActionInterpreter<ActionAnimate> {

	@Override
	protected void interperate(ActionAnimate action, PlatformGameState gameState)
			throws ParameterException {

		PlatformBody body;
		if (action.animateActor) {
			body = action.animateActorData.readActorInstance(gameState);
		} else if (action.animateObject) {
			body = action.animateObjectData.readObjectInstance(gameState);
		} else {
			throw new UnsupportedException();
		}
		
		final BodyAnimation animation;
		if (action.withBounce) {
			WithBounceData data = action.withBounceData;
			int duration = data.readDuration(gameState) * 100;
			int distance = data.readDistance(gameState);
			Vector vector = data.readDirection(gameState);
			animation = new BodyAnimationBounce(
					duration, vector, distance);
			body.animate(animation, false);
		} else if (action.withSwirl) {
			animation = null;
		} else {
			throw new UnsupportedException();
		}
		
		if (action.thenWaitForTheAnimationToEnd) {
			waitChecker = new WaitChecker() {
				@Override
				public boolean isWaiting(PlatformGameState gameState) {
					return animation == null || !animation.isFinished();
				}
			};
		}
	}

}
