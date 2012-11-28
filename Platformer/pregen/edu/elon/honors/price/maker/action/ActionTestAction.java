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
public class ActionTestAction extends ActionInstance {				// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Test Action";				// ActionWriter.writeHeader()
	public static final int ID = 20;								// ActionWriter.writeHeader()
	public static final String CATEGORY = "Test";					// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	public boolean happyYes;										// ActionFragmentWriter.writeElement()
	public HappyYesData happyYesData;								// ActionFragmentWriter.writeElement()
	public class HappyYesData extends ActionFragment {				// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorClass&gt;</b> */						// ActionFragmentWriter.writeElement()
		public int happyActor;										// ActionFragmentWriter.writeElement()
		public ActorClass readHappyActor(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readActorClass(happyActor);			// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			happyActor = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;actorClass&gt;</b> happyActor</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean happyNo;											// ActionFragmentWriter.writeElement()
	public HappyNoData happyNoData;									// ActionFragmentWriter.writeElement()
	public class HappyNoData extends ActionFragment {				// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;objectInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters objectInstance;							// ActionFragmentWriter.writeElement()
		public ObjectBody readObjectInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readObjectInstance(objectInstance);	// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			objectInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionTestAction() {										// ActionFragmentWriter.writeConstructor()
		happyYesData = new HappyYesData();							// ActionFragmentWriter.writeConstructor()
		happyNoData = new HappyNoData();							// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int happy = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		happyYes = happy == 0;										// ActionFragmentWriter.writeReadParams()
		if (happyYes) happyYesData.readParams(iterator);			// ActionFragmentWriter.writeReadParams()
		happyNo = happy == 1;										// ActionFragmentWriter.writeReadParams()
		if (happyNo) happyNoData.readParams(iterator);				// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 020 <b><i>Test Action</i></b> (Test)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> happy</i>:</li><ul>
	 * <li>happyYes:</li>
	 * <ul>
	 * <li><b>&lt;actorClass&gt;</b> happyActor</li>
	 * </ul>
	 * <li>happyNo:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
