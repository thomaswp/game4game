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
public class ActionDebugBox extends ScriptableInstance {
	public static final String NAME = "Debug Box";
	public static final int ID = 3;
	public static final String CATEGORY = "Debug";
	
	public boolean showTheMessage;
	public ShowTheMessageData showTheMessageData;
	public class ShowTheMessageData extends ScriptableFragment {
		/** Type: <b>&lt;string&gt;</b> */
		public String string;
		
		@Override
		public void readParams(Iterator iterator) {
			string = iterator.getString();
		}
		/**
		 * <ul>
		 * <li><b>&lt;string&gt;</b> string</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean showTheSwitch;
	public ShowTheSwitchData showTheSwitchData;
	public class ShowTheSwitchData extends ScriptableFragment {
		/** Type: <b>&lt;switch&gt;</b> */
		public Switch aSwitch;
		public boolean readASwitch(GameState gameState) throws ParameterException {
			return gameState.readSwitch(aSwitch);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			aSwitch = iterator.getSwitch();
		}
		/**
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> aSwitch</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean showTheVariable;
	public ShowTheVariableData showTheVariableData;
	public class ShowTheVariableData extends ScriptableFragment {
		/** Type: <b>&lt;variable&gt;</b> */
		public Variable variable;
		public int readVariable(GameState gameState) throws ParameterException {
			return gameState.readVariable(variable);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			variable = iterator.getVariable();
		}
		/**
		 * <ul>
		 * <li><b>&lt;variable&gt;</b> variable</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	
	public ActionDebugBox() {
		showTheMessageData = new ShowTheMessageData();
		showTheSwitchData = new ShowTheSwitchData();
		showTheVariableData = new ShowTheVariableData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int show = iterator.getInt();
		showTheMessage = show == 0;
		if (showTheMessage) showTheMessageData.readParams(iterator);
		showTheSwitch = show == 1;
		if (showTheSwitch) showTheSwitchData.readParams(iterator);
		showTheVariable = show == 2;
		if (showTheVariable) showTheVariableData.readParams(iterator);
		
	}
	/**
	 * 003 <b><i>Debug Box</i></b> (Debug)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> show</i>:</li><ul>
	 * <li>showTheMessage:</li>
	 * <ul>
	 * <li><b>&lt;string&gt;</b> string</li>
	 * </ul>
	 * <li>showTheSwitch:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * </ul>
	 * <li>showTheVariable:</li>
	 * <ul>
	 * <li><b>&lt;variable&gt;</b> variable</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
