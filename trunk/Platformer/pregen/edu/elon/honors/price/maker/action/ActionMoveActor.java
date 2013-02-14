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
public class ActionMoveActor extends ScriptableInstance {			// ActionFragmentWriter.writeHeader()
	public static final String NAME = "Move Actor";					// ScriptableWriter.writeHeader()
	public static final int ID = 4;									// ScriptableWriter.writeHeader()
	public static final String CATEGORY = "Actor";					// ScriptableWriter.writeHeader()
																	// ScriptableWriter.writeHeader()
	/** Type: <b>&lt;actorInstance&gt;</b> */						// ActionFragmentWriter.writeElement()
	public Parameters actorInstance;								// ActionFragmentWriter.writeElement()
	public ActorBody readActorInstance(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readActorInstance(actorInstance);			// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;point&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters point;										// ActionFragmentWriter.writeElement()
	public Point readPoint(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readPoint(point);							// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
	public boolean facingLeft;										// ActionFragmentWriter.writeElement()
	public boolean facingRight;										// ActionFragmentWriter.writeElement()
	public boolean facingTheSameDirection;							// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		actorInstance = iterator.getParameters();					// ActionFragmentWriter.writeReadParams()
		point = iterator.getParameters();							// ActionFragmentWriter.writeReadParams()
		int facing = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		facingLeft = facing == 0;									// ActionFragmentWriter.writeReadParams()
		facingRight = facing == 1;									// ActionFragmentWriter.writeReadParams()
		facingTheSameDirection = facing == 2;						// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 004 <b><i>Move Actor</i></b> (Actor)<br />
	 * <ul>
	 * <li><b>&lt;actorInstance&gt;</b> actorInstance</li>
	 * <li><b>&lt;point&gt;</b> point</li>
	 * <li><b>&lt;radio&gt;</b> facing</i>:</li><ul>
	 * <li>facingLeft:</li>
	 * <li>facingRight:</li>
	 * <li>facingTheSameDirection:</li>
	 * </ul>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
