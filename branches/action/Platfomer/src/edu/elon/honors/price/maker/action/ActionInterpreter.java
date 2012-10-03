package edu.elon.honors.price.maker.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.game.Game;

public abstract class ActionInterpreter<T extends ActionInstance> {
	
	private static final Class<?>[] interpreters = new Class<?>[] {
			InterpreterDebugBox.class
	};
	
	private static HashMap<Action, ActionInstance> actionMap =
			new HashMap<Action, ActionInstance>();
	
	private static HashMap<Action, ActionInterpreter<?>> interperaterMap =
			new HashMap<Action, ActionInterpreter<?>>();
	
	protected Class<?> getActionClass() {
		return getActionClass(getClass());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Class<?> getActionClass(Class<?> cls) {
		ParameterizedType parameterizedType =
				(ParameterizedType) cls.getGenericSuperclass();
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}
	
	protected abstract void interperate(T action, PlatformGameState gameState) 
			throws ParameterException;
	
	public static void interperate(Action action, PlatformGameState gameState) 
			throws ParameterException {
		if (!actionMap.containsKey(action)) {
			ActionInstance a = ActionFactory.getInstance(action.id);
			if (a == null) throw new ParameterException(
					"Invalid action id: " + action.id);
			a.setParameters(action.params);
			actionMap.put(action, a);
		}

		ActionInstance instance = actionMap.get(action);
		
		if (!interperaterMap.containsKey(action)) {
			ActionInterpreter<?> interp = null;
			for (Class<?> cls : interpreters) {
				if (getActionClass(cls) == instance.getClass()) {
					try {
						interp = (ActionInterpreter<?>)cls
								.getConstructors()[0]
								.newInstance();
					} catch (Exception e) {
						Game.debug(e);
					}
				}
			}
			if (interp == null) throw new ParameterException(
					"No interpreter for action #" + action.id);
			interperaterMap.put(action, interp);
		}
		
		ActionInterpreter<?> interp = interperaterMap.get(action);
		//interp.getClass().getMethod("interperate", instance.getClass(), GameState.class);
	}
}
