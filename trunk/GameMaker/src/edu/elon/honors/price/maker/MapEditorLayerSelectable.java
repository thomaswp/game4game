package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.input.Input;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Paint.Style;

public abstract class MapEditorLayerSelectable<T> extends MapEditorLayer {

	
	public final static int SELECTING_MODE = 1;
	
	protected RectF selection, cSelection;
	protected RectF boundsRect;
	protected boolean selecting;
	protected float startDragX, startDragY;
	protected LinkedList<T> selectedObjects;
	protected int heldFrames;
	protected T draggingItem;
	
	protected PointF dragOffset = new PointF();
	

	public MapEditorLayerSelectable(MapEditorView parent) {
		super(parent);
		selection = new RectF();
		cSelection = new RectF();
		boundsRect = new RectF(); 
		paint.setStrokeWidth(parent.selectionBorderWidth);
		selectedObjects = new LinkedList<T>();
	}

	protected abstract void drawContentNormal(Canvas c);
	protected abstract void drawLayerNormal(Canvas c, DrawMode mode);
	protected abstract void onTouchUpNormal(float x, float y);
	protected void onTouchDragNormal(float x, float y, boolean showPreview) { };
	protected abstract ArrayList<T> getAllItems();
	/**
	 * Returns the drawing bounds of this item on screen, 
	 * factoring in the current offset.
	 */
	protected abstract void getDrawBounds(T item, RectF bounds);
	protected abstract void snapDrawBounds(T item, RectF bounds, List<T> ignore);
	protected abstract Bitmap getBitmap(T item, DrawMode mode);
	protected abstract void shiftItem(T item, float offX, float offY);
	protected abstract void delete(T item);
	protected abstract void add(T item);
	protected abstract boolean inSelectingMode();

	protected void getWorldBounds(T item, RectF bounds) { 
		getDrawBounds(item, bounds);
		bounds.offset(-getOffX(), -getOffY());
	}

	@Override
	public final void drawContent(Canvas c) {
		if (inSelectingMode()) {
			if (selecting) {
				setCSelection();
				paint.setStyle(Style.FILL);
				paint.setColor(parent.selectionFillColor);
				c.drawRect(cSelection, paint);
				paint.setStyle(Style.STROKE);
				paint.setColor(parent.selectionBorderColor);
				c.drawRect(cSelection, paint);
			}
		} else {
			drawContentNormal(c);
		}
	}

	@Override
	public final void drawLayer(Canvas c, DrawMode mode) {
		drawLayerNormal(c, mode);
		if (inSelectingMode()) {
			if (mode == DrawMode.Selected) {
				if (selecting) {
					selectedObjects.clear();

					for (int i = 0; i < getAllItems().size(); i++) {
						T instance = getAllItems().get(i);
						if (instance != null) {
							getDrawBounds(instance, boundsRect);
							if (selecting && RectF.intersects(cSelection, boundsRect)) {
								if (cSelection.width() < 5 && cSelection.height() < 5) {
									selectedObjects.clear();
								}
								selectedObjects.add(instance);
							}
						}
					}
				}

				final float offX = dragOffset.x;
				final float offY = dragOffset.y;
 
				paint.setColor(parent.selectionBorderColor);
				paint.setStyle(Style.STROKE);
				for (int i = 0; i < selectedObjects.size(); i++) {
					T instance = selectedObjects.get(i);
					if (selectedObjects.contains(instance)) {
						getDrawBounds(instance, boundsRect);
						paint.setAlpha(mode == DrawMode.Above ? MapEditorView.TRANS : 255);
						c.drawRect(boundsRect, paint);
						if (!selecting && touchDown && showPreview) {
							paint.setAlpha(paint.getAlpha() / 2);
							boundsRect.offset(offX, offY);
							c.drawBitmap(getBitmap(instance, mode), boundsRect.left, 
									boundsRect.top, paint);
							c.drawRect(boundsRect, paint);
						}
					}

				}
			}
		}
	}

