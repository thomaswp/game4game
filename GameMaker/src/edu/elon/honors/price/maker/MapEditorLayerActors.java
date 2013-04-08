package edu.elon.honors.price.maker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ActorInstance;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.maker.MapEditorView;

public class MapEditorLayerActors extends MapEditorLayerSelectable<ActorInstance> {

	private Bitmap[] actors, darkActors;
	private Bitmap clear; 

	@Override
	public void setGame(PlatformGame game) {
		super.setGame(game);
		this.actors = parent.actors;
		this.darkActors = parent.darkActors;
	}

	public MapEditorLayerActors(MapEditorView parent) {
		super(parent);
		this.actors = parent.actors;
		this.darkActors = parent.darkActors;
		this.clear = BitmapFactory.decodeResource(
				parent.getContext().getResources(), R.drawable.no);
		paint.setTextSize(12);
		paint.setAntiAlias(true);
	}

	private int getTouchedRow(float touchY) {
		Tileset tileset = game.getMapTileset(map);
		int tileHeight = tileset.tileHeight;
		return Math.round((touchY - parent.offY) / tileHeight - 0.5f);
	}

	private int getTouchedCol(float touchX) {
		Tileset tileset = game.getMapTileset(map);
		int tileWidth = tileset.tileWidth;
		return Math.round((touchX - parent.offX) / tileWidth - 0.5f);
	}

	@Override
	public void drawContentNormal(Canvas c) {
		if (touchDown && showPreview) {
			Tileset tileset = game.getMapTileset(map);
			int tileWidth = tileset.tileWidth;
			int tileHeight = tileset.tileHeight;

			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			Bitmap bmp = getSelection();

			float x = getTouchedCol(touchX) * tileWidth + parent.offX + (tileWidth - bmp.getWidth()) / 2;
			float y = getTouchedRow(touchY) * tileHeight + parent.offY + (tileHeight - bmp.getHeight());


			c.drawRect(x, y, x + bmp.getWidth(), y + bmp.getHeight(), paint);
			c.drawBitmap(bmp, x, y, paint);
		}
	}

	@Override
	public void drawLayerNormal(Canvas c, DrawMode mode) {
		Tileset tileset = game.tilesets[map.tilesetId];

		for (int i = 0; i < map.actors.size(); i++) {
			ActorInstance actor = map.actors.get(i);
			float x = actor.column * tileset.tileWidth;
			float y = actor.row * tileset.tileHeight;
			int actorClass = actor.classIndex;

			if (actorClass > -1) {
				Bitmap bmp = mode == DrawMode.Below ? darkActors[actorClass] : actors[actorClass];
				float sx = (tileset.tileWidth - bmp.getWidth()) / 2f;
				float sy = (tileset.tileHeight - bmp.getHeight());// / 2f;
				float dx = x + getOffX() + sx;
				float dy = y + getOffY() + sy;

				paint.setAlpha(mode == DrawMode.Above ? MapEditorView.TRANS : 255);
				parent.drawActor(c, dx, dy, actor.id, bmp, paint);
			}
		}
	}

	@Override
	public void onSelect() {
		parent.selectActor();
	}

	@Override
	public void refreshSelection() { }

	@Override
	public Bitmap getSelection() {
		if (parent.actorSelection == -1) {
			if (clear == null) {
				clear = BitmapFactory.decodeResource(
						parent.getContext().getResources(), R.drawable.no);
			}
			return clear;
		}
		return parent.actors[parent.actorSelection];
	}

	@Override
	public void onTouchUpNormal(float x, float y) {

		final int newClass = parent.actorSelection;
		final int row = getTouchedRow(touchY);
		final int col = getTouchedCol(touchX);
		
		ActorInstance oldInstance = map.getActorInstance(row, col);
		final int oldClass = (oldInstance == null) ? -1 : oldInstance.classIndex;
		final int oldId = (oldInstance == null) ? -1 : oldInstance.id;

		if (newClass == oldClass) {
			return;
		}
		if (oldClass == 0) {
			return;
		}

		Action action;
		if (newClass == 0) {
			final int heroRow = map.getHeroRow();
			final int heroCol = map.getHeroCol();

			action = new Action() {
				@Override
				public void undo(PlatformGame game) {
					map.setActor(heroRow, heroCol, newClass);
					map.setActor(row, col, oldClass, oldId);
				}

				@Override
				public void redo(PlatformGame game) {
					map.setActor(row, col, newClass);
				}
			};
		} else {
			action = new Action() {
				@Override
				public void undo(PlatformGame game) {
					map.setActor(row, col, oldClass, oldId);
				}

				@Override
				public void redo(PlatformGame game) {
					map.setActor(row, col, newClass);
				}
			};
		}
		parent.doAction(action);
	}

