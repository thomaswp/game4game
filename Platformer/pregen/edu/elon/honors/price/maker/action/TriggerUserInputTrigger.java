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
public class TriggerUserInputTrigger extends ScriptableInstance {
	public static final String NAME = "User Input Trigger";
	public static final int ID = 4;
	public static final String CATEGORY = null;
	
	public boolean inputTheButton;
	public InputTheButtonData inputTheButtonData;
	public class InputTheButtonData extends ScriptableFragment {
		/** Type: <b>&lt;button&gt;</b> */
		public int button;
		public Button readButton(GameState gameState) throws ParameterException {
			return gameState.readButton(button);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			button = iterator.getInt();
		}
		/**
		 * <ul>
		 * <li><b>&lt;button&gt;</b> button</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean inputTheJoystick;
	public InputTheJoystickData inputTheJoystickData;
	public class InputTheJoystickData extends ScriptableFragment {
		/** Type: <b>&lt;joystick&gt;</b> */
		public int joystick;
		public JoyStick readJoystick(GameState gameState) throws ParameterException {
			return gameState.readJoystick(joystick);
		}
		
		@Override
		public void readParams(Iterator iterator) {
			joystick = iterator.getInt();
		}
		/**
		 * <ul>
		 * <li><b>&lt;joystick&gt;</b> joystick</li>
		 * </ul>
		 */
		public static final String JAVADOC = "";
	}
	
	public boolean inputTheScreen;
	public boolean actionIsPressed;
	public boolean actionIsReleased;
	public boolean actionIsDragged;
	
	public TriggerUserInputTrigger() {
		inputTheButtonData = new InputTheButtonData();
		inputTheJoystickData = new InputTheJoystickData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int input = iterator.getInt();
		inputTheButton = input == 0;
		if (inputTheButton) inputTheButtonData.readParams(iterator);
		inputTheJoystick = input == 1;
		if (inputTheJoystick) inputTheJoystickData.readParams(iterator);
		inputTheScreen = input == 2;
		
		int action = iterator.getInt();
		actionIsPressed = action == 0;
		actionIsReleased = action == 1;
		actionIsDragged = action == 2;
		
	}
	/**
	 * 004 <b><i>User Input Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> input</i>:</li><ul>
	 * <li>inputTheButton:</li>
	 * <ul>
	 * <li><b>&lt;button&gt;</b> button</li>
	 * </ul>
	 * <li>inputTheJoystick:</li>
	 * <ul>
	 * <li><b>&lt;joystick&gt;</b> joystick</li>
	 * </ul>
	 * <li>inputTheScreen:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> action</i>:</li><ul>
	 * <li>actionIsPressed:</li>
	 * <li>actionIsReleased:</li>
	 * <li>actionIsDragged:</li>
	 * </ul>
	 * </ul>
	 */
	public static final String JAVADOC = "";
}
