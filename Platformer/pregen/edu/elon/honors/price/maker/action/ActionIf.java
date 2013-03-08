package edu.elon.honors.price.maker.action;							// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.*;								// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ScriptableWriter.writeHeader()
import com.twp.platform.*;											// ScriptableWriter.writeHeader()
import edu.elon.honors.price.physics.*;								// ScriptableWriter.writeHeader()
import edu.elon.honors.price.input.*;								// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
@SuppressWarnings("unused")											// ScriptableWriter.writeHeader()
public class ActionIf extends ScriptableInstance {					// ActionFragmentWriter.writeHeader()
	public static final String NAME = "If...";						// ScriptableWriter.writeHeader()
	public static final int ID = 7;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Control";				// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean checkIfTheSwitch;								// ActionFragmentWriter.writeElement()
	public CheckIfTheSwitchData checkIfTheSwitchData;				// ActionFragmentWriter.writeElement()
	public class CheckIfTheSwitchData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;switch&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Switch aSwitch;										// ActionFragmentWriter.writeElement()
		public boolean readASwitch(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readSwitch(aSwitch);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		public boolean operatorEquals;								// ActionFragmentWriter.writeElement()
		public boolean operatorDoesNotEqual;						// ActionFragmentWriter.writeElement()
		public boolean withOn;										// ActionFragmentWriter.writeElement()
		public boolean withOff;										// ActionFragmentWriter.writeElement()
		public boolean withTheSwitch;								// ActionFragmentWriter.writeElement()
		public WithTheSwitchData withTheSwitchData;					// ActionFragmentWriter.writeElement()
		public class WithTheSwitchData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;switch&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Switch aSwitch;									// ActionFragmentWriter.writeElement()
			public boolean readASwitch(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readSwitch(aSwitch);				// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				aSwitch = iterator.getSwitch();						// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;switch&gt;</b> aSwitch</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
		public CheckIfTheSwitchData() {								// ActionFragmentWriter.writeConstructor()
			withTheSwitchData = new WithTheSwitchData();			// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			aSwitch = iterator.getSwitch();							// ActionFragmentWriter.writeReadParams()
			int operator = iterator.getInt();						// ActionFragmentWriter.writeReadParams()
			operatorEquals = operator == 0;							// ActionFragmentWriter.writeReadParams()
			operatorDoesNotEqual = operator == 1;					// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
			int with = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			withOn = with == 0;										// ActionFragmentWriter.writeReadParams()
			withOff = with == 1;									// ActionFragmentWriter.writeReadParams()
			withTheSwitch = with == 2;								// ActionFragmentWriter.writeReadParams()
			if (withTheSwitch) withTheSwitchData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
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
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean checkIfTheVariable;								// ActionFragmentWriter.writeElement()
	public CheckIfTheVariableData checkIfTheVariableData;			// ActionFragmentWriter.writeElement()
	public class CheckIfTheVariableData extends ScriptableFragment {// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;variable&gt;</b> */						// ActionFragmentWriter.writeElement()
		public Variable variable;									// ActionFragmentWriter.writeElement()
		public int readVariable(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readVariable(variable);				// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		public boolean operatorEquals;								// ActionFragmentWriter.writeElement()
		public boolean operatorNotEquals;							// ActionFragmentWriter.writeElement()
		public boolean operatorGreater;								// ActionFragmentWriter.writeElement()
		public boolean operatorGreaterOrEqual;						// ActionFragmentWriter.writeElement()
		public boolean operatorLess;								// ActionFragmentWriter.writeElement()
		public boolean operatorLessOrEqual;							// ActionFragmentWriter.writeElement()
		/** Type: <b>&lt;number&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters number;									// ActionFragmentWriter.writeElement()
		public int readNumber(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readNumber(number);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			variable = iterator.getVariable();						// ActionFragmentWriter.writeReadParams()
			int operator = iterator.getInt();						// ActionFragmentWriter.writeReadParams()
			operatorEquals = operator == 0;							// ActionFragmentWriter.writeReadParams()
			operatorNotEquals = operator == 1;						// ActionFragmentWriter.writeReadParams()
			operatorGreater = operator == 2;						// ActionFragmentWriter.writeReadParams()
			operatorGreaterOrEqual = operator == 3;					// ActionFragmentWriter.writeReadParams()
			operatorLess = operator == 4;							// ActionFragmentWriter.writeReadParams()
			operatorLessOrEqual = operator == 5;					// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
			number = iterator.getParameters();						// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
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
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean checkIfTheActorObject;							// ActionFragmentWriter.writeElement()
	public CheckIfTheActorObjectData checkIfTheActorObjectData;		// ActionFragmentWriter.writeElement()
	public class CheckIfTheActorObjectData extends ScriptableFragment {// ActionFragmentWriter.writeHeader()
		public boolean bodyTheActor;								// ActionFragmentWriter.writeElement()
		public BodyTheActorData bodyTheActorData;					// ActionFragmentWriter.writeElement()
		public class BodyTheActorData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;actorInstance&gt;</b> */				// ActionFragmentWriter.writeElement()
			public Parameters actorInstance;						// ActionFragmentWriter.writeElement()
			public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readActorInstance(actorInstance);	// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				actorInstance = iterator.getParameters();			// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean bodyTheObject;								// ActionFragmentWriter.writeElement()
		public BodyTheObjectData bodyTheObjectData;					// ActionFragmentWriter.writeElement()
		public class BodyTheObjectData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;objectInstance&gt;</b> */				// ActionFragmentWriter.writeElement()
			public Parameters objectInstance;						// ActionFragmentWriter.writeElement()
			public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readObjectInstance(objectInstance);// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				objectInstance = iterator.getParameters();			// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean checkProperty;								// ActionFragmentWriter.writeElement()
		public CheckPropertyData checkPropertyData;					// ActionFragmentWriter.writeElement()
		public class CheckPropertyData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
			public boolean propertyIsAlive;							// ActionFragmentWriter.writeElement()
			public boolean propertyIsDead;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				int property = iterator.getInt();					// ActionFragmentWriter.writeReadParams()
				propertyIsAlive = property == 0;					// ActionFragmentWriter.writeReadParams()
				propertyIsDead = property == 1;						// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;radio&gt;</b> property</i>:</li><ul>
			 * <li>propertyIsAlive:</li>
			 * <li>propertyIsDead:</li>
			 * </ul>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean checkRegion;									// ActionFragmentWriter.writeElement()
		public CheckRegionData checkRegionData;						// ActionFragmentWriter.writeElement()
		public class CheckRegionData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
			public boolean checkIsInside;							// ActionFragmentWriter.writeElement()
			public boolean checkIsTouching;							// ActionFragmentWriter.writeElement()
			public boolean checkIsOutside;							// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;region&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters region;								// ActionFragmentWriter.writeElement()
			public android.graphics.Rect readRegion(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readRegion(region);				// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				int check = iterator.getInt();						// ActionFragmentWriter.writeReadParams()
				checkIsInside = check == 0;							// ActionFragmentWriter.writeReadParams()
				checkIsTouching = check == 1;						// ActionFragmentWriter.writeReadParams()
				checkIsOutside = check == 2;						// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
				region = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;radio&gt;</b> check</i>:</li><ul>
			 * <li>checkIsInside:</li>
			 * <li>checkIsTouching:</li>
			 * <li>checkIsOutside:</li>
			 * </ul>
			 * <li><b>&lt;region&gt;</b> region</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean checkPosition;								// ActionFragmentWriter.writeElement()
		public CheckPositionData checkPositionData;					// ActionFragmentWriter.writeElement()
		public class CheckPositionData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
			public boolean directionIsAbove;						// ActionFragmentWriter.writeElement()
			public boolean directionIsBelow;						// ActionFragmentWriter.writeElement()
			public boolean directionIsLeftOf;						// ActionFragmentWriter.writeElement()
			public boolean directionIsRightOf;						// ActionFragmentWriter.writeElement()
			public boolean ofTheActor;								// ActionFragmentWriter.writeElement()
			public OfTheActorData ofTheActorData;					// ActionFragmentWriter.writeElement()
			public class OfTheActorData extends ScriptableFragment {// ActionFragmentWriter.writeHeader()
				/** Type: <b>&lt;actorInstance&gt;</b> */			// ActionFragmentWriter.writeElement()
				public Parameters actorInstance;					// ActionFragmentWriter.writeElement()
				public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
					return gameState.readActorInstance(actorInstance);// ActionFragmentWriter.writeElement()
				}													// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
				@Override											// ActionFragmentWriter.writeReadParams()
				public void readParams(Iterator iterator) {			// ActionFragmentWriter.writeReadParams()
					actorInstance = iterator.getParameters();		// ActionFragmentWriter.writeReadParams()
				}													// ActionFragmentWriter.writeReadParams()
				/**
				 * <ul>
				 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
				 * </ul>
				 */													// ActionFragmentWriter.writeJavadoc()
				public static final String JAVADOC = "";			// ActionFragmentWriter.writeJavadoc()
			}														// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
			public boolean ofTheObject;								// ActionFragmentWriter.writeElement()
			public OfTheObjectData ofTheObjectData;					// ActionFragmentWriter.writeElement()
			public class OfTheObjectData extends ScriptableFragment {// ActionFragmentWriter.writeHeader()
				/** Type: <b>&lt;objectInstance&gt;</b> */			// ActionFragmentWriter.writeElement()
				public Parameters objectInstance;					// ActionFragmentWriter.writeElement()
				public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
					return gameState.readObjectInstance(objectInstance);// ActionFragmentWriter.writeElement()
				}													// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
				@Override											// ActionFragmentWriter.writeReadParams()
				public void readParams(Iterator iterator) {			// ActionFragmentWriter.writeReadParams()
					objectInstance = iterator.getParameters();		// ActionFragmentWriter.writeReadParams()
				}													// ActionFragmentWriter.writeReadParams()
				/**
				 * <ul>
				 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
				 * </ul>
				 */													// ActionFragmentWriter.writeJavadoc()
				public static final String JAVADOC = "";			// ActionFragmentWriter.writeJavadoc()
			}														// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
			public CheckPositionData() {							// ActionFragmentWriter.writeConstructor()
				ofTheActorData = new OfTheActorData();				// ActionFragmentWriter.writeConstructor()
				ofTheObjectData = new OfTheObjectData();			// ActionFragmentWriter.writeConstructor()
			}														// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				int direction = iterator.getInt();					// ActionFragmentWriter.writeReadParams()
				directionIsAbove = direction == 0;					// ActionFragmentWriter.writeReadParams()
				directionIsBelow = direction == 1;					// ActionFragmentWriter.writeReadParams()
				directionIsLeftOf = direction == 2;					// ActionFragmentWriter.writeReadParams()
				directionIsRightOf = direction == 3;				// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
				int of = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
				ofTheActor = of == 0;								// ActionFragmentWriter.writeReadParams()
				if (ofTheActor) ofTheActorData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
				ofTheObject = of == 1;								// ActionFragmentWriter.writeReadParams()
				if (ofTheObject) ofTheObjectData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
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
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
		public CheckIfTheActorObjectData() {						// ActionFragmentWriter.writeConstructor()
			bodyTheActorData = new BodyTheActorData();				// ActionFragmentWriter.writeConstructor()
			bodyTheObjectData = new BodyTheObjectData();			// ActionFragmentWriter.writeConstructor()
			checkPropertyData = new CheckPropertyData();			// ActionFragmentWriter.writeConstructor()
			checkRegionData = new CheckRegionData();				// ActionFragmentWriter.writeConstructor()
			checkPositionData = new CheckPositionData();			// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			int body = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			bodyTheActor = body == 0;								// ActionFragmentWriter.writeReadParams()
			if (bodyTheActor) bodyTheActorData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
			bodyTheObject = body == 1;								// ActionFragmentWriter.writeReadParams()
			if (bodyTheObject) bodyTheObjectData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
			int check = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			checkProperty = check == 0;								// ActionFragmentWriter.writeReadParams()
			if (checkProperty) checkPropertyData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
			checkRegion = check == 1;								// ActionFragmentWriter.writeReadParams()
			if (checkRegion) checkRegionData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
			checkPosition = check == 2;								// ActionFragmentWriter.writeReadParams()
			if (checkPosition) checkPositionData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
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
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean checkIfTheActor;									// ActionFragmentWriter.writeElement()
	public CheckIfTheActorData checkIfTheActorData;					// ActionFragmentWriter.writeElement()
	public class CheckIfTheActorData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters actorInstance;							// ActionFragmentWriter.writeElement()
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readActorInstance(actorInstance);		// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		public boolean checkType;									// ActionFragmentWriter.writeElement()
		public CheckTypeData checkTypeData;							// ActionFragmentWriter.writeElement()
		public class CheckTypeData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
			public boolean compareIs;								// ActionFragmentWriter.writeElement()
			public boolean compareIsNot;							// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;actorClass&gt;</b> */					// ActionFragmentWriter.writeElement()
			public ActorClassPointer actorClass;					// ActionFragmentWriter.writeElement()
			public ActorClass readActorClass(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readActorClass(actorClass);		// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				int compare = iterator.getInt();					// ActionFragmentWriter.writeReadParams()
				compareIs = compare == 0;							// ActionFragmentWriter.writeReadParams()
				compareIsNot = compare == 1;						// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
				actorClass = iterator.getActorClassPointer();		// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;radio&gt;</b> compare</i>:</li><ul>
			 * <li>compareIs:</li>
			 * <li>compareIsNot:</li>
			 * </ul>
			 * <li><b>&lt;actorClass&gt;</b> actorClass</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
		public CheckIfTheActorData() {								// ActionFragmentWriter.writeConstructor()
			checkTypeData = new CheckTypeData();					// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			actorInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
			int check = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			checkType = check == 0;									// ActionFragmentWriter.writeReadParams()
			if (checkType) checkTypeData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
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
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionIf() {												// ActionFragmentWriter.writeConstructor()
		checkIfTheSwitchData = new CheckIfTheSwitchData();			// ActionFragmentWriter.writeConstructor()
		checkIfTheVariableData = new CheckIfTheVariableData();		// ActionFragmentWriter.writeConstructor()
		checkIfTheActorObjectData = new CheckIfTheActorObjectData();// ActionFragmentWriter.writeConstructor()
		checkIfTheActorData = new CheckIfTheActorData();			// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int checkIf = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		checkIfTheSwitch = checkIf == 0;							// ActionFragmentWriter.writeReadParams()
		if (checkIfTheSwitch) checkIfTheSwitchData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		checkIfTheVariable = checkIf == 1;							// ActionFragmentWriter.writeReadParams()
		if (checkIfTheVariable) checkIfTheVariableData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		checkIfTheActorObject = checkIf == 2;						// ActionFragmentWriter.writeReadParams()
		if (checkIfTheActorObject) checkIfTheActorObjectData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		checkIfTheActor = checkIf == 3;								// ActionFragmentWriter.writeReadParams()
		if (checkIfTheActor) checkIfTheActorData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
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
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
