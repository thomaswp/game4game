package edu.elon.honors.price.audio;

import java.util.*;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Audio {
	private static ArrayList<MediaPlayer> players = new ArrayList<MediaPlayer>(30), remove = new ArrayList<MediaPlayer>(30);
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
		for (int i = 0; i < remove.size(); i++) {
			MediaPlayer p = remove.get(i);
			players.remove(i);
			p.release();
		}
		remove.clear();
	}
	
	public static void mute() {
		for (int i = 0; i < players.size(); i++) {
			MediaPlayer p = players.get(i);
			if (p != null) {
				p.setVolume(0, 0);
			}
		}
	}
	
	public static void pause() {
		for (int i = 0; i < players.size(); i++) {
			MediaPlayer p = players.get(i);
			if (p != null) {
				p.pause();
			}
		}
	}
	
	public static void start() {
		for (int i = 0; i < players.size(); i++) {
			MediaPlayer p = players.get(i);
			if (p != null) {
				p.start();
			}
		}
	}
	
	public static void unmute() {
		for (int i = 0; i < players.size(); i++) {
			MediaPlayer p = players.get(i);
			if (p != null) {
				p.setVolume(1, 1);
			}
		}
	}
	
	public static void stop() {
		for (int i = 0; i < players.size(); i++) {
			MediaPlayer p = players.get(i);
			if (p != null) {
				p.stop();
			}
		}
		update();
	}
}
