package com.twp.input;

import java.util.HashMap;
import java.util.LinkedList;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public final class Input {
	
	public enum KeyStates {
		Lifted,
		Held,
		Triggered,
		Released
	}

	private static HashMap<Integer, KeyStates> keyMap;
	static {
		keyMap = new HashMap<Integer, KeyStates>();
	}
	
	public static boolean isTouchDown() {
		return touchDown;
	}

	public static double getLastTouchX() {
		return lastTouchX;
	}

	public static double getLastTouchY() {
		return lastTouchY;
	}
	
	private static int frameCount = 0;
	private static boolean touchDown = false;
	private static double lastTouchX = 0, lastTouchY = 0;

	public static KeyStates getState(int keycode) {
		if (keyMap.containsKey(keycode)) {
			return keyMap.get(keycode);
		}
		return KeyStates.Lifted;
	}
	
	public static boolean isTriggered(int keycode) {
		return getState(keycode) == KeyStates.Triggered;
	}
	
	public static boolean isHeld(int keycode) {
		return getState(keycode) == KeyStates.Held;
	}
	
	public static boolean isReleased(int keycode) {
		return getState(keycode) == KeyStates.Released;
	}
	
	public static boolean isLifted(int keycode) {
		return getState(keycode) == KeyStates.Lifted;
	}
	
	public static boolean isDown(int keycode) {
		return isHeld(keycode) || isTriggered(keycode);
	}
	
	public static boolean isUp(int keycode) {
		return isLifted(keycode) || isReleased(keycode);
	}
	
	private Input() {}
	
	public static void keyUp(int keycode, KeyEvent msg) {
		keyMap.put(keycode, KeyStates.Released);
	}
	
	public static void keyDown(int keycode, KeyEvent msg) {
		keyMap.put(keycode, KeyStates.Triggered);
	}
	
	public static boolean onTouch(View v, MotionEvent event) {

		
		lastTouchX =  event.getX();
		lastTouchY =  event.getY();
		
		return true;
	}
	
	public static void update() {
		frameCount++;
		
		LinkedList<Integer> released = new LinkedList<Integer>();
		LinkedList<Integer> triggered = new LinkedList<Integer>();
		
		for (int key : keyMap.keySet()) {
			if (keyMap.get(key) == KeyStates.Released) {
				released.add(key);
			} else if (keyMap.get(key) == KeyStates.Triggered) {
				triggered.add(key);
			}
		}
		
		for (int key : released) {
			keyMap.put(key, KeyStates.Lifted);
		}
		
		for (int key : triggered) {
			keyMap.put(key, KeyStates.Held);
		}
	}
}
