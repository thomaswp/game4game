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
public class TriggerActorOrObjectTrigger extends ScriptableInstance {// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Actor or Object Trigger";	// ScriptableWriter.writeHeader()
	public static final int ID = 2;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = null;						// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	/** Type: <b>&lt;body&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters body;											// ActionFragmentWriter.writeElement()
	public boolean whenCollidesWith;								// ActionFragmentWriter.writeElement()
	public WhenCollidesWithData whenCollidesWithData;				// ActionFragmentWriter.writeElement()
	public class WhenCollidesWithData extends ScriptableFragment {	// ActionFragmentWriter.writeHeader()
		public boolean collidesWithHero;							// ActionFragmentWriter.writeElement()
		public boolean collidesWithActor;							// ActionFragmentWriter.writeElement()
		public boolean collidesWithObject;							// ActionFragmentWriter.writeElement()
		public boolean collidesWithWall;							// ActionFragmentWriter.writeElement()
		public boolean collidesWithLedge;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
		@Override													// ActionFragmentWriter.writeReadParams()
		public void readParams(Iterator iterator) {					// ActionFragmentWriter.writeReadParams()
			int collidesWith = iterator.getInt();					// ActionFragmentWriter.writeReadParams()
			collidesWithHero = collidesWith == 0;					// ActionFragmentWriter.writeReadParams()
			collidesWithActor = collidesWith == 1;					// ActionFragmentWriter.writeReadParams()
			collidesWithObject = collidesWith == 2;					// ActionFragmentWriter.writeReadParams()
			collidesWithWall = collidesWith == 3;					// ActionFragmentWriter.writeReadParams()
			collidesWithLedge = collidesWith == 4;					// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		}															// ActionFragmentWriter.writeReadParams()
		/**
		 * <ul>
		 * <li><b>&lt;radio&gt;</b> collidesWith</i>:</li><ul>
		 * <li>collidesWithHero:</li>
		 * <li>collidesWithActor:</li>
		 * <li>collidesWithObject:</li>
		 * <li>collidesWithWall:</li>
		 * <li>collidesWithLedge:</li>
		 * </ul>
		 * </ul>
		 */															// ActionFragmentWriter.writeJavadoc()
		public static final String JAVADOC = "";					// ActionFragmentWriter.writeJavadoc()
	}																// ActionFragmentWriter.writeFooter()
																	// ActionFragmentWriter.endElement()
	public boolean whenIsCreated;									// ActionFragmentWriter.writeElement()
	public boolean whenIsDestroyed;									// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeConstructor()
	public TriggerActorOrObjectTrigger() {							// ActionFragmentWriter.writeConstructor()
		whenCollidesWithData = new WhenCollidesWithData();			// ActionFragmentWriter.writeConstructor()
	}																// ActionFragmentWriter.writeConstructor()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		body = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
		int when = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		whenCollidesWith = when == 0;								// ActionFragmentWriter.writeReadParams()
		if (whenCollidesWith) whenCollidesWithData.readParams(iterator);// ActionFragmentWriter.writeReadParams()
		whenIsCreated = when == 1;									// ActionFragmentWriter.writeReadParams()
		whenIsDestroyed = when == 2;								// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 002 <b><i>Actor or Object Trigger</i></b> (null)<br />
	 * <ul>
	 * <li><b>&lt;body&gt;</b> body</li>
	 * <li><b>&lt;radio&gt;</b> when</i>:</li><ul>
	 * <li>whenCollidesWith:</li>
	 * <ul>
	 * <li><b>&lt;radio&gt;</b> collidesWith</i>:</li><ul>
	 * <li>collidesWithHero:</li>
	 * <li>collidesWithActor:</li>
	 * <li>collidesWithObject:</li>
	 * <li>collidesWithWall:</li>
	 * <li>collidesWithLedge:</li>
	 * </ul>
	 * </ul>
	 * <li>whenIsCreated:</li>
	 * <li>whenIsDestroyed:</li>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
