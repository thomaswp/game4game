package edu.elon.honors.price.maker;

import edu.elon.honors.price.input.Input;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class SelectorActivityVector extends SaveableActivity {

	private float x, y, originalX, originalY;
	private SelectorVectorView view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.selector_activity_vector);
		
		if (getIntent().hasExtra("x")) {
			this.x = getIntent().getExtras().getFloat("x");
			originalX = x;
		}
		if (getIntent().hasExtra("y")) {
			this.y = getIntent().getExtras().getFloat("y");
			originalY = y;
		}
		
		view = new SelectorVectorView(this);
		((LinearLayout)findViewById(R.id.linearLayoutContent)).addView(view);
		
		setDefaultButtonActions();
	}
	
	@Override
	protected boolean hasChanged() {
		return x != originalX || y != originalY;
	}
	
	@Override
	protected void finishOk(Intent data) {
		data.putExtra("x", x);
		data.putExtra("y", y);
		super.finishOk(data);
	}
	
	private class SelectorVectorView extends BasicCanvasView {

		private Paint paint = new Paint();
		
		public SelectorVectorView(Context context) {
			super(context);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(5);
			paint.setTextSize(20);
		}
		
		@Override
		protected void update(long timeElapsed) {
			int cx = width / 2;
			int cy = height / 2;
			
			if (Input.isTouchDown()) {
				double tx = Input.getLastTouchX() - cx;
				double ty = Input.getLastTouchY() - cy;
				
				//double tRad = Math.sqrt(tx * tx + ty * ty);
				
				double deg = Math.atan2(ty, tx);
				double degD = deg * 180 / Math.PI;
				
				int roundedD = (int)(degD / 15 + 0.5) * 15; 
				//if (Math.abs(roundedD - degD) < 10) {
					deg = roundedD / 180.0 * Math.PI;
					x = (float)Math.cos(deg);
					y = (float)Math.sin(deg);
				//}
				
//				tx /= tRad;
//				ty /= tRad;
//				x = (float)tx;
//				y = (float)ty;
			}
		}
		
		@Override
		public void onDraw(Canvas c) {
			
			int radius = Math.min(width, height) / 2;
			int cx = width / 2;
			int cy = height / 2;
			
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.FILL);
			c.drawColor(Color.BLACK);
			

			paint.setColor(Color.LTGRAY);
			c.drawText(String.format("[%.02f, %.02f]", x, y), 0, 
					paint.getTextSize(), paint);
			
			int dx, dy;
			double deg;
			
			if (x == 0 && y == 0) {
				dx = 0; dy = -radius;
				deg = Math.PI / 2;
			} else {
				double vRad = Math.sqrt(x * x + y * y);
				dx = (int)(radius * x / vRad);
				dy = (int)(radius * y / vRad);
				deg = Math.atan2(dy, dx) + Math.PI;
			}
			
			paint.setColor(Color.argb(255, 125, 0, 255));
			paint.setStyle(Style.STROKE);
			
			c.drawLine(cx - dx, cy - dy, cx + dx, cy + dy, paint);
			
			double factor = 0.95;
			
			double aDeg = deg - Math.PI * factor;
			int ax = (int)(Math.cos(aDeg) * radius * factor);
			int ay = (int)(Math.sin(aDeg) * radius * factor);
			c.drawLine(cx + dx, cy + dy, cx + ax, cy + ay, paint);
			
			aDeg = deg + Math.PI * factor;
			ax = (int)(Math.cos(aDeg) * radius * factor);
			ay = (int)(Math.sin(aDeg) * radius * factor);
			c.drawLine(cx + dx, cy + dy, cx + ax, cy + ay, paint);
			
		}
	}
}
