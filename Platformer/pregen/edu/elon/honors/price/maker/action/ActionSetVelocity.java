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
public class ActionSetVelocity extends ScriptableInstance {
	public static final String NAME = "Set Velocity";
	public static final int ID = 10;
	public static final String CATEGORY = "Physics";
	
	public boolean setActor;
	public SetActorData setActorData;
	public class SetActorData extends ScriptableFragment {
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
	
	public boolean setObject;
	public SetObjectData setObjectData;
	public class SetObjectData extends ScriptableFragment {
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
	
	/** Type: <b>&lt;vector&gt;</b> */
	public Parameters directionVector;
	public Vector readDirectionVector(GameState gameState) throws ParameterException {
		return gameState.readVector(directionVector);
	}
	/** Type: <b>&lt;number&gt;</b> */
	public Parameters directionMagnitude;
	public int readDirectionMagnitude(GameState gameState) throws ParameterException {
		return gameState.readNumber(directionMagnitude);
	}
	
	public ActionSetVelocity() {
		setActorData = new SetActorData();
		setObjectData = new SetObjectData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int set = iterator.getInt();
		setActor = set == 0;
		if (setActor) setActorData.readParams(iterator);
		setObject = set == 1;
		if (setObject) setObjectData.readParams(iterator);
		
		directionVector = iterator.getParameters();
		directionMagnitude = iterator.getParameters();
	}
	/**
	 * 010 <b><i>Set Velocity</i></b> (Physics)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> set</i>:</li><ul>
	 * <li>setActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>setObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;vector&gt;</b> directionVector</li>
	 * <li><b>&lt;number&gt;</b> directionMagnitude</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
