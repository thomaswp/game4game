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
public class ActionChangeColor extends ScriptableInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Change Color";				// ScriptableWriter.writeHeader()
	public static final int ID = 24;								// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Actor|Object";			// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean colorTheActor;									// ActionFragmentWriter.writeElement()
	public ColorTheActorData colorTheActorData;						// ActionFragmentWriter.writeElement()
	public class ColorTheActorData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
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
	public boolean colorTheObject;									// ActionFragmentWriter.writeElement()
	public ColorTheObjectData colorTheObjectData;					// ActionFragmentWriter.writeElement()
	public class ColorTheObjectData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
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
	/** Type: <b>&lt;color&gt;</b> */								// ActionFragmentWriter.writeElement()
	public int newColor;											// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionChangeColor() {									// ActionFragmentWriter.writeConstructor()
		colorTheActorData = new ColorTheActorData();				// ActionFragmentWriter.writeConstructor()
		colorTheObjectData = new ColorTheObjectData();				// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int color = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		colorTheActor = color == 0;									// ActionFragmentWriter.writeReadParams()
		if (colorTheActor) colorTheActorData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
		colorTheObject = color == 1;								// ActionFragmentWriter.writeReadParams()
		if (colorTheObject) colorTheObjectData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		newColor = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 024 <b><i>Change Color</i></b> (Actor|Object)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> color</i>:</li><ul>
	 * <li>colorTheActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>colorTheObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;color&gt;</b> newColor</li>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
