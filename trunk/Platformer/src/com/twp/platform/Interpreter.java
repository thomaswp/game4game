package com.twp.platform;

import java.util.Random;

import android.graphics.Point;

import com.twp.platform.PlatformLogic.ActorAddable;
import com.twp.platform.PlatformLogic.ObjectAddable;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.ActionIds;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.physics.Vector;

public class Interpreter extends ActionIds {

	public static final float SCALE = PlatformLogic.SCALE;

	private int actionIndex;
	private PlatformLogic logic;
	private Random rand = new Random();
	
	private Point point = new Point();
	private Vector vector = new Vector();
	
	public Interpreter(PlatformLogic logic) {
		this.logic = logic;
	}

	public void doEvent(Event event) {
		if (event == null)
			return;

		//Game.debug(event.name);

		actionIndex = 0;

		while (actionIndex < event.actions.size()) {

			Action action = event.actions.get(actionIndex);

			Parameters params = action.params;

			Game.debug("%s: %s", Action.ACTION_NAMES[action.id], params.toString());
			
			try {
				//003 Debug Box
				if (action.id == ID_DEBUG_BOX) {
					String text;
					if (params.getInt() == 0 ) {
						text = params.getString(1);
					} else if (params.getInt() == 1) {
						text = "" + readSwitch(params, 1);
					} else {
						text = "" + readVariable(params, 1);
					}
					Game.getCurrentGame().showToast(text);
				}

				//000 Set Switch
				if (action.id == ID_SET_SWITCH) {
					int from, to;
					if (params.getInt() == 0) {
						from = params.getInt(1);
						to = from;
					} else {
						from = params.getParameters(1).getInt();
						to = params.getParameters(1).getInt(1);
					}

					for (int i = from; i <= to; i++) {
						boolean argument;
						if (params.getInt(2) == 0) {
							if (params.getInt(3) == 0) {
								argument = true;
							} else if (params.getInt(3) == 1) {
								argument = false;
							} else if (params.getInt(3) == 2) {
								argument = readSwitch(params, 4); 
							} else {
								argument = rand.nextBoolean();
							}
						} else {
							argument = !Globals.getSwitches()[i];
						}
						Globals.getSwitches()[i] = argument;
					}
				}

				//001 Set Variable
				if (action.id == ID_SET_VARIABLE) {
					int from, to;
					if (params.getInt() == 0) {
						from = params.getInt(1);
						to = from;
					} else {
						from = params.getParameters(1).getInt();
						to = params.getParameters(1).getInt(1);
					}

					for (int i = from; i <= to; i++) {
						int argument;
						if (params.getInt(3) == 0) {
							argument = params.getInt(4);
						} else if (params.getInt(3) == 1) {
							argument = readVariable(params, 4); 
						} else if (params.getInt(3) == 2) {
							int randFrom = params.getParameters(4).getInt();
							int randTo = params.getParameters(4).getInt(1);
							argument = randFrom + rand.nextInt(randTo - randFrom + 1);
						} else {
							ActorBody body = logic.getActorBodyFromId(params.getInt(4));
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
							if (argument == 0) {
								throw new ParameterException("Divide by 0");
							} else {
								value /= argument;
							}
						} else {
							value %= argument;
						}

						Globals.getVariables()[i] = value;
					}
				}

				if (action.id == ID_CREATE_ACTOR) {
					readPoint(params, 1, event, point);
					
					int dir;
					if (params.getInt(2) == 0) {
						dir = -1;
					} else {
						dir = 1;
					}

					ActorClass actor = readActorClass(params, 0);
					if (actor != null)
						logic.addActorBody(new ActorAddable(
								actor, point.x, point.y, dir));
				}

				if (action.id == ID_MOVE_ACTOR) {

					readPoint(params, 1, event, point);
					
					int dir;
					if (params.getInt(2) == 0) {
						dir = -1;
					} else {
						dir = 1;
					}

					ActorBody body = readActorBody(params, 0, event);
					body.getBody().setTransform(point.x / SCALE, 
							point.y / SCALE, body.getBody().getAngle());
					if (params.getInt(2) != 2) {
						body.setDirectionX(dir);
					}
				}

				if (action.id == ID_ACTOR_BEHAVIOR) {
					ActorBody body = readActorBody(params, 0, event);
					if (body != null) {
						body.doBehavior(params.getInt(1), null);
					}
				}

				if (action.id == ID_HERO_SET_LADDER) {
					if (logic.getHero() != null) {
						logic.getHero().setOnLadder(params.getInt() == 0);
					}
				}

				if (action.id == ID_CREATE_OBJECT) {
					readPoint(params, 1, event, point);

					ObjectClass object = readObjectClass(params, 0);
					if (object != null) {
						logic.addObjectBody(new ObjectAddable(
								object, point.x, point.y));
					}
				}
				
				if (action.id == ID_MOVE_OBJECT) {

					readPoint(params, 1, event, point);

					ObjectBody body = readObjectBody(params, 0, event);
					body.getBody().setTransform(point.x / SCALE, 
							point.y / SCALE, body.getBody().getAngle());
				}
				
				if (action.id == ID_SET_VELOCITY) {
					PlatformBody body;
					if (params.getInt() == 0) {
						body = readActorBody(params, 1, event);
					} else {
						body = readObjectBody(params, 1, event);
					}
					readVector(params, 2, event, vector);
					int magnitude = readNumber(params, 3, event);
					vector.multiply(magnitude);
					body.setVelocity(vector.getX(), vector.getY());
				}
				
				if (action.id == ID_DEBUG_MESSAGE) {
					String text;
					if (params.getInt() == 0 ) {
						text = params.getString(1);
					} else if (params.getInt() == 1) {
						text = "" + readSwitch(params, 1);
					} else {
						text = "" + readVariable(params, 1);
					}
					Game.showMessage(text);
				}
				
			} catch (ParameterException e) {
				Game.debug(e.getMessage());
			} finally {
				actionIndex++;
			}

		}
	}

