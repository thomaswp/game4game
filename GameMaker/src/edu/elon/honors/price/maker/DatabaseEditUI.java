package edu.elon.honors.price.maker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.view.Menu;
import android.view.MenuItem;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.UILayout;


public class DatabaseEditUI extends MapActivityBase {

	@Override
	protected MapView getMapView(PlatformGame game) {
		return new EditUIView(this, game);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("New Button");
		menu.add("New Joystick");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().equals("New Button")) {
			
		} else if (item.getTitle().equals("New Joystick")) {
			
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public static class EditUIView extends MapView {
		
		public EditUIView(Context context, PlatformGame game) {
			super(context, game);
		}

		@Override
		protected void doUpdate(int width, int height, float x, float y) {
			doPressButtons(x, y);
			doReleaseTouch(x, y);
		}

		@Override
		protected void drawContent(Canvas c) { }
		
		@Override
		protected void drawGrid(Canvas c) {
			super.drawGrid(c);
			drawUI(c);
		}
		
		private void drawUI(Canvas c) {
			UILayout layout = game.uiLayout;
			paint.setStyle(Style.FILL);
			for (int i = 0; i < layout.buttons.size(); i++) {
				UILayout.Button button = layout.buttons.get(i);
				paint.setColor(button.color);
				int x = button.x >= 0 ? button.x : width + button.x;
				int y = button.y >= 0 ? button.y : height + button.y;
				c.drawCircle(x, y, button.radius, paint);
			}
			for (int i = 0; i < layout.joysticks.size(); i++) {
				UILayout.JoyStick joystick = layout.joysticks.get(i);
				paint.setColor(joystick.color);
				int x = joystick.x >= 0 ? joystick.x : width + joystick.x;
				int y = joystick.y >= 0 ? joystick.y : height + joystick.y;
				c.drawCircle(x, y, joystick.radius, paint);
				paint.setColor(Color.argb(100, 0, 0, 0));
				c.drawCircle(x, y, joystick.radius / 2, paint);
			}
		}
		
	}

}

