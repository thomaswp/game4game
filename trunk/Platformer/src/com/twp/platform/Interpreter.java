package com.twp.platform;

import java.util.Random;

import com.twp.platform.PlatformLogic.ActorAddable;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.ActionIds;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.physics.Vector;

public class Interpreter extends ActionIds {
	
	public static final float SCALE = PlatformLogic.SCALE;
	
	private int actionIndex;
	private PlatformLogic logic;
	private Random rand = new Random();
	
	public Interpreter(PlatformLogic logic) {
		this.logic = logic;
	}
	
	public void doEvent(Event event) {
		if (event == null)
			return;
		
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
						PlatformBody body = logic.getBodyFromId(params.getInt(4));
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
				
				ActorClass actor = logic.getGame().actors[params.getInt()];
				if (actor != null)
					logic.addBody(new ActorAddable(actor, x, y, dir));
			}
			
			if (action.id == ID_MOVE_ACTOR) {
				
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
				
				PlatformBody body = logic.getBodyFromId(params.getInt());
				if (body != null) {
					body.getBody().setTransform(x / SCALE, y / SCALE, body.getBody().getAngle());
					if (params.getInt(5) != 2)
						body.setDirectionX(dir);
				}
			}
			
			if (action.id == ID_ACTOR_BEHAVIOR) {
				PlatformBody body = logic.getBodyFromId(params.getInt());
				if (body != null) {
					body.doBehavior(params.getInt(1), null);
				}
			}
			
			if (action.id == ID_HERO_SET_LADDER) {
				if (logic.getHero() != null) {
					logic.getHero().setOnLadder(params.getInt() == 0);
				}
			}
			
			actionIndex++;
		}
	}
	
	public void update() {
		
	}
}