	private boolean readSwitch(Parameters params, int index) 
	throws ParameterException {
		int i = params.getInt(index);
		if (i < 0 || i >= Globals.getSwitches().length) {
			throw new ParameterException("Switch index out of bounds: " + i);
		}
		return Globals.getSwitches()[i];
	}

	private int readVariable(Parameters params, int index) 
	throws ParameterException {
		int i = params.getInt(index);
		if (i < 0 || i >= Globals.getVariables().length) {
			throw new ParameterException("Variable index out of bounds: " + i);
		}
		return Globals.getVariables()[i];
	}

	private ActorClass readActorClass(Parameters params, int index) 
	throws ParameterException {
		int i = params.getInt(index);
		if (i < 0 || i >= logic.game.actors.length) {
			throw new ParameterException("Actor Class index out of bounds: " + i);
		}
		return logic.game.actors[i];
	}

	private ObjectClass readObjectClass(Parameters params, int index) 
	throws ParameterException {
		int i = params.getInt(index);
		if (i < 0 || i >= logic.game.objects.length) {
			throw new ParameterException("Object Class index out of bounds: " + i);
		}
		return logic.game.objects[i];
	}

	private ActorBody readActorBody(Parameters params, int index, Event event) 
	throws ParameterException {
		Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			int id = ps.getInt(1);
			ActorBody body = logic.getActorBodyFromId(id);
			if (body == null) {
				throw new ParameterException("Actor Instance not found: " + id);
			}
			return body;
		} else {
			if (event.tActor == null) {
				throw new ParameterException("No triggering actor");
			}
			return (ActorBody)event.tActor;
		}
	}
	
	private ObjectBody readObjectBody(Parameters params, int index, Event event) 
	throws ParameterException {
		Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			int id = ps.getInt(1);
			ObjectBody body = logic.getObjectBodyFromId(id);
			if (body == null) {
				throw new ParameterException("Object Instance not found: " + id);
			}
			return body;
		} else {
			if (event.tObject == null) {
				throw new ParameterException("No triggering object");
			}
			return (ObjectBody)event.tObject;
		}
	}
	
	private void readPoint(Parameters params, int index, Event event, 
			Point point) throws ParameterException {
		Parameters ps = params.getParameters(index);
		if (ps.getInt() == 0) {
			point.set(ps.getInt(1), ps.getInt(2));
		} else {
			point.set(readVariable(ps, 0), readVariable(ps, 1));
		}
	}
	
	private void readVector(Parameters params, int index, Event event,
			Vector vector) throws ParameterException {
		Parameters ps = params.getParameters(index);
		if (ps.getInt() == 0) {
			vector.set(ps.getInt(1), ps.getInt(2));
		} else {
			if (event.tVector == null) {
				throw new ParameterException("No triggering vector");
			}
			vector.set((Vector)event.tVector);
		}
	}
	

	private int readNumber(Parameters params, int index, Event event) 
	throws ParameterException {
		Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			return ps.getInt(1);
		} else {
			return readVariable(ps, 1);
		}
	}

	public void update() {

	}

	private static class ParameterException extends Exception {
		private static final long serialVersionUID = 1L;

		public ParameterException(String message) {
			super(message);
		}
	}
}
