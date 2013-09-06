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
public class TriggerRegionTrigger extends ScriptableInstance {
	public static final String NAME = "Region Trigger";
	public static final int ID = 3;
	public static final String CATEGORY = null;
	
	/** Type: <b>&lt;body&gt;</b> */
	public Parameters body;
	public boolean actionBeginsToEnter;
	public boolean actionFullyEnters;
	public boolean actionBeginsToLeave;
	public boolean actionFullyLeaves;
	/** Type: <b>&lt;region&gt;</b> */
	public Parameters region;
	public android.graphics.Rect readRegion(GameState gameState) throws ParameterException {
		return gameState.readRegion(region);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		body = iterator.getParameters();
		int action = iterator.getInt();
		actionBeginsToEnter = action == 0;
		actionFullyEnters = action == 1;
		actionBeginsToLeave = action == 2;
		actionFullyLeaves = action == 3;
		
		region = iterator.getParameters();
	}
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
	 */
	public static final String JAVADOC = "";
}
