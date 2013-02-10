package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.List;

import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.ObjectInstance;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Debug;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

public class MapEditorLayerObjects extends MapEditorLayerSelectable<ObjectInstance> {

	private final static int SNAP_THRESHHOLD = 8; 
	
	private PointF snappedPoint = new PointF();
	private RectF objectRect = new RectF(), snapRect = new RectF();

	private PointF getSnappedTouchPoint() {
		Bitmap bitmap = getSelection();
		ObjectClass object = getObjectClass();
		
		float x = touchX - getOffX();
		float y = touchY - getOffY();
		
		float leftEdge = x - bitmap.getWidth() / 2;
		float rightEdge = x + bitmap.getWidth() / 2;
		float topEdge = y - bitmap.getHeight() / 2;
		float bottomEdge = y + bitmap.getHeight() / 2;
		
		objectRect.set(leftEdge, topEdge, rightEdge, bottomEdge);
		snapRect(object, objectRect, null);
		snappedPoint.set(objectRect.centerX(), objectRect.centerY());
		
		return snappedPoint;
	}
	
	private void snapRect(ObjectClass object, RectF objectRect, 
			List<ObjectInstance> ignore) {
	
		if (object.isPlatform) {
			
			Tileset tileset = game.getSelectedMap().getTileset(game);
			int tileWidth = tileset.tileWidth;
			int tileHeight = tileset.tileHeight;
		
			int startTileX = Math.max((int)(objectRect.left / tileWidth), 0);
			int endTileX = Math.min((int)(objectRect.right / tileWidth + 1.5f), map.columns - 1);
			int startTileY = Math.max((int)(objectRect.top / tileHeight), 0);
			int endTileY = Math.min((int)(objectRect.bottom / tileHeight + 1.5f), map.rows - 1);
		
			for (int i = startTileX; i <= endTileX; i++) {
				for (int j = startTileY; j < endTileY; j++) {
					boolean solid = false;
					for (int k = 0; k < 3; k++) {
						solid |= map.layers[k].tiles[j][i] != 0;
					}
					if (solid) {
						snapRect.set(i * tileWidth, j * tileHeight, 
								(i+1) * tileWidth, (j+1) * tileHeight);
						snap(objectRect, snapRect);
					}
				}
			}
			for (ObjectInstance instance : map.objects) {
				if (ignore != null && ignore.contains(instance)) continue;
				Bitmap bmp = parent.objects[instance.classIndex];
				int dw = bmp.getWidth() / 2, dh = bmp.getHeight() / 2;
				snapRect.set(instance.startX - dw, instance.startY - dh,
						instance.startX + dw, instance.startY + dh);
				snap(objectRect, snapRect);
			}
		}
	}
	
	private void snap(RectF objectRect, RectF snapTo) {
		float disY = Math.abs(objectRect.centerY() - snapTo.centerY());
		float averageHeight = (objectRect.height() + snapTo.height()) / 2;
		if (disY - averageHeight <= SNAP_THRESHHOLD) {
			snapX(objectRect, snapTo.left);
			snapX(objectRect, snapTo.right);
		}
		
		float disX = Math.abs(objectRect.centerX() - snapTo.centerX());
		float averageWidth = (objectRect.width() + snapTo.width()) / 2;
		if (disX - averageWidth <= SNAP_THRESHHOLD) {
			snapY(objectRect, snapTo.top);
			snapY(objectRect, snapTo.bottom);
		}
	}
	
	private void snapX(RectF objectRect, float x) {
		if (Math.abs(objectRect.left - x) <= SNAP_THRESHHOLD) {
			objectRect.offsetTo(x, objectRect.top);
		} else if (Math.abs(objectRect.right - x) <= SNAP_THRESHHOLD) {
			objectRect.offsetTo(x - objectRect.width(), objectRect.top);
		}
	}
	
	private void snapY(RectF objectRect, float y) {
		if (Math.abs(objectRect.top - y) <= SNAP_THRESHHOLD) {
			objectRect.offsetTo(objectRect.left, y);
		} else if (Math.abs(objectRect.bottom - y) <= SNAP_THRESHHOLD) {
			objectRect.offsetTo(objectRect.left, y - objectRect.height());
		}
	}
	
	private ObjectClass getObjectClass() {
		return game.objects[parent.objectSelection];
	}
	
	public MapEditorLayerObjects(MapEditorView parent) {
		super(parent);
	}

	@Override
	public void drawContentNormal(Canvas c) {
		if (touchDown && showPreview) {
			Bitmap bitmap = getSelection();
			PointF snappedPoint = getSnappedTouchPoint();
			float x = snappedPoint.x + getOffX() - bitmap.getWidth() / 2;
			float y = snappedPoint.y + getOffY() - bitmap.getHeight() / 2;
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
	public void refreshSelection() { }

	@Override
	public Bitmap getSelection() {
		return parent.objects[parent.objectSelection];
	}

	@Override
	protected void onTouchUpNormal(float x, float y) {
		PointF snappedPoint = getSnappedTouchPoint();
		int startX = (int)snappedPoint.x;
		int startY = (int)snappedPoint.y;
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
		boundsRect.set(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
	}

	@Override
	protected Bitmap getBitmap(ObjectInstance item, DrawMode mode) {
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

	@Override
	public Bitmap loadIcon() {
		return BitmapFactory.decodeResource(parent.getResources(), 
				R.drawable.layerobject);
	}

	@Override
	protected void loadEditIcons() {
		editIcons.add(
				BitmapFactory.decodeResource(parent.getResources(),
						R.drawable.edit));
		editIcons.add(
				BitmapFactory.decodeResource(parent.getResources(),
						R.drawable.select));
	}

	@Override
	protected void snapDrawBounds(ObjectInstance item, RectF bounds, 
			List<ObjectInstance> ignore) {
		snapRect(item.getObjectClass(game), bounds, ignore);
	}

	@Override
	protected boolean inSelectingMode() {
		return parent.editMode == MapEditorView.EDIT_ALT1;
	}
}
