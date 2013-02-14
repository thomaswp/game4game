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
public class TriggerRegionTrigger extends ScriptableInstance {		// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Region Trigger";				// ScriptableWriter.writeHeader()
	public static final int ID = 3;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = null;						// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	/** Type: <b>&lt;body&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters body;											// ActionFragmentWriter.writeElement()
	public boolean actionBeginsToEnter;								// ActionFragmentWriter.writeElement()
	public boolean actionFullyEnters;								// ActionFragmentWriter.writeElement()
	public boolean actionBeginsToLeave;								// ActionFragmentWriter.writeElement()
	public boolean actionFullyLeaves;								// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;region&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters region;										// ActionFragmentWriter.writeElement()
	public android.graphics.Rect readRegion(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readRegion(region);						// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		body = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
		int action = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		actionBeginsToEnter = action == 0;							// ActionFragmentWriter.writeReadParams()
		actionFullyEnters = action == 1;							// ActionFragmentWriter.writeReadParams()
		actionBeginsToLeave = action == 2;							// ActionFragmentWriter.writeReadParams()
		actionFullyLeaves = action == 3;							// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		region = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 003 <b><i>Region Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;body&gt;</b> body</li>
	 * <li><b>&lt;radio&gt;</b> action</i>:</li><ul>
	 * <li>actionBeginsToEnter:</li>
	 * <li>actionFullyEnters:</li>
	 * <li>actionBeginsToLeave:</li>
	 * <li>actionFullyLeaves:</li>
	 * </ul>
	 * <li><b>&lt;region&gt;</b> region</li>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
