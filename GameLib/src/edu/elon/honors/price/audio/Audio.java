package edu.elon.honors.price.audio;

import java.util.*;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Audio {
	private static LinkedList<MediaPlayer> players = new LinkedList<MediaPlayer>(), remove = new LinkedList<MediaPlayer>();
	private static Context context;
	
	public static void setContext(Context context) {
		Audio.context = context;
	}
	
	public static MediaPlayer play(int resourceId) {
		MediaPlayer mp =  MediaPlayer.create(context, resourceId);
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (!mp.isLooping()) {
					remove.add(mp);
				}
			}
		});
		//mp.start();
		players.add(mp);
		return mp;
	}
	
	public static void update() {
		for (MediaPlayer p : remove) {
			players.remove(p);
			p.release();
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
