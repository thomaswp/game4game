package edu.elon.honors.price.maker;

import android.os.Bundle;
import edu.elon.honors.price.data.Event.RegionTrigger;

public class DatabaseEditTriggerRegion extends DatabaseActivity {
	private RegionTrigger originalTrigger, trigger;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_trigger_region);
		setDefaultButtonActions();
		
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("trigger")) {
			originalTrigger = (RegionTrigger)extras.getSerializable("trigger");
			trigger = (RegionTrigger)extras.getSerializable("trigger");
		} else {
			originalTrigger = new RegionTrigger();
			trigger = new RegionTrigger();
		}
	}
}
