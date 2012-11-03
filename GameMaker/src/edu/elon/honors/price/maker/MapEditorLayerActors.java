package edu.elon.honors.price.maker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.maker.MapEditorView;

public class MapEditorLayerActors extends MapEditorLayer {

	private Bitmap[] actors, darkActors;
	private Bitmap clear; 
	
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
	public void drawContent(Canvas c) {
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
	public void drawLayer(Canvas c, DrawMode mode) {
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
		return parent.actors[parent.actorSelection];
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);

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
	protected Bitmap loadEditIcon() {
		return BitmapFactory.decodeResource(parent.getResources(),
				R.drawable.edit);
	}

	@Override
	protected Bitmap loadEditAltIcon() {
		return BitmapFactory.decodeResource(parent.getResources(),
				R.drawable.edit);
	}
}
