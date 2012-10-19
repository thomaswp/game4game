package edu.elon.honors.price.maker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.FloatMath;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.maker.MapActivityBase.MapView;
import edu.elon.honors.price.maker.MapEditorLayer.Action;
import edu.elon.honors.price.maker.MapEditorLayer.DrawMode;
import edu.elon.honors.price.maker.MapEditor.ReturnResponse;

public class MapEditorView extends MapView {

	public static final float DARK = 0.7f;
	public static final int TRANS = 150;
	public static final int REQ_SELECTOR = 1;
	public static final int MODE_MOVE = 0;
	public static final int MODE_EDIT = 1;
	public static final int EDIT_NORMAL = 0;
	public static final int EDIT_ALT = 1;
	public static final int N_EDIT_MODES = 2;
	protected final static int SCROLL_BORDER = 25;
	protected final static int SCROLL_TICK = 3;


	private MapEditorLayer[] layers;
	private int selectedLayer;
	private int previewSelectedLayer;
	private boolean selectingLayer, selectingEditMode;
	private float hsv[] = new float[3];
	private float layerButtonsExtention, editModeButtonsExtention;
	private Button layerButton, selectionButton, modeButton; 
	private Button undoButton, redoButton;
	private Button[] cancelButtons, cancelReplacedButtons;
	private ArrayList<Action> actions = new ArrayList<Action>();
	private int actionIndex = 0;
	private Paint darkPaint;

	protected int editMode;
	protected Bitmap[] darkTiles;
	protected Bitmap[] darkActors;
	protected Rect tilesetSelection;
	protected Bitmap tilesetImage;
	protected int actorSelection;
	protected Bitmap actorImage;
	protected int objectSelection;
	protected Bitmap objectImage;
	protected int mode;

	private MapEditor getEditor() {
		return (MapEditor)getContext();
	}

	private String getModeButtonText() {
		switch (mode) {
		case 0: return "Move";
		case 1: return "Edit";
		}
		return "";
	}

	public void setGame(PlatformGame game, boolean loadEditorData) {
		synchronized(this.game) {
			updateActors(game);
			updateObjects(game);
			updateTileset(game);
			PlatformGame oldGame = this.game;
			this.game = game;
			updateMidgrounds(oldGame);
			if (layers != null) {
				for (int i = 0; i < layers.length; i++) {
					layers[i].setGame(game);
				}
			}
			layers[selectedLayer].refreshSelection();
			if (loadEditorData && game.getSelectedMap().editorData != null) {
				loadMapData((EditorData)game.getSelectedMap().editorData);
			}
		}
		actions.clear();
		actionIndex = 0;
	}

	public MapEditorView(Context context, PlatformGame game, 
			Bundle savedInstanceState) {
		super(context, game, savedInstanceState);
		createDarkTiles();

		if (game.getSelectedMap().editorData != null) {
			loadMapData((EditorData)game.getSelectedMap().editorData);
		} else {
			loadMapData(new EditorData());
		}

		layers = new MapEditorLayer[] {
				new MapEditorLayerTiles(this, 0),
				new MapEditorLayerTiles(this, 1),
				new MapEditorLayerTiles(this, 2),
				new MapEditorLayerActors(this),
				new MapEditorLayerObjects(this)
		};

		for (int i = 2; i < layers.length; i++) {
			layers[i].refreshSelection();
		}
	}

	public void saveMapData() {
		EditorData data = new EditorData();
		data.layer = selectedLayer;
		data.editMode = editMode;
		data.actorSelection = actorSelection;
		data.tileSelectionLeft = tilesetSelection.left;
		data.tileSelectionTop = tilesetSelection.top;
		data.tileSelectionRight = tilesetSelection.right;
		data.tileSelectionBottom = tilesetSelection.bottom;
		data.objectSelection = objectSelection;

		game.getSelectedMap().editorData = data;
	}

	public void loadMapData(EditorData data) {
		this.selectedLayer = data.layer;
		this.editMode = data.editMode;
		this.mode = MODE_MOVE;
		this.actorSelection = data.actorSelection;
		this.tilesetSelection = new Rect(
				data.tileSelectionLeft,
				data.tileSelectionTop,
				data.tileSelectionRight,
				data.tileSelectionBottom
		);
		this.objectSelection = data.objectSelection;
	}

