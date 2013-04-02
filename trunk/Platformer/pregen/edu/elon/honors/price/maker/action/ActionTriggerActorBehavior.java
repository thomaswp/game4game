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
public class ActionTriggerActorBehavior extends ScriptableInstance {
	public static final String NAME = "Trigger Actor Behavior";
	public static final int ID = 5;
	public static final String CATEGORY = "Actor";
	
	/** Type: <b>&lt;actorInstance&gt;</b> */
	public Parameters actorInstance;
	public ActorBody readActorInstance(GameState gameState) throws ParameterException {
		return gameState.readActorInstance(actorInstance);
	}
	/** Type: <b>&lt;actorBehavior&gt;</b> */
	public int actorBehavior;
	
	@Override
	public void readParams(Iterator iterator) {
		actorInstance = iterator.getParameters();
		actorBehavior = iterator.getInt();
	}
	/**
	 * 005 <b><i>Trigger Actor Behavior</i></b> (Actor)<br />
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * <li><b>&lt;actorBehavior&gt;</b> actorBehavior</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
