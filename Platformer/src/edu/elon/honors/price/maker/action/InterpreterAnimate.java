package edu.elon.honors.price.maker.action;

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
		
		if (action.withBounce) {
			WithBounceData data = action.withBounceData;
			int duration = data.readDuration(gameState) * 100;
			int distance = data.readDistance(gameState);
			Vector vector = data.readDirection(gameState);
			BodyAnimationBounce bounce = new BodyAnimationBounce(
					duration, vector, distance);
			body.animate(bounce, false);
		} else if (action.withSwirl) {
			
		} else {
			throw new UnsupportedException();
		}
		
		if (action.thenWaitForTheAnimationToEnd) {
			
		} else if (action.thenContinueTheEvent) {
			
		} else {
			throw new UnsupportedException();
		}
	}

}
