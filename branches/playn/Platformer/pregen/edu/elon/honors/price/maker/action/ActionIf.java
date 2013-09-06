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
public class ActionIf extends ScriptableInstance {
	public static final String NAME = "If...";
	public static final int ID = 7;
	public static final String CATEGORY = "Control";
	
	public boolean checkIfTheSwitch;
	public CheckIfTheSwitchData checkIfTheSwitchData;
	public class CheckIfTheSwitchData extends ScriptableFragment {
		/** Type: <b>&lt;switch&gt;</b> */
		public Switch aSwitch;
		public boolean readASwitch(GameState gameState) throws ParameterException {
			return gameState.readSwitch(aSwitch);
		}
		public boolean operatorEquals;
		public boolean operatorDoesNotEqual;
		public boolean withOn;
		public boolean withOff;
		public boolean withTheSwitch;
		public WithTheSwitchData withTheSwitchData;
		public class WithTheSwitchData extends ScriptableFragment {
			/** Type: <b>&lt;switch&gt;</b> */
			public Switch aSwitch;
			public boolean readASwitch(GameState gameState) throws ParameterException {
				return gameState.readSwitch(aSwitch);
			}
			
			@Override
			public void readParams(Iterator iterator) {
				aSwitch = iterator.getSwitch();
			}
			/**
			 * <ul>
			 * <li><b>&lt;switch&gt;</b> aSwitch</li>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		
		public CheckIfTheSwitchData() {
			withTheSwitchData = new WithTheSwitchData();
		}
		
		@Override
		public void readParams(Iterator iterator) {
			aSwitch = iterator.getSwitch();
			int operator = iterator.getInt();
			operatorEquals = operator == 0;
			operatorDoesNotEqual = operator == 1;
			
			int with = iterator.getInt();
			withOn = with == 0;
			withOff = with == 1;
			withTheSwitch = with == 2;
			if (withTheSwitch) withTheSwitchData.readParams(iterator);
			
		}
		/**
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> aSwitch</li>
		 * <li><b>&lt;radio&gt;</b> operator</i>:</li><ul>
		 * <li>operatorEquals:</li>
		 * <li>operatorDoesNotEqual:</li>
		 * </ul>
		 * <li><b>&lt;radio&gt;</b> with</i>:</li><ul>
		 * <li>withOn:</li>
		 * <li>withOff:</li>
		 * <li>withTheSwitch:</li>
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> aSwitch</li>
		 * </ul>
		 * </ul>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean checkIfTheVariable;
	public CheckIfTheVariableData checkIfTheVariableData;
	public class CheckIfTheVariableData extends ScriptableFragment {
		/** Type: <b>&lt;variable&gt;</b> */
		public Variable variable;
		public int readVariable(GameState gameState) throws ParameterException {
			return gameState.readVariable(variable);
		}
		public boolean operatorEquals;
		public boolean operatorNotEquals;
		public boolean operatorGreater;
		public boolean operatorGreaterOrEqual;
		public boolean operatorLess;
		public boolean operatorLessOrEqual;
		/** Type: <b>&lt;number&gt;</b> */
		public Parameters number;
		public int readNumber(GameState gameState) throws ParameterException {
			return gameState.readNumber(number);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			variable = iterator.getVariable();
			int operator = iterator.getInt();
			operatorEquals = operator == 0;
			operatorNotEquals = operator == 1;
			operatorGreater = operator == 2;
			operatorGreaterOrEqual = operator == 3;
			operatorLess = operator == 4;
			operatorLessOrEqual = operator == 5;
			
			number = iterator.getParameters();
		}
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> variable</li>
		 * <li><b>&lt;radio&gt;</b> operator</i>:</li><ul>
		 * <li>operatorEquals:</li>
		 * <li>operatorNotEquals:</li>
		 * <li>operatorGreater:</li>
		 * <li>operatorGreaterOrEqual:</li>
		 * <li>operatorLess:</li>
		 * <li>operatorLessOrEqual:</li>
		 * </ul>
		 * <li><b>&lt;number&gt;</b> number</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean checkIfTheActorObject;
	public CheckIfTheActorObjectData checkIfTheActorObjectData;
	public class CheckIfTheActorObjectData extends ScriptableFragment {
		public boolean bodyTheActor;
		public BodyTheActorData bodyTheActorData;
		public class BodyTheActorData extends ScriptableFragment {
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
		
