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
public class ActionWait extends ScriptableInstance {
	public static final String NAME = "Wait";
	public static final int ID = 20;
	public static final String CATEGORY = "Control";
	
	/** Type: <b>&lt;number&gt;</b> */
	public Parameters number;
	public int readNumber(GameState gameState) throws ParameterException {
		return gameState.readNumber(number);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		number = iterator.getParameters();
	}
	/**
	 * 020 <b><i>Wait</i></b> (Control)<br />
	 * <ul>
	 * <li><b>&lt;number&gt;</b> number</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
