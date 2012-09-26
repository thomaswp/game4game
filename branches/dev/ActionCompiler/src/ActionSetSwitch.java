import edu.elon.honors.price.action.Action;
import edu.elon.honors.price.action.ActionFragment;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.types.Switch;


public class ActionSetSwitch extends Action {
	public static final String name = "Set Switch";
	public static final int id = 1;
	
	public ActionSetSwitch(Parameters params) {
		super(params);
	}
		
	public enum NumSwitchesChoice {
		OneSwitch,
		AllSwitchesFrom
	}

	public NumSwitchesChoice numSwitches;
	
	public static class ChoiceOneSwitch extends ActionFragment {
		public ChoiceOneSwitch(Parameters params) {
			super(params);
		}
		
		public Switch switch1;
	}

	public ChoiceOneSwitch numSwitches_OneSwitch;
	
	public static class ChoiceAllSwitchesFrom extends ActionFragment {
		public ChoiceAllSwitchesFrom(Parameters params) {
			super(params);
		}
		
		public Switch from;
		public Switch to;
	}
	
	public ChoiceAllSwitchesFrom numSwitches_AllSwitchesFrom;
	
	public enum SetOrToggleChoice {
		SetItTo,
		ToggleIt
	}
	
	public SetOrToggleChoice setOrToggle;
	
	public static class ChoiceSet extends ActionFragment {
		public ChoiceSet(Parameters params) {
			super(params);
		}
		
		public enum SetToChoice {
			On,
			Off,
			ASwitchsValue,
			ARandomValue
		}
		
		public SetToChoice setTo;
		
		public static class ChoiceASwitchsValue extends ActionFragment {
			public ChoiceASwitchsValue(Parameters params) {
				super(params);
			}
			
			public Switch switch1;
		}
	}
	
	public ChoiceSet setOrToggle_Set;

}
