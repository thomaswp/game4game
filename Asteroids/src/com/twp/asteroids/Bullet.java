package com.twp.asteroids;

import edu.elon.honors.price.physics.Body;
import edu.elon.honors.price.physics.Physics;

public class Bullet extends Body {
	private static final long serialVersionUID = 1L;
	
	public static final transient float SPEED = 0.3f;
	
	public Bullet(Physics physics, float x, float y, float direction) {
		super(physics, x, y);
		setSpeed(SPEED);
		setDirectionRadians(direction);
		//setVelocity(new Vector(0, 0.01f));
		setEdgeBehavior(EdgeBehavior.DESTROY);
	}
}
