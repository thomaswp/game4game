package com.twp.platform;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.UILayout;
import edu.elon.honors.price.game.Debug;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.physics.Vector;

public class PlatformLogic implements Logic {

	private static final int BORDER_Y = 130;
	private static final int BORDER_X = 350;
	public static final float GRAVITY = PhysicsHandler.GRAVITY;
	public static final float SCALE = PhysicsHandler.SCALE;
	public static final int MAX_WIDTH = 1024;

	private Map map;
	private PlatformGame game;

	private PhysicsHandler physics;
	private TriggerHandler triggerHandler;
	private BackgroundHandler backgroundHandler; 

	private LinkedList<JoyStick> joysticks = new LinkedList<JoyStick>();
	private LinkedList<Button> buttons = new LinkedList<Button>();
	private Sprite hero;
	private Vector cameraOffset = new Vector(), joystickPull = new Vector();
	private Vector offset;

	private LinkedList<Sprite> drawScreenSprites = new LinkedList<Sprite>();
	private LinkedList<Sprite> drawWorldSprites = new LinkedList<Sprite>();
	private Viewport drawViewport;

	private Interpreter interpreter;

	private Vector2 antiGravity = new Vector2(0, -PhysicsHandler.GRAVITY),
	zeroVector = new Vector2();

	private long gameTime = 0, lastGameTime = -1;
	
	private boolean paused;

	private String mapName;

	public PlatformGame getGame() {
		return game;
	}
	
	public long getGameTime() {
		return gameTime;
	}
	
	public long getLastGameTime() {
		return lastGameTime;
	}

	public Map getMap() {
		return map;
	}

	public PhysicsHandler getPhysics() {
		return physics;
	}

	public Interpreter getInterpreter() {
		return interpreter;
	}

	public Vector getOffset() {
		return offset;
	}

	public List<JoyStick> getJoysticks() {
		return joysticks;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	@Override
	public void setPaused(boolean paused) {
		paused = true;
	}

	public JoyStick getJoystick(int index) {
		if (joysticks.size() <= index) 
			return null;
		return joysticks.get(index);
	}

	public Button getButton(int index) {
		if (buttons.size() <= index) 
			return null;
		return buttons.get(index);
	}

	public PlatformLogic(String mapName, Game host) {
		this.mapName = mapName;
		//this.test = mapName.equals("final-Map_1");
		paused = true;
	}

	@Override
	public void initialize() {

		if (Graphics.getWidth() > MAX_WIDTH) {
			Graphics.setWidth(MAX_WIDTH);
		}
		Globals.setInstance(new Globals());

		if (game == null) game = new PlatformGame("", "");
		map = game.maps.get(game.selectedMapId);

		backgroundHandler = new BackgroundHandler(game);

		drawViewport = new Viewport();
		drawViewport.setZ(5);

		for (int i = 0; i < game.uiLayout.buttons.size(); i++) {
			UILayout.Button button = game.uiLayout.buttons.get(i);
			int x = button.x >= 0 ? button.x : Graphics.getWidth() + button.x;
			int y = button.y >= 0 ? button.y : Graphics.getHeight() + button.y;
			buttons.add(new Button(x, y, 10, 
					button.radius, button.color));
			buttons.get(i).setActive(button.defaultAction);
		}
		for (int i = 0; i < game.uiLayout.joysticks.size(); i++) {
			UILayout.JoyStick joystick = game.uiLayout.joysticks.get(i);
			int x = joystick.x >= 0 ? joystick.x : Graphics.getWidth() + joystick.x;
			int y = joystick.y >= 0 ? joystick.y : Graphics.getHeight() + joystick.y;
			joysticks.add(new JoyStick(x, y, 10, 
					joystick.radius, joystick.color));
			joysticks.get(i).setActive(joystick.defaultAction);
		}

		offset = new Vector();

		physics = new PhysicsHandler(this);
		hero = physics.getHero().getSprite();
		this.interpreter = new Interpreter(this);
		triggerHandler = new TriggerHandler(this);

		Viewport.DefaultViewport.setZ(3);

		update(1);
	}



	@Override
	public void update(long timeElapsed) {

		if (Input.isTapped()) paused = false;

		if (paused) {
			updateScroll(1);
			return;
		}
		
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).update();
		}
		for (int i = 0; i < joysticks.size(); i++) {
			joysticks.get(i).update();
		}

		ActorBody heroBody = physics.getHero();
		if (!heroBody.isStunned()) {
			for (int i = 0; i < buttons.size(); i++) {
				if (buttons.get(i).isActive() &&
						buttons.get(i).isTapped()) {
					heroBody.jump(true);
				}
			}

			boolean joyFound = false;
			joystickPull.set(0, 0);
			for (int i = 0; i < joysticks.size(); i++) {
				if (joysticks.get(i).isActive()) {
					joystickPull.set(joysticks.get(i).getPull());
					joyFound = true;
				}
			}

			if (heroBody.isOnLadder()) {
				heroBody.setVelocityY(joystickPull.getY() * heroBody.getActor().speed);
				antiGravity.set(0, -PhysicsHandler.GRAVITY * heroBody.getBody().getMass());
				heroBody.getBody().applyForce(antiGravity, zeroVector);
			}
			else {
				if (joyFound) {
					heroBody.setHorizontalVelocity(joystickPull.getX() * heroBody.getActor().speed);
				} else {
					heroBody.setHorizontalVelocity(0);
				}
			}
		}

		physics.update(timeElapsed, offset);
		updateWorldSprites();

