package edu.elon.honors.price.input;

import java.util.ArrayList;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * A class which handles the all game input.
 * 
 * @author Thomas Price
 *
 */
public final class Input {

	/**
	 * An enum listing the possible states of a Key.
	 * 
	 * Released: The key was pressed and has just become lifted
	 * Lifted: The key is currently lifted
	 * Triggered: The key has just been pressed
	 * Held: The key has been pressed and is being held
	 * 
	 * @author Thomas Price
	 */
	public enum KeyStates {
		Lifted,
		Held,
		Triggered,
		Released
	}

	private static final int POINTERS = 4;
	private static final int EVENTS = 30;

	private static boolean multiTouch;
	
	//A list of unprocessed TouchEvents
	private static ArrayList<ArrayList<TouchEvent>> touchEvents = new ArrayList<ArrayList<TouchEvent>>(POINTERS);
	private static ArrayList<TouchState> touchStates;
	static {
		touchStates = new ArrayList<Input.TouchState>(POINTERS);
		for (int i = 0; i < POINTERS; i++) {
			touchStates.add(new TouchState());
		}
	}

	//A map of Keys and their current KeyState
	private static KeyStates[] keyMap = new KeyStates[100];
	
	private static Vibrator vibrator;

	public static boolean isMultiTouch() {
		return multiTouch;
	}

	public static void setMultiTouch(boolean multiTouch) {
		Input.multiTouch = multiTouch;
	}

	/**
	 * Returns whether or not the user is touching the screen
	 * @return
	 */
	public static boolean isTouchDown() {
		return isTouchDown(0);
	}

	public static boolean isTouchDown(int pid) {
		return touchStates.get(pid).touchDown;
	}

	/**
	 * Gets the last X Coordinate where the user touched the screen
	 * @return The X Coordinate
	 */
	public static float getLastTouchX() {
		return getLastTouchX(0);
	}

	public static float getLastTouchX(int pid) {
		return touchStates.get(pid).lastTouchX;
	}

	/**
	 * Gets the last Y Coordinate where the user touched the screen
	 * @return The Y Coordinate
	 */
	public static float getLastTouchY() {
		return getLastTouchY(0);
	}

	public static float getLastTouchY(int pid) {
		return touchStates.get(pid).lastTouchY;
	}

	/**
	 * Gets whether or not the user has just touched the screen 
	 * @return
	 */
	public static boolean isTapped() {
		if (multiTouch) {
			for (int i = 0; i < touchStates.size(); i++) {
				if (touchStates.get(i).tapped)
					return true;
			}
		} else {
			return touchStates.get(0).tapped;
		}
		return false;
	}

	public static int getTappedPointer() {
		for (int i = 0; i < touchStates.size(); i++) {
			if (touchStates.get(i).tapped)
				return i;
		}
		return -1;
	}

	/**
	 * Gets the X Coordinate of where the user first touched the
	 * screen during this touch event.
	 * @return The X Coordinate
	 */
	public static float getStartTouchX() {
		return getStartTouchX(0);
	}

	public static float getStartTouchX(int pid) {
		return touchStates.get(pid).startTouchX;
	}

	/**
	 * Gets the Y Coordinate of where the user first touched the
	 * screen during this touch event.
	 * @return The Y Coordinate
	 */
	public static float getStartTouchY() {
		return getStartTouchY(0);
	}

	public static float getStartTouchY(int pid) {
		return touchStates.get(pid).startTouchY;
	}

	/**
	 * Gets the X distance the user's touch has moved during this touch event.
	 * @return The X distance
	 */
	public static float getDistanceTouchX() {
		return getDistanceTouchX(0);
	}

	public static float getDistanceTouchX(int pid) {
		return touchStates.get(pid).lastTouchX - touchStates.get(pid).startTouchX;
	}

	/**
	 * Gets the Y distance the user's touch has moved during this touch event.
	 * @return The Y distance
	 */
	public static float getDistanceTouchY() {
		return getDistanceTouchY(0);
	}

	public static float getDistanceTouchY(int pid) {
		return touchStates.get(pid).lastTouchY - touchStates.get(pid).startTouchY;
	}
	/**
	 * Gets the state of the key with the given keycode
	 * @param keycode The keycode
	 * @return The state of this key
	 */
	public static KeyStates getState(int keycode) {
		if (keyMap[keycode] != null) {
			return keyMap[keycode];
		}
		return KeyStates.Lifted;
	}

	/**
	 * Returns whether or not the key with the given keycode
	 * has been tiggered this frame.
	 * @param keycode The keycode
	 * @return
	 */
	public static boolean isTriggered(int keycode) {
		return getState(keycode) == KeyStates.Triggered;
	}

	/**
	 * Returns whether or not the key with the given keycode
	 * is being held this frame.
	 * @param keycode The keycode
	 * @return
	 */
	public static boolean isHeld(int keycode) {
		return getState(keycode) == KeyStates.Held;
	}

	/**
	 * Returns whether or not the key with the given keycode
	 * has been released this frame.
	 * @param keycode The keycode
	 * @return
	 */
	public static boolean isReleased(int keycode) {
		return getState(keycode) == KeyStates.Released;
	}

	/**
	 * Returns whether or not the key with the given keycode
	 * is lifted this frame.
	 * @param keycode The keycode
	 * @return
	 */
	public static boolean isLifted(int keycode) {
		return getState(keycode) == KeyStates.Lifted;
	}

	/**
	 * Returns whether or not the key with the given keycode
	 * is down (Triggered or Held) this frame. 
	 * @param keycode The keycode
	 * @return
	 */
	public static boolean isDown(int keycode) {
		return isHeld(keycode) || isTriggered(keycode);
	}

