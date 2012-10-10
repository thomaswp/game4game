package edu.elon.honors.price.maker.action;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Random;

import com.twp.platform.PlatformLogic;

import android.util.SparseArray;

import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.game.Game;

public abstract class ActionInterpreter<T extends ActionInstance> {

	Random rand = new Random();
	public static final float SCALE = PlatformLogic.SCALE;
	
	private static final Class<?>[] interpreters = new Class<?>[] {
		InterpreterSetSwtich.class,
		InterpreterDebugBox.class
	};

	private static HashMap<Action, ActionInstance> actionMap =
			new HashMap<Action, ActionInstance>();

	private static SparseArray<ActionInterpreter<?>> interperaterMap =
			new SparseArray<ActionInterpreter<?>>();

	protected Class<?> getActionClass() {
		return getActionClass(getClass());
	}

	private static Class<?> getActionClass(Class<?> cls) {
		ParameterizedType parameterizedType =
				(ParameterizedType) cls.getGenericSuperclass();
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}

	protected abstract void interperate(T action, PlatformGameState gameState) 
			throws ParameterException;

	protected void updateControl(ActionControl control) {
		control.actionIndex++;
	}
		
	public static void interperate(Action action, PlatformGameState gameState,
			ActionControl control) throws ParameterException {
		
		ActionInstance instance = actionMap.get(action);
		if (instance == null) {
			instance = ActionFactory.getInstance(action.id);
			if (instance == null) throw new ParameterException(
					"Invalid action id: " + action.id);
			instance.setParameters(action.params);
			actionMap.put(action, instance);
		}

		ActionInterpreter<?> interp = interperaterMap.get(action.id);
		if (interp == null) {
			for (Class<?> cls : interpreters) {
				if (getActionClass(cls) == instance.getClass()) {
					try {
						interp = (ActionInterpreter<?>)cls
								.getConstructors()[0]
										.newInstance();
						break;
					} catch (Exception e) {
						Game.debug(e);
					}
				}
			}
			if (interp == null) throw new ParameterException(
					"No interpreter for action #" + action.id);
			interperaterMap.put(action.id, interp);
		}


		invoke(instance.getClass(), interp, instance, gameState);
		interp.updateControl(control);
	}

	@SuppressWarnings("unchecked")
	private static <S extends ActionInstance> void invoke(Class<S> cls, 
			ActionInterpreter<?> interp, ActionInstance instance,
			PlatformGameState gameState) throws ParameterException {
		ActionInterpreter<S> castInterp = (ActionInterpreter<S>)interp;
		S castAction = (S)instance;
		castInterp.interperate(castAction, gameState);
	}
}