		public boolean bodyTheObject;
		public BodyTheObjectData bodyTheObjectData;
		public class BodyTheObjectData extends ScriptableFragment {
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
		
		public boolean checkProperty;
		public CheckPropertyData checkPropertyData;
		public class CheckPropertyData extends ScriptableFragment {
			public boolean propertyIsAlive;
			public boolean propertyIsDead;
			
			@Override
			public void readParams(Iterator iterator) {
				int property = iterator.getInt();
				propertyIsAlive = property == 0;
				propertyIsDead = property == 1;
				
			}
			/**
			 * <ul>
			 * <li><b>&lt;radio&gt;</b> property</i>:</li><ul>
			 * <li>propertyIsAlive:</li>
			 * <li>propertyIsDead:</li>
			 * </ul>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		public boolean checkRegion;
		public CheckRegionData checkRegionData;
		public class CheckRegionData extends ScriptableFragment {
			public boolean checkIsInside;
			public boolean checkIsTouching;
			public boolean checkIsOutside;
			/** Type: <b>&lt;region&gt;</b> */
			public Parameters region;
			public android.graphics.Rect readRegion(GameState gameState) throws ParameterException {
				return gameState.readRegion(region);
			}
			
			@Override
			public void readParams(Iterator iterator) {
				int check = iterator.getInt();
				checkIsInside = check == 0;
				checkIsTouching = check == 1;
				checkIsOutside = check == 2;
				
				region = iterator.getParameters();
			}
			/**
			 * <ul>
			 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
			 * <li>checkIsInside:</li>
			 * <li>checkIsTouching:</li>
			 * <li>checkIsOutside:</li>
			 * </ul>
			 * <li><b>&lt;region&gt;</b> region</li>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		public boolean checkPosition;
		public CheckPositionData checkPositionData;
		public class CheckPositionData extends ScriptableFragment {
			public boolean directionIsAbove;
			public boolean directionIsBelow;
			public boolean directionIsLeftOf;
			public boolean directionIsRightOf;
			public boolean ofTheActor;
			public OfTheActorData ofTheActorData;
			public class OfTheActorData extends ScriptableFragment {
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
			
			public boolean ofTheObject;
			public OfTheObjectData ofTheObjectData;
			public class OfTheObjectData extends ScriptableFragment {
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
			
			
			public CheckPositionData() {
				ofTheActorData = new OfTheActorData();
				ofTheObjectData = new OfTheObjectData();
			}
			
			@Override
			public void readParams(Iterator iterator) {
				int direction = iterator.getInt();
				directionIsAbove = direction == 0;
				directionIsBelow = direction == 1;
				directionIsLeftOf = direction == 2;
				directionIsRightOf = direction == 3;
				
				int of = iterator.getInt();
				ofTheActor = of == 0;
				if (ofTheActor) ofTheActorData.readParams(iterator);
				ofTheObject = of == 1;
				if (ofTheObject) ofTheObjectData.readParams(iterator);
				
			}
			/**
			 * <ul>
			 * <li><b>&lt;radio&gt;</b> direction</i>:</li><ul>
			 * <li>directionIsAbove:</li>
			 * <li>directionIsBelow:</li>
			 * <li>directionIsLeftOf:</li>
			 * <li>directionIsRightOf:</li>
			 * </ul>
			 * <li><b>&lt;radio&gt;</b> of</i>:</li><ul>
			 * <li>ofTheActor:</li>
			 * <ul>
			 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
			 * </ul>
			 * <li>ofTheObject:</li>
			 * <ul>
			 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
			 * </ul>
			 * </ul>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		
		public CheckIfTheActorObjectData() {
			bodyTheActorData = new BodyTheActorData();
			bodyTheObjectData = new BodyTheObjectData();
			checkPropertyData = new CheckPropertyData();
			checkRegionData = new CheckRegionData();
			checkPositionData = new CheckPositionData();
		}
		
		@Override
		public void readParams(Iterator iterator) {
			int body = iterator.getInt();
			bodyTheActor = body == 0;
			if (bodyTheActor) bodyTheActorData.readParams(iterator);
			bodyTheObject = body == 1;
			if (bodyTheObject) bodyTheObjectData.readParams(iterator);
			
			int check = iterator.getInt();
			checkProperty = check == 0;
			if (checkProperty) checkPropertyData.readParams(iterator);
			checkRegion = check == 1;
			if (checkRegion) checkRegionData.readParams(iterator);
			checkPosition = check == 2;
			if (checkPosition) checkPositionData.readParams(iterator);
			
		}
		/**
		 * <ul>
		 * <li><b>&lt;radio&gt;</b> body</i>:</li><ul>
		 * <li>bodyTheActor:</li>
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * </ul>
		 * <li>bodyTheObject:</li>
		 * <ul>
		 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
		 * </ul>
		 * </ul>
		 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
		 * <li>checkProperty:</li>
		 * <ul>
		 * <li><b>&lt;radio&gt;</b> property</i>:</li><ul>
		 * <li>propertyIsAlive:</li>
		 * <li>propertyIsDead:</li>
		 * </ul>
		 * </ul>
		 * <li>checkRegion:</li>
		 * <ul>
		 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
		 * <li>checkIsInside:</li>
		 * <li>checkIsTouching:</li>
		 * <li>checkIsOutside:</li>
		 * </ul>
		 * <li><b>&lt;region&gt;</b> region</li>
		 * </ul>
		 * <li>checkPosition:</li>
		 * <ul>
		 * <li><b>&lt;radio&gt;</b> direction</i>:</li><ul>
		 * <li>directionIsAbove:</li>
		 * <li>directionIsBelow:</li>
		 * <li>directionIsLeftOf:</li>
		 * <li>directionIsRightOf:</li>
		 * </ul>
		 * <li><b>&lt;radio&gt;</b> of</i>:</li><ul>
		 * <li>ofTheActor:</li>
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * </ul>
		 * <li>ofTheObject:</li>
		 * <ul>
		 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
		 * </ul>
		 * </ul>
		 * </ul>
		 * </ul>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean checkIfTheActor;
	public CheckIfTheActorData checkIfTheActorData;
	public class CheckIfTheActorData extends ScriptableFragment {
		/** Type: <b>&lt;actorInstance&gt;</b> */
		public Parameters actorInstance;
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {
			return gameState.readActorInstance(actorInstance);
		}
		public boolean checkType;
		public CheckTypeData checkTypeData;
		public class CheckTypeData extends ScriptableFragment {
			public boolean compareIs;
			public boolean compareIsNot;
			/** Type: <b>&lt;actorClass&gt;</b> */
			public ActorClassPointer actorClass;
			public ActorClass readActorClass(GameState gameState) throws ParameterException {
				return gameState.readActorClass(actorClass);
			}
			
