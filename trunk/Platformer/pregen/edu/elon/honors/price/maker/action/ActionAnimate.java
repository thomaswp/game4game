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
public class ActionAnimate extends ScriptableInstance {				// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Animate";					// ScriptableWriter.writeHeader()
	public static final int ID = 19;								// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Animate";				// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	public boolean animateActor;									// ActionFragmentWriter.writeElement()
	public AnimateActorData animateActorData;						// ActionFragmentWriter.writeElement()
	public class AnimateActorData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;actorInstance&gt;</b> */					// ActionFragmentWriter.writeElement()
		public Parameters actorInstance;							// ActionFragmentWriter.writeElement()
		public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readActorInstance(actorInstance);		// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			actorInstance = iterator.getParameters();				// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean animateObject;									// ActionFragmentWriter.writeElement()
	public AnimateObjectData animateObjectData;						// ActionFragmentWriter.writeElement()
	public class AnimateObjectData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
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
	public boolean withBounce;										// ActionFragmentWriter.writeElement()
	public WithBounceData withBounceData;							// ActionFragmentWriter.writeElement()
	public class WithBounceData extends ScriptableFragment {		// ActionFragmentWriter.writeHeader()
		/** Type: <b>&lt;vector&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters direction;								// ActionFragmentWriter.writeElement()
		public Vector readDirection(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readVector(direction);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		/** Type: <b>&lt;number&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters distance;									// ActionFragmentWriter.writeElement()
		public int readDistance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readNumber(distance);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
		/** Type: <b>&lt;number&gt;</b> */							// ActionFragmentWriter.writeElement()
		public Parameters duration;									// ActionFragmentWriter.writeElement()
		public int readDuration(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
			return gameState.readNumber(duration);					// ActionFragmentWriter.writeElement()
		}															// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			direction = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
			distance = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
			duration = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;vector&gt;</b> direction</li>
		 * <li><b>&lt;number&gt;</b> distance</li>
		 * <li><b>&lt;number&gt;</b> duration</li>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean withSwirl;										// ActionFragmentWriter.writeElement()
	public boolean thenWaitForTheAnimationToEnd;					// ActionFragmentWriter.writeElement()
	public boolean thenContinueTheEvent;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
	public ActionAnimate() {										// ActionFragmentWriter.writeConstructor()
		animateActorData = new AnimateActorData();					// ActionFragmentWriter.writeConstructor()
		animateObjectData = new AnimateObjectData();				// ActionFragmentWriter.writeConstructor()
		withBounceData = new WithBounceData();						// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		int animate = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
		animateActor = animate == 0;								// ActionFragmentWriter.writeReadParams()
		if (animateActor) animateActorData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
		animateObject = animate == 1;								// ActionFragmentWriter.writeReadParams()
		if (animateObject) animateObjectData.readParams(iterator);	// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		int with = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		withBounce = with == 0;										// ActionFragmentWriter.writeReadParams()
		if (withBounce) withBounceData.readParams(iterator);		// ActionFragmentWriter.writeReadParams()
		withSwirl = with == 1;										// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		int then = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		thenWaitForTheAnimationToEnd = then == 0;					// ActionFragmentWriter.writeReadParams()
		thenContinueTheEvent = then == 1;							// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 019 <b><i>Animate</i></b> (Animate)<br />
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> animate</i>:</li><ul>
	 * <li>animateActor:</li>
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * </ul>
	 * <li>animateObject:</li>
	 * <ul>
	 * <li><b>&lt;objectInstance&gt;</b> objectInstance</li>
	 * </ul>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> with</i>:</li><ul>
	 * <li>withBounce:</li>
	 * <ul>
	 * <li><b>&lt;vector&gt;</b> direction</li>
	 * <li><b>&lt;number&gt;</b> distance</li>
	 * <li><b>&lt;number&gt;</b> duration</li>
	 * </ul>
	 * <li>withSwirl:</li>
	 * </ul>
	 * <li><b>&lt;radio&gt;</b> then</i>:</li><ul>
	 * <li>thenWaitForTheAnimationToEnd:</li>
	 * <li>thenContinueTheEvent:</li>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
