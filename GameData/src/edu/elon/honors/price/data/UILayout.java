package edu.elon.honors.price.data;

import java.io.Serializable;
import java.util.LinkedList;

import android.graphics.Color;

public class UILayout implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public LinkedList<Button> buttons = new LinkedList<Button>();
	public LinkedList<JoyStick> joysticks = new LinkedList<JoyStick>();
	
	public UILayout() {
		buttons.add(new Button(-65, -65, 50, Color.argb(150, 255, 0, 0), true));
		joysticks.add(new JoyStick(65, -65, 50, Color.argb(150, 0, 0, 255), true));
	}
	
	public static abstract class CircleControl implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public int x, y;
		public int radius, color;
		public boolean defaultAction;
		
		public CircleControl(int x, int y, int radius, int color,
				boolean defaultAction) {
			super();
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.color = color;
			this.defaultAction = defaultAction;
		}
	}
	
	public static class Button extends CircleControl {
		private static final long serialVersionUID = 1L;

		public Button(int x, int y, int radius, int color,
				boolean defaultAction) {
			super(x, y, radius, color, defaultAction);
		}
	}
	
	public static class JoyStick extends CircleControl {
		private static final long serialVersionUID = 1L;

		public JoyStick(int x, int y, int radius, int color,
				boolean defaultAction) {
			super(x, y, radius, color, defaultAction);
		}
	}

}