			@Override
			public void readParams(Iterator iterator) {
				int compare = iterator.getInt();
				compareIs = compare == 0;
				compareIsNot = compare == 1;
				
				actorClass = iterator.getActorClassPointer();
			}
			/**
			 * <ul>
			 * <li><b>&lt;radio&gt;</b> compare</i>:</li><ul>
			 * <li>compareIs:</li>
			 * <li>compareIsNot:</li>
			 * </ul>
			 * <li><b>&lt;actorClass&gt;</b> actorClass</li>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		
		public CheckIfTheActorData() {
			checkTypeData = new CheckTypeData();
		}
		
		@Override
		public void readParams(Iterator iterator) {
			actorInstance = iterator.getParameters();
			int check = iterator.getInt();
			checkType = check == 0;
			if (checkType) checkTypeData.readParams(iterator);
			
		}
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
		 * <li>checkType:</li>
		 * <ul>
		 * <li><b>&lt;radio&gt;</b> compare</i>:</li><ul>
		 * <li>compareIs:</li>
		 * <li>compareIsNot:</li>
		 * </ul>
		 * <li><b>&lt;actorClass&gt;</b> actorClass</li>
		 * </ul>
		 * </ul>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	
	public ActionIf() {
		checkIfTheSwitchData = new CheckIfTheSwitchData();
		checkIfTheVariableData = new CheckIfTheVariableData();
		checkIfTheActorObjectData = new CheckIfTheActorObjectData();
		checkIfTheActorData = new CheckIfTheActorData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int checkIf = iterator.getInt();
		checkIfTheSwitch = checkIf == 0;
		if (checkIfTheSwitch) checkIfTheSwitchData.readParams(iterator);
		checkIfTheVariable = checkIf == 1;
		if (checkIfTheVariable) checkIfTheVariableData.readParams(iterator);
		checkIfTheActorObject = checkIf == 2;
		if (checkIfTheActorObject) checkIfTheActorObjectData.readParams(iterator);
		checkIfTheActor = checkIf == 3;
		if (checkIfTheActor) checkIfTheActorData.readParams(iterator);
		
	}
	/**
	 * 007 <b><i>If...</i></b> (Control)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> checkIf</i>:</li><ul>
	 * <li>checkIfTheSwitch:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * <li><b>&lt;radio&gt;</b> operator</i>:</li><ul>
	 * <li>operatorEquals:</li>
	 * <li>operatorDoesNotEqual:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> with</i>:</li><ul>
	 * <li>withOn:</li>
	 * <li>withOff:</li>
	 * <li>withTheSwitch:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>checkIfTheVariable:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * <li><b>&lt;radio&gt;</b> operator</i>:</li><ul>
	 * <li>operatorEquals:</li>
	 * <li>operatorNotEquals:</li>
	 * <li>operatorGreater:</li>
	 * <li>operatorGreaterOrEqual:</li>
	 * <li>operatorLess:</li>
	 * <li>operatorLessOrEqual:</li>
	 * </ul>
	 * <li><b>&lt;number&gt;</b> number</li>
	 * </ul>
	 * <li>checkIfTheActorObject:</li>
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> body</i>:</li><ul>
	 * <li>bodyTheActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>bodyTheObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
	 * <li>checkProperty:</li>
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> property</i>:</li><ul>
	 * <li>propertyIsAlive:</li>
	 * <li>propertyIsDead:</li>
	 * </ul>
	 * </ul>
	 * <li>checkRegion:</li>
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
	 * <li>checkIsInside:</li>
	 * <li>checkIsTouching:</li>
	 * <li>checkIsOutside:</li>
	 * </ul>
	 * <li><b>&lt;region&gt;</b> region</li>
	 * </ul>
	 * <li>checkPosition:</li>
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> direction</i>:</li><ul>
	 * <li>directionIsAbove:</li>
	 * <li>directionIsBelow:</li>
	 * <li>directionIsLeftOf:</li>
	 * <li>directionIsRightOf:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> of</i>:</li><ul>
	 * <li>ofTheActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>ofTheObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * <li>checkIfTheActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
	 * <li>checkType:</li>
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> compare</i>:</li><ul>
	 * <li>compareIs:</li>
	 * <li>compareIsNot:</li>
	 * </ul>
	 * <li><b>&lt;actorClass&gt;</b> actorClass</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
