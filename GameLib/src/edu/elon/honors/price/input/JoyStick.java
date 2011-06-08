package edu.elon.honors.price.input;

import android.graphics.Color;
import android.graphics.Paint;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Vector;

public class JoyStick {
	private Viewport viewport;
	private float x, y, z, radius;
	private Sprite outer, inner;
	private boolean drag;
	private Vector dragStart;
	private Vector pull;

	public Vector getPull() {
		return pull;
	}
	
	public JoyStick(int x, int y, int z, int radius) {
		viewport = new Viewport();
		viewport.setZ(z);
		this.x = x;
		this.y = y;
		this.radius = radius;

		Paint paint = new Paint();

		paint.setColor(Color.argb(100, 0, 0, 255));		
		outer = new Sprite(viewport, x, y, radius * 2, radius * 2);
		outer.centerOrigin();
		outer.getBitmapCanvas().drawCircle(radius, radius, radius, paint);

		paint.setColor(Color.argb(100, 100, 100, 100));
		inner = new Sprite(viewport, x, y, radius, radius);
		inner.centerOrigin();
		inner.getBitmapCanvas().drawCircle(radius / 2, radius / 2, radius / 2, paint);
	}

	public void update() {

		if (Input.isTouchDown()) {
			Vector touch = new Vector(Input.getLastTouchX(), Input.getLastTouchY());
			Vector center = new Vector(x, y);
			if (Input.isTapped()) {
				Vector r = touch.minus(center);
				if (r.magnitude() <= radius) {
					drag = true;
					dragStart = new Vector(Input.getLastTouchX(), Input.getLastTouchY());
				}
			}
			if (drag) {
				pull = touch.minus(dragStart);
				if (pull.magnitude() > radius) {
					pull.multiply(radius / pull.magnitude());
				}
				Vector r = center.plus(pull);
				inner.setX(r.getX());
				inner.setY(r.getY());
			}
		} else {
			inner.setX(x);
			inner.setY(y);
			drag = false;
			pull.setX(0);
			pull.setY(0);
		}
	}
}
