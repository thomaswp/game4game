package edu.elon.honors.price.input;

import android.graphics.Color;
import android.graphics.Paint;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.physics.Vector;

public class Button extends UIControl {
	private Viewport viewport;
	private float radius;
	private Vector center, touch, dragStart, pull, temp;
	private Sprite outer, top;
	private int pid = -1;
	private boolean tapped;
	private boolean released;
	
	private static Paint paint = new Paint();

	public boolean isPressed() {
		return !top.isVisible();
	}
	
	public boolean isTapped() {
		return tapped;
	}
	
	public boolean isReleased() {
		return released;
	}
	
	public int getPID() {
		return pid;
	}
	
	public Button(int x, int y, int z, int radius, int color) {
		Input.setMultiTouch(true);
		
		viewport = new Viewport();
		viewport.setZ(z);
		viewport.setSorted(false);
		center = new Vector(x, y);
		pull = new Vector();
		touch = new Vector();
		temp = new Vector();
		dragStart = new Vector();
		this.radius = radius;

		paint.setColor(color);
		outer = new Sprite(viewport, x, y, radius * 2, radius * 2);
		outer.centerOrigin();
		outer.getBitmapCanvas().drawCircle(radius, radius, radius, paint);
		
		paint.setColor(Color.argb(100, 0, 0, 0));		
		top = new Sprite(viewport, x, y, radius * 2, radius * 2);
		top.centerOrigin();
		top.getBitmapCanvas().drawCircle(radius, radius, radius, paint);
	}

	public void update() {
		this.tapped = false;
		this.released = false;
		if (Input.isTapped() && pid < 0) {
			int tapped = Input.getTappedPointer();
			touch.set(Input.getLastTouchX(tapped), Input.getLastTouchY(tapped));
			temp.set(touch);
			temp.subtract(center);
			if (temp.magnitude() <= radius) {
				dragStart.set(touch);
				pid = tapped;
				this.tapped = true;
				Input.getVibrator().vibrate(40);
			}
		}
		if (pid >= 0 && Input.isTouchDown(pid)) {
			touch.set(Input.getLastTouchX(pid), Input.getLastTouchY(pid));
			pull = touch.subtract(dragStart);
			if (pull.magnitude() > radius) {
				top.setVisible(false);
			} else {
				top.setVisible(true);
			}
			//Debug.write(pull);
		} else {
			if (pid >= 0) {
				released = true;
			}
			top.setVisible(false);
			pull.set(0, 0);
			pid = -1;
		}
	}
	
	public void setVisible(boolean visible) {
		outer.setVisible(visible);
	}

	@Override
	public boolean isVisible() {
		return outer.isVisible();
	}
}
