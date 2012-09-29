import edu.elon.honors.price.action.Action;
import edu.elon.honors.price.action.ActionFragment;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.types.Switch;


public class ActionSetSwitch extends Action {
	public static final String name = "Set Switch";
	public static final int id = 1;
	
	public static class OneSwitchData extends ActionFragment {
		public Switch switch1;
		
		@Override
		protected void readParams() {
			switch1 = params.getSwitch(0);
		}
	}

	public boolean setOneSwitch;
	public OneSwitchData setOneSwitchData;

	
	public static class AllSwitchesFromData extends ActionFragment {
		
		public Switch from;
		public Switch to;
		
		@Override
		protected void readParams() {
			from = params.getSwitch(0);
			to = params.getSwitch(1);
		}
	}
	
	public boolean setAllSwitchesFrom;
	public AllSwitchesFromData setAllSwitchesFromData;

	
	public boolean actionSetItTo;
	public SetItToData actionSetItToData;
	
	public static class SetItToData extends ActionFragment {
		
		public boolean setToOn;
		public boolean setToOff;
		public boolean setToASwitchsValue;
		public SetToASwitchsValueData setToASwitchsValueData;
		
		public static class SetToASwitchsValueData extends ActionFragment {
			
			public Switch switch1;
			
			@Override
			protected void readParams() {
				switch1 = params.getSwitch(0);
			}
		}
		
		public boolean setToARandomValue;
		
		@Override
		protected void readParams() {
			int setTo = params.getInt(0);
			setToOn = setTo == 0;
			setToOff = setTo == 1;
			setToASwitchsValue = setTo == 2;
			setToASwitchsValueData.setParameters(params.getParameters(1));
			setToARandomValue = setTo == 3;
		}
		
		public SetItToData() {
			setToASwitchsValueData = new SetToASwitchsValueData();
		}
	}
	
	public boolean actionToggleIt;
	
	@Override
	protected void readParams() {
		int set = params.getInt(0);
		setOneSwitch = set == 0;
		setOneSwitchData.setParameters(params.getParameters(1));
		setAllSwitchesFrom = set == 1;
		setAllSwitchesFromData.setParameters(params.getParameters(1));
		
		int action = params.getInt(2);
		actionSetItTo = action == 0;
		actionSetItToData.setParameters(params.getParameters(3));
		actionToggleIt = action == 1;
	}
	
	public ActionSetSwitch() {
		setOneSwitchData = new OneSwitchData();
		setAllSwitchesFromData = new AllSwitchesFromData();
		actionSetItToData = new SetItToData();
	}
}
