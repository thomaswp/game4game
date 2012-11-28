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
public class ActionUIAction extends ActionInstance {				// ActionFragmentWriter.writeHeader()
	public static final String NAME = "UI Action";					// ActionWriter.writeHeader()
	public static final int ID = 14;								// ActionWriter.writeHeader()
	public static final String CATEGORY = "UI";						// ActionWriter.writeHeader()
																	// ActionWriter.writeHeader()
	/** Type: <b>&lt;ui&gt;</b> */									// ActionFragmentWriter.writeElement()
	public Parameters ui;											// ActionFragmentWriter.writeElement()
	public UIControl readUi(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readUi(ui);								// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
	public boolean setItsVisibility;								// ActionFragmentWriter.writeElement()
	public boolean setItsDefaultBehavior;							// ActionFragmentWriter.writeElement()
	/** Type: <b>&lt;boolean&gt;</b> */								// ActionFragmentWriter.writeElement()
	public Parameters to;											// ActionFragmentWriter.writeElement()
	public boolean readTo(GameState gameState) throws ParameterException {// ActionFragmentWriter.writeElement()
		return gameState.readBoolean(to);							// ActionFragmentWriter.writeElement()
	}																// ActionFragmentWriter.writeElement()
																	// ActionFragmentWriter.writeReadParams()
	@Override														// ActionFragmentWriter.writeReadParams()
	public void readParams(Iterator iterator) {						// ActionFragmentWriter.writeReadParams()
		ui = iterator.getParameters();								// ActionFragmentWriter.writeReadParams()
		int setIts = iterator.getInt();								// ActionFragmentWriter.writeReadParams()
		setItsVisibility = setIts == 0;								// ActionFragmentWriter.writeReadParams()
		setItsDefaultBehavior = setIts == 1;						// ActionFragmentWriter.writeReadParams()
																	// ActionFragmentWriter.writeReadParams()
		to = iterator.getParameters();								// ActionFragmentWriter.writeReadParams()
	}																// ActionFragmentWriter.writeReadParams()
	/**
	 * 014 <b><i>UI Action</i></b> (UI)<br />
	 * <ul>
	 * <li><b>&lt;ui&gt;</b> ui</li>
	 * <li><b>&lt;radio&gt;</b> setIts</i>:</li><ul>
	 * <li>setItsVisibility:</li>
	 * <li>setItsDefaultBehavior:</li>
	 * </ul>
	 * <li><b>&lt;boolean&gt;</b> to</li>
	 * </ul>
	 */																// ActionFragmentWriter.writeJavadoc()
	public static final String JAVADOC = "";						// ActionFragmentWriter.writeJavadoc()
}																	// ActionFragmentWriter.writeFooter()
