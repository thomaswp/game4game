package edu.elon.honors.price.maker.action;							// GameStateWriter.writeHeader()
																	// GameStateWriter.writeHeader()
import edu.elon.honors.price.maker.action.*;						// GameStateWriter.writeHeader()
import edu.elon.honors.price.data.*;								// GameStateWriter.writeHeader()
import edu.elon.honors.price.data.types.*;							// GameStateWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters.Iterator;		// GameStateWriter.writeHeader()
import edu.elon.honors.price.data.Event.Parameters;					// GameStateWriter.writeHeader()
import com.twp.platform.*;											// GameStateWriter.writeHeader()
import edu.elon.honors.price.physics.*;								// GameStateWriter.writeHeader()
import edu.elon.honors.price.input.*;								// GameStateWriter.writeHeader()
public interface GameState {										// GameStateWriter.writeHeader()
	public Point readPoint(Parameters params);						// GameStateWriter.writeHeader()
	public JoyStick readJoystick(Parameters params);				// GameStateWriter.writeHeader()
	public Button readButton(Parameters params);					// GameStateWriter.writeHeader()
	public Vector readVector(Parameters params);					// GameStateWriter.writeHeader()
	public ActorClass readActorClass(Parameters params);			// GameStateWriter.writeHeader()
	public ObjectBody readObjectInstance(Parameters params);		// GameStateWriter.writeHeader()
	public boolean readBoolean(Parameters params);					// GameStateWriter.writeHeader()
	public int readNumber(Parameters params);						// GameStateWriter.writeHeader()
	public ObjectClass readObjectClass(Parameters params);			// GameStateWriter.writeHeader()
	public boolean readSwitch(Switch params);						// GameStateWriter.writeHeader()
	public ActorBody readActorInstance(Parameters params);			// GameStateWriter.writeHeader()
	public int readVariable(Variable params);						// GameStateWriter.writeHeader()
}																	// GameStateWriter.writeHeader()
