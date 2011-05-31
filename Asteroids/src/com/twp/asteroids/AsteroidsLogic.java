package com.twp.asteroids;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.Region.Op;
import edu.elon.honors.price.game.Data;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.game.Logic;
import edu.elon.honors.price.graphics.Graphics;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;

public class AsteroidsLogic implements Logic {
	
	private final static float SHIP_ACCEL = 0.007f;
	private final static float DRAG = 0.001f;
	private final static int MOVE_DELAY = 70;
	private final static int THRESH = 3;
	private final static int MAX_BULLETS = 4;
	private final int START_ASTEROIDS = 5;
	private final int FRAME_LENGTH = 30;
	private final int NEXT_LEVEL_WAIT = 2000;
	private final static Random rand = new Random();
	
	private Sprite ship, thrust, pauseScreen, defeat, victory, stars, score;
	private AsteroidsData data;
	private boolean paused, newGame;
	private long lastTime, lastShoot;
	private Bitmap[] expAnimation;
	private int nextAnim;
	private long winTime;
	
	public void setNewGame(boolean newGame) {
		this.newGame = newGame;
	}
	
	public AsteroidsLogic() {
		newGame = true;
		createExpAnimation();
	}
	
	@Override
	public void initialize() {
		if (newGame) {
			newGame();
		}
		else {
			loadSprites();
			paused = true;
		}
	}

	public void newGame() {
		data = new AsteroidsData();
		
		nextLevel();
	}
	
	@Override
	public void dispose() {
		ship.dispose();
		thrust.dispose();
		for (Asteroid a : data.asteroids) {
			a.dispose();
		}
		for (Bullet b : data.bullets) {
			b.dispose();
		}
		for (Explosion e : data.explosions) {
			e.dispose();
		}
		pauseScreen.dispose();
		victory.dispose();
		defeat.dispose();
		stars.dispose();
		score.dispose();
	}

	@Override
	public void update() {
		if (Input.isTapped()) {
			paused = false;
			lastTime =  System.currentTimeMillis();
		}
		
		if (!paused) {
			long msPassed = System.currentTimeMillis() - lastTime;
			lastTime += msPassed;
			
			int s = data.score;
			
			updatePlayer(msPassed);
			updateAsteroids(msPassed);
			updateBullets(msPassed);
			updateExplosions(msPassed);
			
			if (s != data.score) {
				drawScoreBitmap();
			}
		}
		
		updateSprites();
	}

	@Override
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	@Override
	public void save(Activity parent) {
		Data.saveObject("ast", parent, data);		
	}

	@Override
	public void load(Activity parent) {
		AsteroidsData d = (AsteroidsData)Data.loadObject("ast", parent);
		if (d != null) {
			newGame = false;
			data = d;
		}
	}
	
	private void nextLevel() {
		data.level++;
		
		data.shipX = Graphics.getWidth() / 2;
		data.shipY = Graphics.getHeight() / 2;
		data.shipVX = 0;
		data.shipVY = 0;
		data.bullets = new LinkedList<Bullet>();
		data.asteroids = new LinkedList<Asteroid>();
		data.explosions = new LinkedList<Explosion>();
		data.state = 0;
		
		for (int i = 0; i < START_ASTEROIDS + data.level - 1; i++) {
			data.asteroids.add(new Asteroid(rand.nextInt(i/2+1)));
		}

		loadSprites();
		paused = true;
	}
	
	private void updateExplosions(long msPassed) {
		nextAnim -= msPassed;
		if (nextAnim <= 0) {
			nextAnim = FRAME_LENGTH;
			
			LinkedList<Explosion> remove = new LinkedList<Explosion>();
			
			for (Explosion e : data.explosions) {
				e.state++;
				if (e.state > expAnimation.length) {
					remove.add(e);
				}
			}
			
			for (Explosion e : remove) {
				e.dispose();
				data.explosions.remove(e);
			}
		}
	}
	
