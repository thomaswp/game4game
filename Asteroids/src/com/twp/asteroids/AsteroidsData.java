package com.twp.asteroids;

import java.io.Serializable;
import java.util.ArrayList;

public class AsteroidsData implements Serializable {
	private static final long serialVersionUID = 7L;
	
	public float shipX, shipY, shipVX, shipVY, shipRot;
	public int state, score, level;
	public ArrayList<Asteroid> asteroids;
	public ArrayList<Bullet> bullets;
	public ArrayList<Explosion> explosions;
}
