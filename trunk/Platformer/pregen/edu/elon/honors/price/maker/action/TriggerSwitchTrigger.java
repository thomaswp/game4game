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
public class TriggerSwitchTrigger extends ScriptableInstance {		// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Switch Trigger";				// ScriptableWriter.writeHeader()
	public static final int ID = 0;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = null;						// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	/** Type: <b>&lt;switch&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Switch aSwitch;											// ActionFragmentWriter.writeElement()
	public boolean readASwitch(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readSwitch(aSwitch);						// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;boolean&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters value;										// ActionFragmentWriter.writeElement()
	public boolean readValue(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readBoolean(value);						// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		aSwitch = iterator.getSwitch();								// ActionFragmentWriter.writeReadParams()
		value = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 000 <b><i>Switch Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * <li><b>&lt;boolean&gt;</b> value</li>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
