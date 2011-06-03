package edu.elon.honors.price.physics;

import java.util.LinkedList;


public class Physics {

	private LinkedList<Body> bodies = new LinkedList<Body>(), toRemove = new LinkedList<Body>();

	public void addBody(Body body) {
		bodies.add(body);
	}

	public void removeBody(Body body) {
		toRemove.add(body);
	}

	public void update(long timeElapsed) {
		if (toRemove.size() > 0) {
			for (Body b : toRemove) {
				bodies.remove(b);
			}
			toRemove.clear();
		}
		for (Body b : bodies) {
			b.update(timeElapsed);
		}
	}
}