	private void updateBullets(long msPassed) {
		if (Input.isTapped() && data.bullets.size() < MAX_BULLETS && data.state != -1) {
			float dir = (data.shipRot - 90) / 180 * (float)Math.PI;
			float rad = ship.getWidth() / 2;
			float x = (float)(Math.cos(dir) * rad) + data.shipX;
			float y = (float)(Math.sin(dir) * rad) + data.shipY;
			Bullet b = new Bullet(x, y, dir);
			b.sprite = getNewBullet();
			data.bullets.add(b);
		}
		
		LinkedList<Bullet> remove = new LinkedList<Bullet>();
		LinkedList<Asteroid> hit = new LinkedList<Asteroid>();
		
		for (Bullet b : data.bullets) {
			b.x += b.getDx() * msPassed;
			b.y += b.getDy() * msPassed;
			
			for (Asteroid a : data.asteroids) {
				if (!a.sprite.intersection(b.sprite).isEmpty()) {
					if (!hit.contains(a)) {
						hit.add(a);
					}
					remove.add(b);
				}
			}
			
			if (b.x < 0 || b.x > Graphics.getWidth() ||
					b.y < 0 || b.y > Graphics.getHeight()) {
				remove.add(b);
			}
		}
		
		for (Asteroid a : hit) {
			data.asteroids.remove(a);
			float dir = 2 * (float)Math.PI * rand.nextFloat();
			int size = a.size + 1;
			if (size <= Asteroid.SMALLEST_SIZE) {
				float rad = Asteroid.getZoom(size) * a.sprite.getBitmap().getWidth() / 1.5f;
				float x1 = rad * (float)Math.cos(dir) + a.x, y1 = rad * (float)Math.sin(dir) + a.y;
				float x2 = rad * (float)Math.cos(dir + 180) + a.x, y2 = rad * (float)Math.sin(dir + 180) + a.y;
				Asteroid a1 = new Asteroid(x1, y1, size), a2 = new Asteroid(x2, y2, size);
				a1.sprite = getNewAsteroid(); a2.sprite = getNewAsteroid();
				data.asteroids.add(a1); data.asteroids.add(a2);
			}
			
			Explosion ex = new Explosion(a.x, a.y, a.getZoom() * 2, expAnimation);
			ex.sprite = getNewExplosion();
			data.explosions.add(ex);

			data.score += 50 * (Asteroid.SMALLEST_SIZE - a.size + 1); 
			
			a.dispose();
		}
		
		for (Bullet b : remove) {
			data.bullets.remove(b);
			b.dispose();
		}
	}
	
	private void updateAsteroids(long msPassed) {
		if (data.asteroids.size() == 0) {
			if (data.state != 1) {
				data.state = 1;
				winTime = System.currentTimeMillis();
			} else if (System.currentTimeMillis() - winTime > NEXT_LEVEL_WAIT) {
				dispose();
				nextLevel();
				return;
			}
		}
		
		for (int i = 0; i < data.asteroids.size(); i++) {
			Asteroid a = data.asteroids.get(i);
			
			a.x += a.dx * msPassed;
			a.y += a.dy * msPassed;
			
			a.x = flip(a.x, a.sprite.getWidth(), Graphics.getWidth());
			a.y = flip(a.y, a.sprite.getHeight(), Graphics.getHeight());
			
			a.rotation += a.dRotation * msPassed;
			
			for (int j = i + 1; j < data.asteroids.size(); j++) {
				Asteroid a2 = data.asteroids.get(j);
				if (a != a2) {
					if (!a.sprite.intersection(a2.sprite).isEmpty()) {
						
						double speed1 = Math.sqrt(a.dx * a.dx + a.dy * a.dy);
						double speed2 = Math.sqrt(a2.dx * a2.dx + a2.dy * a2.dy);
						float dx = a.x - a2.x;
						float dy = a.y - a2.y;
						double bSpeed = Math.sqrt(dx * dx + dy * dy);
						a.dx = (float)(dx * speed1 / bSpeed);
						a.dy = (float)(dy * speed1 / bSpeed);
						a2.dx = (float)(-dx * speed2 / bSpeed);
						a2.dy = (float)(-dy * speed2 / bSpeed);
						
					}
				}
			}
		}
	}
	
