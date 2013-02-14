package edu.elon.honors.price.maker.action;							// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.*;								// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ScriptableWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ScriptableWriter.writeHeader()
import com.twp.platform.*;											// ScriptableWriter.writeHeader()
import edu.elon.honors.price.physics.*;								// ScriptableWriter.writeHeader()
import edu.elon.honors.price.input.*;								// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
@SuppressWarnings("unused")											// ScriptableWriter.writeHeader()
public class TriggerUserInputTrigger extends ScriptableInstance {	// ActionFragmentWriter.writeHeader()
	public static final String NAME = "User Input Trigger";			// ScriptableWriter.writeHeader()
	public static final int ID = 4;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = null;						// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean inputTheButton;									// ActionFragmentWriter.writeElement()
	public InputTheButtonData inputTheButtonData;					// ActionFragmentWriter.writeElement()
	public class InputTheButtonData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;button&gt;</b> */							// ActionFragmentWriter.writeElement()
		public int button;											// ActionFragmentWriter.writeElement()
		public Button readButton(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readButton(button);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			button = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;button&gt;</b> button</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean inputTheJoystick;								// ActionFragmentWriter.writeElement()
	public InputTheJoystickData inputTheJoystickData;				// ActionFragmentWriter.writeElement()
	public class InputTheJoystickData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;joystick&gt;</b> */						// ActionFragmentWriter.writeElement()
		public int joystick;										// ActionFragmentWriter.writeElement()
		public JoyStick readJoystick(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readJoystick(joystick);				// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			joystick = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;joystick&gt;</b> joystick</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean inputTheScreen;									// ActionFragmentWriter.writeElement()
	public boolean actionIsPressed;									// ActionFragmentWriter.writeElement()
	public boolean actionIsReleased;								// ActionFragmentWriter.writeElement()
	public boolean actionIsDragged;									// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
	public TriggerUserInputTrigger() {								// ActionFragmentWriter.writeConstructor()
		inputTheButtonData = new InputTheButtonData();				// ActionFragmentWriter.writeConstructor()
		inputTheJoystickData = new InputTheJoystickData();			// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int input = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		inputTheButton = input == 0;								// ActionFragmentWriter.writeReadParams()
		if (inputTheButton) inputTheButtonData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		inputTheJoystick = input == 1;								// ActionFragmentWriter.writeReadParams()
		if (inputTheJoystick) inputTheJoystickData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		inputTheScreen = input == 2;								// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		int action = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		actionIsPressed = action == 0;								// ActionFragmentWriter.writeReadParams()
		actionIsReleased = action == 1;								// ActionFragmentWriter.writeReadParams()
		actionIsDragged = action == 2;								// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
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
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
