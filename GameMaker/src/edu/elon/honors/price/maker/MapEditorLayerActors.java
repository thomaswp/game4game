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

	public MapEditorLayerActors(MapEditorView parent) {
		super(parent);
		this.actors = parent.actors;
		this.darkActors = parent.darkActors;
		paint.setTextSize(12);
		paint.setAntiAlias(true);
	}
	
	private int getBitmapRow(float touchY) {
		Tileset tileset = game.getMapTileset(map);
		int tileHeight = tileset.tileHeight;
		Bitmap bmp = parent.actorImage;
		return (int)Math.round((touchY - parent.offY - bmp.getHeight() / 2) / tileHeight);
	}

	private int getBitmapCol(float touchX) {
		Tileset tileset = game.getMapTileset(map);
		int tileWidth = tileset.tileWidth;
		Bitmap bmp = parent.actorImage;
		return (int)Math.round((touchX - parent.offX - bmp.getWidth() / 2) / tileWidth);
	}

	@Override
	public void drawContent(Canvas c) {
		if (touchDown && showPreview) {
			Tileset tileset = game.getMapTileset(map);
			int tileWidth = tileset.tileWidth;
			int tileHeight = tileset.tileHeight;

			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			Bitmap bmp = parent.actorImage;

//			int x = (int)(touchX - bmp.getWidth() / 2);
//			int edgeX = (int)parent.offX % tileWidth;
//			x = (x - edgeX + tileWidth / 2) / tileWidth;
//			x = x * tileWidth + edgeX + (tileWidth - bmp.getWidth()) / 2;
//
//			int y = (int)(touchY - bmp.getHeight() / 2);
//			int edgeY = (int)parent.offY % tileHeight;
//			y = (y - edgeY + tileHeight / 2) / tileHeight;
//			y = y * tileHeight + edgeY  + (tileHeight- bmp.getHeight()) / 2;

			float x = getBitmapCol(touchX) * tileWidth + parent.offX + (tileWidth - bmp.getWidth()) / 2;
			float y = getBitmapRow(touchY) * tileHeight + parent.offY + (tileHeight - bmp.getHeight()) / 2;
			
			
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
					float sy = (tileset.tileHeight - bmp.getHeight()) / 2f;
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
	public void refreshSelection() {
		int id = parent.actorSelection;
		if (id == -1) {
			parent.actorImage = BitmapFactory.decodeResource(
					parent.getContext().getResources(), R.drawable.no);
			return;
		}
		ActorClass actor = id == 0 ? game.hero : game.actors[id]; 
		Bitmap bmp = Data.loadActor(actor.imageName);
		bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() / 4, bmp.getHeight() / 4);
		parent.actorImage = bmp;
	}

	@Override
	public Bitmap getSelection() {
		return parent.actorImage;
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);

		final int newClass = parent.actorSelection;
		final int row = getBitmapRow(touchY);
		final int col = getBitmapCol(touchX);
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
