package edu.elon.honors.price.physics;

import java.util.ArrayList;


public class Physics {

	private ArrayList<Body> bodies = new ArrayList<Body>(100);

	public void addBody(Body body) {
		if (!bodies.contains(body)) {
			bodies.add(body);
		}
	}

	public void updatePhysics(long timeElapsed) {
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			if (b.isDisposed()) {
				bodies.remove(i);
				i--;
			}
			else {
				b.updatePhysics(timeElapsed);
			}
		}
	}
	
	public void updateSprites() {
		for (int i = 0; i < bodies.size(); i++) {
			Body b = bodies.get(i);
			if (b.isDisposed()) {
				bodies.remove(i);
				i--;
			}
			else {
				b.updateSprite();
			}
		}
	}
}
