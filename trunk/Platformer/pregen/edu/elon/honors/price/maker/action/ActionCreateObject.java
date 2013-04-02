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
public class ActionCreateObject extends ScriptableInstance {
	public static final String NAME = "Create Object";
	public static final int ID = 8;
	public static final String CATEGORY = "Object";
	
	/** Type: <b>&lt;objectClass&gt;</b> */
	public ObjectClassPointer objectClass;
	public ObjectClass readObjectClass(GameState gameState) throws ParameterException {
		return gameState.readObjectClass(objectClass);
	}
	/** Type: <b>&lt;point&gt;</b> */
	public Parameters point;
	public Point readPoint(GameState gameState) throws ParameterException {
		return gameState.readPoint(point);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		objectClass = iterator.getObjectClassPointer();
		point = iterator.getParameters();
	}
	/**
	 * 008 <b><i>Create Object</i></b> (Object)<br />
	 * <ul>
	 * <li><b>&lt;objectClass&gt;</b> objectClass</li>
	 * <li><b>&lt;point&gt;</b> point</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
