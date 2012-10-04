package edu.elon.honors.price.maker.action;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import edu.elon.honors.price.data.Event.Action;
import edu.elon.honors.price.game.Game;

public abstract class ActionInterpreter<T extends ActionInstance> {

	private static final Class<?>[] interpreters = new Class<?>[] {
		InterpreterSetSwtich.class,
		InterpreterDebugBox.class
	};

	private static HashMap<Action, ActionInstance> actionMap =
			new HashMap<Action, ActionInstance>();

	private static HashMap<Action, ActionInterpreter<?>> interperaterMap =
			new HashMap<Action, ActionInterpreter<?>>();

//	private static HashMap<Action, Method> methodMap =
//			new HashMap<Action, Method>();

	protected Class<?> getActionClass() {
		return getActionClass(getClass());
	}

	//@SuppressWarnings({ "unchecked", "rawtypes" })
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
						break;
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

		invoke(instance.getClass(), interp, instance, gameState);

		//		if (!methodMap.containsKey(action)) {
		//			try {
		//				Method method = interp.getClass().getMethod("interperate", 
		//						instance.getClass(), GameState.class);
		//				methodMap.put(action, method);
		//				
		//			} catch (Exception e) {
		//				Game.debug(e);
		//			}
		//		}
		//		
		//		Method method = methodMap.get(action);
		//		try {
		//			method.invoke(interp, instance, gameState);
		//		} catch (Exception e) {
		//			Game.debug(e);
		//		}
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
