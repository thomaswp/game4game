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
public class ActionDestroyObject extends ScriptableInstance {
	public static final String NAME = "Destroy Object";
	public static final int ID = 15;
	public static final String CATEGORY = "Object";
	
	/** Type: <b>&lt;objectInstance&gt;</b> */
	public Parameters objectInstance;
	public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {
		return gameState.readObjectInstance(objectInstance);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		objectInstance = iterator.getParameters();
	}
	/**
	 * 015 <b><i>Destroy Object</i></b> (Object)<br />
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
