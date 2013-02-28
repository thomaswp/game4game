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
public class ActionCreateObject extends ScriptableInstance {		// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Create Object";				// ScriptableWriter.writeHeader()
	public static final int ID = 8;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Object";					// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	/** Type: <b>&lt;objectClass&gt;</b> */							// ActionFragmentWriter.writeElement()
	public ObjectClassPointer objectClass;							// ActionFragmentWriter.writeElement()
	public ObjectClass readObjectClass(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readObjectClass(objectClass);				// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;point&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters point;										// ActionFragmentWriter.writeElement()
	public Point readPoint(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readPoint(point);							// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		objectClass = iterator.getObjectClassPointer();				// ActionFragmentWriter.writeReadParams()
		point = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 008 <b><i>Create Object</i></b> (Object)<br />
	 * <ul>
	 * <li><b>&lt;objectClass&gt;</b> objectClass</li>
	 * <li><b>&lt;point&gt;</b> point</li>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
