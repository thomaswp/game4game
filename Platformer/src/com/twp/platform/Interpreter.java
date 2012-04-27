package com.twp.platform;

import java.util.Random;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;

import com.twp.platform.PlatformLogic.ActorAddable;
import com.twp.platform.PlatformLogic.ObjectAddable;

import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Event;
import edu.elon.honors.price.data.ActionIds;
import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.physics.Vector;

public class Interpreter extends ActionIds {

	public static final float SCALE = PlatformLogic.SCALE;

	private int actionIndex;
	private PlatformLogic logic;
	private PhysicsHandler physics;
	private PlatformGame game;
	private Random rand = new Random();

	private Point point = new Point();
	private Vector vector = new Vector();
	private Paint paint = new Paint();


	public Interpreter(PlatformLogic logic) {
		this.logic = logic;
		this.physics = logic.getPhysics();
		this.game = logic.getGame();
	}

	public void doEvent(Event event) {
		if (event == null)
			return;

		//Game.debug(event.name);

		actionIndex = 0;

		while (actionIndex < event.actions.size()) {

			Action action = event.actions.get(actionIndex);

			Parameters params = action.params;

			Game.debug("%s: %s", ActionIds.ACTION_NAMES[action.id], params.toString());

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
							ActorBody body = readActorBody(params, 4, event);
							Vector pos = body.getScaledPosition();
							if (params.getInt(5) == 0) {
								argument = round(pos.getX());
							} else {
								argument = round(pos.getY());
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

				if (action.id == ID_POINT_OPERATION) {
					int x = readVariable(params, 0);
					int y = readVariable(params, 1);

					int ox, oy;
					int type = params.getInt(3);
					if (type == 0) {
						readPoint(params, 4, event, point);
						ox = point.x;
						oy = point.y;
					} else if (type == 1) {
						readVector(params, 4, event, vector);
						vector.multiply(readNumber(params, 5, event));
						ox = round(vector.getX());
						oy = round(vector.getY());
					} else if (type == 2) {
						ActorBody body = readActorBody(params, 4, event);
						ox = round(body.getScaledPosition().getX());
						oy = round(body.getScaledPosition().getY());
					} else {
						ObjectBody body = readObjectBody(params, 4, event);
						ox = round(body.getScaledPosition().getX());
						oy = round(body.getScaledPosition().getY());
					}

					int operator = params.getInt(2);
					if (operator == 0) {
						x = ox;
						y = oy;
					} else if (operator == 1) {
						x += ox;
						y += oy;
					} else {
						x -= ox;
						y -= oy;
					}

					Globals.getVariables()[params.getInt(0)] = x;
					Globals.getVariables()[params.getInt(1)] = y;
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
						physics.addActorBody(new ActorAddable(
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
					if (physics.getHero() != null) {
						physics.getHero().setOnLadder(params.getInt() == 0);
					}
				}

				if (action.id == ID_CREATE_OBJECT) {
					readPoint(params, 1, event, point);

					ObjectClass object = readObjectClass(params, 0);
					if (object != null) {
						physics.addObjectBody(new ObjectAddable(
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

				if (action.id == ID_IF) {
					boolean result;
					try {
						int type = params.getInt();
						if (type == 0) {
							boolean s = readSwitch(params, 1);
							boolean compare;
							int with = params.getInt(3); 
							if (with == 0) {
								compare = true;
							} else if (with == 1) {
								compare = false;
							} else {
								compare = readSwitch(params, 4);
							}
							if (params.getInt(2) == 1) compare = !compare;
							result = s == compare;
						} else if (type == 1) {
							int v = readVariable(params, 1);
							int op = params.getInt(2);
							int compare = readNumber(params, 3, event);
							if (op == 0) {
								result = v == compare;
							} else if (op == 1) {
								result = v != compare;
							} else if (op == 2) {
								result = v > compare;
							} else if (op == 3) {
								result = v >= compare;
							} else if (op == 4) {
								result = v < compare;
							} else {
								result = v <= compare;
							}
						} else {
							//ActorBody body = readActorBody(params, 1, event);
							result = false;
						}
					} catch (ParameterException e) {
						e.printStackTrace();
						result = false;
					}

					if (result == false) {
						while (actionIndex + 1 < event.actions.size()
								&& event.actions.get(actionIndex + 1).indent >
						action.indent) {
							actionIndex++;
						}
					}
				}
				
				if (action.id == ID_CHANGE_GRAVITY) {
					readVector(params, 0, event, vector);
					int mag = readNumber(params, 1, event);
					vector.multiply(mag);
					physics.setGravity(vector);
				}

				if (action.id == ID_UI_ACTION) {
					Parameters control = params.getParameters();
					boolean setTo = readBoolean(params, 2);
					if (params.getInt(1) == 0) {
						if (control.getInt() == 0) {
							readButton(control, 1).setVisible(setTo);
						} else {
							readJoystick(control, 1).setVisible(setTo);
						}
					} else {
						int index = control.getInt(1);
						if (control.getInt() == 0) {
							if (index >= game.uiLayout.buttons.size()) {
								throw new ParameterException("No Button found with id " + index);
							}
							game.uiLayout.buttons.get(index).defaultAction = setTo;
						} else {
							if (index >= game.uiLayout.joysticks.size()) {
								throw new ParameterException("No Button found with id " + index);
							}
							game.uiLayout.joysticks.get(index).defaultAction = setTo;
						}
					}
				}
				
				if (action.id == ID_DESTROY_OBJECT) {
					readObjectBody(params, 0, event).dispose();
				}
				
				if (action.id == ID_DRAW_TO_SCREEN) {
					int mode = params.getInt();
					if (mode == 0) {
						logic.clearDrawScreen();
					} else if (mode == 1) {
						paint.reset();
						paint.setColor(params.getInt(1));
						if (params.getInt(2) == 0) {
							paint.setStyle(Style.STROKE);
							paint.setStrokeWidth(3);
						} else {
							paint.setStyle(Style.FILL);
						}
						boolean world = params.getInt(6) == 0;
						int shape = params.getInt(3);
						if (shape == 0) {
							readPoint(params, 4, event, point);
							int x1 = point.x, y1 = point.y;
							readPoint(params, 5, event, point);
							int x2 = point.x, y2 = point.y;
							logic.drawLine(paint, x1, y1, x2, y2, world);
						} else if (shape == 1) {
							readPoint(params, 4, event, point);
							int rad = readNumber(params, 5, event);
							logic.drawCircle(paint, point.x, point.y, rad, world);
						}
					}
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
		if (i < 0 || i >= game.actors.length) {
			throw new ParameterException("Actor Class index out of bounds: " + i);
		}
		return game.actors[i];
	}

	private ObjectClass readObjectClass(Parameters params, int index) 
	throws ParameterException {
		int i = params.getInt(index);
		if (i < 0 || i >= game.objects.length) {
			throw new ParameterException("Object Class index out of bounds: " + i);
		}
		return game.objects[i];
	}

	private ActorBody readActorBody(Parameters params, int index, Event event) 
	throws ParameterException {
		Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			int id = ps.getInt(1);
			ActorBody body = physics.getActorBodyFromId(id);
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
			ObjectBody body = physics.getObjectBodyFromId(id);
			if (body == null) {
				throw new ParameterException("Object Instance not found: " + id);
			}
			return body;
		} else if (mode == 1) {
			if (event.tObject == null) {
				throw new ParameterException("No triggering object");
			}
			return (ObjectBody)event.tObject;
		} else {
			ObjectBody last = physics.getLastCreatedObject();
			if (last == null || last.isDisposed()) {
				throw new ParameterException("No last created object");
			}
			return last;
		}
	}

	private void readPoint(Parameters params, int index, Event event, 
			Point point) throws ParameterException {
		Parameters ps = params.getParameters(index);
		if (ps.getInt() == 0) {
			point.set(ps.getInt(1), ps.getInt(2));
		} else {
			point.set(readVariable(ps, 1), readVariable(ps, 2));
		}
	}

	private void readVector(Parameters params, int index, Event event,
			Vector vector) throws ParameterException {
		Parameters ps = params.getParameters(index);
		int type = ps.getInt();
		if (type == 0) {
			vector.set(ps.getFloat(1), ps.getFloat(2));
		} else if (type == 1) {
			if (event.tVector == null) {
				throw new ParameterException("No triggering vector");
			}
			vector.set((Vector)event.tVector);
		} else {
			JoyStick joy = readJoystick(ps, 1);
			vector.set(joy.getLastPull());
			vector.multiply(1f / joy.getLastPull().magnitude());
			Game.debug(vector);
		}
	}
	
	private JoyStick readJoystick(Parameters params, int index) 
	throws ParameterException {
		JoyStick joy = logic.getJoystick(params.getInt(index));
		if (joy == null) throw new ParameterException(
				"No joystick found with id " + params.getInt(index));
		return joy;
	}
	
	private Button readButton(Parameters params, int index) 
	throws ParameterException {
		Button button = logic.getButton(params.getInt(index));
		if (button == null) throw new ParameterException(
				"No button found with id " + params.getInt(index));
		return button;
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
	
	public boolean readBoolean(Parameters params, int index)
	throws ParameterException {
		Parameters ps = params.getParameters(index);
		int mode = ps.getInt();
		if (mode == 0) {
			return true;
		} else if (mode == 1) {
			return false;
		} else {
			return readSwitch(ps, 1);
		}
	}

	private int round(float x) {
		return (int)(x + 0.5f);
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
