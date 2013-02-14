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
public class ActionDebugBox extends ScriptableInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Debug Box";					// ScriptableWriter.writeHeader()
	public static final int ID = 3;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Debug";					// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean showTheMessage;									// ActionFragmentWriter.writeElement()
	public ShowTheMessageData showTheMessageData;					// ActionFragmentWriter.writeElement()
	public class ShowTheMessageData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;string&gt;</b> */							// ActionFragmentWriter.writeElement()
		public String string;										// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			string = iterator.getString();							// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;string&gt;</b> string</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean showTheSwitch;									// ActionFragmentWriter.writeElement()
	public ShowTheSwitchData showTheSwitchData;						// ActionFragmentWriter.writeElement()
	public class ShowTheSwitchData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;switch&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Switch aSwitch;										// ActionFragmentWriter.writeElement()
		public boolean readASwitch(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readSwitch(aSwitch);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			aSwitch = iterator.getSwitch();							// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> aSwitch</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean showTheVariable;									// ActionFragmentWriter.writeElement()
	public ShowTheVariableData showTheVariableData;					// ActionFragmentWriter.writeElement()
	public class ShowTheVariableData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
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
																	// ActionFragmentWriter.writeConstructor()
	public ActionDebugBox() {										// ActionFragmentWriter.writeConstructor()
		showTheMessageData = new ShowTheMessageData();				// ActionFragmentWriter.writeConstructor()
		showTheSwitchData = new ShowTheSwitchData();				// ActionFragmentWriter.writeConstructor()
		showTheVariableData = new ShowTheVariableData();			// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
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
	/**
	 * 003 <b><i>Debug Box</i></b> (Debug)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> show</i>:</li><ul>
	 * <li>showTheMessage:</li>
	 * <ul>
	 * <li><b>&lt;string&gt;</b> string</li>
	 * </ul>
	 * <li>showTheSwitch:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * </ul>
	 * <li>showTheVariable:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
