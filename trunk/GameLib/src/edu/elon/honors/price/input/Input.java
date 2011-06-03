package edu.elon.honors.price.input;

import java.util.HashMap;
import java.util.LinkedList;

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

	//Is the user touching the screen, have they just tapped it
	private static boolean touchDown = false, tapped = false;
	//Last coordinates of a TouchEvent
	private static float lastTouchX = 0, lastTouchY = 0;
	//Starting coordinates of a TouchEvent
	private static float startTouchX = 0, startTouchY = 0;

	//A list of unprocessed TouchEvents
	private static LinkedList<TouchEvent> touchEvents = new LinkedList<TouchEvent>();

	//A map of Keys and their current KeyState
	private static HashMap<Integer, KeyStates> keyMap = new HashMap<Integer, Input.KeyStates>();

	/**
	 * Returns whether or not the user is touching the screen
	 * @return
	 */
	public static boolean isTouchDown() {
		return touchDown;
	}

	/**
	 * Gets the last X Coordinate where the user touched the screen
	 * @return The X Coordinate
	 */
	public static float getLastTouchX() {
		return lastTouchX;
	}

	/**
	 * Gets the last Y Coordinate where the user touched the screen
	 * @return The Y Coordinate
	 */
	public static float getLastTouchY() {
		return lastTouchY;
	}

	/**
	 * Gets whether or not the user has just touched the screen 
	 * @return
	 */
	public static boolean isTapped() {
		return tapped;
	}

	/**
	 * Gets the X Coordinate of where the user first touched the
	 * screen during this touch event.
	 * @return The X Coordinate
	 */
	public static float getStartTouchX() {
		return startTouchX;
	}

	/**
	 * Gets the Y Coordinate of where the user first touched the
	 * screen during this touch event.
	 * @return The Y Coordinate
	 */
	public static float getStartTouchY() {
		return startTouchY;
	}

	/**
	 * Gets the X distance the user's touch has moved during this touch event.
	 * @return The X distance
	 */
	public static float getDistanceTouchX() {
		return lastTouchX - startTouchX;
	}

	/**
	 * Gets the Y distance the user's touch has moved during this touch event.
	 * @return The Y distance
	 */
	public static float getDistanceTouchY() {
		return lastTouchY - startTouchY;
	}

	/**
	 * Gets the state of the key with the given keycode
	 * @param keycode The keycode
	 * @return The state of this key
	 */
	public static KeyStates getState(int keycode) {
		if (keyMap.containsKey(keycode)) {
			return keyMap.get(keycode);
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

	private Input() {}

	/**
	 * Records a KeyUp event.
	 * @param keycode The KeyCode
	 * @param msg The Event Message
	 */
	public static void keyUp(int keycode, KeyEvent msg) {
		keyMap.put(keycode, KeyStates.Released);
	}

	/**
	 * Records a KeyDown event.
	 * @param keycode The KeyCode
	 * @param msg The Event Message
	 */
	public static void keyDown(int keycode, KeyEvent msg) {
		keyMap.put(keycode, KeyStates.Triggered);
	}

	/**
	 * Records a TouchEvent.
	 * @param v The View the was touched
	 * @param event The event to record
	 * @return True if the event was successfully handled
	 */
	public static boolean onTouch(View v, MotionEvent event) {
		synchronized(touchEvents) {
			//We want to override the last event if it wasn't up or down
			if (touchEvents.size() > 0) {
				TouchEvent e = touchEvents.get(0);
				if (!(e.getAction() == MotionEvent.ACTION_DOWN || 
						e.getAction() == MotionEvent.ACTION_UP)) {
					touchEvents.remove(0);
				}
			}
			//record the TouchEvent, but don't process it until the next frame			
			touchEvents.add(new TouchEvent(event));
		}
		return true;
	}

	private static void handleTouchEvents() {
		//process the touch event

		if (touchEvents.size() == 0)
			return;

		synchronized (touchEvents) {
			TouchEvent event = touchEvents.remove(0);

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				//User just touched the screen
				touchDown = true;
				tapped = true;
				startTouchX = event.getX();
				startTouchY = event.getY();
			} else {
				//User is still touching the screen
				tapped = false;
			}

			if (event.getAction() == MotionEvent.ACTION_UP) {
				//User just lifted
				touchDown = false;
			}

			lastTouchX =  event.getX();
			lastTouchY =  event.getY();
		}
	}

	public static void update(long timeElapsed) {
		LinkedList<Integer> released = new LinkedList<Integer>();
		LinkedList<Integer> triggered = new LinkedList<Integer>();

		//Get all the released and triggered keys
		for (int key : keyMap.keySet()) {
			if (keyMap.get(key) == KeyStates.Released) {
				released.add(key);
			} else if (keyMap.get(key) == KeyStates.Triggered) {
				triggered.add(key);
			}
		}


		//Change their state to Lifted or Held if they're staying that way 
		for (int key : released) {
			keyMap.put(key, KeyStates.Lifted);
		}

		for (int key : triggered) {
			keyMap.put(key, KeyStates.Held);
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

		public TouchEvent(MotionEvent event) {
			x = event.getX();
			y = event.getY();
			action = event.getAction();
		}
	}
}
