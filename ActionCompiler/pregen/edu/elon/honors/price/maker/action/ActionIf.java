package edu.elon.honors.price.maker.action;							// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ActionWriter.writeHeader()
import edu.elon.honors.price.data.*;								// ActionWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ActionWriter.writeHeader()
import com.twp.platform.*;											// ActionWriter.writeHeader()
import edu.elon.honors.price.physics.*;								// ActionWriter.writeHeader()
import edu.elon.honors.price.input.*;								// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
public class ActionIf extends Action {								// ActionFragmentWriter.writeHeader()
	public static final String NAME = "If...";						// ActionWriter.writeHeader()
	public static final int ID = 7;									// ActionWriter.writeHeader()
	public static final String CATEGORY = "Control";				// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	public boolean checkIfTheSwitch;								// ActionFragmentWriter.writeElement()
	public CheckIfTheSwitchData checkIfTheSwitchData;				// ActionFragmentWriter.writeElement()
	public class CheckIfTheSwitchData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;switch&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Switch aSwitch;										// ActionFragmentWriter.writeElement()
		public boolean readASwitch(GameState gameState) {			// ActionFragmentWriter.writeElement()
			return gameState.readSwitch(aSwitch);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		public boolean operatorEquals;								// ActionFragmentWriter.writeElement()
		public boolean operatorDoesNotEqual;						// ActionFragmentWriter.writeElement()
		public boolean withOn;										// ActionFragmentWriter.writeElement()
		public boolean withOff;										// ActionFragmentWriter.writeElement()
		public boolean withTheSwitch;								// ActionFragmentWriter.writeElement()
		public WithTheSwitchData withTheSwitchData;					// ActionFragmentWriter.writeElement()
		public class WithTheSwitchData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;switch&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Switch aSwitch;									// ActionFragmentWriter.writeElement()
			public boolean readASwitch(GameState gameState) {		// ActionFragmentWriter.writeElement()
				return gameState.readSwitch(aSwitch);				// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
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
	public class CheckIfTheVariableData extends ActionFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;variable&gt;</b> */						// ActionFragmentWriter.writeElement()
		public Variable variable;									// ActionFragmentWriter.writeElement()
		public int readVariable(GameState gameState) {				// ActionFragmentWriter.writeElement()
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
		public int readNumber(GameState gameState) {				// ActionFragmentWriter.writeElement()
			return gameState.readNumber(number);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
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
	public boolean checkIfTheActor;									// ActionFragmentWriter.writeElement()
	public CheckIfTheActorData checkIfTheActorData;					// ActionFragmentWriter.writeElement()
	public class CheckIfTheActorData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters actorInstance;							// ActionFragmentWriter.writeElement()
		public ActorBody readActorInstance(GameState gameState) {	// ActionFragmentWriter.writeElement()
			return gameState.readActorInstance(actorInstance);		// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		public boolean checkProperty;								// ActionFragmentWriter.writeElement()
		public CheckPropertyData checkPropertyData;					// ActionFragmentWriter.writeElement()
		public class CheckPropertyData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
			public boolean propertyIsAlive;							// ActionFragmentWriter.writeElement()
			public boolean propertyIsDead;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
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
		public class CheckRegionData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
			public boolean checkIsInside;							// ActionFragmentWriter.writeElement()
			public boolean checkIsTouching;							// ActionFragmentWriter.writeElement()
			public boolean checkIsOutside;							// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;region&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters region;								// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
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
																	// ActionFragmentWriter.writeConstructor()
		public CheckIfTheActorData() {								// ActionFragmentWriter.writeConstructor()
			checkPropertyData = new CheckPropertyData();			// ActionFragmentWriter.writeConstructor()
			checkRegionData = new CheckRegionData();				// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			actorInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
			int check = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			checkProperty = check == 0;								// ActionFragmentWriter.writeReadParams()
			if (checkProperty) checkPropertyData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
			checkRegion = check == 1;								// ActionFragmentWriter.writeReadParams()
			if (checkRegion) checkRegionData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
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
		checkIfTheActorData = new CheckIfTheActorData();			// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int checkIf = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		checkIfTheSwitch = checkIf == 0;							// ActionFragmentWriter.writeReadParams()
		if (checkIfTheSwitch) checkIfTheSwitchData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		checkIfTheVariable = checkIf == 1;							// ActionFragmentWriter.writeReadParams()
		if (checkIfTheVariable) checkIfTheVariableData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		checkIfTheActor = checkIf == 2;								// ActionFragmentWriter.writeReadParams()
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
	 * <li>checkIfTheActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
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
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
