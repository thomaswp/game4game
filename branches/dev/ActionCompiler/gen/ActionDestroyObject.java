																	// ActionWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ActionWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
public class ActionDestroyObject extends Action {					// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Destroy Object";				// ActionWriter.writeHeader()
	public static final int ID = 15;								// ActionWriter.writeHeader()
	public static final String CATEGORY = null;						// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	/** Type: <b>&lt;objectInstance&gt;</b> */						// ActionFragmentWriter.writeElement()
	public Parameters objectInstance;								// ActionFragmentWriter.writeElement()
	/**
	 * This is sample javadoc!
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
																	// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		objectInstance = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
}																	// ActionFragmentWriter.writeFooter()
