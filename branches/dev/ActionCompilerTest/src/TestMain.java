import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.maker.action.ActionDebugBox;
import edu.elon.honors.price.maker.action.ActionInterpreter;
import edu.elon.honors.price.maker.action.ParameterException;
import edu.elon.honors.price.maker.action.PlatformGameState;


public class TestMain {
	public static void main(String[] args) throws ParameterException {
		Parameters params = new Parameters();
		params.addParam(0);
		params.addParam("Hello");
		Action action = new Action(3, params);
		PlatformGameState gameState = null;
		ActionInterpreter.interperate(action, gameState);
	}
}
