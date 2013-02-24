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
public class TriggerTimeTrigger extends ScriptableInstance {		// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Time Trigger";				// ScriptableWriter.writeHeader()
	public static final int ID = 5;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = null;						// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean triggerAfter;									// ActionFragmentWriter.writeElement()
	public boolean triggerEvery;									// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;exactNumber&gt;</b> */							// ActionFragmentWriter.writeElement()
	public int exactNumber;											// ActionFragmentWriter.writeElement()
	public boolean inTenthsOfASecond;								// ActionFragmentWriter.writeElement()
	public boolean inSeconds;										// ActionFragmentWriter.writeElement()
	public boolean inMinutes;										// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int trigger = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		triggerAfter = trigger == 0;								// ActionFragmentWriter.writeReadParams()
		triggerEvery = trigger == 1;								// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		exactNumber = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		int in = iterator.getInt();									// ActionFragmentWriter.writeReadParams()
		inTenthsOfASecond = in == 0;								// ActionFragmentWriter.writeReadParams()
		inSeconds = in == 1;										// ActionFragmentWriter.writeReadParams()
		inMinutes = in == 2;										// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 005 <b><i>Time Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> trigger</i>:</li><ul>
	 * <li>triggerAfter:</li>
	 * <li>triggerEvery:</li>
	 * </ul>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
	 * <li><b>&lt;radio&gt;</b> in</i>:</li><ul>
	 * <li>inTenthsOfASecond:</li>
	 * <li>inSeconds:</li>
	 * <li>inMinutes:</li>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
