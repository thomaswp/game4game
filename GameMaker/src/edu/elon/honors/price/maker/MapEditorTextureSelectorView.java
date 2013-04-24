package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButtonAction;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Input;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MapEditorTextureSelectorView extends BasicCanvasView {

	private Bitmap bitmap;
	private SurfaceHolder holder;
	private Paint paint = new Paint();
	private float bitmapX, bitmapY, startBitmapY;
	private boolean move;
	private Rect selection = new Rect(0, 0, 1, 1), drawRect = new Rect();
	private int tileWidth, tileHeight;
	private float startSelectX, startSelectY;
	private PointF okCenter;
	private int okRad;
	private Poster poster;
	private boolean buttonDown;

	public void setPoster(Poster poster) {
		this.poster = poster;
	}
	
	public MapEditorTextureSelectorView(Context context, Tileset tileset, Rect selection) {
		super(context);
		this.bitmap = Data.loadTileset(tileset.bitmapName);
		this.tileWidth = tileset.tileWidth;
		this.tileHeight = tileset.tileHeight;
		this.selection.set(selection);
		holder = getHolder();
		holder.addCallback(this);
		okRad = (int)context.getResources().getDimension(
				R.dimen.terrain_selector_button_width);

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Link it to Input
				return Input.onTouch(v, event);
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = bitmap.getWidth() + okRad;
		int height = bitmap.getHeight();
		
		width = boundDim(width, widthMeasureSpec);
		height = boundDim(height, heightMeasureSpec);

		setMeasuredDimension(width, height);
	}
	
	protected int boundDim(int dim, int measureSpec) {
		int size = MeasureSpec.getSize(measureSpec);
		int mode = MeasureSpec.getMode(measureSpec);
		
		if (mode == MeasureSpec.AT_MOST) {
			return Math.min(dim, size);
		} else if (mode == MeasureSpec.EXACTLY) {
			return size;
		} else {
			return dim;
		}
	}


	@Override
	protected void initializeGraphics() {
		float x = bitmap.getWidth() + okRad;
		float y = width - x;
		okCenter = new PointF(x, y);

		Debug.write("%d, %d", width, height);
	}

	@Override
	public void onDraw(Canvas c) {
		c.drawColor(Color.LTGRAY);
		paint.setColor(Color.WHITE);
		c.drawRect(bitmapX, 0, bitmapX + bitmap.getWidth(), 
				height, paint);
		c.drawBitmap(bitmap, bitmapX, bitmapY, paint);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(MapActivityBase.SELECTION_BORDER_WIDTH);
		paint.setColor(MapActivityBase.SELECTION_BORDER_COLOR);
		paint.setAlpha(255);
		drawRect.set(selection.left * tileWidth, selection.top * tileHeight + (int)bitmapY, 
				selection.right * tileWidth, selection.bottom * tileHeight + (int)bitmapY);
		c.drawRect(drawRect, paint);

		if (height < bitmap.getHeight()) {
			int barHeight = height * height / bitmap.getHeight();
			float scrollPerc = -(float)bitmapY / (bitmap.getHeight() - height);
			int barScroll = (int)(scrollPerc * (height - barHeight));
			paint.setStyle(Style.FILL);
			paint.setColor(Color.DKGRAY);
			c.drawRect(width - 10, barScroll, width, barScroll + barHeight, paint);
		}

		int alpha = buttonDown ? 255 : 150;
		paint.setColor(Color.DKGRAY);
		if (TutorialUtils.isHighlighted(EditorButton.TextureSelectorOk)) {
			paint.setColor(TutorialUtils.getHightlightColor());
		}
		paint.setAlpha(alpha);
		paint.setStyle(Style.FILL);
		c.drawCircle(okCenter.x, okCenter.y, okRad, paint);
		paint.setColor(Color.LTGRAY);
		paint.setAlpha(alpha);
		c.drawCircle(okCenter.x, okCenter.y, okRad * 0.9f, paint);

		paint.setTextSize(Screen.spToPx(
				MapActivityBase.BUTTON_TEXT_SIZE - 2, getContext()));
		String text = "Ok";
		paint.setColor(Color.BLACK);
		float textWidth = paint.measureText(text);
		float x = (width + bitmap.getWidth()) / 2f - textWidth * 1 / 3;
		float y = (okRad + paint.getTextSize()) * 0.3f;
		c.drawText(text, x, y, paint);
	}

	@Override
	protected void update(long timeElapsed) {
		float dx = Input.getLastTouchX() - okCenter.x;
		float dy = Input.getLastTouchY() - okCenter.y;
		boolean inButton = (dx * dx + dy * dy < okRad * okRad);

		if (Input.isTapped()) {
			if (inButton) {
				buttonDown = true;
				move = false;
				TutorialUtils.fireCondition(EditorButton.TextureSelectorOk, 
						EditorButtonAction.ButtonDown, getContext());
			} else {
				startBitmapY = bitmapY;
				move = Input.getLastTouchX() > bitmap.getWidth();
				if (!move) {
					startSelectX = Input.getLastTouchX();
					startSelectY = Input.getLastTouchY() - bitmapY;
				}					
			}
		}

		if (Input.isTouchDown()) {
			if (move) {
				bitmapY = startBitmapY + Input.getDistanceTouchY();
				if (bitmapY + bitmap.getHeight() < height) {
					startBitmapY -= (bitmapY - (height - bitmap.getHeight()));
					bitmapY = height - bitmap.getHeight();
				}
				if (bitmapY > 0) {
					startBitmapY -= bitmapY;
					bitmapY = 0;
				}
			} else if (!buttonDown) {
				float x = Input.getLastTouchX(), y = Input.getLastTouchY() - bitmapY;
				float left = Math.min(startSelectX, x), right = Math.max(startSelectX, x);
				float top = Math.min(startSelectY, y), bottom = Math.max(startSelectY, y);
				selection.set((int)(left / tileWidth), (int)(top / tileHeight), 
						(int)(right / tileWidth) + 1, (int)(bottom / tileHeight) + 1);
				selection.left = Math.max(selection.left, 0);
				selection.top = Math.max(selection.top, 0);
				selection.right = Math.min(selection.right, bitmap.getWidth() / tileWidth);
				selection.bottom = Math.min(selection.bottom, bitmap.getHeight() / tileHeight);
			}
		} else {
			if (buttonDown && inButton) {
				poster.post(selection);
				TutorialUtils.fireCondition(EditorButton.TextureSelectorOk, 
						EditorButtonAction.ButtonUp, getContext());
			}
			buttonDown = false;
		}
	}

	public static abstract class Poster {
		abstract void post(Rect rect);
	}
}
