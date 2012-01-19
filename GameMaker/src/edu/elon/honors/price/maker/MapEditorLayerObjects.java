package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.maker.MapEditorLayer.Action;
import edu.elon.honors.price.maker.MapEditorLayer.DrawMode;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class MapEditorLayerObjects extends MapEditorLayerSelectable<ObjectInstance> {

	public MapEditorLayerObjects(MapEditorView parent) {
		super(parent);
	}

	public void drawContentNormal(Canvas c) {
		if (touchDown && showPreview) {
			Bitmap bitmap = parent.objectImage;
			float x = touchX - bitmap.getWidth() / 2;
			float y = touchY - bitmap.getHeight() / 2;
			c.drawBitmap(bitmap, x, y, paint);
		}
	}

	@Override
	protected void drawLayerNormal(Canvas c, DrawMode mode) {
		setCSelection();
		paint.setAlpha(mode == DrawMode.Above ? MapEditorView.TRANS : 255);

		for (int i = 0; i < map.objects.size(); i++) {
			ObjectInstance instance = map.objects.get(i);
			if (instance != null) {
				Bitmap bitmap = parent.objects[instance.classIndex];

				float x = instance.startX + getOffX() - bitmap.getWidth() / 2;
				float y = instance.startY + getOffY() - bitmap.getHeight() / 2;
				c.drawBitmap(bitmap, x, y, paint);
			}
		}
	}

	@Override
	public void onSelect() {
		parent.selectObject();
	}

	@Override
	public void refreshSelection() {
		parent.objectImage = parent.objects[parent.objectSelection];
	}

	@Override
	public Bitmap getSelection() {
		return parent.objectImage;
	}

	protected void onTouchUpNormal(float x, float y) {
		int startX = (int)(touchX - getOffX());
		int startY = (int)(touchY - getOffY());
		int classIndex = parent.objectSelection;
		int index = game.getSelectedMap().objects.size();
		final ObjectInstance object = new ObjectInstance(index, classIndex, startX, startY);

		Action action = new Action() {
			@Override
			public void undo(PlatformGame game) {
				game.getSelectedMap().objects.remove(object);
				if (selectedObjects.contains(object)) {
					selectedObjects.remove(object);
				}
			}

			@Override
			public void redo(PlatformGame game) {
				game.getSelectedMap().objects.add(object);
			}
		};

		parent.doAction(action);	
	}

	@Override
	protected ArrayList<ObjectInstance> getAllItems() {
		return map.objects;
	}

	@Override
	protected void getDrawBounds(ObjectInstance item, RectF bounds) {
		Bitmap bitmap = parent.objects[item.classIndex];
		float x = item.startX + getOffX() - bitmap.getWidth() / 2;
		float y = item.startY + getOffY() - bitmap.getHeight() / 2;
		drawRect.set(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
	}

	@Override
	protected Bitmap getBitmap(ObjectInstance item) {
		return parent.objects[item.classIndex];
		
	}

	@Override
	protected void shiftItem(ObjectInstance item, float offX, float offY) {
		item.startX += offX;
		item.startY += offY;
	}

	@Override
	protected void delete(ObjectInstance item) {
		game.getSelectedMap().objects.remove(item);
	}

	@Override
	protected void add(ObjectInstance item) {
		game.getSelectedMap().objects.add(item);
	}
}
