package edu.elon.honors.price.maker.action;

public class ActionFactory {
	public final static int[] ACTION_IDS = new int[] {
		0,
		1,
		2,
		3,
		4,
		5,
		7,
		8,
		9,
		10,
		11,
		12,
		13,
		14,
		15,
		16,
		17,
		18,
		19,
		20,
		21,
		22,
		23,
		24,
		25,
		26,
	};
	public final static String[] ACTION_NAMES = new String[] {
		"Set Switch",
		"Set Variable",
		"Create Actor",
		"Debug Box",
		"Move Actor",
		"Trigger Actor Behavior",
		"",
		"If...",
		"Create Object",
		"Move Object",
		"Set Velocity",
		"Debug Message",
		"Point Operation",
		"Change Gravity",
		"UI Action",
		"Destroy Object",
		"Draw to Screen",
		"Loop...",
		"Trigger Actor Animation",
		"Animate",
		"Wait",
		"Stop",
		"Else",
		"Trigger Event",
		"Change Color",
		"Set Behavior Parameters",
		"Change Scale",
	};
	public final static String[] ACTION_CATEGORIES = new String[] {
		"Variables",
		"Variables",
		"Actor",
		"Debug",
		"Actor",
		"Actor",
		"Misc",
		"Control",
		"Object",
		"Object",
		"Physics",
		"Debug",
		"Variables",
		"Physics",
		"UI",
		"Object",
		"UI",
		"Control",
		"Animate",
		"Animate",
		"Control",
		"Control",
		"Hidden",
		"Control",
		"Actor|Object",
		"Actor|Object",
		"Actor|Object",
	};
	public static ScriptableInstance getInstance(int id) {
		if (id == ActionSetSwitch.ID) return new ActionSetSwitch();
		if (id == ActionSetVariable.ID) return new ActionSetVariable();
		if (id == ActionCreateActor.ID) return new ActionCreateActor();
		if (id == ActionDebugBox.ID) return new ActionDebugBox();
		if (id == ActionMoveActor.ID) return new ActionMoveActor();
		if (id == ActionTriggerActorBehavior.ID) return new ActionTriggerActorBehavior();
		if (id == ActionIf.ID) return new ActionIf();
		if (id == ActionCreateObject.ID) return new ActionCreateObject();
		if (id == ActionMoveObject.ID) return new ActionMoveObject();
		if (id == ActionSetVelocity.ID) return new ActionSetVelocity();
		if (id == ActionDebugMessage.ID) return new ActionDebugMessage();
		if (id == ActionPointOperation.ID) return new ActionPointOperation();
		if (id == ActionChangeGravity.ID) return new ActionChangeGravity();
		if (id == ActionUIAction.ID) return new ActionUIAction();
		if (id == ActionDestroyObject.ID) return new ActionDestroyObject();
		if (id == ActionDrawToScreen.ID) return new ActionDrawToScreen();
		if (id == ActionLoop.ID) return new ActionLoop();
		if (id == ActionTriggerActorAnimation.ID) return new ActionTriggerActorAnimation();
		if (id == ActionAnimate.ID) return new ActionAnimate();
		if (id == ActionWait.ID) return new ActionWait();
		if (id == ActionStop.ID) return new ActionStop();
		if (id == ActionElse.ID) return new ActionElse();
		if (id == ActionTriggerEvent.ID) return new ActionTriggerEvent();
		if (id == ActionChangeColor.ID) return new ActionChangeColor();
		if (id == ActionSetBehaviorParameters.ID) return new ActionSetBehaviorParameters();
		if (id == ActionChangeScale.ID) return new ActionChangeScale();
		return null;
	}
	public static boolean isParent(int id) {
		if (id == ActionIf.ID) return true;
		if (id == ActionLoop.ID) return true;
		if (id == ActionElse.ID) return true;
		return false;
	}
	public static java.util.LinkedList<Class<?>> getInterpreters() {
		java.util.LinkedList<Class<?>> classes = new java.util.LinkedList<Class<?>>();
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterSetSwitch"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterSetVariable"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterCreateActor"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterDebugBox"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterMoveActor"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterTriggerActorBehavior"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterIf"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterCreateObject"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterMoveObject"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterSetVelocity"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterDebugMessage"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterPointOperation"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterChangeGravity"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterUIAction"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterDestroyObject"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterDrawToScreen"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterLoop"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterTriggerActorAnimation"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterAnimate"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterWait"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterStop"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterElse"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterTriggerEvent"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterChangeColor"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterSetBehaviorParameters"));
		} catch (Exception e) { }
		try {
			classes.add(Class.forName("edu.elon.honors.price.maker.action.InterpreterChangeScale"));
		} catch (Exception e) { }
		return classes;
	}
}