	private void updatePlayer(long msPassed) {
		
		thrust.setVisible(false);
		
		if (data.state == -1)
			return;
		
		if (Input.isTapped()) {
			lastShoot = lastTime;
		}
		if (Input.isTouchDown()) {
			float tx = Input.getLastTouchX();
			float ty = Input.getLastTouchY();
			
			float dx = tx - data.shipX;
			float dy = ty - data.shipY;
			
			double angle;
			if (dx == 0) {
				angle = dy > 0 ? Math.PI / 2 : 3 * Math.PI / 2;
			} else {
				angle = Math.atan(dy / dx);;
				if (dx < 0) angle += Math.PI;
			}
			
			data.shipRot = (float)(angle * 180 / Math.PI) + 90;
			
			if (lastTime - lastShoot > MOVE_DELAY) {
				data.shipVX += (float)Math.cos(angle) * SHIP_ACCEL;
				data.shipVY += (float)Math.sin(angle) * SHIP_ACCEL;
				thrust.setVisible(true);
			}
		}
		
		float dragC = 1 - DRAG * msPassed;
		
		data.shipVX *= dragC;
		data.shipVY *= dragC;
		
		data.shipX += data.shipVX * msPassed;
		data.shipY += data.shipVY * msPassed;
		
		int width = Graphics.getWidth();
		int height = Graphics.getHeight();
		float sWidth = ship.getWidth();
		float sHeight = ship.getHeight();
		
		data.shipX = flip(data.shipX, sWidth, width);
		data.shipY = flip(data.shipY, sHeight, height);
		
		for (Asteroid a : data.asteroids) {
			if (!a.sprite.intersection(ship).isEmpty()) {
				Explosion e = new Explosion(data.shipX, data.shipY, 2, expAnimation);
				e.sprite = getNewExplosion();
				data.explosions.add(e);
				data.state = -1;
				break;
			}
		}
	}
	
	private float flip(float x, float size, float max) {
		if (x > max + size / 2 + THRESH) {
			x = -size / 2;
		}
		if (x < -size / 2 - THRESH) {
			x = max + size / 2;
		}
		return x;
	}
	
	private void updateSprites() {
		ship.setX(data.shipX);
		ship.setY(data.shipY);
		ship.setRotation(data.shipRot);
		ship.setVisible(data.state != -1);
		thrust.setX(data.shipX);
		thrust.setY(data.shipY);
		thrust.setRotation(data.shipRot);
		
		for (Asteroid a : data.asteroids) {
			a.updateSprite();
		}
		
		for (Bullet b : data.bullets) {
			b.updateSprite();
		}
		
		for (Explosion e : data.explosions) {
			e.updateSprite();
		}
		
		pauseScreen.setVisible(paused && data.state == 0);
		victory.setVisible(data.state == 1);
		defeat.setVisible(data.state == -1);
	}
	
	private void loadSprites() {
		Bitmap bmp = Data.loadBitmap(R.drawable.ship);
		ship = new Sprite(Viewport.DefaultViewport, bmp);
		ship.centerOrigin();
		
		Bitmap sBmp = Data.loadBitmap(R.drawable.thrust);
		thrust = new Sprite(Viewport.DefaultViewport, sBmp);
		thrust.centerOrigin();
		thrust.setVisible(false);
		
		pauseScreen = getNewPause();
		drawPauseBitmap();
		
		victory = getTextSprite("Victory!", 36, Color.BLUE);
		defeat = getTextSprite("Defeat!", 36, Color.RED);
		
		stars = getNewStars();
		
		score = getNewScore();
		drawScoreBitmap();
		
		for (Asteroid a : data.asteroids) {
			a.sprite = getNewAsteroid();
		}
		
		for (Bullet b : data.bullets) {
			b.sprite = getNewBullet();
		}
		
		for (Explosion e : data.explosions) {
			e.sprite = getNewExplosion();
			e.expAnimation = expAnimation;
		}
	}
	
