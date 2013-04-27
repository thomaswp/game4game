package edu.elon.honors.price.maker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Tileset;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorAction;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButton;
import edu.elon.honors.price.data.tutorial.Tutorial.EditorButtonAction;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.maker.MapActivityBase.MapView;
import edu.elon.honors.price.maker.MapEditorLayer.Action;
import edu.elon.honors.price.maker.MapEditorLayer.DrawMode;
import edu.elon.honors.price.maker.MapEditor.ReturnResponse;
import edu.elon.honors.price.maker.MapEditorTextureSelectorView.Poster;
import edu.elon.honors.price.maker.ScrollingImageSelectorView.OnSelectionListener;
import edu.elon.honors.price.maker.action.ActionChangeColor.TurnFlashingForData;

public class MapEditorView extends MapView {

	public static final float DARK = 0.7f;
	public static final int TRANS = 150;
	public static final int REQ_SELECTOR = 1;
	public static final int MODE_MOVE = 0;
	public static final int MODE_EDIT = 1;
	public static final int EDIT_NORMAL = 0;
	public static final int EDIT_ALT1 = 1;
	public static final int EDIT_ALT2 = 2;
	protected final static int SCROLL_BORDER = 25;
	protected final static int SCROLL_TICK = 3;

	private MapEditorLayer[] layers;
	private int selectedLayer;
	//private boolean selectingLayer, selectingEditMode, selectingMenu;
	//private float hsv[] = new float[3];
	//private float layerButtonsExtention, editModeButtonsExtention, menuButtonsExtention;
	private ButtonFan layerFan, editModeFan, menuFan;
	private Button layerButton, selectionButton, modeButton;
	//Keeps track of when the player drags out of the selection button
	private boolean strayedOutOfSelectionButton;
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
	protected int objectSelection;
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
			TutorialUtils.setTutorial(game.tutorial, getContext());
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
		
		boolean goBack = game.tutorial != null && game.tutorial.hasPrevious();
		TutorialUtils.setTutorial(game.tutorial, getContext());
		if (goBack) TutorialUtils.backOneMessage(context);

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
		layerButton.editorButton = EditorButton.MapEditorLayer;
		layerFan = new LayerFan(layerButton);
		
		buttons.add(layerButton);

