package edu.elon.honors.price.input;

import android.graphics.Color;
import android.graphics.Paint;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Vector;

public class JoyStick extends UIControl {
	private Viewport viewport;
	private float radius;
	private Vector center, touch, dragStart, temp, pull, lastPull;
	private Sprite outer, inner;
	private int pid = -1;
	private boolean tapped, released, pressed;
	
	private static Paint paint = new Paint();

	public Vector getPull() {
		temp.set(pull);
		temp.multiply(1 / radius);
		return temp;
	}
	
	public Vector getLastPull() {
		temp.set(lastPull);
		temp.multiply(1 / radius);
		return temp;
	}

	public boolean isTapped() {
		return tapped;
	}
	
	public boolean isReleased() {
		return released;
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public int getPID() {
		return pid;
	}

	public JoyStick(int x, int y, int z, int radius, int color) {
		Input.setMultiTouch(true);
		viewport = new Viewport();
		viewport.setZ(z);
		viewport.setSorted(false);
		center = new Vector(x, y);
		touch = new Vector();
		pull = new Vector();
		temp = new Vector();
		lastPull = new Vector();
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
		tapped = false;
		released = false;
		pressed = false;
		lastPull.set(pull);
		if (Input.isTapped() && pid < 0) {
			int tapped = Input.getTappedPointer();
			touch.set(Input.getLastTouchX(tapped), Input.getLastTouchY(tapped));
			temp.set(touch);
			temp.subtract(center);
			if (temp.magnitude() <= radius) {
				this.tapped = true;
				dragStart.set(touch);
				pid = tapped;
				Input.getVibrator().vibrate(40);
			}
		}
		if (pid >= 0 && Input.isTouchDown(pid)) {
			pressed = true;
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
			if (pid >= 0) {
				released = true;
				Input.getVibrator().vibrate(40);
			}
			inner.setX(center.getX());
			inner.setY(center.getY());
			pull.set(0, 0);
			pid = -1;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		outer.setVisible(visible);
		inner.setVisible(visible);
	}
	
	@Override
	public boolean isVisible() {
		return outer.isVisible();
	}
}
