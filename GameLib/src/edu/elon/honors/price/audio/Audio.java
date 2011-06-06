package edu.elon.honors.price.audio;

import java.util.*;

import android.content.Context;
import android.media.MediaPlayer;

public class Audio {
	private static LinkedList<MediaPlayer> players = new LinkedList<MediaPlayer>();
	private static Context context;
	
	public static void setContext(Context context) {
		Audio.context = context;
	}
	
	public static MediaPlayer play(int resourceId) {
		MediaPlayer mp =  MediaPlayer.create(context, resourceId);
		mp.start();
		players.add(mp);
		return mp;
	}
	
	public static void update() {
		LinkedList<MediaPlayer> remove = new LinkedList<MediaPlayer>();
//		for (MediaPlayer p : players) {
//			if (p != null) {
//				if ((p.getCurrentPosition() == p.getDuration()) && !p.isLooping()) {
//					remove.add(p);
//				}
//			}
//		}
		for (MediaPlayer p : remove) {
			players.remove(p);
		}
	}
	
	public static void mute() {
		for (MediaPlayer p : players) {
			if (p != null) {
				p.setVolume(0, 0);
			}
		}
	}
	
	public static void pause() {
		for (MediaPlayer p : players) {
			if (p != null) {
				p.pause();
			}
		}
	}
	
	public static void start() {
		for (MediaPlayer p : players) {
			if (p != null) {
				p.start();
			}
		}
	}
	
	public static void unmute() {
		for (MediaPlayer p : players) {
			if (p != null) {
				p.setVolume(1, 1);
			}
		}
	}
	
	public static void stop() {
		for (MediaPlayer p : players) {
			if (p != null) {
				p.stop();
			}
		}
		update();
	}
}
