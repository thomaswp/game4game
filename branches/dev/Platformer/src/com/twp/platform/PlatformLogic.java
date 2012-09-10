package com.twp.platform;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.ActorClass;
import edu.elon.honors.price.data.ObjectClass;
import edu.elon.honors.price.data.PlatformGame;
import edu.elon.honors.price.data.Map;
import edu.elon.honors.price.data.UILayout;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.BackgroundSprite;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Button;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.input.JoyStick;
import edu.elon.honors.price.physics.Vector;

public class PlatformLogic implements Logic {

	private static final int BORDER = 130;
	public static final float GRAVITY = PhysicsHandler.GRAVITY;
	public static final float SCALE = PhysicsHandler.SCALE;

	private Map map;
	private PlatformGame game;

	private PhysicsHandler physics;
	private TriggerHandler triggerHandler;

	private BackgroundSprite foreground, background, midground;
	private LinkedList<JoyStick> joysticks = new LinkedList<JoyStick>();
	private LinkedList<Button> buttons = new LinkedList<Button>();
	private AnimatedSprite hero;
	private Vector p = new Vector(), joystickPull = new Vector();
	private Vector offset;

	private LinkedList<Sprite> drawScreenSprites = new LinkedList<Sprite>();
	private LinkedList<Sprite> drawWorldSprites = new LinkedList<Sprite>();
	private Viewport drawViewport;

	private Interpreter interpreter;

	private Vector2 antiGravity = new Vector2(0, -PhysicsHandler.GRAVITY),
	zeroVector = new Vector2();

	private float bgX, bgY, skyScroll;
	private int startBackgroundY, startForegroundY, startMidgroundY;

	private boolean paused;

	private String mapName;

	public PlatformGame getGame() {
		return game;
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
		Globals.setInstance(new Globals());

		if (game == null) game = new PlatformGame();
		map = game.maps.get(game.startMapId);

		Bitmap bmp = Data.loadForeground(map.groundImageName);
		int bottom = game.tilesets[map.tilesetId].tileHeight * map.rows;
		startForegroundY = bottom - map.groundY;
		foreground = new BackgroundSprite(bmp, new Rect(0, startForegroundY, 
				Graphics.getWidth(), startForegroundY + bmp.getHeight()), -6);

		bmp = Data.loadBackground(map.skyImageName);
		//Game.debug("%dx%d", Graphics.getWidth(), Graphics.getHeight());
		startBackgroundY = startForegroundY - Graphics.getHeight();
		background = new BackgroundSprite(bmp, new Rect(0, startBackgroundY, 
				Graphics.getWidth(), startForegroundY), -7);
		background.scroll(0, Graphics.getHeight() - bmp.getHeight());		

		if (map.midGrounds.size() > 0) {
			Bitmap mid = null;
			Paint paint = new Paint();
			paint.setFilterBitmap(true);
			for (int i = 0; i < map.midGrounds.size(); i++) {
				String path = map.midGrounds.get(i);
				bmp = Data.loadMidground(path);
				//Game.debug("%dx%d", bmp.getWidth(), bmp.getHeight());
				if (mid == null) {
					mid = bmp.copy(bmp.getConfig(), true);
				} else {
					Canvas c = new Canvas(mid);
					c.drawBitmap(bmp, 0, 0, paint);
				}
			}
			startMidgroundY = startForegroundY - mid.getHeight() + 80;
			midground = new BackgroundSprite(mid, new Rect(0, 
					startMidgroundY, Graphics.getWidth(), startMidgroundY + 
					mid.getHeight()), -5);
		}

		drawViewport = new Viewport();
		drawViewport.setZ(5);

		for (int i = 0; i < game.uiLayout.buttons.size(); i++) {
			UILayout.Button button = game.uiLayout.buttons.get(i);
			int x = button.x >= 0 ? button.x : Graphics.getWidth() + button.x;
			int y = button.y >= 0 ? button.y : Graphics.getHeight() + button.y;
			buttons.add(new Button(x, y, 10, 
					button.radius, button.color));
		}
		for (int i = 0; i < game.uiLayout.joysticks.size(); i++) {
			UILayout.JoyStick joystick = game.uiLayout.joysticks.get(i);
			int x = joystick.x >= 0 ? joystick.x : Graphics.getWidth() + joystick.x;
			int y = joystick.y >= 0 ? joystick.y : Graphics.getHeight() + joystick.y;
			joysticks.add(new JoyStick(x, y, 10, 
					joystick.radius, joystick.color));
		}

		offset = new Vector();

		physics = new PhysicsHandler(this);
		hero = physics.getHero().getSprite();
		this.interpreter = new Interpreter(this);
		triggerHandler = new TriggerHandler(this);

		Viewport.DefaultViewport.setZ(3);

		update(1);
		
//		for (int i = 0; i < 100; i++) {
//			Game.debug(i);
//			Bitmap b = Data.loadBackground("sky.png");
//			Sprite s = new Sprite(Viewport.DefaultViewport, b);
//		}
	}



	@Override
	public void update(long timeElapsed) {

		if (Input.isTapped()) paused = false;

		if (paused) {
			updateScroll();
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
				if (game.uiLayout.buttons.get(i).defaultAction &&
						buttons.get(i).isTapped()) {
					heroBody.jump(true);
				}
			}

			boolean joyFound = false;
			joystickPull.set(0, 0);
			for (int i = 0; i < joysticks.size(); i++) {
				if (game.uiLayout.joysticks.get(i).defaultAction) {
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

		updateScroll();
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
		Game.debug("Draw: %d %d %d", x, y, rad);
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

	private void updateScroll() {
		p.clear();

		int mHeight = game.tilesets[map.tilesetId].tileHeight * map.rows;
		int maxBgY = mHeight - Graphics.getHeight() - 96; 

		RectF heroRect = hero.getRect();
		if (heroRect.left < BORDER) {
			p.setX(BORDER - heroRect.left);
		}
		if (heroRect.right > Graphics.getWidth() - BORDER) {
			p.setX((Graphics.getWidth() - BORDER) - heroRect.right);
		}
		if (heroRect.top < BORDER) {
			p.setY(BORDER - heroRect.top);
		}
		if (heroRect.bottom > Graphics.getHeight() - BORDER) {
			if (bgY < maxBgY)
				p.setY((Graphics.getHeight() - BORDER) - heroRect.bottom);
		}

		offset.add(p);


		physics.updateScroll(offset);
		for (int i = 0; i < drawScreenSprites.size(); i++) {
			Sprite sprite = drawScreenSprites.get(i);
			sprite.setX(offset.getX());
			sprite.setY(offset.getY());
		}

		p.multiply(-1);

		p.multiply(0.7f);
		p.setX(p.getX() * 0.7f);

		bgX += p.getX(); bgY += p.getY();
		bgY = Math.min(maxBgY, bgY);

		foreground.scroll(p.getX(), 0);
		foreground.setY(startForegroundY - bgY);

		if (midground != null) {
			midground.scroll(p.getX(), 0);
			midground.setY(startMidgroundY - bgY);
		}

		background.setY(Math.min(0, startBackgroundY - bgY));
		float scroll = Math.min(0, background.getY() + bgY);
		background.scroll(p.getX(), scroll - skyScroll);
		skyScroll = scroll;
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
