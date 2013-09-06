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
public class ActionChangeGravity extends ScriptableInstance {
	public static final String NAME = "Change Gravity";
	public static final int ID = 13;
	public static final String CATEGORY = "Physics";
	
	/** Type: <b>&lt;vector&gt;</b> */
	public Parameters vector;
	public Vector readVector(GameState gameState) throws ParameterException {
		return gameState.readVector(vector);
	}
	/** Type: <b>&lt;number&gt;</b> */
	public Parameters magnitude;
	public int readMagnitude(GameState gameState) throws ParameterException {
		return gameState.readNumber(magnitude);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		vector = iterator.getParameters();
		magnitude = iterator.getParameters();
	}
	/**
	 * 013 <b><i>Change Gravity</i></b> (Physics)<br />
	 * <ul>
	 * <li><b>&lt;vector&gt;</b> vector</li>
	 * <li><b>&lt;number&gt;</b> magnitude</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
