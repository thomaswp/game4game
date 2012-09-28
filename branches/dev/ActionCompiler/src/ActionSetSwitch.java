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
	
	public static class OneSwitchData extends ActionFragment {
		public OneSwitchData(Parameters params) {
			super(params);
		}
		
		public Switch switch1;
	}

	public boolean setOneSwitch;
	public OneSwitchData setOneSwitchData;

	
	public static class AllSwitchesFromData extends ActionFragment {
		public AllSwitchesFromData(Parameters params) {
			super(params);
		}
		
		public Switch from;
		public Switch to;
	}
	
	public boolean setAllSwitchesFrom;
	public AllSwitchesFromData setAllSwitchesFromData;

	
	public boolean setItTo;
	public SetItToData setItToData;
	
	public static class SetItToData extends ActionFragment {
		public SetItToData(Parameters params) {
			super(params);
		}
		
		public boolean setToOn;
		public boolean setToOff;
		public boolean setToASwitchsValue;
		public SetToASwitchsValueData setToASwitchsValueData;
		
		public static class SetToASwitchsValueData extends ActionFragment {
			public SetToASwitchsValueData(Parameters params) {
				super(params);
			}
			
			public Switch switch1;
		}
		
		
		public boolean setToARandomValue;
	}
	
	public boolean toggleIt;

}
