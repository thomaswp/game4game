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
public class ActionTriggerEvent extends ScriptableInstance {
	public static final String NAME = "Trigger Event";
	public static final int ID = 23;
	public static final String CATEGORY = "Control";
	
	/** Type: <b>&lt;event&gt;</b> */
	public Object event;
	public Event readEvent(GameState gameState) throws ParameterException {
		return gameState.readEvent(event);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		event = iterator.getObject();
	}
	/**
	 * 023 <b><i>Trigger Event</i></b> (Control)<br />
	 * <ul>
	 * <li><b>&lt;event&gt;</b> event</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
