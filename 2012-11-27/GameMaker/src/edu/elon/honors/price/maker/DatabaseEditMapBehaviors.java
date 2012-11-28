package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Behavior.BehaviorType;
import android.content.Intent;
import android.os.Bundle;

public class DatabaseEditMapBehaviors extends DatabaseActivity {
	
	SelectorBehaviorInstances selectorBehaviorInstances;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database_edit_map_behaviors);
		setDefaultButtonActions();
		
		selectorBehaviorInstances = (SelectorBehaviorInstances)
				findViewById(R.id.selectorBehaviorInstances);
		
		selectorBehaviorInstances.setBehaviors(game.getSelectedMap().behaviors, 
				BehaviorType.Map);
		selectorBehaviorInstances.populate(game);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			selectorBehaviorInstances.populate(game);
			selectorBehaviorInstances.onActivityResult(requestCode, data);
		}
	}
	
}
