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
public class ActionSetVariable extends ScriptableInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Set Variable";				// ScriptableWriter.writeHeader()
	public static final int ID = 1;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Variables";				// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean setOneVariable;									// ActionFragmentWriter.writeElement()
	public SetOneVariableData setOneVariableData;					// ActionFragmentWriter.writeElement()
	public class SetOneVariableData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;variable&gt;</b> */						// ActionFragmentWriter.writeElement()
		public Variable variable;									// ActionFragmentWriter.writeElement()
		public int readVariable(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readVariable(variable);				// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			variable = iterator.getVariable();						// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> variable</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean setAllVariablesFrom;								// ActionFragmentWriter.writeElement()
	public SetAllVariablesFromData setAllVariablesFromData;			// ActionFragmentWriter.writeElement()
	public class SetAllVariablesFromData extends ScriptableFragment {// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;variable&gt;</b> */						// ActionFragmentWriter.writeElement()
		public Variable from;										// ActionFragmentWriter.writeElement()
		public int readFrom(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readVariable(from);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		/** Type: <b>&lt;variable&gt;</b> */						// ActionFragmentWriter.writeElement()
		public Variable to;											// ActionFragmentWriter.writeElement()
		public int readTo(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readVariable(to);						// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			from = iterator.getVariable();							// ActionFragmentWriter.writeReadParams()
			to = iterator.getVariable();							// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> from</li>
		 * <li><b>&lt;variable&gt;</b> to</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean operationSetItTo;								// ActionFragmentWriter.writeElement()
	public boolean operationAdd;									// ActionFragmentWriter.writeElement()
	public boolean operationSubtract;								// ActionFragmentWriter.writeElement()
	public boolean operationMultiply;								// ActionFragmentWriter.writeElement()
	public boolean operationDivideBy;								// ActionFragmentWriter.writeElement()
	public boolean operationModBy;									// ActionFragmentWriter.writeElement()
	public boolean withTheValue;									// ActionFragmentWriter.writeElement()
	public WithTheValueData withTheValueData;						// ActionFragmentWriter.writeElement()
	public class WithTheValueData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;exactNumber&gt;</b> */						// ActionFragmentWriter.writeElement()
		public int exactNumber;										// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			exactNumber = iterator.getInt();						// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean withAVariable;									// ActionFragmentWriter.writeElement()
	public WithAVariableData withAVariableData;						// ActionFragmentWriter.writeElement()
	public class WithAVariableData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;variable&gt;</b> */						// ActionFragmentWriter.writeElement()
		public Variable variable;									// ActionFragmentWriter.writeElement()
		public int readVariable(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readVariable(variable);				// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			variable = iterator.getVariable();						// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> variable</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean withARandomNumber;								// ActionFragmentWriter.writeElement()
	public WithARandomNumberData withARandomNumberData;				// ActionFragmentWriter.writeElement()
	public class WithARandomNumberData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		public Group group;											// ActionFragmentWriter.writeElement()
		public class Group extends ScriptableFragment {				// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;exactNumber&gt;</b> */					// ActionFragmentWriter.writeElement()
			public int exactNumber;									// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;exactNumber&gt;</b> */					// ActionFragmentWriter.writeElement()
			public int exactNumber2;								// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				exactNumber = iterator.getInt();					// ActionFragmentWriter.writeReadParams()
				exactNumber2 = iterator.getInt();					// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
			 * <li><b>&lt;exactNumber&gt;</b> exactNumber2</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
		public WithARandomNumberData() {							// ActionFragmentWriter.writeConstructor()
			group = new Group();									// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			Iterator it2 = iterator.getParameters().iterator(true);	// ActionFragmentWriter.writeReadParams()
			group.readParams(it2);									// ActionFragmentWriter.writeReadParams()
			it2.dispose();											// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;group&gt;</b> group:</li>
		 * <ul>
		 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
		 * <li><b>&lt;exactNumber&gt;</b> exactNumber2</li>
		 * </ul>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean withAnActorProperty;								// ActionFragmentWriter.writeElement()
	public WithAnActorPropertyData withAnActorPropertyData;			// ActionFragmentWriter.writeElement()
	public class WithAnActorPropertyData extends ScriptableFragment {// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters actorInstance;							// ActionFragmentWriter.writeElement()
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readActorInstance(actorInstance);		// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		public boolean coordinateX;									// ActionFragmentWriter.writeElement()
		public boolean coordinateY;									// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			actorInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
			int coordinate = iterator.getInt();						// ActionFragmentWriter.writeReadParams()
			coordinateX = coordinate == 0;							// ActionFragmentWriter.writeReadParams()
			coordinateY = coordinate == 1;							// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * <li><b>&lt;radio&gt;</b> coordinate</i>:</li><ul>
		 * <li>coordinateX:</li>
		 * <li>coordinateY:</li>
		 * </ul>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionSetVariable() {									// ActionFragmentWriter.writeConstructor()
		setOneVariableData = new SetOneVariableData();				// ActionFragmentWriter.writeConstructor()
		setAllVariablesFromData = new SetAllVariablesFromData();	// ActionFragmentWriter.writeConstructor()
		withTheValueData = new WithTheValueData();					// ActionFragmentWriter.writeConstructor()
		withAVariableData = new WithAVariableData();				// ActionFragmentWriter.writeConstructor()
		withARandomNumberData = new WithARandomNumberData();		// ActionFragmentWriter.writeConstructor()
		withAnActorPropertyData = new WithAnActorPropertyData();	// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int set = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		setOneVariable = set == 0;									// ActionFragmentWriter.writeReadParams()
		if (setOneVariable) setOneVariableData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		setAllVariablesFrom = set == 1;								// ActionFragmentWriter.writeReadParams()
		if (setAllVariablesFrom) setAllVariablesFromData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		int operation = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		operationSetItTo = operation == 0;							// ActionFragmentWriter.writeReadParams()
		operationAdd = operation == 1;								// ActionFragmentWriter.writeReadParams()
		operationSubtract = operation == 2;							// ActionFragmentWriter.writeReadParams()
		operationMultiply = operation == 3;							// ActionFragmentWriter.writeReadParams()
		operationDivideBy = operation == 4;							// ActionFragmentWriter.writeReadParams()
		operationModBy = operation == 5;							// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		int with = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		withTheValue = with == 0;									// ActionFragmentWriter.writeReadParams()
		if (withTheValue) withTheValueData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
		withAVariable = with == 1;									// ActionFragmentWriter.writeReadParams()
		if (withAVariable) withAVariableData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
		withARandomNumber = with == 2;								// ActionFragmentWriter.writeReadParams()
		if (withARandomNumber) withARandomNumberData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		withAnActorProperty = with == 3;							// ActionFragmentWriter.writeReadParams()
		if (withAnActorProperty) withAnActorPropertyData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 001 <b><i>Set Variable</i></b> (Variables)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> set</i>:</li><ul>
	 * <li>setOneVariable:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * </ul>
	 * <li>setAllVariablesFrom:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> from</li>
	 * <li><b>&lt;variable&gt;</b> to</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> operation</i>:</li><ul>
	 * <li>operationSetItTo:</li>
	 * <li>operationAdd:</li>
	 * <li>operationSubtract:</li>
	 * <li>operationMultiply:</li>
	 * <li>operationDivideBy:</li>
	 * <li>operationModBy:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> with</i>:</li><ul>
	 * <li>withTheValue:</li>
	 * <ul>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
	 * </ul>
	 * <li>withAVariable:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * </ul>
	 * <li>withARandomNumber:</li>
	 * <ul>
	 * <li><b>&lt;group&gt;</b> group:</li>
	 * <ul>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber2</li>
	 * </ul>
	 * </ul>
	 * <li>withAnActorProperty:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * <li><b>&lt;radio&gt;</b> coordinate</i>:</li><ul>
	 * <li>coordinateX:</li>
	 * <li>coordinateY:</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()