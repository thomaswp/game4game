package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

public class MapEditorObjectSelector extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		PlatformGame game = (PlatformGame)getIntent().getExtras().getSerializable("game");
		int id = getIntent().getExtras().getInt("id");

		setContentView(new ObjectSelectorView(this, game, id));
	}
	
	public void doSelection(int id) {
		Intent intent = new Intent();
		intent.putExtra("id", id);
		setResult(RESULT_OK, intent);
		finish();
	}

	public class ObjectSelectorView extends BasicCanvasView {

		private static final int DRAG_BORDER = 40;
		
		private PlatformGame game;
		private int selectedId;
		private float offX;
		private float lastTouchX;
		private float dragVelocity;
		private boolean dragging;
		private Bitmap[] objects;
		private int[] objectXs;
		private Paint paint;
		private int spacing;
		private int totalWidth;
		private LinearGradient gradient;
		private float[] lastVelocities;
		private int velocityIndex;

		public ObjectSelectorView(Context context, PlatformGame game, int id) {
			super(context);
			this.game = game;
			selectedId = id;
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			objects = new Bitmap[game.objects.length];
			objectXs = new int[objects.length];
		}

		@Override
		public void initializeGraphics() {
			for (int i = 0; i < game.objects.length; i++) {
				objects[i] = Data.loadObject(game.objects[i].imageName);
				objects[i] = Bitmap.createScaledBitmap(objects[i], 
						(int)(objects[i].getWidth() * game.objects[i].zoom), 
						(int)(objects[i].getHeight() * game.objects[i].zoom), true);
				objectXs[i] = totalWidth;
				totalWidth += objects[i].getWidth();
			}

			if (totalWidth < width) {
				spacing = (width - totalWidth) / (objects.length + 1);
			} else {
				spacing = 20;
			}

			totalWidth += spacing * (objects.length + 1);
			totalWidth = Math.max(totalWidth, width);
			gradient = new LinearGradient(0, 0, totalWidth + DRAG_BORDER * 2, height, 
					Color.LTGRAY, Color.DKGRAY, Shader.TileMode.CLAMP);
			
			float centerX = objectXs[selectedId] + spacing * (selectedId + 1) + 
					objects[selectedId].getWidth() / 2 - width / 2;
			offX = Math.min(Math.max(-centerX, width - totalWidth), 0);
		}

		@Override
		protected void onDraw(Canvas c) {
			paint.setStyle(Style.FILL);
			paint.setShader(gradient);
			paint.setDither(true);
			c.drawRect(offX - DRAG_BORDER, 0, offX + totalWidth + DRAG_BORDER, height, paint);

			boolean visible = false;
			
			paint.setStyle(Style.STROKE);
			paint.setShader(null);
			paint.setDither(false);
			for (int i = 0; i < objects.length; i++) {
				float x = objectXs[i] + offX + spacing * (i + 1);
				float y = (height - objects[i].getHeight()) / 2;

				c.drawBitmap(objects[i], x, y, paint);
				if (i == selectedId) {
					paint.setColor(Color.GREEN);
					c.drawRect(x, y, x + objects[i].getWidth(), 
							y + objects[i].getHeight(), paint);
					visible = (x + objects[i].getWidth() > 0 && x < width);
				}
			}
			
			for (int i = 0; i < objects.length; i++) {
				if (i == selectedId) {
					paint.setStyle(Style.FILL);
					paint.setColor(Color.BLACK);
					paint.setTextSize(36);
					float ty = paint.getTextSize();
					c.drawText(game.objects[i].name, 5, ty, paint);
					
					if (visible) {
						String text = "Tap again to select";
						paint.setTextSize(18);
						c.drawText(text, width - paint.measureText(text) - 5, 
								height - 5, paint);
					}
				}
			}
		}
		
		private void doTouch(float x, float y) {
			x -= offX;
			for (int i = 0; i < objectXs.length; i++) {
				float minX = objectXs[i] + spacing * (i + 1);
				if (x >= minX && x <= minX + objects[i].getWidth()) {
					float minY = (height - objects[i].getHeight()) / 2;
					if (y >= minY && y <= minY + objects[i].getHeight()) {
						if (selectedId == i) {
							((MapEditorObjectSelector)getContext()).doSelection(i);
						} else {
							selectedId = i;
						}
						return;
					}
				}
			}
		}

		@Override
		protected void update(long timeElapsed) {			
			if (dragging) {
				if (Input.isTouchDown()) {
					dragVelocity = Input.getLastTouchX() - lastTouchX;
					lastTouchX = Input.getLastTouchX();
					lastVelocities[velocityIndex] = dragVelocity;
					velocityIndex = (velocityIndex + 1) % lastVelocities.length;
				} else {
					dragging = false;
					dragVelocity = 0;
					for (int i = 0; i < lastVelocities.length; i++) {
						dragVelocity += lastVelocities[i];
					}
					dragVelocity /= lastVelocities.length;
					
					float dx = Input.getDistanceTouchX();
					float dy = Input.getDistanceTouchY();
					if (Math.sqrt(dx * dx + dy * dy) < 15) {
						doTouch(Input.getLastTouchX(), Input.getLastTouchY());
					}
				}
			} else {
				if (Input.isTouchDown()) {
					dragging = true;
					lastTouchX = Input.getLastTouchX();
					lastVelocities = new float[5];
				} else {
					dragVelocity *= 0.95f;
					if (Math.abs(dragVelocity) < 0.001) dragVelocity = 0;
				}
			}
			
			int min = width - totalWidth;
			
			offX += dragVelocity;
			if (offX > DRAG_BORDER) {
				offX = DRAG_BORDER;
				dragVelocity = 0;
			}
			if (offX < min - DRAG_BORDER) {
				offX = min - DRAG_BORDER;
				dragVelocity = 0;
			}
			
			if (!Input.isTouchDown()) {
				if (offX > 0) {
					offX *= 0.8f;
				}
				if (offX < min) {
					offX = (0.8f * offX + 0.2f * min);
				}
			}
		}
	}
}
