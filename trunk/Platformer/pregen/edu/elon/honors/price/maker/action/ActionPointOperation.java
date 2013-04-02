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
public class ActionPointOperation extends ScriptableInstance {
	public static final String NAME = "Point Operation";
	public static final int ID = 12;
	public static final String CATEGORY = "Variables";
	
	/** Type: <b>&lt;variablePoint&gt;</b> */
	public Parameters point;
	public boolean operatorSetItTo;
	public boolean operatorAdd;
	public boolean operatorSubtract;
	public boolean withPoint;
	public WithPointData withPointData;
	public class WithPointData extends ScriptableFragment {
		/** Type: <b>&lt;point&gt;</b> */
		public Parameters point;
		public Point readPoint(GameState gameState) throws ParameterException {
			return gameState.readPoint(point);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			point = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;point&gt;</b> point</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean withVector;
	public WithVectorData withVectorData;
	public class WithVectorData extends ScriptableFragment {
		/** Type: <b>&lt;vector&gt;</b> */
		public Parameters vector;
		public Vector readVector(GameState gameState) throws ParameterException {
			return gameState.readVector(vector);
		}
		/** Type: <b>&lt;number&gt;</b> */
		public Parameters magnitude;
		public int readMagnitude(GameState gameState) throws ParameterException {
			return gameState.readNumber(magnitude);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			vector = iterator.getParameters();
			magnitude = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;vector&gt;</b> vector</li>
		 * <li><b>&lt;number&gt;</b> magnitude</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean withActorLocation;
	public WithActorLocationData withActorLocationData;
	public class WithActorLocationData extends ScriptableFragment {
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
	
	public boolean withObjectLocation;
	public WithObjectLocationData withObjectLocationData;
	public class WithObjectLocationData extends ScriptableFragment {
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
	
	
	public ActionPointOperation() {
		withPointData = new WithPointData();
		withVectorData = new WithVectorData();
		withActorLocationData = new WithActorLocationData();
		withObjectLocationData = new WithObjectLocationData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		point = iterator.getParameters();
		int operator = iterator.getInt();
		operatorSetItTo = operator == 0;
		operatorAdd = operator == 1;
		operatorSubtract = operator == 2;
		
		int with = iterator.getInt();
		withPoint = with == 0;
		if (withPoint) withPointData.readParams(iterator);
		withVector = with == 1;
		if (withVector) withVectorData.readParams(iterator);
		withActorLocation = with == 2;
		if (withActorLocation) withActorLocationData.readParams(iterator);
		withObjectLocation = with == 3;
		if (withObjectLocation) withObjectLocationData.readParams(iterator);
		
	}
	/**
	 * 012 <b><i>Point Operation</i></b> (Variables)<br />
	 * <ul>
	 * <li><b>&lt;variablePoint&gt;</b> point</li>
	 * <li><b>&lt;radio&gt;</b> operator</i>:</li><ul>
	 * <li>operatorSetItTo:</li>
	 * <li>operatorAdd:</li>
	 * <li>operatorSubtract:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> with</i>:</li><ul>
	 * <li>withPoint:</li>
	 * <ul>
	 * <li><b>&lt;point&gt;</b> point</li>
	 * </ul>
	 * <li>withVector:</li>
	 * <ul>
	 * <li><b>&lt;vector&gt;</b> vector</li>
	 * <li><b>&lt;number&gt;</b> magnitude</li>
	 * </ul>
	 * <li>withActorLocation:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>withObjectLocation:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