	@Override
	protected void createButtons() {
		layerButton = createBottomRightButton("Layer");
		layerButton.onPressedHandler = new Runnable() {
			@Override
			public void run() {
				selectingLayer = true;
			}
		};
		buttons.add(layerButton);

		selectionButton = createTopRightButton("");
		//selectionButton.imageBorder = true;
		selectionButton.onPressedHandler = new Runnable() {
			@Override
			public void run() {
				selectingEditMode = true;
			}
		};
		selectionButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				editModeButtonsExtention = 0;
				layers[selectedLayer].onSelect();
			}
		};
		buttons.add(selectionButton);

		modeButton = createBottomLeftButton("Move");
		modeButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				mode = (mode + 1) % 2;
			}
		};
		buttons.add(modeButton);

		cancelButtons = new Button[4];
		cancelReplacedButtons = new Button[4];
		cancelButtons[0] = createTopLeftButton("");
		cancelButtons[1] = createTopRightButton("");
		cancelButtons[2] = createBottomLeftButton("");
		cancelButtons[3] = createBottomRightButton("");
		cancelReplacedButtons[0] = null;
		cancelReplacedButtons[1] = selectionButton;
		cancelReplacedButtons[2] = modeButton;
		cancelReplacedButtons[3] = layerButton;
		for (int i = 0; i < 4; i++) {
			cancelButtons[i].text = "Cancel";
			cancelButtons[i].textColor = Color.RED;
			cancelButtons[i].showing = false;
			buttons.add(cancelButtons[i]);
		}

		int rad = (int)(getButtonRad() * 0.8f); 
		int x = width / 2 - (int)(rad * 1.2f);
		int y = height + getButtonBorder() / 2;
		float cty = height - getButtonBorder() * 0.8f;
		undoButton = new Button(x, y, x, cty, rad, "Undo");
		undoButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				undoAction();
			}
		};
		buttons.add(undoButton);

		x = width - x;
		redoButton = new Button(x, y, x, cty, rad, "Redo");
		redoButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				redoAction();
			}
		};
		buttons.add(redoButton);
	}

	@Override
	protected void doUpdate(int width, int height, float x, float y) {
		MapEditorLayer layer = layers[selectedLayer];
		undimButtons();

		doReleaseTouch(x, y);
		if (!Input.isTouchDown()) {
			if (selectingLayer) {
				selectLayer();
			}
			if (selectingEditMode) {
				selectEditMode();
			}
			if (layer.isTouchDown()) {
				if (checkCancel(x, y)) {
					layer.touchDown = false;
				} else {
					if (layer.touchDown) {
						layer.onTouchUp(x, y);
					}
				}

			}
		} else {
			if (layer.isTouchDown()) {
				doScroll(x, y, width, height);
				boolean show = !checkCancelDrag(x, y);
				dimButtons();
				layer.onTouchDrag(x, y, show);
			}
		}

		if (Input.isTapped()) {
			if (!doPressButtons(x, y)) {
				if (mode == MODE_MOVE) {
					doMovementStart();
				} else {
					layer.onTouchDown(x, y);
					if (editMode == EDIT_NORMAL) {
						showCancelButton(x, y);
					}
				}
			}
		}

		redoButton.enabled = actionIndex < actions.size();
		undoButton.enabled = actionIndex > 0;

		doMovement();
		doOriginBounding(width, height);
	}

	protected void doScroll(float x, float y, int width, int height) {
		if (x < SCROLL_BORDER) {
			offX += SCROLL_TICK;
		}
		if (x > this.width - SCROLL_BORDER) {
			offX -= SCROLL_TICK;
			Game.debug(offX);
		}
		if (y < SCROLL_BORDER) {
			offY += SCROLL_TICK;
		}
		if (y > this.height - SCROLL_BORDER) {
			offY -= SCROLL_TICK;
		}
		doOriginBounding(width, height);
	}

	protected void dimButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).alphaFactor = 0.3f;
		}
		for (int i = 0; i < cancelButtons.length; i++) {
			cancelButtons[i].alphaFactor = 1;
		}
	}

	protected void undimButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).alphaFactor = 1;
		}
	}

	protected boolean checkCancelDrag(float x, float y) {
		for (int i = 0; i < cancelButtons.length; i++) {
			if (cancelButtons[i].showing) {
				if (cancelButtons[i].isInButton(x, y)) {
					cancelButtons[i].opaque = true;
					return true;
				}
				cancelButtons[i].opaque = false;
			}
		}
		return false;
	}

	protected void showCancelButton(float x, float y) {
		int button = 0;
		if (x < width / 2) button += 1;
		if (y < height / 2) button += 2;
		if (cancelReplacedButtons[button] != null) {
			cancelReplacedButtons[button].showing = false;
		}
		cancelButtons[button].showing = true;
	}

	protected boolean checkCancel(float x, float y) {
		for (int i = 0; i < cancelButtons.length; i++) {
			if (cancelButtons[i].showing) {
				boolean cancel = cancelButtons[i].isInButton(x, y);
				cancelButtons[i].showing = false;
				if (cancelReplacedButtons[i] != null) {
					cancelReplacedButtons[i].showing = true;
				}
				if (cancel) {
					return true;
				}
			}
		}
		return false;
	}

	protected void selectLayer() {
		selectingLayer = false;
		int button = getTouchingLayerButton();
		if (button >= 0) {
			if (selectedLayer != button) {
				editMode = EDIT_NORMAL;
				selectedLayer = button;
			}
		}
	}

	protected void selectEditMode() {
		selectingEditMode = false;
		int button = getTouchingEditModeButton();
		if (button >= 0) {
			editMode = button;
		}
	}

	@Override
	protected void drawContent(Canvas c) {
		
		int selectedLayer = previewSelectedLayer >= 0 ? 
				previewSelectedLayer : this.selectedLayer;
		for (int i = 0; i < layers.length; i++) {
			DrawMode mode = DrawMode.Selected;
			if (i < selectedLayer) mode = DrawMode.Below;
			if (i > selectedLayer) mode = DrawMode.Above;

			layers[i].drawLayer(c, mode);
		}
	}

	@Override
	public void drawGrid(Canvas c) {
		super.drawGrid(c);
		layers[selectedLayer].drawContent(c);
	}

	protected float getOptionButtonRadius() {
		return getButtonRad() / 1.8f;
	}

	protected float getOptionButtonOuterRadius() {
		return getOptionButtonRadius() * layers.length * 4 / (float)Math.PI / 1.5f;
	}

	protected int getTouchingLayerButton() {
		float dx = width - Input.getLastTouchX();
		float dy = height - Input.getLastTouchY();
		float rad = (float)Math.pow(dx * dx + dy * dy, 0.5);
		if (Math.abs(rad - getOptionButtonOuterRadius()) < getOptionButtonRadius() * 1.5f) {
			int layer = (int)((-Math.atan2(-dy, dx)) / (Math.PI / 2) * layers.length);
			return Math.min(Math.max(layer, 0), layers.length - 1);
		}
		return -1;
	}

	protected int getTouchingEditModeButton() {
		float dx = width - Input.getLastTouchX();
		float dy = Input.getLastTouchY();
		float rad = (float)Math.pow(dx * dx + dy * dy, 0.5);
		if (Math.abs(rad - getOptionButtonOuterRadius()) < getOptionButtonRadius() * 1.5f) {
			int layer = (int)((Math.atan2(dy, dx)) / (Math.PI / 2) * N_EDIT_MODES);
			return Math.min(Math.max(layer, 0), N_EDIT_MODES - 1);
		}
		return -1;
	}

	@Override
	protected void drawButtons(Canvas c) {
		drawLayerButtons(c);
		drawEditModeButtons(c);

		int layer = previewSelectedLayer >= 0 ? previewSelectedLayer : selectedLayer;
		selectionButton.image = layers[layer].getSelection();
		modeButton.text = getModeButtonText();
		super.drawButtons(c);
	}

	private void drawLayerButtons(Canvas c) {
		if (selectingLayer) {
			layerButtonsExtention = (5 * layerButtonsExtention + 1) / 6;
		} else {
			layerButtonsExtention = (5 * layerButtonsExtention) / 6;
		}

		if (layerButtonsExtention > 0.2) {
			int nOptions = layers.length;
			int button = getTouchingLayerButton();
			button = button >= 0 ? button : selectedLayer;

			for (int i = 0; i < nOptions; i++) {
				if (i != button) drawLayerButton(c, i, false);
			}
			if (button >= 0) drawLayerButton(c, button, true);
			previewSelectedLayer = button;
		} else {
			previewSelectedLayer = -1;
		}
	}

	private void drawEditModeButtons(Canvas c) {
		if (selectingEditMode) {
			//			if (editModeButtonsExtention < 0.001) {
			//				editModeButtonsExtention += 0.001 / 20;
			//			} else {
			editModeButtonsExtention = (5 * editModeButtonsExtention + 1) / 6;
			//			}
		} else {
			editModeButtonsExtention = (5 * editModeButtonsExtention) / 6;
		}

		if (editModeButtonsExtention > 0.2) {
			int nOptions = N_EDIT_MODES;
			int button = getTouchingEditModeButton();
			button = button >= 0 ? button : editMode;

			for (int i = 0; i < nOptions; i++) {
				if (i != button) drawEditModeButton(c, i, false);
			}
			if (button >= 0) drawEditModeButton(c, button, true);
		}
	}

	private void drawLayerButton(Canvas c, int layer, boolean selected) {
		int nOptions = layers.length;
		float rad = getOptionButtonRadius();

		float degree = (float)(Math.PI - Math.PI / 2 * (layer + 0.5) / (nOptions));
		float outterRadius = getOptionButtonOuterRadius() * layerButtonsExtention;
		float x = FloatMath.cos(degree) * outterRadius + width;
		float y = -FloatMath.sin(degree) * outterRadius + height;

		int alpha = (int)((selected ? 255 : 150) * layerButtonsExtention); 

		if (selected) {
			paint.setColor(Color.DKGRAY);
			paint.setAlpha(alpha);
			c.drawCircle(x, y, rad * 1.1f, paint);
		}

		hsv[0] = (layer + 0.5f) * 160f / nOptions + 100;
		hsv[1] = 0.6f;
		hsv[2] = 0.8f;
		paint.setColor(Color.HSVToColor(alpha, hsv));

		c.drawCircle(x, y, rad, paint);

		Bitmap icon = layers[layer].getIcon();
		float demi = (rad / FloatMath.sqrt(2));
		destRect.set(x - demi, y - demi, x + demi, y + demi);
		c.drawBitmap(icon, null, destRect, paint);
	}
	private RectF destRect = new RectF();


	private void drawEditModeButton(Canvas c, int layer, boolean selected) {
		int nOptions = N_EDIT_MODES;
		float rad = getOptionButtonRadius();

		float degree = (float)(Math.PI + Math.PI / 2 * (layer + 0.5) / (nOptions));
		float outterRadius = getOptionButtonOuterRadius() * editModeButtonsExtention;
		float x = FloatMath.cos(degree) * outterRadius + width;
		float y = -FloatMath.sin(degree) * outterRadius;

		int alpha = (int)((selected ? 255 : 150) * editModeButtonsExtention); 

		if (selected) {
			paint.setColor(Color.DKGRAY);
			paint.setAlpha(alpha);
			c.drawCircle(x, y, rad * 1.1f, paint);
		}

		hsv[0] = (layer + 0.5f) * 80f / nOptions;
		hsv[1] = 0.6f;
		hsv[2] = 0.8f;
		paint.setColor(Color.HSVToColor(alpha, hsv));

		c.drawCircle(x, y, rad, paint);

		Bitmap icon = layer == EDIT_NORMAL ? 
			layers[selectedLayer].getEditIcon() : 
			layers[selectedLayer].getEditAltIcon();
		float demi = (float)(rad / Math.sqrt(2));
		destRect.set(x - demi, y - demi, x + demi, y + demi);
		c.drawBitmap(icon, null, destRect, paint);
	}

	private void updateActors(PlatformGame newGame) {
		this.game.actors = Arrays.copyOf(this.game.actors, newGame.actors.length);
		actors = Arrays.copyOf(actors, newGame.actors.length);
		darkActors = Arrays.copyOf(darkActors, actors.length);
		for (int i = 0; i < newGame.actors.length; i++) {
			ActorClass newActor = i == 0 ? newGame.hero : newGame.actors[i];
			String newName = newActor.imageName;
			ActorClass oldActor = null; String oldName = null;
			oldActor = i == 0 ? this.game.hero : this.game.actors[i];
			oldName = oldActor == null ? null : oldActor.imageName;
			if (oldActor == null || !oldName.equals(newName)) {
				actors[i] = Data.loadActorIcon(newGame.actors[i].imageName);
				darkActors[i] = darken(actors[i]);
			}
		}
	}

	private void updateObjects(PlatformGame newGame) {
		this.game.objects = Arrays.copyOf(this.game.objects, newGame.objects.length);
		objects = Arrays.copyOf(objects, newGame.objects.length);
		for (int i = 0; i < newGame.objects.length; i++) {
			ObjectClass newObject = newGame.objects[i];
			String newName = newObject.imageName;
			ObjectClass oldObject = null; String oldName = null;
			oldObject = this.game.objects[i];
			oldName = oldObject == null ? null : oldObject.imageName;
			if (oldObject == null || !oldName.equals(newName) || 
					oldObject.zoom != newObject.zoom) {
				objects[i] = Data.loadObject(newObject.imageName);
				objects[i] = Bitmap.createScaledBitmap(objects[i],
						(int)(objects[i].getWidth() * newObject.zoom),
						(int)(objects[i].getHeight() * newObject.zoom), true);
			}
		}
	}
	
	private void updateTileset(PlatformGame newGame) {
		if (game.getSelectedMap().tilesetId != 
			newGame.getSelectedMap().tilesetId) {
			Tileset t = newGame.tilesets[newGame.getSelectedMap().tilesetId];
			tiles = createTiles(t, getContext());
			createDarkTiles();
		}
	}
	
	private void updateMidgrounds(PlatformGame oldGame) {
		boolean needRefresh = false;
		List<String> oldMidgrounds = game.getSelectedMap().midGrounds;
		List<String> newMidgrounds = oldGame.getSelectedMap().midGrounds;
		
		
		if (oldMidgrounds.size() != newMidgrounds.size()) needRefresh = true;
		if (!needRefresh) {
			for (int i = 0; i < oldMidgrounds.size(); i++) {
				if (!oldMidgrounds.get(i).equals(newMidgrounds.get(i))) {
					needRefresh = true;
					break;
				}
			}
		}
		if (needRefresh) {
			createMidgrounds();
		}
	}

	private void createDarkTiles() {
		darkTiles = new Bitmap[tiles.length];
		for (int i = 0; i < darkTiles.length; i++) {
			darkTiles[i] = darken(tiles[i]);
		}

		darkActors = new Bitmap[actors.length];
		for (int i = 0; i < darkActors.length; i++) {
			darkActors[i] = darken(actors[i]);
		}
	}

	private Bitmap darken(Bitmap base) {
		if (darkPaint == null) {
			darkPaint = new Paint();
			ColorMatrix cm = new ColorMatrix();
			cm.setScale(DARK, DARK, DARK, 1);
			darkPaint.setColorFilter(new ColorMatrixColorFilter(cm));
		}

		Bitmap bmp = Bitmap.createBitmap(base.getWidth(), 
				base.getHeight(), base.getConfig());
		new Canvas(bmp).drawBitmap(base, 0, 0, darkPaint);
		return bmp;
	}

	public void selectActor() {
		Intent intent = new Intent(getContext(), MapEditorActorSelector.class);
		intent.putExtra("game", game);
		intent.putExtra("id", actorSelection);

		getEditor().returnResponse = new ReturnResponse() {
			@Override
			public void onReturn(Intent data) {
				actorSelection = data.getExtras().getInt("id");
			}
		};

		getEditor().startActivityForResult(intent, REQ_SELECTOR);
	}

	public void selectTileset() {
		Intent intent = new Intent(getContext(), MapTextureSelector.class);
		int id = game.getSelectedMap().tilesetId;
		Tileset tileset = game.tilesets[id];
		intent.putExtra("id", tileset.bitmapName);
		intent.putExtra("tileWidth", tileset.tileWidth);
		intent.putExtra("tileHeight", tileset.tileHeight);
		intent.putExtra("game", game);
		intent.putExtra("left", tilesetSelection.left);
		intent.putExtra("top", tilesetSelection.top);
		intent.putExtra("right", tilesetSelection.right);
		intent.putExtra("bottom", tilesetSelection.bottom);

		getEditor().returnResponse = new ReturnResponse() {
			@Override
			public void onReturn(Intent data) {
				int left = data.getExtras().getInt("left");
				int top = data.getExtras().getInt("top");
				int right = data.getExtras().getInt("right");
				int bottom = data.getExtras().getInt("bottom");
				tilesetSelection.set(left, top, right, bottom);
			}
		};

		getEditor().startActivityForResult(intent, REQ_SELECTOR);
	}

	public void selectObject() {
		Intent intent = new Intent(getContext(), MapEditorObjectSelector.class);
		intent.putExtra("game", game);
		intent.putExtra("id", objectSelection);

		getEditor().returnResponse = new ReturnResponse() {
			@Override
			public void onReturn(Intent data) {
				objectSelection =  data.getExtras().getInt("id");
			}
		};

		getEditor().startActivityForResult(intent, REQ_SELECTOR);
	}

	public void refresh() {
		layers[selectedLayer].refreshSelection();
	}

	public void doAction(Action action) {
		while (actions.size() > actionIndex) {
			actions.remove(actions.size() - 1);
		}
		actions.add(action);
		redoAction();
	}

	private void undoAction() {
		actionIndex--;
		actions.get(actionIndex).undo(game);
	}

	private void redoAction() {
		actions.get(actionIndex).redo(game);
		actionIndex++;
	}

	public static class EditorData implements Serializable {
		private static final long serialVersionUID = 1L;

		public int layer = 1, editMode = 0;
		public int tileSelectionLeft, tileSelectionTop; 
		public int tileSelectionRight = 1, tileSelectionBottom = 1;
		public int actorSelection, objectSelection;
	}
}
