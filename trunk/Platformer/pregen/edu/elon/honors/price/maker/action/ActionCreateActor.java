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
public class ActionCreateActor extends ScriptableInstance {
	public static final String NAME = "Create Actor";
	public static final int ID = 2;
	public static final String CATEGORY = "Actor";
	
	/** Type: <b>&lt;actorClass&gt;</b> */
	public ActorClassPointer actorClass;
	public ActorClass readActorClass(GameState gameState) throws ParameterException {
		return gameState.readActorClass(actorClass);
	}
	/** Type: <b>&lt;point&gt;</b> */
	public Parameters point;
	public Point readPoint(GameState gameState) throws ParameterException {
		return gameState.readPoint(point);
	}
	public boolean facingLeft;
	public boolean facingRight;
	
	@Override
	public void readParams(Iterator iterator) {
		actorClass = iterator.getActorClassPointer();
		point = iterator.getParameters();
		int facing = iterator.getInt();
		facingLeft = facing == 0;
		facingRight = facing == 1;
		
	}
	/**
	 * 002 <b><i>Create Actor</i></b> (Actor)<br />
	 * <ul>
	 * <li><b>&lt;actorClass&gt;</b> actorClass</li>
	 * <li><b>&lt;point&gt;</b> point</li>
	 * <li><b>&lt;radio&gt;</b> facing</i>:</li><ul>
	 * <li>facingLeft:</li>
	 * <li>facingRight:</li>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
