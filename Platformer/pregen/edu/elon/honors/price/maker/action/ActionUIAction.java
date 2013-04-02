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
public class ActionUIAction extends ScriptableInstance {
	public static final String NAME = "UI Action";
	public static final int ID = 14;
	public static final String CATEGORY = "UI";
	
	/** Type: <b>&lt;ui&gt;</b> */
	public Parameters ui;
	public UIControl readUi(GameState gameState) throws ParameterException {
		return gameState.readUi(ui);
	}
	public boolean setItsVisibility;
	public boolean setItsDefaultBehavior;
	/** Type: <b>&lt;boolean&gt;</b> */
	public Parameters to;
	public boolean readTo(GameState gameState) throws ParameterException {
		return gameState.readBoolean(to);
	}
	
	@Override
	public void readParams(Iterator iterator) {
		ui = iterator.getParameters();
		int setIts = iterator.getInt();
		setItsVisibility = setIts == 0;
		setItsDefaultBehavior = setIts == 1;
		
		to = iterator.getParameters();
	}
	/**
	 * 014 <b><i>UI Action</i></b> (UI)<br />
	 * <ul>
	 * <li><b>&lt;ui&gt;</b> ui</li>
	 * <li><b>&lt;radio&gt;</b> setIts</i>:</li><ul>
	 * <li>setItsVisibility:</li>
	 * <li>setItsDefaultBehavior:</li>
	 * </ul>
	 * <li><b>&lt;boolean&gt;</b> to</li>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
