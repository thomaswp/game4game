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
public class TriggerTimeTrigger extends ScriptableInstance {
	public static final String NAME = "Time Trigger";
	public static final int ID = 5;
	public static final String CATEGORY = null;
	
	public boolean triggerAfter;
	public boolean triggerEvery;
	/** Type: <b>&lt;exactNumber&gt;</b> */
	public int exactNumber;
	public boolean inTenthsOfASecond;
	public boolean inSeconds;
	public boolean inMinutes;
	
	@Override
	public void readParams(Iterator iterator) {
		int trigger = iterator.getInt();
		triggerAfter = trigger == 0;
		triggerEvery = trigger == 1;
		
		exactNumber = iterator.getInt();
		int in = iterator.getInt();
		inTenthsOfASecond = in == 0;
		inSeconds = in == 1;
		inMinutes = in == 2;
		
	}
	/**
	 * 005 <b><i>Time Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> trigger</i>:</li><ul>
	 * <li>triggerAfter:</li>
	 * <li>triggerEvery:</li>
	 * </ul>
	 * <li><b>&lt;exactNumber&gt;</b> exactNumber</li>
	 * <li><b>&lt;radio&gt;</b> in</i>:</li><ul>
	 * <li>inTenthsOfASecond:</li>
	 * <li>inSeconds:</li>
	 * <li>inMinutes:</li>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
