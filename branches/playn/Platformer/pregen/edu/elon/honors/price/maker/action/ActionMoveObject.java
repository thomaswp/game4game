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
public class ActionMoveObject extends ScriptableInstance {
	public static final String NAME = "Move Object";
	public static final int ID = 9;
	public static final String CATEGORY = "Object";
	
	/** Type: <b>&lt;objectInstance&gt;</b> */
	public Parameters objectInstance;
	public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {
		return gameState.readObjectInstance(objectInstance);
	}
	/** Type: <b>&lt;point&gt;</b> */
	public Parameters point;
	public Point readPoint(GameState gameState) throws ParameterException {
		return gameState.readPoint(point);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		objectInstance = iterator.getParameters();
		point = iterator.getParameters();
	}
	/**
	 * 009 <b><i>Move Object</i></b> (Object)<br />
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * <li><b>&lt;point&gt;</b> point</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
