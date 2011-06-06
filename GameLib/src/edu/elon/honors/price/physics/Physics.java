package edu.elon.honors.price.physics;

import java.util.LinkedList;


public class Physics {

	private LinkedList<Body> bodies = new LinkedList<Body>(), toRemove = new LinkedList<Body>();

	public void addBody(Body body) {
		if (!bodies.contains(body)) {
			bodies.add(body);
		}
	}

	public void removeBody(Body body) {
		toRemove.add(body);
	}

	public void updatePhysics(long timeElapsed) {
		cleanup();
		for (Body b : bodies) {
			b.updatePhysics(timeElapsed);
		}
	}
	
	public void updateSprites() {
		cleanup();
		for (Body b : bodies) {
			b.updateSprite();
		}
	}
	
	private void cleanup() {
		if (toRemove.size() > 0) {
			for (Body b : toRemove) {
				bodies.remove(b);
			}
			toRemove.clear();
		}
	}
}
