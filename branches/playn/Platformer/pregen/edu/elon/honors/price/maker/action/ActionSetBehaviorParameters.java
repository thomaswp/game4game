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
public class ActionSetBehaviorParameters extends ScriptableInstance {
	public static final String NAME = "Set Behavior Parameters";
	public static final int ID = 25;
	public static final String CATEGORY = "Actor|Object";
	
	public boolean ofTheActor;
	public OfTheActorData ofTheActorData;
	public class OfTheActorData extends ScriptableFragment {
		/** Type: <b>&lt;actorInstance&gt;</b> */
		public Parameters actorInstance;
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {
			return gameState.readActorInstance(actorInstance);
		}
		/** Type: <b>&lt;behavior&gt;</b> */
		public Parameters behavior;
		
		@Override
		public void readParams(Iterator iterator) {
			actorInstance = iterator.getParameters();
			behavior = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * <li><b>&lt;behavior&gt;</b> behavior</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean ofTheObject;
	public OfTheObjectData ofTheObjectData;
	public class OfTheObjectData extends ScriptableFragment {
		/** Type: <b>&lt;objectInstance&gt;</b> */
		public Parameters objectInstance;
		public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {
			return gameState.readObjectInstance(objectInstance);
		}
		/** Type: <b>&lt;behavior&gt;</b> */
		public Parameters behavior;
		
		@Override
		public void readParams(Iterator iterator) {
			objectInstance = iterator.getParameters();
			behavior = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
		 * <li><b>&lt;behavior&gt;</b> behavior</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	
	public ActionSetBehaviorParameters() {
		ofTheActorData = new OfTheActorData();
		ofTheObjectData = new OfTheObjectData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int of = iterator.getInt();
		ofTheActor = of == 0;
		if (ofTheActor) ofTheActorData.readParams(iterator);
		ofTheObject = of == 1;
		if (ofTheObject) ofTheObjectData.readParams(iterator);
		
	}
	/**
	 * 025 <b><i>Set Behavior Parameters</i></b> (Actor|Object)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> of</i>:</li><ul>
	 * <li>ofTheActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * <li><b>&lt;behavior&gt;</b> behavior</li>
	 * </ul>
	 * <li>ofTheObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * <li><b>&lt;behavior&gt;</b> behavior</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
