import edu.elon.honors.price.data.Event.Parameters.Iterator;
import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.maker.action.ScriptableInstance;
import edu.elon.honors.price.maker.action.ScriptableFragment;


public class ActionSetSwitchSample extends ScriptableInstance {
	public static final String name = "Set Switch";
	public static final int id = 1;
	
	public static class OneSwitchData extends ScriptableFragment {
		public Switch switch1;
		
		@Override
		protected void readParams(Iterator iterator) {
			switch1 = iterator.getSwitch();
		}
	}

	public boolean setOneSwitch;
	public OneSwitchData setOneSwitchData;

	
	public static class AllSwitchesFromData extends ScriptableFragment {
		
		public Switch from;
		public Switch to;
		
		@Override
		protected void readParams(Iterator iterator) {
			from = iterator.getSwitch();
			to = iterator.getSwitch();
		}
	}
	
	public boolean setAllSwitchesFrom;
	public AllSwitchesFromData setAllSwitchesFromData;

	
	public boolean actionSetItTo;
	public SetItToData actionSetItToData;
	
	public static class SetItToData extends ScriptableFragment {
		
		public boolean setToOn;
		public boolean setToOff;
		public boolean setToASwitchsValue;
		public SetToASwitchsValueData setToASwitchsValueData;
		
		public static class SetToASwitchsValueData extends ScriptableFragment {
			
			public Switch switch1;
			
			@Override
			protected void readParams(Iterator iterator) {
				switch1 = iterator.getSwitch();
			}
		}
		
		public boolean setToARandomValue;
		
		@Override
		protected void readParams(Iterator iterator) {
			int setTo = iterator.getInt();
			setToOn = setTo == 0;
			setToOff = setTo == 1;
			setToASwitchsValue = setTo == 2;
			setToASwitchsValueData.setParameters(iterator.getParameters());
			setToARandomValue = setTo == 3;
		}
		
		public SetItToData() {
			setToASwitchsValueData = new SetToASwitchsValueData();
		}
	}
	
	public boolean actionToggleIt;
	
	@Override
	protected void readParams(Iterator iterator) {
		int set = iterator.getInt();
		setOneSwitch = set == 0;
		setOneSwitchData.setParameters(iterator.getParameters());
		setAllSwitchesFrom = set == 1;
		setAllSwitchesFromData.setParameters(iterator.getParameters());
		
		int action = iterator.getInt();
		actionSetItTo = action == 0;
		actionSetItToData.setParameters(iterator.getParameters());
		actionToggleIt = action == 1;
	}
	
	public ActionSetSwitchSample() {
		setOneSwitchData = new OneSwitchData();
		setAllSwitchesFromData = new AllSwitchesFromData();
		actionSetItToData = new SetItToData();
	}
}
