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
public class ActionTriggerActorAnimation extends ScriptableInstance {
	public static final String NAME = "Trigger Actor Animation";
	public static final int ID = 18;
	public static final String CATEGORY = "Animate";
	
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
	 * 018 <b><i>Trigger Actor Animation</i></b> (Animate)<br />
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
