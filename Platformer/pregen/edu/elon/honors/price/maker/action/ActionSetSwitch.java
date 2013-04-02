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
public class ActionSetSwitch extends ScriptableInstance {
	public static final String NAME = "Set Switch";
	public static final int ID = 0;
	public static final String CATEGORY = "Variables";
	
	public boolean setOneSwitch;
	public SetOneSwitchData setOneSwitchData;
	public class SetOneSwitchData extends ScriptableFragment {
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
	
	public boolean setAllSwitchesFrom;
	public SetAllSwitchesFromData setAllSwitchesFromData;
	public class SetAllSwitchesFromData extends ScriptableFragment {
		/** Type: <b>&lt;switch&gt;</b> */
		public Switch from;
		public boolean readFrom(GameState gameState) throws ParameterException {
			return gameState.readSwitch(from);
		}
		/** Type: <b>&lt;switch&gt;</b> */
		public Switch to;
		public boolean readTo(GameState gameState) throws ParameterException {
			return gameState.readSwitch(to);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			from = iterator.getSwitch();
			to = iterator.getSwitch();
		}
		/**
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> from</li>
		 * <li><b>&lt;switch&gt;</b> to</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean actionSetItTo;
	public ActionSetItToData actionSetItToData;
	public class ActionSetItToData extends ScriptableFragment {
		public boolean setToOn;
		public boolean setToOff;
		public boolean setToASwitchsValue;
		public SetToASwitchsValueData setToASwitchsValueData;
		public class SetToASwitchsValueData extends ScriptableFragment {
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
		
		public boolean setToARandomValue;
		
		public ActionSetItToData() {
			setToASwitchsValueData = new SetToASwitchsValueData();
		}
		
		@Override
		public void readParams(Iterator iterator) {
			int setTo = iterator.getInt();
			setToOn = setTo == 0;
			setToOff = setTo == 1;
			setToASwitchsValue = setTo == 2;
			if (setToASwitchsValue) setToASwitchsValueData.readParams(iterator);
			setToARandomValue = setTo == 3;
			
		}
		/**
		 * <ul>
		 * <li><b>&lt;radio&gt;</b> setTo</i>:</li><ul>
		 * <li>setToOn:</li>
		 * <li>setToOff:</li>
		 * <li>setToASwitchsValue:</li>
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> aSwitch</li>
		 * </ul>
		 * <li>setToARandomValue:</li>
		 * </ul>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean actionToggleIt;
	
	public ActionSetSwitch() {
		setOneSwitchData = new SetOneSwitchData();
		setAllSwitchesFromData = new SetAllSwitchesFromData();
		actionSetItToData = new ActionSetItToData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int set = iterator.getInt();
		setOneSwitch = set == 0;
		if (setOneSwitch) setOneSwitchData.readParams(iterator);
		setAllSwitchesFrom = set == 1;
		if (setAllSwitchesFrom) setAllSwitchesFromData.readParams(iterator);
		
		int action = iterator.getInt();
		actionSetItTo = action == 0;
		if (actionSetItTo) actionSetItToData.readParams(iterator);
		actionToggleIt = action == 1;
		
	}
	/**
	 * 000 <b><i>Set Switch</i></b> (Variables)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> set</i>:</li><ul>
	 * <li>setOneSwitch:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * </ul>
	 * <li>setAllSwitchesFrom:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> from</li>
	 * <li><b>&lt;switch&gt;</b> to</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> action</i>:</li><ul>
	 * <li>actionSetItTo:</li>
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> setTo</i>:</li><ul>
	 * <li>setToOn:</li>
	 * <li>setToOff:</li>
	 * <li>setToASwitchsValue:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> aSwitch</li>
	 * </ul>
	 * <li>setToARandomValue:</li>
	 * </ul>
	 * </ul>
	 * <li>actionToggleIt:</li>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
