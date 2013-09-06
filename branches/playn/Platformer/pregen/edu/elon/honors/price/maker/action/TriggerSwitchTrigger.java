package edu.elon.honors.price.maker.action;

import edu.elon.honors.price.maker.action.*;
import edu.elon.honors.price.data.*;
import edu.elon.honors.price.data.types.*;
import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.Event.Parameters;
import com.twp.platform.*;
import edu.elon.honors.price.physics.*;
import edu.elon.honors.price.input.*;

@SuppressWarnings("unused")
public class TriggerSwitchTrigger extends ScriptableInstance {
	public static final String NAME = "Switch Trigger";
	public static final int ID = 0;
	public static final String CATEGORY = null;
	
	/** Type: <b>&lt;switch&gt;</b> */
	public Switch aSwitch;
	public boolean readASwitch(GameState gameState) throws ParameterException {
		return gameState.readSwitch(aSwitch);
	}
	/** Type: <b>&lt;boolean&gt;</b> */
	public Parameters value;
	public boolean readValue(GameState gameState) throws ParameterException {
		return gameState.readBoolean(value);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		aSwitch = iterator.getSwitch();
		value = iterator.getParameters();
	}
	/**
	 * 000 <b><i>Switch Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * <li><b>&lt;boolean&gt;</b> value</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