		Button trb = createTopRightButton("");
		selectionButton = new Button(trb.cx, trb.cy, trb.ctx, trb.cty, 
				trb.radius, trb.text) {
						
			private Rect src = new Rect();
			private RectF dest = new RectF();
			
			@Override
			protected void drawButtonImage(Canvas c, Paint p, int trans) {

				super.drawButtonImage(c, p, trans);
				
				Bitmap cornerImage = layers[selectedLayer].editIcons.get(editMode);

				float cW = radius * 0.15f;
				float cH = radius * 0.15f;
				
				float ccx = cx - radius * 0.0f;
				float ccy = cy + radius * 0.5f;
				
				src.set(0, 0, cornerImage.getWidth(), cornerImage.getHeight());
				dest.set(ccx - cW, ccy - cH, ccx + cW, ccy + cH);
				
				paint.setColor(Color.LTGRAY);
				paint.setAlpha(trans);
				paint.setStyle(Style.FILL);
				c.drawRect(dest, paint);
				
				paint.setColor(Color.DKGRAY);
				paint.setAlpha(trans);
				paint.setStyle(Style.STROKE);
				c.drawRect(dest, paint);
				
				c.drawBitmap(cornerImage, src, dest, paint);

			}
		};
		selectionButton.editorButton = EditorButton.MapEditorSelection;
		//selectionButton.imageBorder = true;
		selectionButton.onPressedHandler = new Runnable() {
			@Override
			public void run() {
				strayedOutOfSelectionButton = false;
			}
		};
		selectionButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				if (!strayedOutOfSelectionButton) {
					editModeFan.collapse();
					layers[selectedLayer].onSelect();
				}
			}
		};
		editModeFan = new EditModeFan(selectionButton);
		buttons.add(selectionButton);

		modeButton = createBottomLeftButton("Move");
		modeButton.editorButton = EditorButton.MapEditorMoveMode;
		modeButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				mode = (mode + 1) % 2;
			}
		};
		buttons.add(modeButton);

		Button menuButton = null;

		//if (android.os.Build.VERSION.SDK_INT > 10) {
			menuButton = createTopLeftButton("Menu");
			menuButton.onReleasedHandler = new Runnable() {
				@Override
				public void run() {
					post(new Runnable() {
						@Override
						public void run() {
							getEditor().openOptionsMenu();
						}
					});
				}
			};
			menuButton.editorButton = EditorButton.MapEditorMenu;
			menuFan = new MenuFan(menuButton);
			buttons.add(menuButton);
		//}

		cancelButtons = new Button[4];
		cancelReplacedButtons = new Button[4];
		cancelButtons[0] = createTopLeftButton("");
		cancelButtons[1] = createTopRightButton("");
		cancelButtons[2] = createBottomLeftButton("");
		cancelButtons[3] = createBottomRightButton("");
		cancelReplacedButtons[0] = menuButton;
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
		undoButton.editorButton = EditorButton.MapEditorUndo;
		undoButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				undoAction();
			}
		};
		buttons.add(undoButton);

		x = width - x;
		redoButton = new Button(x, y, x, cty, rad, "Redo");
		redoButton.editorButton = EditorButton.MapEditorRedo;
		redoButton.onReleasedHandler = new Runnable() {
			@Override
			public void run() {
				redoAction();
			}
		};
		buttons.add(redoButton);
		
		if (game.tutorial != null) {
			rad = (int)(modeButton.radius / 2.5f);
			int offX = (int)(rad * 0.6);
			x = rad - offX;
			y = height / 2;
			Button helpButton = new Button(x, y, x + offX / 4, y, rad, "?");
			helpButton.editorButton = EditorButton.MapEditorHelpButton;
			helpButton.onReleasedHandler = new Runnable() {
				@Override
				public void run() {
					TutorialUtils.backOneMessage(getContext());
				}
			};
			buttons.add(helpButton);
		}
	}

	@Override
	protected void doUpdate(int width, int height, float x, float y) {
		MapEditorLayer layer = layers[selectedLayer];
		undimButtons();

		doReleaseTouch(x, y);
		
		layerFan.upate();
		editModeFan.upate();
		if (menuFan != null) menuFan.upate();
		
		if (!Input.isTouchDown()) {
			if (layer.isTouchDown()) {
				if (checkCancel(x, y)) {
					layer.touchDown = false;
					layer.onTouchCanceled(x, y);
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
			if (selectionButton.down && !selectionButton.isInButton(x, y)) {
				strayedOutOfSelectionButton = true;
			}
		}
		
		updateTouchInput(true);
		PointF screenTapped = getTappedPoint(); 
		if (screenTapped != null) {
			layer.onTouchDown(screenTapped.x, screenTapped.y);
			if (editMode == EDIT_NORMAL) {
				showCancelButton(screenTapped.x, screenTapped.y);
			}
		}

		redoButton.enabled = actionIndex < actions.size();
		undoButton.enabled = actionIndex > 0;

		doMovement();
		doOriginBounding(width, height);
	}
	
	@Override
	protected boolean inMovementMode() {
		return mode == MODE_MOVE;
	}

	protected void doScroll(float x, float y, int width, int height) {
		if (x < SCROLL_BORDER) {
			offX += SCROLL_TICK;
		}
		if (x > this.width - SCROLL_BORDER) {
			offX -= SCROLL_TICK;
			Debug.write(offX);
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
	
	protected void cancelShowCancel() {
		for (int i = 0; i < cancelButtons.length; i++) {
			cancelButtons[i].showing = false;
		}
	}

	protected void showCancelButton(float x, float y) {
		showCancelButton(x, y, "Cancel");
	}
	
	protected void showCancelButton(float x, float y, String text) {
		int button = 0;
		if (x < width / 2) button += 1;
		if (y < height / 2) button += 2;
		if (cancelReplacedButtons[button] != null) {
			cancelReplacedButtons[button].showing = false;
		}
		cancelButtons[button].showing = true;
		cancelButtons[button].text = text;
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

	@Override
	protected void drawContent(Canvas c) {

		int selectedLayer = layerFan.getPreviewSelectedOption();
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

//	protected int getTouchingLayerButton() {
//		float dx = width - Input.getLastTouchX();
//		float dy = height - Input.getLastTouchY();
//		float rad = (float)Math.pow(dx * dx + dy * dy, 0.5);
//		if (Math.abs(rad - getOptionButtonOuterRadius()) < getOptionButtonRadius() * 1.5f) {
//			int layer = (int)((-Math.atan2(-dy, dx)) / (Math.PI / 2) * layers.length);
//			return Math.min(Math.max(layer, 0), layers.length - 1);
//		}
//		return -1;
//	}
//
//	protected int getTouchingEditModeButton() {
//		int nButtons = layers[selectedLayer].editIcons.size();
//		float dx = width - Input.getLastTouchX();
//		float dy = Input.getLastTouchY();
//		float rad = (float)Math.pow(dx * dx + dy * dy, 0.5);
//		if (Math.abs(rad - getOptionButtonOuterRadius()) < getOptionButtonRadius() * 1.5f) {
//			int layer = (int)((Math.atan2(dy, dx)) / (Math.PI / 2) * nButtons);
//			return Math.min(Math.max(layer, 0), nButtons - 1);
//		}
//		return -1;
//	}
//	
//	protected int getTouchingMenuButton() {
//		int nButtons = MENU_OPTIONS;
//		float dx = Input.getLastTouchX();
//		float dy = Input.getLastTouchY();
//		float rad = (float)Math.pow(dx * dx + dy * dy, 0.5);
//		if (Math.abs(rad - getOptionButtonOuterRadius()) < getOptionButtonRadius() * 1.5f) {
//			int layer = (int)((Math.PI - Math.atan2(dy, dx)) / (Math.PI / 2) * nButtons);
//			return Math.min(Math.max(layer, 0), nButtons - 1);
//		}
//		return -1;
//	}

	@Override
	protected void drawButtons(Canvas c) {
		layerFan.draw(c, paint);
		editModeFan.draw(c, paint);
		if (menuFan != null) menuFan.draw(c, paint);

		int layer = layerFan.getPreviewSelectedOption();
		selectionButton.image = layers[layer].getSelection();
		modeButton.text = getModeButtonText();
		super.drawButtons(c);
	}

//	private void drawLayerButtons(Canvas c) {
//		if (selectingLayer) {
//			layerButtonsExtention = (5 * layerButtonsExtention + 1) / 6;
//		} else {
//			layerButtonsExtention = (5 * layerButtonsExtention) / 6;
//		}
//
//		if (layerButtonsExtention > 0.2) {
//			int nOptions = layers.length;
//			int button = getTouchingLayerButton();
//			button = button >= 0 ? button : selectedLayer;
//
//			for (int i = 0; i < nOptions; i++) {
//				if (i != button) drawLayerButton(c, i, false);
//			}
//			if (button >= 0) drawLayerButton(c, button, true);
//			previewSelectedLayer = button;
//		} else {
//			previewSelectedLayer = -1;
//		}
//	}
//
//	private void drawEditModeButtons(Canvas c) {
//		if (selectingEditMode) {
//			editModeButtonsExtention = (5 * editModeButtonsExtention + 1) / 6;
//		} else {
//			editModeButtonsExtention = (5 * editModeButtonsExtention) / 6;
//		}
//
//		if (editModeButtonsExtention > 0.2) {
//			int nOptions = layers[selectedLayer].editIcons.size();
//			int button = getTouchingEditModeButton();
//			button = button >= 0 ? button : editMode;
//
//			for (int i = 0; i < nOptions; i++) {
//				if (i != button) drawEditModeButton(c, i, false);
//			}
//			if (button >= 0) drawEditModeButton(c, button, true);
//		}
//	}
//	
//	private void drawMenuButtons(Canvas c) {
//		if (selectingMenu) {
//			menuButtonsExtention = (5 * menuButtonsExtention + 1) / 6;
//		} else {
//			menuButtonsExtention = (5 * menuButtonsExtention) / 6;
//		}
//
//		if (menuButtonsExtention > 0.2) {
//			int nOptions = MENU_OPTIONS;
//			int button = getTouchingMenuButton();
//			button = button >= 0 ? button : editMode;
//
//			for (int i = 0; i < nOptions; i++) {
//				if (i != button) drawMenuButton(c, i, false);
//			}
//			if (button >= 0) drawMenuButton(c, button, true);
//		}
//	}
//
//	

//	
//	private void drawLayerButton(Canvas c, int layer, boolean selected) {
//		int nOptions = layers.length;
//		float rad = getOptionButtonRadius();
//
//		float degree = (float)(Math.PI - Math.PI / 2 * (layer + 0.5) / (nOptions));
//		float outterRadius = getOptionButtonOuterRadius() * layerButtonsExtention;
//		float x = FloatMath.cos(degree) * outterRadius + width;
//		float y = -FloatMath.sin(degree) * outterRadius + height;
//
//		int alpha = (int)((selected ? 255 : 150) * layerButtonsExtention); 
//		
//		if (selected) {
//			paint.setColor(Color.DKGRAY);
//			paint.setAlpha(alpha);
//			c.drawCircle(x, y, rad * 1.1f, paint);
//		}
//
//		hsv[0] = (layer + 0.5f) * 160f / nOptions + 100;
//		hsv[1] = 0.6f;
//		hsv[2] = 0.8f;
//		paint.setColor(Color.HSVToColor(alpha, hsv));
//		
//		if (TutorialUtils.isHighlighted(LAYER_BUTTONS[layer])) {
//			paint.setColor(TutorialUtils.getHightlightColor());
//		}
//
//		c.drawCircle(x, y, rad, paint);
//
//		Bitmap icon = layers[layer].getIcon();
//		float demi = (rad / FloatMath.sqrt(2));
//		destRect.set(x - demi, y - demi, x + demi, y + demi);
//		c.drawBitmap(icon, null, destRect, paint);
//	}
//	private RectF destRect = new RectF();
//	
//	private void drawEditModeButton(Canvas c, int layer, boolean selected) {
//		int nOptions = layers[selectedLayer].editIcons.size();
//		float rad = getOptionButtonRadius();
//
//		float degree = (float)(Math.PI + Math.PI / 2 * (layer + 0.5) / (nOptions));
//		float outterRadius = getOptionButtonOuterRadius() * editModeButtonsExtention;
//		float x = FloatMath.cos(degree) * outterRadius + width;
//		float y = -FloatMath.sin(degree) * outterRadius;
//
//		int alpha = (int)((selected ? 255 : 150) * editModeButtonsExtention); 
//
//		if (selected) {
//			paint.setColor(Color.DKGRAY);
//			paint.setAlpha(alpha);
//			c.drawCircle(x, y, rad * 1.1f, paint);
//		}
//
//		hsv[0] = (layer + 0.5f) * 80f / nOptions;
//		hsv[1] = 0.6f;
//		hsv[2] = 0.8f;
//		paint.setColor(Color.HSVToColor(alpha, hsv));
//		
//		if (TutorialUtils.isHighlighted(layers[selectedLayer].getEditButtons()[layer])) {
//			paint.setColor(TutorialUtils.getHightlightColor());
//		}
//
//		c.drawCircle(x, y, rad, paint);
//
//		Bitmap icon = layers[selectedLayer].editIcons.get(layer);
//		float demi = (float)(rad / Math.sqrt(2));
//		destRect.set(x - demi, y - demi, x + demi, y + demi);
//		c.drawBitmap(icon, null, destRect, paint);
//	}
//	
//	private void drawMenuButton(Canvas c, int layer, boolean selected) {
//		int nOptions = MENU_OPTIONS;
//		float rad = getOptionButtonRadius();
//
//		float degree = (float)(-Math.PI / 2 * (layer + 0.5) / (nOptions));
//		float outterRadius = getOptionButtonOuterRadius() * menuButtonsExtention;
//		float x = FloatMath.cos(degree) * outterRadius;
//		float y = -FloatMath.sin(degree) * outterRadius;
//
//		int alpha = (int)((selected ? 255 : 150) * menuButtonsExtention); 
//
//		if (selected) {
//			paint.setColor(Color.DKGRAY);
//			paint.setAlpha(alpha);
//			c.drawCircle(x, y, rad * 1.1f, paint);
//		}
//
//		hsv[0] = (layer + 0.5f) * 80f / nOptions;
//		hsv[1] = 0.6f;
//		hsv[2] = 0.8f;
//		paint.setColor(Color.HSVToColor(alpha, hsv));
//
//		c.drawCircle(x, y, rad, paint);
//
////		Bitmap icon = layers[selectedLayer].editIcons.get(layer);
////		float demi = (float)(rad / Math.sqrt(2));
////		destRect.set(x - demi, y - demi, x + demi, y + demi);
////		c.drawBitmap(icon, null, destRect, paint);
//	}

	private void updateActors(PlatformGame newGame) {
		this.game.actors = Arrays.copyOf(this.game.actors, newGame.actors.length);
		actors = Arrays.copyOf(actors, newGame.actors.length);
		darkActors = Arrays.copyOf(darkActors, actors.length);
		for (int i = 0; i < newGame.actors.length; i++) {
			ActorClass newActor = newGame.actors[i];
			String newName = newActor.imageName;
			ActorClass oldActor = null; String oldName = null;
			oldActor = this.game.actors[i];
			oldName = oldActor == null ? null : oldActor.imageName;
			if (oldActor == null || !oldName.equals(newName) || 
					oldActor.zoom != newActor.zoom) {
				actors[i] = createActor(i, newGame);
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
				objects[i] = createObject(i, newGame);
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
		postOnUiThread(new Runnable() {
			@Override
			public void run() {
				pause();
				MapEditorActorSelectorView view = new MapEditorActorSelectorView(
						getContext(), actorSelection + 1, game);
				final Dialog dialog = getViewDialog(view);
				dialog.setOnShowListener(new OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						TutorialUtils.fireCondition(
								EditorAction.MapEditorStartActorSelection, 
								getContext());
					}
				});
				view.setOnSelectionListener(new OnSelectionListener() {
					
					@Override
					public void onSelection(int id) {
						dialog.dismiss();
						actorSelection = id - 1;
						refreshLayers();
						TutorialUtils.fireCondition(
								EditorAction.MapEditorFinishSelection, getContext());
					}
				});
				dialog.show();
			}
		});
	}

	public void selectTileset() {
		int id = game.getSelectedMap().tilesetId;
		final Tileset tileset = game.tilesets[id];
		
		postOnUiThread(new Runnable() {
			@Override
			public void run() {
				pause();
				MapEditorTextureSelectorView view = new MapEditorTextureSelectorView(
						getContext(), tileset, tilesetSelection); 
				final Dialog dialog = getViewDialog(view);
				dialog.setOnShowListener(new OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						TutorialUtils.fireCondition(
								EditorAction.MapEditorStartTextureSelection, 
								getContext());
					}
				});
				view.setPoster(new Poster() {
					@Override
					void post(Rect rect) {
						dialog.dismiss();
						tilesetSelection.set(rect);
						refreshLayers();
						TutorialUtils.fireCondition(
								EditorAction.MapEditorFinishSelection, getContext());
					}
				});
				dialog.show();
			}
		});
	}
	
	private void postOnUiThread(Runnable r) {
		new Handler(getContext().getMainLooper()).post(r);
	}
	
//	private AlertDialog getViewDialog(View view) {
//		AlertDialog dialog = new AlertDialog.Builder(getContext())
//		.setView(view)
//		.create();
//		dialog.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				resume();
//			}
//		});
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.getWindow().clearFlags(
//				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		return dialog;
//	}
	
	private Dialog getViewDialog(View view) {
		Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				resume();
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		return dialog;
	}

	public void selectObject() {
		postOnUiThread(new Runnable() {
			@Override
			public void run() {
				pause();
				MapEditorObjectSelectorView view = new MapEditorObjectSelectorView(
						getContext(), objectSelection, game);
				final Dialog dialog = getViewDialog(view);
				dialog.setOnShowListener(new OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						TutorialUtils.fireCondition(
								EditorAction.MapEditorStartObjectSelection, 
								getContext());
					}
				});
				view.setOnSelectionListener(new OnSelectionListener() {
					
					@Override
					public void onSelection(int id) {
						dialog.dismiss();
						objectSelection = id;
						refreshLayers();
						TutorialUtils.fireCondition(
								EditorAction.MapEditorFinishSelection, getContext());
					}
				});
				dialog.show();
			}
		});
	}

	public void refreshLayers() {
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
	
	private class LayerFan extends ButtonFan {

		public LayerFan(Button button) {
			super(button, Math.PI, -1);
		}

		@Override
		protected Bitmap getBitmap(int index) {
			return layers[index].getIcon();
		}

		@Override
		protected int getOptionsCount() {
			return layers.length;
		}

		private final EditorButton[] LAYER_BUTTONS = new EditorButton[] {
			EditorButton.MapEditorLayerTerrain1,
			EditorButton.MapEditorLayerTerrain2,
			EditorButton.MapEditorLayerTerrain3,
			EditorButton.MapEditorLayerActors,
			EditorButton.MapEditorLayerObjects
		};
		
		@Override
		protected EditorButton getEditorButton(int index) {
			return LAYER_BUTTONS[index];
		}

		@Override
		protected void onSelected(int option) {
			if (selectedLayer != option) {
				//adjust for the paint mode
				if (selectedLayer < 3 && option >= 3) {
					//switch off of tile layer
					if (editMode == EDIT_ALT1) editMode = EDIT_NORMAL;
					if (editMode == EDIT_ALT2) editMode = EDIT_ALT1;
				} else if (selectedLayer >= 3 && option < 3) {
					//switch onto tile layer
					if (editMode == EDIT_ALT1) editMode = EDIT_ALT2;
				}
				
				layers[selectedLayer].deSelect();
				selectedLayer = option;
			}
		}

		@Override
		protected int getSelectedOption() {
			return selectedLayer;
		}

		@Override
		protected int getColor(int option) {
			return getColorFromHue((option + 0.5f) * 160f / getOptionsCount() + 100);
		}
		
	}
	
	private class EditModeFan extends ButtonFan {

		public EditModeFan(Button button) {
			super(button, Math.PI, 1);
		}

		@Override
		protected Bitmap getBitmap(int index) {
			return layers[selectedLayer].editIcons.get(index);
		}

		@Override
		protected int getOptionsCount() {
			return layers[selectedLayer].editIcons.size();
		}

		@Override
		protected EditorButton getEditorButton(int index) {
			return layers[selectedLayer].getEditButtons()[index];
		}

		@Override
		protected void onSelected(int option) {
			editMode = option;
		}

		@Override
		protected int getSelectedOption() {
			return editMode;
		}

		@Override
		protected int getColor(int option) {
			return getColorFromHue((option + 0.5f) * 80f / getOptionsCount());
		}
		
	}
	
	private class MenuFan extends ButtonFan {

		private final Bitmap playIcon, databaseIcon;
		private final Handler handler;
		
		public MenuFan(Button button) {
			super(button, 0, -1);
			playIcon = BitmapFactory.decodeResource(getResources(), R.drawable.play);
			databaseIcon = BitmapFactory.decodeResource(getResources(), R.drawable.database);
			handler = new Handler(getContext().getMainLooper());
		}

		@Override
		protected Bitmap getBitmap(int index) {
			return index == 0 ? playIcon : databaseIcon;
		}

		@Override
		protected int getOptionsCount() {
			return 2;
		}

		@Override
		protected EditorButton getEditorButton(int index) {
			return index == 0 ? EditorButton.MapEditorMenuPlay : 
				EditorButton.MapEditorMenuDatabase;
		}

		@Override
		protected void onSelected(final int option) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					collapse();
					if (option == 0) {
						((MapEditor)getContext()).save();
						((MapEditor)getContext()).test();
					} else if (option == 1) {
						((MapEditor)getContext()).openDatabase();
					}
				}
			});
		}

		@Override
		protected int getSelectedOption() {
			return -1;
		}

		@Override
		protected int getColor(int option) {
			return Color.LTGRAY;
		}
		
	}
	
	private abstract class ButtonFan {
		
		private final static float SHOW_EXTENTION = 0.2f;
		
		private float extention;
		private boolean selecting;
		private boolean buttonDown;
		
		private final float hsv[] = new float[3];
		private final RectF destRect = new RectF();
		
		private final int dir;
		private final double startAngle;
		private final Button button;
		
		protected abstract Bitmap getBitmap(int option);
		protected abstract int getOptionsCount();
		protected abstract EditorButton getEditorButton(int index);
		protected abstract void onSelected(int option);
		protected abstract int getSelectedOption();
		protected abstract int getColor(int option);
		
		public int getPreviewSelectedOption() {
			if (extention > SHOW_EXTENTION) {
				int preview = getTouchingButton();
				return preview >= 0 ? preview : getSelectedOption();
			}
			return getSelectedOption();
		}
		
		public void collapse() {
			extention = 0;
		}
		
		public ButtonFan(Button button, double startAngle, int dir) {
			this.startAngle = startAngle;
			this.dir = dir;
			this.button = button;
		}
		
		public void upate() {
			if (selecting && !Input.isTouchDown()) {
				selecting = false;
				int selected = getTouchingButton();
				if (selected >= 0) {
					onSelected(selected);
					TutorialUtils.fireCondition(getEditorButton(selected), 
							getContext());
				}
			}
			
			if (buttonDown != button.down) {
				buttonDown = button.down;
				if (buttonDown) selecting = true;
			}
			
			if (selecting) {
				extention = (5 * extention + 1) / 6;
			} else {
				extention = (5 * extention) / 6;
			}
		}
		
		private int getTouchingButton() {
			int nButtons = getOptionsCount();
			float dx = Input.getLastTouchX() - button.cx;
			float dy = -(Input.getLastTouchY() - button.cy);
			float rad = (float)Math.pow(dx * dx + dy * dy, 0.5);
			if (Math.abs(rad - getOptionButtonOuterRadius()) < getOptionButtonRadius() * 1.5f) {
				double angle = Math.atan2(dy, dx) * dir - startAngle;
				angle = (angle + 2 * Math.PI) % (Math.PI * 2);
				int layer = (int)(angle / (Math.PI / 2) * nButtons);
				return Math.min(Math.max(layer, 0), nButtons - 1);
			}
			return -1;
		}
		
		public void draw(Canvas c, Paint paint) {

			if (extention > SHOW_EXTENTION) {
				int nOptions = getOptionsCount();
				int button = getTouchingButton();
				button = button >= 0 ? button : getSelectedOption();

				for (int i = 0; i < nOptions; i++) {
					if (i != button) drawButton(c, paint, i, false);
				}
				if (button >= 0) drawButton(c, paint, button, true);
			}
		}
		
		protected int getColorFromHue(float hue) {
			hsv[0] = hue;
			hsv[1] = 0.6f;
			hsv[2] = 0.8f;
			return Color.HSVToColor(hsv);
		}
		
		private void drawButton(Canvas c, Paint paint, int index, boolean selected) {
			float rad = getOptionButtonRadius();

			int nOptions = getOptionsCount();
			
			float degree = (float)(startAngle + dir * Math.PI / 2 * (index + 0.5) / (nOptions));
			float outterRadius = getOptionButtonOuterRadius() * extention;
			float x = button.cx +  FloatMath.cos(degree) * outterRadius;
			float y = button.cy - FloatMath.sin(degree) * outterRadius;

			int alpha = (int)((selected ? 255 : 150) * extention); 
			
			if (selected) {
				paint.setColor(Color.DKGRAY);
				paint.setAlpha(alpha);
				c.drawCircle(x, y, rad * 1.1f, paint);
			}

			paint.setColor(getColor(index));
			paint.setAlpha(alpha);
			
			if (TutorialUtils.isHighlighted(getEditorButton(index))) {
				paint.setColor(TutorialUtils.getHightlightColor());
			}

			c.drawCircle(x, y, rad, paint);

			Bitmap icon = getBitmap(index);
			if (icon != null) {
				float demi = (rad / FloatMath.sqrt(2));
				destRect.set(x - demi, y - demi, x + demi, y + demi);
				c.drawBitmap(icon, null, destRect, paint);
			}
		}
	}

	public static class EditorData implements Serializable {
		private static final long serialVersionUID = 1L;

		public int layer = 1, editMode = 0;
		public int tileSelectionLeft, tileSelectionTop; 
		public int tileSelectionRight = 1, tileSelectionBottom = 1;
		public int actorSelection, objectSelection;
	}
}
