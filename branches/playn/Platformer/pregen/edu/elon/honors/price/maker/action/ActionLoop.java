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
public class ActionLoop extends ScriptableInstance {
	public static final String NAME = "Loop...";
	public static final int ID = 17;
	public static final String CATEGORY = "Control";
	
	/** Type: <b>&lt;number&gt;</b> */
	public Parameters numTimes;
	public int readNumTimes(GameState gameState) throws ParameterException {
		return gameState.readNumber(numTimes);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		numTimes = iterator.getParameters();
	}
	/**
	 * 017 <b><i>Loop...</i></b> (Control)<br />
	 * <ul>
	 * <li><b>&lt;number&gt;</b> numTimes</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
