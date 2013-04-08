package edu.elon.honors.price.maker;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import edu.elon.honors.price.data.UILayout;
import edu.elon.honors.price.data.UILayout.CircleControl;
import edu.elon.honors.price.data.UILayout.JoyStick;
import edu.elon.honors.price.input.Input;


public class DatabaseEditUI extends DatabaseActivity {

	EditUIView view;

	public static void startForResult(DatabaseActivity activity, int requestCode) {
		activity.startActivityForResult(
				activity.getNewGameIntent(DatabaseEditUI.class),
				requestCode);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = new EditUIView(this);
		setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("New Button");
		menu.add("New Joystick");
		menu.add("Ok");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getTitle().equals("New Button")) {
			view.addButton();
		} else if (item.getTitle().equals("New Joystick")) {
			view.addJoystick();
		} else if (item.getTitle().equals("Ok")) {
			finishOk();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public class EditUIView extends BasicCanvasView {

		private final int SELECTION_FILL_COLOR =  
			MapActivityBase.SELECTION_FILL_COLOR;
		private final int SELECTION_BORDER_COLOR = 
			MapActivityBase.SELECTION_BORDER_COLOR;
		private final int SELECTION_BORDER_WIDTH = 
			MapActivityBase.SELECTION_BORDER_WIDTH;
		private final int HOLD_TIME = 700;
		private final int HOLD_RAD = 5;
		private final int HOLD_VIB = 100;

		private Paint paint, textPaint;
		private UILayout.CircleControl selectedControl;
		private boolean dragging;
		private int startDragX, startDragY;
		private int holdingTime;
		private AlertDialog contextDialog;

		public EditUIView(Context context) {
			super(context);
			paint = new Paint();
			textPaint = new Paint();
		}

		public void addButton() {
			game.uiLayout.buttons.add(
					new UILayout.Button(width / 2, height / 2, UILayout.DEFAULT_RAD, 
							getRandomColor(), false)
			);
		}

		public void addJoystick() {
			game.uiLayout.joysticks.add(
					new UILayout.JoyStick(width / 2, height / 2, UILayout.DEFAULT_RAD, 
							getRandomColor(), false)
			);
		}

		private int getRandomColor() {
			return Color.argb(UILayout.DEFAULT_ALPHA, (int)(255 * Math.random()), 
					(int)(255 * Math.random()), (int)(255 * Math.random()));
		}

		@Override
		protected void update(long timeElapsed) {
			float x = Input.getLastTouchX();
			float y = Input.getLastTouchY();

			UILayout layout = game.uiLayout;

			if (Input.isTapped()) {
				boolean selected = false;
				for (int i = 0; i < layout.buttons.size(); i++) {
					UILayout.Button button = layout.buttons.get(i);
					if (selectControl(button, x, y)) {
						selected = true;
						break;
					}
				}
				if (!selected) {
					for (int i = 0; i < layout.joysticks.size(); i++) {
						UILayout.JoyStick joystick = layout.joysticks.get(i);
						if (selectControl(joystick, x, y)) {
							selected = true;
							break;
						}
					}
				}
				if (selected) {
					dragging = true;
					holdingTime = 0;
					startDragX = selectedControl.getRealX(width);
					startDragY = selectedControl.getRealY(height);
				}
			} else if (Input.isTouchDown()) {
				if (dragging) {
					float dx = Input.getDistanceTouchX();
					float dy = Input.getDistanceTouchY();

					int newX = startDragX + (int)dx;
					int newY = startDragY + (int)dy;
					selectedControl.setRealX(newX, width);
					selectedControl.setRealY(newY, height);

					if (holdingTime >= 0 &&
							dx * dx + dy * dy < HOLD_RAD * HOLD_RAD) {
						holdingTime += timeElapsed;
						if (holdingTime > HOLD_TIME) {
							Input.getVibrator().vibrate(HOLD_VIB);
							holdingTime = -1;
							post(new Runnable() {
								@Override
								public void run() {
									showMenu();
								}
							});
						}
					} else {
						holdingTime = -1;
					}
				}
			}

			if (dragging && !Input.isTouchDown()) {
				dragging = false;
			}
		}

		private void showMenu() {
			if (selectedControl != null) {
				dragging = false;
				
				View view = LayoutInflater.from(DatabaseEditUI.this).inflate(
						R.layout.database_edit_ui_context_menu, null);
				contextDialog = 
					new AlertDialog.Builder(DatabaseEditUI.this)
				.setView(view)
				.setNegativeButton("Delete", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						game.uiLayout.buttons.remove(selectedControl);
						game.uiLayout.joysticks.remove(selectedControl);
						selectedControl = null;
					}
				})
				.setNeutralButton("Cancel", null)
				.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText editText = (EditText)contextDialog.findViewById(R.id.editTextName);
						CheckBox checkBox = (CheckBox)contextDialog.findViewById(R.id.checkBoxDefault);
						selectedControl.name = editText.getText().toString();
						selectedControl.defaultAction = checkBox.isChecked();
					}
				})
				.create();

				contextDialog.show();

				Button buttonColor = (Button)contextDialog.findViewById(R.id.buttonColor);
				EditText editText = (EditText)contextDialog.findViewById(R.id.editTextName);
				CheckBox checkBox = (CheckBox)contextDialog.findViewById(R.id.checkBoxDefault);
				buttonColor.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new AmbilWarnaDialog(getContext(), selectedControl.color, new OnAmbilWarnaListener() {
							@Override
							public void onOk(AmbilWarnaDialog dialog, int color) {
								selectedControl.color = Color.argb(UILayout.DEFAULT_ALPHA, Color.red(color), 
										Color.green(color), Color.blue(color));
							}
							
							@Override
							public void onCancel(AmbilWarnaDialog dialog) { }
						}).show();
					}
				});
				editText.setText(selectedControl.name == null ? "" : selectedControl.name);
				checkBox.setChecked(selectedControl.defaultAction);
				
			}
		}

		private boolean selectControl(CircleControl control, float x, float y) {
			float dx = x - control.getRealX(width);
			float dy = y - control.getRealY(height);

			if (dx * dx + dy * dy <= control.radius * control.radius) {
				selectedControl = control;
				return true;
			}
			return false;
		}

		@Override
		public void onDraw(Canvas c) {
			c.drawColor(Color.LTGRAY);

			UILayout layout = game.uiLayout;
			paint.setStyle(Style.FILL);
			for (int i = 0; i < layout.buttons.size(); i++) {
				UILayout.Button button = layout.buttons.get(i);
				drawCircleControl(c, button);
			}
			for (int i = 0; i < layout.joysticks.size(); i++) {
				UILayout.JoyStick joystick = layout.joysticks.get(i);
				drawCircleControl(c, joystick);
			}
		}

		private void drawCircleControl(Canvas c, CircleControl control) {
			int x = control.getRealX(width);
			int y = control.getRealY(height);
			int rad = control.radius;

			paint.setStyle(Style.FILL);
			if (control == selectedControl) {
				paint.setColor(SELECTION_FILL_COLOR);
				c.drawRect(x - rad, y - rad, x + rad, y + rad, paint);
			}

			paint.setColor(control.color);
			c.drawCircle(x, y, rad, paint);

			if (control instanceof JoyStick) {
				paint.setColor(Color.argb(100, 0, 0, 0));
				c.drawCircle(x, y, rad / 2, paint);
			}

			if (control == selectedControl) {
				paint.setColor(SELECTION_BORDER_COLOR);
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(SELECTION_BORDER_WIDTH);
				c.drawRect(x - rad, y - rad, x + rad, y + rad, paint);

				if (dragging) {
					paint.setAlpha(150);
					textPaint.setTextSize(20);
					textPaint.setTextAlign(Align.CENTER);
					textPaint.setAntiAlias(true);

					int rx = control.x;
					int ry = control.y;
					int dx = (Math.abs(rx) - rad);
					String sdx = "" + dx;
					float dxWidth = paint.measureText(sdx);
					int dy = (Math.abs(ry) - rad);
					String sdy = "" + dy;
					float dyWidth = textPaint.measureText(sdy);
					float paintMY = textPaint.getTextSize() / 2.5f;
					if (rx > 0) {
						c.drawLine(0, y, x - rad, y, paint);
						if (dx > dxWidth) {
							c.drawText(sdx, (x - rad) / 2, y + paintMY, textPaint);
						}
					} else {
						c.drawLine(width, y, x + rad, y, paint);
						if (dx > dxWidth) {
							c.drawText(sdx, (x + rad + width) / 2, y + paintMY, textPaint);
						}
					}
					if (ry > 0) {
						c.drawLine(x, y - rad, x, 0, paint);
						if (dy > dyWidth) {
							c.drawText(sdy, x, (y - rad) / 2 + paintMY, textPaint);
						}
					} else {
						c.drawLine(x, y + rad, x, height, paint);
						if (dy > dyWidth) {
							c.drawText(sdy, x, (y + rad + height) / 2 + paintMY, textPaint);
						}
					}

				}
			}
		}
	}

}

