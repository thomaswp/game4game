package com.twp.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import edu.elon.honors.price.data.PlatformEvent;
import edu.elon.honors.price.data.PlatformEventIds;
import edu.elon.honors.price.data.PlatformEvent.Action;
import edu.elon.honors.price.data.PlatformEvent.Parameters;
import edu.elon.honors.price.game.Game;

public class Interpreter extends PlatformEventIds {
	
	private int actionIndex;
	private PlatformEvent event;
	private PlatformLogicGDX logic;
	
	public Interpreter(PlatformLogicGDX logic) {
		this.logic = logic;
	}
	
	public void doEvent(PlatformEvent event) {
		if (event == null)
			return;
		
		this.event = event;
		actionIndex = 0;

		while (actionIndex < event.actions.size()) {
			
			Action action = event.actions.get(actionIndex);
			
			Parameters params = action.params;
			
			if (action.id == ID_DEBUG_BOX) {
				String text;
				if (params.getInt() == 0 ) {
					text = params.getString(1);
				} else if (params.getInt() == 1) {
					text = "" + Globals.getSwitches()[params.getInt(1)];
				} else {
					text = "" + Globals.getVariables()[params.getInt(1)];
				}
				Game.getCurrentGame().showToast(text);
			}
			
			
			
			actionIndex++;
		}
	}
	
	public void update() {
		
	}
}
