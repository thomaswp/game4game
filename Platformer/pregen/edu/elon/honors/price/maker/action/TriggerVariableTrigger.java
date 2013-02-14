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
public class TriggerVariableTrigger extends ScriptableInstance {	// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Variable Trigger";			// ScriptableWriter.writeHeader()
	public static final int ID = 1;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = null;						// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	/** Type: <b>&lt;variable&gt;</b> */							// ActionFragmentWriter.writeElement()
	public Variable variable;										// ActionFragmentWriter.writeElement()
	public int readVariable(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readVariable(variable);					// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
	public boolean operatorEquals;									// ActionFragmentWriter.writeElement()
	public boolean operatorNotEquals;								// ActionFragmentWriter.writeElement()
	public boolean operatorGreater;									// ActionFragmentWriter.writeElement()
	public boolean operatorGreaterOrEqual;							// ActionFragmentWriter.writeElement()
	public boolean operatorLess;									// ActionFragmentWriter.writeElement()
	public boolean operatorLessOrEqual;								// ActionFragmentWriter.writeElement()
	public boolean operatorDivisible;								// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;number&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters value;										// ActionFragmentWriter.writeElement()
	public int readValue(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readNumber(value);							// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		variable = iterator.getVariable();							// ActionFragmentWriter.writeReadParams()
		int operator = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		operatorEquals = operator == 0;								// ActionFragmentWriter.writeReadParams()
		operatorNotEquals = operator == 1;							// ActionFragmentWriter.writeReadParams()
		operatorGreater = operator == 2;							// ActionFragmentWriter.writeReadParams()
		operatorGreaterOrEqual = operator == 3;						// ActionFragmentWriter.writeReadParams()
		operatorLess = operator == 4;								// ActionFragmentWriter.writeReadParams()
		operatorLessOrEqual = operator == 5;						// ActionFragmentWriter.writeReadParams()
		operatorDivisible = operator == 6;							// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		value = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 001 <b><i>Variable Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * <li><b>&lt;radio&gt;</b> operator</i>:</li><ul>
	 * <li>operatorEquals:</li>
	 * <li>operatorNotEquals:</li>
	 * <li>operatorGreater:</li>
	 * <li>operatorGreaterOrEqual:</li>
	 * <li>operatorLess:</li>
	 * <li>operatorLessOrEqual:</li>
	 * <li>operatorDivisible:</li>
	 * </ul>
	 * <li><b>&lt;number&gt;</b> value</li>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
