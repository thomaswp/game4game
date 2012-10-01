																	// ActionWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ActionWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
public class ActionSetVelocity extends Action {						// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Set Velocity";				// ActionWriter.writeHeader()
	public static final int ID = 10;								// ActionWriter.writeHeader()
	public static final String CATEGORY = "Physics";				// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	public boolean setActor;										// ActionFragmentWriter.writeElement()
	public SetActorData setActorData;								// ActionFragmentWriter.writeElement()
	public class SetActorData extends ActionFragment {				// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters actorInstance;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			actorInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean setObject;										// ActionFragmentWriter.writeElement()
	public SetObjectData setObjectData;								// ActionFragmentWriter.writeElement()
	public class SetObjectData extends ActionFragment {				// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;objectInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters objectInstance;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			objectInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	/** Type: <b>&lt;vector&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters directionVector;								// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;number&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters directionMagnitude;							// ActionFragmentWriter.writeElement()
	/**
	 * This is sample javadoc!
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
																	// ActionFragmentWriter.writeConstructor()
	public ActionSetVelocity() {									// ActionFragmentWriter.writeConstructor()
		setActorData = new SetActorData();							// ActionFragmentWriter.writeConstructor()
		setObjectData = new SetObjectData();						// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int set = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		setActor = set == 0;										// ActionFragmentWriter.writeReadParams()
		if (setActor) setActorData.readParams(iterator);			// ActionFragmentWriter.writeReadParams()
		setObject = set == 1;										// ActionFragmentWriter.writeReadParams()
		if (setObject) setObjectData.readParams(iterator);			// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		directionVector = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
		directionMagnitude = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
}																	// ActionFragmentWriter.writeFooter()