		triggerHandler.checkTriggers();

		physics.checkBehaviors();
		physics.addAndRemove();

		interpreter.update();

		updateScroll(0.2f);
	
		lastGameTime = gameTime;
		gameTime += timeElapsed;
	}

	private void updateWorldSprites() {
		for (int i = 0; i < drawWorldSprites.size(); i++) {
			drawWorldSprites.get(i).setOriginX(offset.getX());
			drawWorldSprites.get(i).setOriginY(offset.getY());
		}
	}

	public void clearDrawScreen() {
		for (int i = 0; i < drawScreenSprites.size(); i++) {
			drawScreenSprites.get(i).dispose();
		}
		drawScreenSprites.clear();
		for (int i = 9; i < drawWorldSprites.size(); i++) {
			drawWorldSprites.get(i).dispose();
		}
		drawWorldSprites.clear();
	}

	public void drawLine(Paint paint, int x1, int y1, int x2, int y2, boolean world) {
		int left = Math.min(x1, x2), top = Math.min(y1, y2);
		int right = Math.max(x1, x2), bot = Math.max(y1, y2);
		int width = right - left, height = bot - top;
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Sprite s = new Sprite(drawViewport, bmp);
		s.getBitmapCanvas().drawLine(x1 - left, y1 - top, x2 - left, y2 - top, paint);
		s.setX(left);
		s.setY(top);
		addDrawSprite(s, world);
	}

	public void drawCircle(Paint paint, int x, int y, int rad, boolean world) {
		Debug.write("Draw: %d %d %d", x, y, rad);
		Bitmap bmp = Bitmap.createBitmap(rad * 2, rad * 2, Config.ARGB_8888);
		Sprite s = new Sprite(drawViewport, bmp);
		s.getBitmapCanvas().drawCircle(rad, rad, rad, paint);
		s.setX(x - rad);
		s.setY(y - rad);
		addDrawSprite(s, world);
	}

	public void drawBox(Paint paint, int x1, int y1, int x2, int y2, boolean world) {
		int left = Math.min(x1, x2), top = Math.min(y1, y2);
		int right = Math.max(x1, x2), bot = Math.max(y1, y2);
		int width = right - left, height = bot - top;
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Sprite s = new Sprite(drawViewport, bmp);
		s.getBitmapCanvas().drawRect(0, 0, width, height, paint);
		s.setX(left);
		s.setY(top);
		addDrawSprite(s, world);
	}

	private void addDrawSprite(Sprite s, boolean world) {
		if (world) {
			drawWorldSprites.add(s);
		} else {
			drawScreenSprites.add(s);
		}
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
		game = (PlatformGame) Data.loadGame(mapName, Game.getCurrentGame());
	}

	private void updateScroll(float snap) {
		cameraOffset.clear();

		RectF heroRect = hero.getRect();
		if (heroRect.left < BORDER_X) {
			cameraOffset.setX(BORDER_X - heroRect.left);
		}
		if (heroRect.right > Graphics.getWidth() - BORDER_X) {
			cameraOffset.setX((Graphics.getWidth() - BORDER_X) - heroRect.right);
		}
		if (heroRect.top < BORDER_Y) {
			cameraOffset.setY(BORDER_Y - heroRect.top);
		}
		if (heroRect.bottom > Graphics.getHeight() - BORDER_Y) {
			cameraOffset.setY((Graphics.getHeight() - BORDER_Y) - heroRect.bottom);
		}
		
		cameraOffset.multiply(snap);
		offset.add(cameraOffset);
		
		//bound camera to map borders
		int mapHeight = game.getSelectedMap().height(game);
		//The bottom doesn't show no matter what
		if (-offset.getY() + Graphics.getHeight() > physics.getMapFloorPx()) {
			offset.setY(Graphics.getHeight() - physics.getMapFloorPx());
		}
		if (mapHeight > Graphics.getHeight()) {
			//If we have more map height than screen height, bound the roof
			if (-offset.getY() < 0) {
				offset.setY(0);
			}
		}
		
		int mapWidth = game.getSelectedMap().width(game); 
		if (mapWidth < Graphics.getWidth()) {
			//If our map is smaller than our screen, center it
			offset.setX((Graphics.getWidth() - mapWidth) / 2);
		} else {
			if (-offset.getX() < physics.getMapLeftPx()) {
				offset.setX(physics.getMapLeftPx());
			}
			if (-offset.getX() + Graphics.getWidth() > physics.getMapRightPx()) {
				offset.setX(Graphics.getWidth() - physics.getMapRightPx());
			}
		}
		
		//Debug.write(offset.toString());


		physics.updateScroll(offset);
		for (int i = 0; i < drawScreenSprites.size(); i++) {
			Sprite sprite = drawScreenSprites.get(i);
			sprite.setX(offset.getX());
			sprite.setY(offset.getY());
		}

		backgroundHandler.updateScroll(offset);
	}

	public static class ActorAddable {
		public ActorClass actor;
		public float startX, startY;
		public int startDir;

		public ActorAddable(ActorClass actor, float startX, float startY) {
			this(actor, startX, startY, 1);
		}

		public ActorAddable(ActorClass actor, float startX, float startY, int startDir) {
			this.actor = actor;
			this.startX = startX;
			this.startY = startY;
			this.startDir = startDir;
		}
	}

	public static class ObjectAddable {
		public ObjectClass object;
		public float startX, startY;

		public ObjectAddable(ObjectClass object, float startX, float startY) {
			this.object = object;
			this.startX = startX;
			this.startY = startY;
		}
	}
}