	private void drawPauseBitmap() {
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(24);
		
		String text = "Paused - Level " + (data.level);
		
		float width = p.measureText(text);
		
		pauseScreen.getBitmap().eraseColor(Color.TRANSPARENT);
		pauseScreen.getBitmapCanvas().drawText(text, 
				(pauseScreen.getWidth() - width) / 2, 
				(pauseScreen.getHeight() + 24) / 2, p);
	}
	
	private Sprite getNewPause() {
		Sprite s = new Sprite(Viewport.DefaultViewport, 
				Graphics.getWidth() / 2, 
				Graphics.getHeight() / 2, 
				200, 50);
		s.centerOrigin();
		s.setZ(10);
		return s;
	}
	
	private void drawScoreBitmap() {
		score.getBitmap().eraseColor(Color.TRANSPARENT);
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(24);
		score.getBitmapCanvas().drawText("" + data.score, 0, 22, p);
	}
	
	private Sprite getNewScore() {
		Sprite s = new Sprite(Viewport.DefaultViewport, 0, 0, 200, 30);
		s.setZ(10);
		return s;
	}
	
	
	private Sprite getNewStars() {
		Sprite s = new Sprite(Viewport.DefaultViewport, 0, 0, 
				Graphics.getWidth(), Graphics.getHeight());
		Bitmap bg = Data.loadBitmap(R.drawable.stars);
		int bgWidth = bg.getWidth(), bgHeight = bg.getHeight();
		Paint p = new Paint();
		for (int i = 0; i < s.getWidth(); i += bgWidth) {
			for (int j = 0; j < s.getHeight(); j += bgHeight) {
				s.getBitmapCanvas().drawBitmap(bg, i, j, p);
			}
		}
		s.setZ(-10);
		return s;
	}
	
	private Sprite getTextSprite(String text, float textSize, int color) {
		Paint p = new Paint();
		p.setColor(color);
		p.setTextSize(textSize);
		
		float width = p.measureText(text);
		Sprite s = new Sprite(Viewport.DefaultViewport, 
				Graphics.getWidth() / 2, 
				Graphics.getHeight() / 2,
				(int)width, (int)textSize * 2);
		s.centerOrigin();
		s.setZ(10);
		s.getBitmapCanvas().drawText(text, 0, textSize, p);
		return s;
	}
	
	private Sprite getNewExplosion() {
		Sprite s = new Sprite(Viewport.DefaultViewport, expAnimation[0]);
		s.setZ(5);
		s.centerOrigin();
		return s;
	}
	
	private Sprite getNewAsteroid() {
		Bitmap aBmp = Data.loadBitmap(R.drawable.rock);
		Sprite s = new Sprite(Viewport.DefaultViewport, aBmp);
		s.centerOrigin();
		return s;
	}
	
	private Sprite getNewBullet() {
		Sprite s = new Sprite(Viewport.DefaultViewport, 0, 0, 7, 7);
		s.getBitmapCanvas().drawCircle(3, 3, 3, Sprite.paintFromColor(Color.WHITE));
		s.centerOrigin();
		return s;
	}
	
	private void createExpAnimation() {
		expAnimation = new Bitmap[16];
		Bitmap exp = Data.loadBitmap(R.drawable.exp);
		Matrix m = new Matrix();
		int width = exp.getWidth(), height = exp.getHeight();
		int k = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int x = width * j / 4;
				int y = height * i / 4;
				Bitmap bmp = Bitmap.createBitmap(exp, x, y, width / 4, height / 4, m, true);
				expAnimation[k++] =  bmp;
			}
		}
	}
}
