package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.LinkedList;

import android.graphics.Color;

public class UILayout extends GameData {
	private static final long serialVersionUID = 1L;
	
	public final static int DEFAULT_RAD = 65; 
	public final static int DEFAULT_ALPHA = 150;
	
	public LinkedList<Button> buttons = new LinkedList<Button>();
	public LinkedList<JoyStick> joysticks = new LinkedList<JoyStick>();
	
	public UILayout() {
		buttons.add(new Button(-80, -80, DEFAULT_RAD, Color.argb(DEFAULT_ALPHA, 255, 0, 0), true));
		joysticks.add(new JoyStick(80, -80, DEFAULT_RAD, Color.argb(DEFAULT_ALPHA, 0, 0, 255), true));
	}
	
	public static abstract class CircleControl implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public int x, y;
		public int radius, color;
		public boolean defaultAction;
		public String name;
		
		public CircleControl(int x, int y, int radius, int color,
				boolean defaultAction) {
			super();
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.color = color;
			this.defaultAction = defaultAction;
		}
		
		public int getRealX(int width) {
			return x >= 0 ? x : width + x;
		}
		
		public int getRealY(int height) {
			return y >= 0 ? y : height+ y;
		}
		
		public void setRealX(int newX, int width) {
			if (newX < 0) newX = 0;
			if (newX > width - 1) newX = width - 1;
			
			x = newX > width / 2 ? newX - width : newX;
		}
		
		public void setRealY(int newY, int height) {
			if (newY < 0) newY = 0;
			if (newY > height - 1) newY = height - 1;
			
			y = newY > height / 2 ? newY - height : newY;
		}

		public void setRelX(int newX, int width) {
			if (newX < -width / 2) {
				x = width + newX;
			} else if (newX > width / 2) {
				x  = -width + newX;
			} else {
				x = newX;
			}
		}

		public void setRelY(int newY, int height) {
			if (newY < -height/ 2) {
				y = height + newY;
			} else if (newY > height / 2) {
				y  = -height + newY;
			} else {
				y = newY;
			}
		}
	}
	
	public static class Button extends CircleControl {
		private static final long serialVersionUID = 1L;

		public Button(int x, int y, int radius, int color,
				boolean defaultAction) {
			super(x, y, radius, color, defaultAction);
			name = "New Button";
		}
	}
	
	public static class JoyStick extends CircleControl {
		private static final long serialVersionUID = 1L;

		public JoyStick(int x, int y, int radius, int color,
				boolean defaultAction) {
			super(x, y, radius, color, defaultAction);
			name = "New Joy Stick";
		}
	}

}
