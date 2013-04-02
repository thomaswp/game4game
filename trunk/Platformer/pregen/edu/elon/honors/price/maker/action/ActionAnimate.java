package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.maker.action.*;
import edu.elon.honors.price.data.*;
import edu.elon.honors.price.data.types.*;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.Event.Parameters;
import com.twp.platform.*;
import edu.elon.honors.price.physics.*;
import edu.elon.honors.price.input.*;

@SuppressWarnings("unused")
public class ActionAnimate extends ScriptableInstance {
	public static final String NAME = "Animate";
	public static final int ID = 19;
	public static final String CATEGORY = "Animate";
	
	public boolean animateActor;
	public AnimateActorData animateActorData;
	public class AnimateActorData extends ScriptableFragment {
		/** Type: <b>&lt;actorInstance&gt;</b> */
		public Parameters actorInstance;
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {
			return gameState.readActorInstance(actorInstance);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			actorInstance = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean animateObject;
	public AnimateObjectData animateObjectData;
	public class AnimateObjectData extends ScriptableFragment {
		/** Type: <b>&lt;objectInstance&gt;</b> */
		public Parameters objectInstance;
		public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {
			return gameState.readObjectInstance(objectInstance);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			objectInstance = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean withBounce;
	public WithBounceData withBounceData;
	public class WithBounceData extends ScriptableFragment {
		/** Type: <b>&lt;vector&gt;</b> */
		public Parameters direction;
		public Vector readDirection(GameState gameState) throws ParameterException {
			return gameState.readVector(direction);
		}
		/** Type: <b>&lt;number&gt;</b> */
		public Parameters distance;
		public int readDistance(GameState gameState) throws ParameterException {
			return gameState.readNumber(distance);
		}
		/** Type: <b>&lt;number&gt;</b> */
		public Parameters duration;
		public int readDuration(GameState gameState) throws ParameterException {
			return gameState.readNumber(duration);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			direction = iterator.getParameters();
			distance = iterator.getParameters();
			duration = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;vector&gt;</b> direction</li>
		 * <li><b>&lt;number&gt;</b> distance</li>
		 * <li><b>&lt;number&gt;</b> duration</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean withSwirl;
	public boolean thenWaitForTheAnimationToEnd;
	public boolean thenContinueTheEvent;
	
	public ActionAnimate() {
		animateActorData = new AnimateActorData();
		animateObjectData = new AnimateObjectData();
		withBounceData = new WithBounceData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int animate = iterator.getInt();
		animateActor = animate == 0;
		if (animateActor) animateActorData.readParams(iterator);
		animateObject = animate == 1;
		if (animateObject) animateObjectData.readParams(iterator);
		
		int with = iterator.getInt();
		withBounce = with == 0;
		if (withBounce) withBounceData.readParams(iterator);
		withSwirl = with == 1;
		
		int then = iterator.getInt();
		thenWaitForTheAnimationToEnd = then == 0;
		thenContinueTheEvent = then == 1;
		
	}
	/**
	 * 019 <b><i>Animate</i></b> (Animate)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> animate</i>:</li><ul>
	 * <li>animateActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>animateObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> with</i>:</li><ul>
	 * <li>withBounce:</li>
	 * <ul>
	 * <li><b>&lt;vector&gt;</b> direction</li>
	 * <li><b>&lt;number&gt;</b> distance</li>
	 * <li><b>&lt;number&gt;</b> duration</li>
	 * </ul>
	 * <li>withSwirl:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> then</i>:</li><ul>
	 * <li>thenWaitForTheAnimationToEnd:</li>
	 * <li>thenContinueTheEvent:</li>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