	@Override
	public final void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);
		if (inSelectingMode()) {
			if (!selecting) {
				final LinkedList<T> objects = new LinkedList<T>();
				objects.addAll(selectedObjects);
				final float offX = dragOffset.x;
				final float offY = dragOffset.y;
				Action action = new Action() {
					@Override
					public void undo(PlatformGame game) {
						for (int i = 0; i < objects.size(); i++) {
							T object = objects.get(i);
							shiftItem(object, -offX, -offY);
						}
					}
					@Override
					public void redo(PlatformGame game) {
						for (int i = 0; i < objects.size(); i++) {
							T object = objects.get(i);
							shiftItem(object, offX, offY);
						}
					}
				};
				parent.doAction(action);
			}
			selecting = false;
		} else {
			onTouchUpNormal(x, y);
		}
	}
	
	@Override
	public final void onTouchCanceled(float x, float y) {
		doDelete();
	}

	@Override 
	public final void onTouchDrag(float x, float y, boolean showPreview) {
		super.onTouchDrag(x, y, showPreview);
		if (!inSelectingMode()) {
			onTouchDragNormal(x, y, showPreview);
		} else {
			if (selecting) {
				selection.set(selection.left, selection.top, 
						x - getOffX(), y - getOffY());
			} else if (draggingItem != null) {
				//get how much the player has dragged the items
				float offX = x - getOffX() - startDragX;
				float offY = y - getOffY() - startDragY;
				
				//get the world position of dragging item and record it
				getWorldBounds(draggingItem, boundsRect);
				float startLeft = boundsRect.left;
				float startTop = boundsRect.top;
				
				//offset it by the drag and snap it
				boundsRect.offset(offX, offY);
				snapDrawBounds(draggingItem, boundsRect, selectedObjects);
				
				//the real offset is the snapped position minus the start
				offX = boundsRect.left - startLeft;
				offY = boundsRect.top - startTop;
				
				//bound the offset so it can't go off the screen.
				dragOffset.set(offX, offY);
				boundOffset(dragOffset);
			}
		}
	}

	@Override
	public final void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);
		if (inSelectingMode()) {
			T selectedInstance = null;
			for (int i = 0; i < getAllItems().size(); i++) {
				T instance = getAllItems().get(i);
				if (instance != null) {
					getDrawBounds(instance, boundsRect);
					if (boundsRect.contains(x,y)) {
						selectedInstance = instance;
						break;
					}
				}
			}
			if (selectedInstance != null) {
				if (!selectedObjects.contains(selectedInstance)) {
					selectedObjects.clear();
					selectedObjects.add(selectedInstance);
				}
				startDragX = x - getOffX();
				startDragY = y - getOffY();
				dragOffset.set(0, 0);
				draggingItem = selectedInstance;
				heldFrames = 0;
				parent.showCancelButton(x, y, "Delete");
			} else {
				
				selection.set(x - getOffX(), y - getOffY(), 
						x - getOffX(), y - getOffY());
				selecting = true;
			}
		}
	}
	
	protected void showContextMenu() {
		parent.post(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(parent.getContext())
				.setItems(new String[] {
						"Delete"
				}, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							doDelete();
						}
					}
				})
				.setTitle("Menu")
				.setNegativeButton("Cancel", null)
				.show();
			}
		});
	}

	private void doDelete() {
		final LinkedList<T> selected = new LinkedList<T>();
		selected.addAll(selectedObjects);
		Action action = new Action() {
			@Override
			public void undo(PlatformGame game) {
				for (int i = 0; i < selected.size(); i++) {
					add(selected.get(i));
				}
			}
			
			@Override
			public void redo(PlatformGame game) {
				for (int i = 0; i < selected.size(); i++) {
					delete(selected.get(i));
					if (selectedObjects.contains(selected.get(i))) {
						selectedObjects.remove(selected.get(i));
					}
				}
			}
		};
		parent.doAction(action);
	}
	
	protected void setCSelection() {
		cSelection.set(selection);
		cSelection.offset(getOffX(), getOffY());
		if (cSelection.left > cSelection.right) {
			float right = cSelection.right;
			cSelection.right = cSelection.left;
			cSelection.left = right;
		}
		if (cSelection.top > cSelection.bottom) {
			float bottom = cSelection.bottom;
			cSelection.bottom = cSelection.top;
			cSelection.top = bottom;
		}
	}
	
	protected void boundOffset(PointF offset) {
		float offX = offset.x, offY = offset.y;
		
		Tileset tileset = game.getMapTileset(map);
		int width = tileset.tileWidth * map.columns;
		int height = tileset.tileHeight * map.rows;
		for (int i = 0; i < selectedObjects.size(); i++) {
			T instance = selectedObjects.get(i);
			getDrawBounds(instance, boundsRect);
			
			float x = boundsRect.centerX() - getOffX();
			if (offX < -x) {
				offX = -x;
			}
			if (offX > width - x) {
				offX = width - x;
			}
			
			float y = boundsRect.centerY() - getOffY();
			if (offY < -y) {
				offY = -y;
			}
			if (offY > height - y) {
				offY = height - y;
			}
		}
		
		offset.set(offX, offY);
	}

}