	/**
	 * Returns whether or not the key with the given keycode
	 * is up (Lifted or Released) this frame. 
	 * @param keycode The keycode
	 * @return
	 */
	public static boolean isUp(int keycode) {
		return isLifted(keycode) || isReleased(keycode);
	}
	
	public static Vibrator getVibrator() {
		return vibrator;
	}

	public static void setVibrator(Vibrator vibrator) {
		Input.vibrator = vibrator;
	}

	private Input() {}

	public static void reset() {
		touchEvents.clear();
		for (int i = 0; i < touchStates.size(); i++) {
			touchStates.set(i, new TouchState());
		}
		keyMap = new KeyStates[keyMap.length];
	}
	
	/**
	 * Records a KeyUp event.
	 * @param keycode The KeyCode
	 * @param msg The Event Message
	 */
	public static void keyUp(int keycode, KeyEvent msg) {
		keyMap[keycode] = KeyStates.Released;
	}

	/**
	 * Records a KeyDown event.
	 * @param keycode The KeyCode
	 * @param msg The Event Message
	 */
	public static void keyDown(int keycode, KeyEvent msg) {
		keyMap[keycode] = KeyStates.Triggered;
	}

	/**
	 * Records a TouchEvent.
	 * @param v The View the was touched
	 * @param event The event to record
	 * @return True if the event was successfully handled
	 */
	public static boolean onTouch(View v, MotionEvent event) {
		//dumpEvent(event);
		synchronized(touchEvents) {
			synchronized (event) {
				for (int i = 0; i < event.getPointerCount(); i++) {
					int pid = event.getPointerId(i);

					if (pid == touchEvents.size()) {
						touchEvents.add(new ArrayList<Input.TouchEvent>(EVENTS));
					}
					if (pid == touchStates.size()) {
						touchStates.add(new TouchState());
					}

					ArrayList<Input.TouchEvent> touchEventList = touchEvents.get(pid);
					
					boolean added = false;
					//We want to override the last event if it wasn't up or down
					if (touchEventList.size() > 0) {
						TouchEvent e = touchEventList.get(touchEventList.size() - 1);
						if (e.getAction() == MotionEvent.ACTION_MOVE && event.getAction() == MotionEvent.ACTION_MOVE) {
							e.set(event, i);
							added = true;
						}
					}
					if (!added) {
						//record the TouchEvent, but don't process it until the next frame			
						touchEventList.add(new TouchEvent(event, i));
					}
				}
			}
		}
		return true;
	}

	
	@SuppressWarnings("unused")
	private static void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
				"POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_" ).append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid " ).append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")" );
		}
		sb.append("[" );
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#" ).append(i);
			sb.append("(pid " ).append(event.getPointerId(i));
			sb.append(")=" ).append((int) event.getX(i));
			sb.append("," ).append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";" );
		}
		sb.append("]" );
		Log.d("DUMP", sb.toString());
	}

	private static void handleTouchEvents() {
		//process the touch event
		String out = "";

		synchronized (touchEvents) {
			for (int i = 0; i < touchEvents.size(); i++) {

				ArrayList<Input.TouchEvent> touchEventList = touchEvents.get(i);

				TouchState touchState = touchStates.get(i);
				
				if (touchEventList.size() == 0) {
					continue;
				}

				TouchEvent event = touchEventList.get(0);

				if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
					//User just touched the screen
					touchState.touchDown = true;
					touchState.tapped = true;
					touchState.startTouchX = event.getX();
					touchState.startTouchY = event.getY();
				} else {
					//User is still touching the screen
					touchState.tapped = false;
				}

				if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_POINTER_UP) {
					//User just lifted
					touchState.touchDown = false;
				}

				touchState.lastTouchX =  event.getX();
				touchState.lastTouchY =  event.getY();

				out += i + "-" + touchState + ";";
			}
			
			for (int i = 0; i < touchEvents.size(); i++) {
				ArrayList<Input.TouchEvent> touchEventList = touchEvents.get(i);
				if (touchEventList.size() == 0)
					continue;
				if (touchEventList.size() == 1 && touchEventList.get(0).action == MotionEvent.ACTION_MOVE)
					continue;
				touchEventList.remove(0);
			}
		}

		//if (out.length() > 0) Game.debug(out);
	}

	public static void update(long timeElapsed) {

		//Get all the released and triggered keys
		for (int i = 0; i < keyMap.length; i++) {
			if (keyMap[i] == KeyStates.Released) {
				keyMap[i] = KeyStates.Lifted;
			} else if (keyMap[i] == KeyStates.Triggered) {
				keyMap[i] = KeyStates.Held;
			}
		}

		handleTouchEvents();
	}

	//Keeps track of the data in a TouchEvent
	private static class TouchEvent {
		float x, y;
		int action;

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public int getAction() {
			return action;
		}

		public TouchEvent(MotionEvent event, int i) {
			set(event, i);
		}
		
		public void set(MotionEvent event, int i) {
			x = event.getX(i);
			y = event.getY(i);
			
			int eventPid = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
			
			if (event.getPointerId(i) == eventPid || 
					event.getAction() == MotionEvent.ACTION_DOWN || 
					event.getAction() == MotionEvent.ACTION_UP) {
				action = event.getAction() & MotionEvent.ACTION_MASK;
			} else {
				action = MotionEvent.ACTION_MOVE;
			}
		}
	}

	private static class TouchState {
		public boolean touchDown, tapped;
		public float startTouchX, startTouchY, lastTouchX, lastTouchY;

		@Override
		public String toString() {
			return (touchDown ? (tapped ? "Tapped" : "Down") : "Up") + "{" + lastTouchX + ", " + lastTouchY + "}"; 
		}
	}
}
