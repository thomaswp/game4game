package edu.elon.honors.price.maker;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ActorInstance;
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

		for (int i = 0; i < map.actorLayer.rows; i++) {
			for (int j = 0; j < map.actorLayer.columns; j++) {
				float x = j * tileset.tileWidth;
				float y = i * tileset.tileHeight;
				int instanceId = map.actorLayer.tiles[i][j];
				int actorClass = map.getActorType(i, j);

				if (actorClass > -1) {
					Bitmap bmp = mode == DrawMode.Below ? darkActors[actorClass] : actors[actorClass];
					float sx = (tileset.tileWidth - bmp.getWidth()) / 2f;
					float sy = (tileset.tileHeight - bmp.getHeight());// / 2f;
					float dx = x + getOffX() + sx;
					float dy = y + getOffY() + sy;

					paint.setAlpha(mode == DrawMode.Above ? MapEditorView.TRANS : 255);
					parent.drawActor(c, dx, dy, instanceId, bmp, paint);
				}
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
		final int oldClass = map.getActorType(row, col);

		//int previousId = game.getSelectedMap().actorLayer.tiles[row][col];

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
					map.setActor(row, col, oldClass);
					map.setActor(heroRow, heroCol, 0);
				}

				@Override
				public void redo(PlatformGame game) {
					map.setActor(heroRow, heroCol, -1);
					map.setActor(row, col, newClass);
				}
			};
		} else {
			action = new Action() {
				@Override
				public void undo(PlatformGame game) {
					map.setActor(row, col, oldClass);
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
//		editIcons.add(
//				BitmapFactory.decodeResource(parent.getResources(),
//						R.drawable.no));
	}

	@Override
	protected ArrayList<ActorInstance> getAllItems() {
		return map.actors;
	}

	@Override
	protected void getDrawBounds(ActorInstance item, RectF bounds) {
		Tileset tileset = game.getMapTileset(map);
		
		Bitmap bmp = actors[item.classIndex];
		float x = item.column * tileset.tileWidth;
		float y = item.row * tileset.tileHeight;
		float sx = (tileset.tileWidth - bmp.getWidth()) / 2f;
		float sy = (tileset.tileHeight - bmp.getHeight());// / 2f;
		float dx = x + getOffX() + sx;
		float dy = y + getOffY() + sy;
		
		bounds.set(dx, dy, dx + bmp.getWidth(), dy + bmp.getHeight());
	}

	@Override
	protected Bitmap getBitmap(ActorInstance item, DrawMode mode) {
		int actorClass = item.classIndex;
		return mode == DrawMode.Below ? darkActors[actorClass] : actors[actorClass];
	}

	@Override
	protected void shiftItem(ActorInstance item, float offX, float offY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void delete(ActorInstance item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void add(ActorInstance item) {
	}
}
