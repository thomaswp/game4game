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
public class ActionStop extends ScriptableInstance {
	public static final String NAME = "Stop";
	public static final int ID = 21;
	public static final String CATEGORY = "Control";
	
	
	@Override
	public void readParams(Iterator iterator) {
	}
	/**
	 * 021 <b><i>Stop</i></b> (Control)<br />
	 * <ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
