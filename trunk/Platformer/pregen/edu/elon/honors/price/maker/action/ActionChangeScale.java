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
public class ActionChangeScale extends ScriptableInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Change Scale";				// ScriptableWriter.writeHeader()
	public static final int ID = 26;								// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Actor|Object";			// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean ofTheActor;										// ActionFragmentWriter.writeElement()
	public OfTheActorData ofTheActorData;							// ActionFragmentWriter.writeElement()
	public class OfTheActorData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters actorInstance;							// ActionFragmentWriter.writeElement()
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readActorInstance(actorInstance);		// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
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
	public boolean ofTheObject;										// ActionFragmentWriter.writeElement()
	public OfTheObjectData ofTheObjectData;							// ActionFragmentWriter.writeElement()
	public class OfTheObjectData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;objectInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters objectInstance;							// ActionFragmentWriter.writeElement()
		public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readObjectInstance(objectInstance);	// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
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
	/** Type: <b>&lt;seekBar&gt;</b> */								// ActionFragmentWriter.writeElement()
	public float scale;												// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionChangeScale() {									// ActionFragmentWriter.writeConstructor()
		ofTheActorData = new OfTheActorData();						// ActionFragmentWriter.writeConstructor()
		ofTheObjectData = new OfTheObjectData();					// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int of = iterator.getInt();									// ActionFragmentWriter.writeReadParams()
		ofTheActor = of == 0;										// ActionFragmentWriter.writeReadParams()
		if (ofTheActor) ofTheActorData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
		ofTheObject = of == 1;										// ActionFragmentWriter.writeReadParams()
		if (ofTheObject) ofTheObjectData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		scale = iterator.getFloat();								// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 026 <b><i>Change Scale</i></b> (Actor|Object)<br />
	 * <ul>
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
	 * <li><b>&lt;seekBar&gt;</b> scale</li>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
