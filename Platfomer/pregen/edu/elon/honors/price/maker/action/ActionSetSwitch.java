package edu.elon.honors.price.maker.action;							// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// ActionWriter.writeHeader()
import edu.elon.honors.price.data.*;								// ActionWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// ActionWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// ActionWriter.writeHeader()
import com.twp.platform.*;											// ActionWriter.writeHeader()
import edu.elon.honors.price.physics.*;								// ActionWriter.writeHeader()
import edu.elon.honors.price.input.*;								// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
@SuppressWarnings("unused")											// ActionWriter.writeHeader()
public class ActionSetSwitch extends ActionInstance {				// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Set Switch";					// ActionWriter.writeHeader()
	public static final int ID = 0;									// ActionWriter.writeHeader()
	public static final String CATEGORY = "Variables";				// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	public boolean setOneSwitch;									// ActionFragmentWriter.writeElement()
	public SetOneSwitchData setOneSwitchData;						// ActionFragmentWriter.writeElement()
	public class SetOneSwitchData extends ActionFragment {			// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;switch&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Switch aSwitch;										// ActionFragmentWriter.writeElement()
		public boolean readASwitch(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readSwitch(aSwitch);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			aSwitch = iterator.getSwitch();							// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> aSwitch</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean setAllSwitchesFrom;								// ActionFragmentWriter.writeElement()
	public SetAllSwitchesFromData setAllSwitchesFromData;			// ActionFragmentWriter.writeElement()
	public class SetAllSwitchesFromData extends ActionFragment {	// ActionFragmentWriter.writeHeader()
		public Group group;											// ActionFragmentWriter.writeElement()
		public class Group extends ActionFragment {					// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;switch&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Switch from;										// ActionFragmentWriter.writeElement()
			public boolean readFrom(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readSwitch(from);					// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;switch&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Switch to;										// ActionFragmentWriter.writeElement()
			public boolean readTo(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readSwitch(to);					// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				from = iterator.getSwitch();						// ActionFragmentWriter.writeReadParams()
				to = iterator.getSwitch();							// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;switch&gt;</b> from</li>
			 * <li><b>&lt;switch&gt;</b> to</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
		public SetAllSwitchesFromData() {							// ActionFragmentWriter.writeConstructor()
			group = new Group();									// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			group.readParams(iterator.getParameters().iterator());	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;group&gt;</b> group:</li>
		 * <ul>
		 * <li><b>&lt;switch&gt;</b> from</li>
		 * <li><b>&lt;switch&gt;</b> to</li>
		 * </ul>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean actionSetItTo;									// ActionFragmentWriter.writeElement()
	public ActionSetItToData actionSetItToData;						// ActionFragmentWriter.writeElement()
	public class ActionSetItToData extends ActionFragment {			// ActionFragmentWriter.writeHeader()
		public boolean setToOn;										// ActionFragmentWriter.writeElement()
		public boolean setToOff;									// ActionFragmentWriter.writeElement()
		public boolean setToASwitchsValue;							// ActionFragmentWriter.writeElement()
		public SetToASwitchsValueData setToASwitchsValueData;		// ActionFragmentWriter.writeElement()
		public class SetToASwitchsValueData extends ActionFragment {// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;switch&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Switch aSwitch;									// ActionFragmentWriter.writeElement()
			public boolean readASwitch(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readSwitch(aSwitch);				// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			@Override												// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				aSwitch = iterator.getSwitch();						// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;switch&gt;</b> aSwitch</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean setToARandomValue;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
		public ActionSetItToData() {								// ActionFragmentWriter.writeConstructor()
			setToASwitchsValueData = new SetToASwitchsValueData();	// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			int setTo = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			setToOn = setTo == 0;									// ActionFragmentWriter.writeReadParams()
			setToOff = setTo == 1;									// ActionFragmentWriter.writeReadParams()
			setToASwitchsValue = setTo == 2;						// ActionFragmentWriter.writeReadParams()
			if (setToASwitchsValue) setToASwitchsValueData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
			setToARandomValue = setTo == 3;							// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
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
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean actionToggleIt;									// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionSetSwitch() {										// ActionFragmentWriter.writeConstructor()
		setOneSwitchData = new SetOneSwitchData();					// ActionFragmentWriter.writeConstructor()
		setAllSwitchesFromData = new SetAllSwitchesFromData();		// ActionFragmentWriter.writeConstructor()
		actionSetItToData = new ActionSetItToData();				// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int set = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		setOneSwitch = set == 0;									// ActionFragmentWriter.writeReadParams()
		if (setOneSwitch) setOneSwitchData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
		setAllSwitchesFrom = set == 1;								// ActionFragmentWriter.writeReadParams()
		if (setAllSwitchesFrom) setAllSwitchesFromData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		int action = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		actionSetItTo = action == 0;								// ActionFragmentWriter.writeReadParams()
		if (actionSetItTo) actionSetItToData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
		actionToggleIt = action == 1;								// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
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
	 * <li><b>&lt;group&gt;</b> group:</li>
	 * <ul>
	 * <li><b>&lt;switch&gt;</b> from</li>
	 * <li><b>&lt;switch&gt;</b> to</li>
	 * </ul>
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
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