	@Override
	protected Bitmap loadIcon() {
		return BitmapFactory.decodeResource(parent.getResources(), 
				R.drawable.layeractor);
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
	protected ArrayList<ActorInstance> getAllItems() {
		return map.actors;
	}

	@Override
	protected void getDrawBounds(ActorInstance item, RectF bounds) {
		Tileset tileset = game.getMapTileset(map);

		Bitmap bmp = actors[item.classIndex];
		float width = bmp.getWidth();
		float height = bmp.getHeight();
		float x = item.column * tileset.tileWidth;
		float y = item.row * tileset.tileHeight;
		float sx = (tileset.tileWidth - width) / 2f;
		float sy = (tileset.tileHeight - height);
		float dx = x + getOffX() + sx;
		float dy = y + getOffY() + sy;

		bounds.set(dx, dy, dx + bmp.getWidth(), dy + bmp.getHeight());
	}

	@Override
	protected Bitmap getBitmap(ActorInstance item, DrawMode mode) {
		int actorClass = item.classIndex;
		return mode == DrawMode.Below ? darkActors[actorClass] : actors[actorClass];
	}

//	@Override
//	protected void shiftItem(ActorInstance item, float offX, float offY) {
//		Tileset tileset = map.getTileset(game);
//		
//		item.column = Math.round(item.column + offX / tileset.tileWidth);
//		item.row = Math.round(item.row + offY / tileset.tileHeight);
//	}
//
//	@Override
//	protected void delete(ActorInstance item) {
//		map.actors.remove(item);
//	}
//
//	@Override
//	protected void add(ActorInstance item) {
//		map.setActor(item.row, item.column, item.classIndex, item.id);
//	}

	@Override
	protected void snapDrawBounds(ActorInstance item, RectF bounds, 
			List<ActorInstance> ignore) {
		float centerX = bounds.centerX();
		float bottom = bounds.bottom;
		
		Tileset tileset = map.getTileset(game);
		int tileWidth = tileset.tileWidth;
		int tileHeight = tileset.tileHeight;
		
		centerX = (int)(centerX / tileWidth) * tileWidth + tileWidth / 2;
		bottom = (int)(bottom / tileHeight + 0.5f) * tileHeight;
		
		bounds.offsetTo(centerX - bounds.width() / 2, bottom - bounds.height());
	}

	@Override
	protected boolean inSelectingMode() {
		return parent.editMode == MapEditorView.EDIT_ALT1;
	}

	@Override
	protected Action doShift(final LinkedList<ActorInstance> items, final float offX,
			final float offY) {
		final LinkedList<ActorInstance> toDelete = new LinkedList<ActorInstance>();
		//final LinkedList<ActorInstance> toIgnore = new LinkedList<ActorInstance>();
		
		Tileset tileset = game.tilesets[game.getSelectedMap().tilesetId];

		final int offRows = Math.round(offY / tileset.tileHeight);
		final int offCols = Math.round(offX / tileset.tileWidth);
		
		if (offRows == 0 && offCols == 0) return null;
		
		for (ActorInstance toPlace : items) {
			ActorInstance oldInstance = map.getActorInstance(
					toPlace.row + offRows, 
					toPlace.column + offCols);
			
			if (oldInstance == null) continue;
			
			boolean skipDelete = false;
			for (ActorInstance check : items) {
				if (check.row == oldInstance.row &&
						check.column == oldInstance.column) {
					skipDelete = true;
					break;
				}
			}
			
			if (skipDelete) continue;
			
			if (oldInstance.id != Map.HERO_ID) {
				toDelete.add(oldInstance);
			} else {
				toDelete.add(toPlace);
			}
		}
		
		if (toDelete.size() > 0) {
			return null;
		}
		
		return new Action() {
			@Override
			public void undo(PlatformGame game) {
				for (ActorInstance move : items) {
					if (!toDelete.contains(move)) {
						move.row -= offRows;
						move.column -= offCols;
					}
				}
				for (ActorInstance instance : toDelete) {
					Map map = game.getSelectedMap();
					map.setActor(instance.row, instance.column, 
							instance.classIndex, instance.id);
				}
				cleanupSelection();
			}
			
			@Override
			public void redo(PlatformGame game) {
				for (ActorInstance instance : toDelete) {
					Map map = game.getSelectedMap();
					ActorInstance delete = map.getActorInstance(
							instance.row, instance.column);
					map.actors.remove(delete);
				}
				for (ActorInstance move : items) {
					if (!toDelete.contains(move)) {
						move.row += offRows;
						move.column += offCols;
					}
				}
				cleanupSelection();
			}
		};
	}
	
	private void cleanupSelection() {
		for (int i = 0; i < selectedObjects.size(); i++) {
			ActorInstance selected = selectedObjects.get(i);
			ActorInstance real = map.getActorInstanceById(selected.id);
			if (real == null) {
				selectedObjects.remove(i);
				i--;
			} else {
				selectedObjects.set(i, real);
			}
		}
	}

	@Override
	protected Action doDelete(final LinkedList<ActorInstance> items) {
		for (ActorInstance instance : items) {
			if (instance.id == Map.HERO_ID) return null;
		}
		
		return new Action() {
			
			@Override
			public void undo(PlatformGame game) {
				for (ActorInstance instance : items) {
					if (instance.id != Map.HERO_ID) {
						map.addActor(instance);
					}
				}
			}
			
			@Override
			public void redo(PlatformGame game) {
				for (ActorInstance instance : items) {
					ActorInstance toRemove = map.getActorInstance(
							instance.row, instance.column); 
					map.actors.remove(toRemove);
					selectedObjects.remove(toRemove);
				}
			}
		};
	}
}
