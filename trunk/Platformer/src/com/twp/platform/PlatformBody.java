package com.twp.platform;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import edu.elon.honors.price.data.Data;
import edu.elon.honors.price.data.PlatformActor;
import edu.elon.honors.price.data.PlatformMap;
import edu.elon.honors.price.game.Game;
import edu.elon.honors.price.graphics.AnimatedSprite;
import edu.elon.honors.price.graphics.Sprite;
import edu.elon.honors.price.graphics.Tilemap;
import edu.elon.honors.price.graphics.Viewport;
import edu.elon.honors.price.input.Input;
import edu.elon.honors.price.physics.Body;
import edu.elon.honors.price.physics.Physics;
import edu.elon.honors.price.physics.Vector;

public class PlatformBody {

	private static final int FRAME = 150;
	private static final int BEHAVIOR_REST = 3;

	private Body body;
	private AnimatedSprite sprite;
	private PlatformActor actor;
	private Tilemap[] layers;
	private PlatformMap map;
	private RectF rect = new RectF();
	private Vector temp = new Vector();
	private boolean grounded;
	private boolean isHero;
	private ArrayList<PlatformBody> actors;
	private int lastDir;
	private int lastBehavior;
	private RectF lastRect = new RectF();
	private boolean collideX, collideY, collideBoth, edge;
	private int stun;

	public boolean isStunned() {
		return stun > 0;
	}

	public Body getBody() {
		return body;
	}

	public Vector getVelocity() {
		return body.getVelocity();
	}

	public Vector getPosition() {
		return body.getPosition();
	}

	public int getFacingDirectionX() {
		return (sprite.getFrame() / 4) == 1 ? -1 : 1;
	}
	
	public AnimatedSprite getSprite() {
		return sprite;
	}

	public PlatformBody(Viewport viewport, Physics physics, PlatformActor actor, float startX, float startY, 
			Tilemap[] layers, PlatformMap map, boolean isHero, ArrayList<PlatformBody> actors) {
		this.actor = actor;
		Bitmap bitmap = Data.loadActor(actor.imageName);
		Bitmap[] frames = Tilemap.createTiles(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, 0); 
		for (Bitmap bmp : frames) {
			Canvas c = new Canvas();
			c.setBitmap(bmp);
			Paint p = new Paint();
			p.setStyle(Style.STROKE);
			p.setColor(Color.BLACK);
			c.drawRect(new Rect(0, 0, bmp.getWidth() - 1, bmp.getHeight() - 1), p);
		}
		this.sprite = new AnimatedSprite(viewport, frames, startX, startY);
		this.body = new Body(physics, sprite);
		this.layers = layers;
		this.map = map;
		this.isHero = isHero;
		this.actors = actors;

		this.body.getVelocity().setX(actor.speed);
	}

	public void update(long timeElapsed) {

		Vector velocity = body.getVelocity();
		rect.set(body.getSprite().getRect());

		if (stun <= 0) {
			int frame = sprite.getFrame();
			if (velocity.getX() > 0 && (frame / 4 != 2 || !sprite.isAnimated())) {
				sprite.Animate(FRAME, 8, 4);
			} else if (velocity.getX() < 0 && (frame / 4 != 1 || !sprite.isAnimated())) {
				sprite.Animate(FRAME, 4, 4);
			}
			if (sprite.isAnimated()) {
				if (velocity.getX() == 0) {
					sprite.setFrame(frame - frame % 4);
				}
			}
		}

		velocity.addY(PlatformLogic.GRAVITY);

		temp.set(velocity);

		collideX = false;
		collideY = false;
		collideBoth = false;
		edge = false;

		for (int k = 0; k < layers.length; k++) {

			if (!map.layers[k].active)
				continue;

			Sprite[][] sprites = layers[k].getSprites();

			if (velocity.getX() != 0) {
				rect.offset(velocity.getX() * timeElapsed, 0);
				for (int i = 0; i < sprites.length; i++) {
					for (int j = 0; j < sprites[i].length; j++) {
						Sprite s = sprites[i][j];
						if (s != null) {
							if (RectF.intersects(s.getRect(), rect)) {
								rect.offset(-velocity.getX() * timeElapsed, 0);
								velocity.setX(0);
								collideX = true;
								break;

							}

						}
					}
				}
				rect.offset(-velocity.getX() * timeElapsed, 0);
			}

			if (velocity.getY() != 0) {
				int edgeCount = 0;
				boolean thisEdge = false;
				rect.offset(0, velocity.getY() * timeElapsed);
				for (int i = 0; i < sprites.length; i++) {
					for (int j = 0; j < sprites[i].length; j++) {
						Sprite s = sprites[i][j];
						if (s != null) {
							if (RectF.intersects(s.getRect(), rect)) {
								if (edgeCount > 0) {
									thisEdge = false;
									break;
								}

								if (rect.left < s.getRect().left && velocity.getX() < 0 ||
										rect.right > s.getRect().right && velocity.getX() > 0) {
									thisEdge = true;
								}
								edgeCount++;

								collideY = true;
							}
						}
					}
				}
				rect.offset(0, -velocity.getY() * timeElapsed);
				if (collideY) {
					velocity.setY(0);
				}
				edge |= thisEdge;
			}

			if (velocity.getX() != 0 && velocity.getY() != 0) {
				rect.offset(velocity.getX() * timeElapsed, velocity.getY() * timeElapsed);
				for (int i = 0; i < sprites.length; i++) {
					for (int j = 0; j < sprites[i].length; j++) {
						Sprite s = sprites[i][j];
						if (s != null) {
							if (RectF.intersects(s.getRect(), rect)) {
								rect.offset(-velocity.getX() * timeElapsed, -velocity.getY() * timeElapsed);
								velocity.clear();
								collideBoth = true;
								break;

							}

						}
					}
				}
				rect.offset(-velocity.getX() * timeElapsed, -velocity.getY() * timeElapsed);
			}
		}


		lastRect.set(sprite.getRect());
		if (velocity.getX() != 0) lastDir = (int)Math.signum(velocity.getX());
		grounded = velocity.getY() == 0;
		body.updatePhysics(timeElapsed);
		body.updateSprite();

		stun -= timeElapsed;
	}

