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
public class ActionDrawToScreen extends ScriptableInstance {
	public static final String NAME = "Draw to Screen";
	public static final int ID = 16;
	public static final String CATEGORY = "UI";
	
	public boolean actionClearTheScreen;
	public boolean actionDrawA;
	public ActionDrawAData actionDrawAData;
	public class ActionDrawAData extends ScriptableFragment {
		/** Type: <b>&lt;color&gt;</b> */
		public int color;
		public boolean styleHollow;
		public boolean styleFilledIn;
		public boolean shapeLine;
		public ShapeLineData shapeLineData;
		public class ShapeLineData extends ScriptableFragment {
			/** Type: <b>&lt;point&gt;</b> */
			public Parameters from;
			public Point readFrom(GameState gameState) throws ParameterException {
				return gameState.readPoint(from);
			}
			/** Type: <b>&lt;point&gt;</b> */
			public Parameters to;
			public Point readTo(GameState gameState) throws ParameterException {
				return gameState.readPoint(to);
			}
			
			@Override
			public void readParams(Iterator iterator) {
				from = iterator.getParameters();
				to = iterator.getParameters();
			}
			/**
			 * <ul>
			 * <li><b>&lt;point&gt;</b> from</li>
			 * <li><b>&lt;point&gt;</b> to</li>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		public boolean shapeCircle;
		public ShapeCircleData shapeCircleData;
		public class ShapeCircleData extends ScriptableFragment {
			/** Type: <b>&lt;point&gt;</b> */
			public Parameters center;
			public Point readCenter(GameState gameState) throws ParameterException {
				return gameState.readPoint(center);
			}
			/** Type: <b>&lt;number&gt;</b> */
			public Parameters radius;
			public int readRadius(GameState gameState) throws ParameterException {
				return gameState.readNumber(radius);
			}
			
			@Override
			public void readParams(Iterator iterator) {
				center = iterator.getParameters();
				radius = iterator.getParameters();
			}
			/**
			 * <ul>
			 * <li><b>&lt;point&gt;</b> center</li>
			 * <li><b>&lt;number&gt;</b> radius</li>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		public boolean shapeBox;
		public ShapeBoxData shapeBoxData;
		public class ShapeBoxData extends ScriptableFragment {
			/** Type: <b>&lt;point&gt;</b> */
			public Parameters corner1;
			public Point readCorner1(GameState gameState) throws ParameterException {
				return gameState.readPoint(corner1);
			}
			/** Type: <b>&lt;point&gt;</b> */
			public Parameters corner2;
			public Point readCorner2(GameState gameState) throws ParameterException {
				return gameState.readPoint(corner2);
			}
			
			@Override
			public void readParams(Iterator iterator) {
				corner1 = iterator.getParameters();
				corner2 = iterator.getParameters();
			}
			/**
			 * <ul>
			 * <li><b>&lt;point&gt;</b> corner1</li>
			 * <li><b>&lt;point&gt;</b> corner2</li>
			 * </ul>
			 */
			public static final String JAVADOC = "";
		}
		
		public boolean useWorldCoordinates;
		public boolean useScreenCoordinates;
		
		public ActionDrawAData() {
			shapeLineData = new ShapeLineData();
			shapeCircleData = new ShapeCircleData();
			shapeBoxData = new ShapeBoxData();
		}
		
		@Override
		public void readParams(Iterator iterator) {
			color = iterator.getInt();
			int style = iterator.getInt();
			styleHollow = style == 0;
			styleFilledIn = style == 1;
			
			int shape = iterator.getInt();
			shapeLine = shape == 0;
			if (shapeLine) shapeLineData.readParams(iterator);
			shapeCircle = shape == 1;
			if (shapeCircle) shapeCircleData.readParams(iterator);
			shapeBox = shape == 2;
			if (shapeBox) shapeBoxData.readParams(iterator);
			
			int use = iterator.getInt();
			useWorldCoordinates = use == 0;
			useScreenCoordinates = use == 1;
			
		}
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
		 */
		public static final String JAVADOC = "";
	}
	
	
	public ActionDrawToScreen() {
		actionDrawAData = new ActionDrawAData();
	}
	
	@Override
	public void readParams(Iterator iterator) {
		int action = iterator.getInt();
		actionClearTheScreen = action == 0;
		actionDrawA = action == 1;
		if (actionDrawA) actionDrawAData.readParams(iterator);
		
	}
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
	 */
	public static final String JAVADOC = "";
}
