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
public class ActionPointOperation extends ActionInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Point Operation";			// ActionWriter.writeHeader()
	public static final int ID = 12;								// ActionWriter.writeHeader()
	public static final String CATEGORY = null;						// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	/** Type: <b>&lt;variablePoint&gt;</b> */						// ActionFragmentWriter.writeElement()
	public Parameters point;										// ActionFragmentWriter.writeElement()
	public boolean operatorSetItTo;									// ActionFragmentWriter.writeElement()
	public boolean operatorAdd;										// ActionFragmentWriter.writeElement()
	public boolean operatorSubtract;								// ActionFragmentWriter.writeElement()
	public boolean withPoint;										// ActionFragmentWriter.writeElement()
	public WithPointData withPointData;								// ActionFragmentWriter.writeElement()
	public class WithPointData extends ActionFragment {				// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;point&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters point;									// ActionFragmentWriter.writeElement()
		public Point readPoint(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readPoint(point);						// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			point = iterator.getParameters();						// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;point&gt;</b> point</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean withVector;										// ActionFragmentWriter.writeElement()
	public WithVectorData withVectorData;							// ActionFragmentWriter.writeElement()
	public class WithVectorData extends ActionFragment {			// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;vector&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters vector;									// ActionFragmentWriter.writeElement()
		public Vector readVector(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readVector(vector);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		/** Type: <b>&lt;number&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters magnitude;								// ActionFragmentWriter.writeElement()
		public int readMagnitude(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readNumber(magnitude);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			vector = iterator.getParameters();						// ActionFragmentWriter.writeReadParams()
			magnitude = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;vector&gt;</b> vector</li>
		 * <li><b>&lt;number&gt;</b> magnitude</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean withActorLocation;								// ActionFragmentWriter.writeElement()
	public WithActorLocationData withActorLocationData;				// ActionFragmentWriter.writeElement()
	public class WithActorLocationData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters actorInstance;							// ActionFragmentWriter.writeElement()
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readActorInstance(actorInstance);		// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			actorInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean withObjectLocation;								// ActionFragmentWriter.writeElement()
	public WithObjectLocationData withObjectLocationData;			// ActionFragmentWriter.writeElement()
	public class WithObjectLocationData extends ActionFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;objectInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters objectInstance;							// ActionFragmentWriter.writeElement()
		public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readObjectInstance(objectInstance);	// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			objectInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionPointOperation() {									// ActionFragmentWriter.writeConstructor()
		withPointData = new WithPointData();						// ActionFragmentWriter.writeConstructor()
		withVectorData = new WithVectorData();						// ActionFragmentWriter.writeConstructor()
		withActorLocationData = new WithActorLocationData();		// ActionFragmentWriter.writeConstructor()
		withObjectLocationData = new WithObjectLocationData();		// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		point = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
		int operator = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		operatorSetItTo = operator == 0;							// ActionFragmentWriter.writeReadParams()
		operatorAdd = operator == 1;								// ActionFragmentWriter.writeReadParams()
		operatorSubtract = operator == 2;							// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		int with = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		withPoint = with == 0;										// ActionFragmentWriter.writeReadParams()
		if (withPoint) withPointData.readParams(iterator);			// ActionFragmentWriter.writeReadParams()
		withVector = with == 1;										// ActionFragmentWriter.writeReadParams()
		if (withVector) withVectorData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
		withActorLocation = with == 2;								// ActionFragmentWriter.writeReadParams()
		if (withActorLocation) withActorLocationData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		withObjectLocation = with == 3;								// ActionFragmentWriter.writeReadParams()
		if (withObjectLocation) withObjectLocationData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 012 <b><i>Point Operation</i></b> (null)<br />
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
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
