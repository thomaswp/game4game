																	// ActionWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ActionWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
public class ActionLoop extends Action {							// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Loop...";					// ActionWriter.writeHeader()
	public static final int ID = 17;								// ActionWriter.writeHeader()
	public static final String CATEGORY = "Control";				// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	/** Type: <b>&lt;number&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters numTimes;										// ActionFragmentWriter.writeElement()
	/**
	 * This is sample javadoc!
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
																	// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		numTimes = iterator.getParameters();						// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
}																	// ActionFragmentWriter.writeFooter()
