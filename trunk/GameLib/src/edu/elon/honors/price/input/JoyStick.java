package edu.elon.honors.price.input;

import android.graphics.Color;
import android.graphics.Paint;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Vector;

public class JoyStick {
	private Viewport viewport;
	private float radius;
	private Vector center, touch, dragStart, temp, pull;
	private Sprite outer, inner;
	private int pid = -1;
	
	private static Paint paint = new Paint();

	public Vector getPull() {
		temp.set(pull);
		temp.multiply(1 / radius);
		return temp;
	}

	public JoyStick(int x, int y, int z, int radius, int color) {
		viewport = new Viewport();
		viewport.setZ(z);
		viewport.setSorted(false);
		center = new Vector(x, y);
		touch = new Vector();
		pull = new Vector();
		temp = new Vector();
		dragStart = new Vector();
		this.radius = radius;

		paint.setColor(color);		
		outer = new Sprite(viewport, x, y, radius * 2, radius * 2);
		outer.centerOrigin();
		outer.getBitmapCanvas().drawCircle(radius, radius, radius, paint);

		paint.setColor(Color.argb(100, 100, 100, 100));
		inner = new Sprite(viewport, x, y, radius, radius);
		inner.centerOrigin();
		inner.getBitmapCanvas().drawCircle(radius / 2, radius / 2, radius / 2, paint);
	}

	public void update() {
		if (Input.isTapped() && pid < 0) {
			int tapped = Input.getTappedPointer();
			touch.set(Input.getLastTouchX(tapped), Input.getLastTouchY(tapped));
			temp.set(touch);
			temp.subtract(center);
			if (temp.magnitude() <= radius) {
				dragStart.set(touch);
				pid = tapped;
				Input.getVibrator().vibrate(40);
			}
		}
		if (pid >= 0 && Input.isTouchDown(pid)) {
			touch.set(Input.getLastTouchX(pid), Input.getLastTouchY(pid));
			pull = touch.subtract(dragStart);
			if (pull.magnitude() > radius) {
				pull.multiply(radius / pull.magnitude());
			}
			temp.set(center);
			temp.add(pull);
			inner.setX(temp.getX());
			inner.setY(temp.getY());
		} else {
			inner.setX(center.getX());
			inner.setY(center.getY());
			pull.set(0, 0);
			pid = -1;
		}
	}
}
