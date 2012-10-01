																	// ActionWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ActionWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
public class ActionDebugBox extends Action {						// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Debug Box";					// ActionWriter.writeHeader()
	public static final int ID = 3;									// ActionWriter.writeHeader()
	public static final String CATEGORY = null;						// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	public boolean showTheMessage;									// ActionFragmentWriter.writeElement()
	public ShowTheMessageData showTheMessageData;					// ActionFragmentWriter.writeElement()
	public class ShowTheMessageData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;string&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters string;									// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			string = iterator.getParameters();						// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean showTheSwitch;									// ActionFragmentWriter.writeElement()
	public ShowTheSwitchData showTheSwitchData;						// ActionFragmentWriter.writeElement()
	public class ShowTheSwitchData extends ActionFragment {			// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;switch&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Switch aSwitch;										// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			aSwitch = iterator.getSwitch();							// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean showTheVariable;									// ActionFragmentWriter.writeElement()
	public ShowTheVariableData showTheVariableData;					// ActionFragmentWriter.writeElement()
	public class ShowTheVariableData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;variable&gt;</b> */						// ActionFragmentWriter.writeElement()
		public Variable variable;									// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			variable = iterator.getVariable();						// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	/**
	 * This is sample javadoc!
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
																	// ActionFragmentWriter.writeConstructor()
	public ActionDebugBox() {										// ActionFragmentWriter.writeConstructor()
		showTheMessageData = new ShowTheMessageData();				// ActionFragmentWriter.writeConstructor()
		showTheSwitchData = new ShowTheSwitchData();				// ActionFragmentWriter.writeConstructor()
		showTheVariableData = new ShowTheVariableData();			// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int show = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		showTheMessage = show == 0;									// ActionFragmentWriter.writeReadParams()
		if (showTheMessage) showTheMessageData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		showTheSwitch = show == 1;									// ActionFragmentWriter.writeReadParams()
		if (showTheSwitch) showTheSwitchData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
		showTheVariable = show == 2;								// ActionFragmentWriter.writeReadParams()
		if (showTheVariable) showTheVariableData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
}																	// ActionFragmentWriter.writeFooter()
