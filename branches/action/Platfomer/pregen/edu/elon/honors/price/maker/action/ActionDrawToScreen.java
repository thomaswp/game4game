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
public class ActionDrawToScreen extends ActionInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Draw to Screen";				// ActionWriter.writeHeader()
	public static final int ID = 16;								// ActionWriter.writeHeader()
	public static final String CATEGORY = "UI";						// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	public boolean actionClearTheScreen;							// ActionFragmentWriter.writeElement()
	public boolean actionDrawA;										// ActionFragmentWriter.writeElement()
	public ActionDrawAData actionDrawAData;							// ActionFragmentWriter.writeElement()
	public class ActionDrawAData extends ActionFragment {			// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;color&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters color;									// ActionFragmentWriter.writeElement()
		public boolean styleHollow;									// ActionFragmentWriter.writeElement()
		public boolean styleFilledIn;								// ActionFragmentWriter.writeElement()
		public boolean shapeLine;									// ActionFragmentWriter.writeElement()
		public ShapeLineData shapeLineData;							// ActionFragmentWriter.writeElement()
		public class ShapeLineData extends ActionFragment {			// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;point&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters from;									// ActionFragmentWriter.writeElement()
			public Point readFrom(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readPoint(from);					// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;point&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters to;									// ActionFragmentWriter.writeElement()
			public Point readTo(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readPoint(to);						// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				from = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
				to = iterator.getParameters();						// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;point&gt;</b> from</li>
			 * <li><b>&lt;point&gt;</b> to</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean shapeCircle;									// ActionFragmentWriter.writeElement()
		public ShapeCircleData shapeCircleData;						// ActionFragmentWriter.writeElement()
		public class ShapeCircleData extends ActionFragment {		// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;point&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters center;								// ActionFragmentWriter.writeElement()
			public Point readCenter(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readPoint(center);					// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;number&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters radius;								// ActionFragmentWriter.writeElement()
			public int readRadius(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readNumber(radius);				// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				center = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
				radius = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;point&gt;</b> center</li>
			 * <li><b>&lt;number&gt;</b> radius</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean shapeBox;									// ActionFragmentWriter.writeElement()
		public ShapeBoxData shapeBoxData;							// ActionFragmentWriter.writeElement()
		public class ShapeBoxData extends ActionFragment {			// ActionFragmentWriter.writeHeader()
			/** Type: <b>&lt;point&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters corner1;								// ActionFragmentWriter.writeElement()
			public Point readCorner1(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readPoint(corner1);				// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
			/** Type: <b>&lt;point&gt;</b> */						// ActionFragmentWriter.writeElement()
			public Parameters corner2;								// ActionFragmentWriter.writeElement()
			public Point readCorner2(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
				return gameState.readPoint(corner2);				// ActionFragmentWriter.writeElement()
			}														// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
			public void readParams(Iterator iterator) {				// ActionFragmentWriter.writeReadParams()
				corner1 = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
				corner2 = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
			}														// ActionFragmentWriter.writeReadParams()
			/**
			 * <ul>
			 * <li><b>&lt;point&gt;</b> corner1</li>
			 * <li><b>&lt;point&gt;</b> corner2</li>
			 * </ul>
			 */														// ActionFragmentWriter.writeJavadoc()
			public static final String JAVADOC = "";				// ActionFragmentWriter.writeJavadoc()
		}															// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
		public boolean useWorldCoordinates;							// ActionFragmentWriter.writeElement()
		public boolean useScreenCoordinates;						// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
		public ActionDrawAData() {									// ActionFragmentWriter.writeConstructor()
			shapeLineData = new ShapeLineData();					// ActionFragmentWriter.writeConstructor()
			shapeCircleData = new ShapeCircleData();				// ActionFragmentWriter.writeConstructor()
			shapeBoxData = new ShapeBoxData();						// ActionFragmentWriter.writeConstructor()
		}															// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			color = iterator.getParameters();						// ActionFragmentWriter.writeReadParams()
			int style = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			styleHollow = style == 0;								// ActionFragmentWriter.writeReadParams()
			styleFilledIn = style == 1;								// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
			int shape = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			shapeLine = shape == 0;									// ActionFragmentWriter.writeReadParams()
			if (shapeLine) shapeLineData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
			shapeCircle = shape == 1;								// ActionFragmentWriter.writeReadParams()
			if (shapeCircle) shapeCircleData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
			shapeBox = shape == 2;									// ActionFragmentWriter.writeReadParams()
			if (shapeBox) shapeBoxData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
			int use = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
			useWorldCoordinates = use == 0;							// ActionFragmentWriter.writeReadParams()
			useScreenCoordinates = use == 1;						// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;color&gt;</b> color</li>
		 * <li><b>&lt;radio&gt;</b> style</i>:</li><ul>
		 * <li>styleHollow:</li>
		 * <li>styleFilledIn:</li>
		 * </ul>
		 * <li><b>&lt;radio&gt;</b> shape</i>:</li><ul>
		 * <li>shapeLine:</li>
		 * <ul>
		 * <li><b>&lt;point&gt;</b> from</li>
		 * <li><b>&lt;point&gt;</b> to</li>
		 * </ul>
		 * <li>shapeCircle:</li>
		 * <ul>
		 * <li><b>&lt;point&gt;</b> center</li>
		 * <li><b>&lt;number&gt;</b> radius</li>
		 * </ul>
		 * <li>shapeBox:</li>
		 * <ul>
		 * <li><b>&lt;point&gt;</b> corner1</li>
		 * <li><b>&lt;point&gt;</b> corner2</li>
		 * </ul>
		 * </ul>
		 * <li><b>&lt;radio&gt;</b> use</i>:</li><ul>
		 * <li>useWorldCoordinates:</li>
		 * <li>useScreenCoordinates:</li>
		 * </ul>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionDrawToScreen() {									// ActionFragmentWriter.writeConstructor()
		actionDrawAData = new ActionDrawAData();					// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int action = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		actionClearTheScreen = action == 0;							// ActionFragmentWriter.writeReadParams()
		actionDrawA = action == 1;									// ActionFragmentWriter.writeReadParams()
		if (actionDrawA) actionDrawAData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 016 <b><i>Draw to Screen</i></b> (UI)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> action</i>:</li><ul>
	 * <li>actionClearTheScreen:</li>
	 * <li>actionDrawA:</li>
	 * <ul>
	 * <li><b>&lt;color&gt;</b> color</li>
	 * <li><b>&lt;radio&gt;</b> style</i>:</li><ul>
	 * <li>styleHollow:</li>
	 * <li>styleFilledIn:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> shape</i>:</li><ul>
	 * <li>shapeLine:</li>
	 * <ul>
	 * <li><b>&lt;point&gt;</b> from</li>
	 * <li><b>&lt;point&gt;</b> to</li>
	 * </ul>
	 * <li>shapeCircle:</li>
	 * <ul>
	 * <li><b>&lt;point&gt;</b> center</li>
	 * <li><b>&lt;number&gt;</b> radius</li>
	 * </ul>
	 * <li>shapeBox:</li>
	 * <ul>
	 * <li><b>&lt;point&gt;</b> corner1</li>
	 * <li><b>&lt;point&gt;</b> corner2</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> use</i>:</li><ul>
	 * <li>useWorldCoordinates:</li>
	 * <li>useScreenCoordinates:</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
