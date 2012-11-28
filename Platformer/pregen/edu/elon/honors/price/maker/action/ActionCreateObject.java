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
public class ActionCreateObject extends ActionInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Create Object";				// ActionWriter.writeHeader()
	public static final int ID = 8;									// ActionWriter.writeHeader()
	public static final String CATEGORY = "Object";					// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	/** Type: <b>&lt;objectClass&gt;</b> */							// ActionFragmentWriter.writeElement()
	public int objectClass;											// ActionFragmentWriter.writeElement()
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
		objectClass = iterator.getInt();							// ActionFragmentWriter.writeReadParams()
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
