package edu.elon.honors.price.maker;

import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.maker.MapEditorView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class MapEditorLayer {
	public enum DrawMode {
		Above,
		Selected,
		Below
	}
	
	protected MapEditorView parent;
	protected Paint paint;
	protected Map map;
	protected PlatformGame game;
	protected boolean touchDown;
	protected float touchX, touchY;
	protected boolean showPreview;
	
	public boolean isTouchDown() {
		return touchDown;
	}
	
	protected float getOffX() {
		return parent.offX;
	}
	
	protected float getOffY() {
		return parent.offY;
	}
	
	public void setGame(PlatformGame game) {
		this.game = game;
		this.map = game.getSelectedMap();
	}
	
	public MapEditorLayer(MapEditorView parent) {
		this.parent = parent;
		this.paint = new Paint();
		this.map = parent.game.getSelectedMap();
		this.game = parent.game;
	}
	
	public abstract void drawLayer(Canvas c, DrawMode mode);
	public abstract void drawContent(Canvas c);
	public abstract void refreshSelection();
	public abstract Bitmap getSelection();
	public abstract void onSelect();
	
	public void onTouchDown(float x, float y) {
		touchDown = true;
		touchX = x; touchY = y;
	}

	public void onTouchDrag(float x, float y, boolean showPreview) {
		touchX = x; touchY = y;
		this.showPreview = showPreview;
	}

	public void onTouchUp(float x, float y) {
		touchDown = false;
		touchX = x; touchY = y;
	}
	
	public static abstract class Action {
		public abstract void undo(PlatformGame game);
		public abstract void redo(PlatformGame game);
	}
}
