package com.twp.platform;

import java.util.Random;

import com.twp.platform.PlatformLogicGDX.ActorAddable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformEvent;
import edu.elon.honors.price.data.PlatformEventIds;
import edu.elon.honors.price.data.PlatformEvent.Action;
import edu.elon.honors.price.data.PlatformEvent.Parameters;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.physics.Vector;

public class Interpreter extends PlatformEventIds {
	
	private int actionIndex;
	private PlatformEvent event;
	private PlatformLogicGDX logic;
	private Random rand = new Random();
	
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
			
			if (action.id == ID_SET_SWITCH) {
				int from, to;
				if (params.getInt() == 0) {
					from = params.getInt(1);
					to = from;
				} else {
					from = params.getParameters(1).getInt();
					to = params.getParameters(1).getInt(1);
				}
				
				for (int i = 0; i <= to; i++) {
					boolean argument;
					if (params.getInt(3) == 0) {
						argument = params.getBoolean(4);
					} else if (params.getInt(3) == 1) {
						argument = Globals.getSwitches()[params.getInt(4)]; 
					} else {
						argument = rand.nextBoolean();
					}
					Globals.getSwitches()[i] = argument;
				}
			}
			
			if (action.id == ID_SET_VARIABLE) {
				int from, to;
				if (params.getInt() == 0) {
					from = params.getInt(1);
					to = from;
				} else {
					from = params.getParameters(1).getInt();
					to = params.getParameters(1).getInt(1);
				}
				
				for (int i = 0; i <= to; i++) {
					int argument;
					if (params.getInt(3) == 0) {
						argument = params.getInt(4);
					} else if (params.getInt(3) == 1) {
						argument = Globals.getVariables()[params.getInt(4)]; 
					} else if (params.getInt(3) == 2) {
						int randFrom = params.getParameters(4).getInt();
						int randTo = params.getParameters(4).getInt(1);
						argument = randFrom + rand.nextInt(randTo - randFrom + 1);
					} else {
						PlatformBodyGDX body = logic.getBodyFromId(params.getInt(4));
						if (body != null) {
							Vector pos = body.getScaledPosition();
							if (params.getInt(5) == 0)
								argument = (int)(pos.getX() + 0.5f);
							else
								argument = (int)(pos.getY() + 0.5f);
						} else {
							argument = 0;
						}
					}
					
					int value = Globals.getVariables()[i];
					if (params.getInt(2) == 0) {
						value = argument;
					} else if (params.getInt(2) == 1) {
						value += argument;
					} else if (params.getInt(2) == 2) {
						value -= argument;
					} else if (params.getInt(2) == 3) {
						value *= argument;
					} else if (params.getInt(2) == 4) {
						value /= argument;
					} else {
						value %= argument;
					}
					
					Globals.getVariables()[i] = value;
				}
			}
			
			if (action.id == ID_CREATE_ACTOR) {
				int x, y, dir;
				if (params.getInt(1) == 0) {
					x = params.getInt(2);
				} else {
					x = Globals.getVariables()[params.getInt(2)];
				}
				if (params.getInt(3) == 0) {
					y = params.getInt(4);
				} else {
					y = Globals.getVariables()[params.getInt(4)];
				}
				if (params.getInt(5) == 0) {
					dir = -1;
				} else {
					dir = 1;
				}
				
				PlatformActor actor = logic.getGame().actors[params.getInt()];
				logic.queueActor(new ActorAddable(actor, x, y, dir));
			}
			
			actionIndex++;
		}
	}
	
	public void update() {
		
	}
}