	public void updateSprite() {
		body.updateSprite();
	}

	public void updateEvents() {
		if (stun > 0)
			return;

		RectF myRect = sprite.getRect();

		lastBehavior++;

		for (int i = 0; i < actors.size(); i++) {
			PlatformBody thatActor = actors.get(i);
			if (this != thatActor && !thatActor.isStunned()) {
				RectF sRect = thatActor.getSprite().getRect();
				RectF lastSRect = thatActor.lastRect;
				if (RectF.intersects(myRect, sRect)) {
					int[] behaviors = thatActor.isHero ? actor.heroContactBehaviors : actor.actorContactBehaviors;

					if (RectF.intersects(lastSRect, lastRect))
						continue;

					if (myRect.right > sRect.left && myRect.left < sRect.left &&
							!(lastRect.right > lastSRect.left && lastRect.left < lastSRect.left)) {
						//Game.debug("Right");
						doBehavior(behaviors[PlatformActor.RIGHT], temp, thatActor);
						//break;
					} if (myRect.left < sRect.right && myRect.right > sRect.right &&
							!(lastRect.left < lastSRect.right && lastRect.right > lastSRect.right)) {
						//Game.debug("Last: " + lastRect + ", " + lastSRect);
						//Game.debug("This: " + myRect + ", " + sRect);
						doBehavior(behaviors[PlatformActor.LEFT], temp, thatActor);
						//break;
					} if (myRect.bottom > sRect.top && myRect.top < sRect.top &&
							!(lastRect.bottom > lastSRect.top && lastRect.top < lastSRect.top)) {
						//Game.debug("Bottom");
						doBehavior(behaviors[PlatformActor.BELOW], temp, thatActor);
						//break;
					} if (myRect.top < sRect.bottom && myRect.bottom > sRect.bottom &&
							!(lastRect.top < lastSRect.bottom && lastRect.bottom > lastSRect.bottom)) {
						//Game.debug("Top");
						doBehavior(behaviors[PlatformActor.ABOVE], temp, thatActor);
						//break;
					}
				}
			}
		}

		if (collideX) {
			doBehavior(actor.wallBehavior, temp, null);
		} else if (edge) {
			doBehavior(actor.edgeBehavior, temp, null);
		}
	}
	
	public void dispose() {
		this.sprite.dispose();
		this.body.dispose();
		this.actors.remove(this);
	}

	public void doBehavior(int behavior, Vector oldVelocity, PlatformBody cause) {
		//		if (lastBehavior < BEHAVIOR_REST)
		//			return;
		lastBehavior = 0;

		Vector velocity = body.getVelocity();
		switch (behavior) {
		case PlatformActor.BEHAVIOR_STOP:
			velocity.setX(0);
			break;
		case PlatformActor.BEHAVIOR_JUMP_TURN:
			velocity.setY(-actor.jumpVelocity);
		case PlatformActor.BEHAVIOR_TURN:
			velocity.setX(-Math.signum(oldVelocity.getX()) * actor.speed);
			break;
		case PlatformActor.BEHAVIOR_JUMP:
			velocity.setY(-actor.jumpVelocity);
			break;
		case PlatformActor.BEHAVIOR_START_STOP:
			if (velocity.getX() == 0) {
				if (lastDir == 0) {
					velocity.setX(actor.speed);
				} else {
					velocity.setX(actor.speed * lastDir);
				}
			} else {
				velocity.setX(0);
			}
			break;
		case PlatformActor.BEHAVIOR_STUN:
			stun = actor.stunDuration;
			velocity.setY(-actor.jumpVelocity / 1.5f);
			sprite.flash(Color.RED, actor.stunDuration);
			if (cause == null)
				velocity.setX(actor.speed * -getFacingDirectionX() / 1.5f);
			else {
				float dir = Math.signum(this.getSprite().getRect().left - cause.getSprite().getRect().left);
				velocity.setX(actor.speed * dir / 1.5f);
			}
			if (isHero) {
				Input.getVibrator().vibrate(actor.stunDuration / 2);
			}
			break;
		case PlatformActor.BEHAVIOR_DIE:
			dispose();
			break;
		}	
	}
}
