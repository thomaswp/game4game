package com.twp.asteroids;

import java.io.Serializable;
import java.util.LinkedList;

public class AsteroidsData implements Serializable {
	private static final long serialVersionUID = 6L;
	
	public float shipX, shipY, shipVX, shipVY, shipRot;
	public int state, score, level;
	public LinkedList<Asteroid> asteroids;
	public LinkedList<Bullet> bullets;
	public LinkedList<Explosion> explosions;
}
