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
public class ActionMoveActor extends ScriptableInstance {
	public static final String NAME = "Move Actor";
	public static final int ID = 4;
	public static final String CATEGORY = "Actor";
	
	/** Type: <b>&lt;actorInstance&gt;</b> */
	public Parameters actorInstance;
	public ActorBody readActorInstance(GameState gameState) throws ParameterException {
		return gameState.readActorInstance(actorInstance);
	}
	/** Type: <b>&lt;point&gt;</b> */
	public Parameters point;
	public Point readPoint(GameState gameState) throws ParameterException {
		return gameState.readPoint(point);
	}
	public boolean facingLeft;
	public boolean facingRight;
	public boolean facingTheSameDirection;
	
	@Override
	public void readParams(Iterator iterator) {
		actorInstance = iterator.getParameters();
		point = iterator.getParameters();
		int facing = iterator.getInt();
		facingLeft = facing == 0;
		facingRight = facing == 1;
		facingTheSameDirection = facing == 2;
		
	}
	/**
	 * 004 <b><i>Move Actor</i></b> (Actor)<br />
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * <li><b>&lt;point&gt;</b> point</li>
	 * <li><b>&lt;radio&gt;</b> facing</i>:</li><ul>
	 * <li>facingLeft:</li>
	 * <li>facingRight:</li>
	 * <li>facingTheSameDirection:</li>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
